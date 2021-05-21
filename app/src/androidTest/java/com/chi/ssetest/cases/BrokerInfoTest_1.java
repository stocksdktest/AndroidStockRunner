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
import com.mitake.core.BrokerInfoItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BrokerInfoRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BrokerInfoResponse;
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
 *经纪席位
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.BROKERINFOTEST_1)
public class BrokerInfoTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.BROKERINFOTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("BrokerInfoTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("BrokerInfoTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            BrokerInfoRequest request = new BrokerInfoRequest();
            request.send(quoteNumbers,new IResponseInfoCallback<BrokerInfoResponse>() {
                @Override
                public void callback(BrokerInfoResponse brokerInfoResponse) {
                    try {
                        assertNotNull(brokerInfoResponse.list);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    ArrayList<BrokerInfoItem> list=brokerInfoResponse.list;
                    JSONObject uploadObj = new JSONObject();
                    try {
                        if(list!=null){
                            for (int i=0;i<list.size();i++) {
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("corp",list.get(i).corp);
                                uploadObj_1.put("corporation",list.get(i).corporation);
                                uploadObj_1.put("state",list.get(i).state);
//                                Log.d("data", String.valueOf(uploadObj_1));
                                uploadObj.put(String.valueOf(i+1),uploadObj_1);
                            }
                        }
//                        Log.d("data", String.valueOf(uploadObj));
                        result.complete(uploadObj);
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
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