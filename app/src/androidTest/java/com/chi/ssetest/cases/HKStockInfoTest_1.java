package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.HKOtherItem;
import com.mitake.core.HKOthersInfo;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.HKStockInfoRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.HKStockInfoResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *港股其他
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.HKSTOCKINFOTEST_1)
public class HKStockInfoTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.HKSTOCKINFOTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass

    public static void setup() throws Exception {
        Log.d(" HKStockInfoTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("HKStockInfoTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final String quoteNumbers1 = rule.getParam().optString("SUBTYPE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            HKStockInfoRequest request = new HKStockInfoRequest();
            request.send(quoteNumbers,quoteNumbers1, new IResponseInfoCallback<HKStockInfoResponse>() {
                @Override
                public void callback(HKStockInfoResponse hkStockInfoResponse) {
                    try {
                        assertNotNull(hkStockInfoResponse.info);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    HKOthersInfo list=hkStockInfoResponse.info;
                    JSONObject uploadObj = new JSONObject();
                    // TODO fill uploadObj with QuoteResponse value
                    try {
                        if(list!=null){
                            uploadObj.put("vcmDataTimestamp",list.vcmDataTimestamp);
                            uploadObj.put("vcmStartTime",list.vcmStartTime);
                            uploadObj.put("vcmEndTime",list.vcmEndTime);
                            uploadObj.put("vcmReffPrice",list.vcmReffPrice);
                            uploadObj.put("vcmLowerPrice",list.vcmLowerPrice);
                            uploadObj.put("vcmUpperPrice",list.vcmUpperPrice);
                            uploadObj.put("casDataTimestamp",list.casDataTimestamp);
                            uploadObj.put("casOrdImbDirection",list.casOrdImbDirection);
                            uploadObj.put("casOrdImbQty",list.casOrdImbQty);
                            uploadObj.put("casReffPrice",list.casReffPrice);
                            uploadObj.put("casLowerPrice",list.casLowerPrice);
                            uploadObj.put("casUpperPrice",list.casUpperPrice);

                            if (list.list!=null&&list.list.size()>0){
                                for (int k=0;k<list.list.size();k++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("OrderId",list.list.get(k).OrderId);
                                    uploadObj_1.put("OrderQty",list.list.get(k).OrderQty);
                                    uploadObj_1.put("Price",list.list.get(k).Price);
                                    uploadObj_1.put("BrokerID",list.list.get(k).BrokerID);
                                    uploadObj_1.put("Side",list.list.get(k).Side);
                                    uploadObj_1.put("DataTimestamp",list.list.get(k).DataTimestamp);
                                    uploadObj.put(list.list.get(k).OrderId,uploadObj_1);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
//                    Log.d("data", String.valueOf(uploadObj));
                    result.complete(uploadObj);
                }
                @Override
                public void exception(ErrorInfo errorInfo) {
                    result.completeExceptionally(new Exception(errorInfo.toString()));
                }
            });
            try {
                JSONObject resultObj = (JSONObject)result.get(timeout_ms, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                //                throw new Exception(e);
                throw new TestcaseException(e,rule.getParam());
            }
//        }
    }
}