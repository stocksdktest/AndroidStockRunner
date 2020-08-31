package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.ConvertibleDebtItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.ConvertibleDebtListRequest;
import com.mitake.core.response.ConvertibleDebtListResponse;
import com.mitake.core.response.IResponseInfoCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *可转债获取列表
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.CONVERTIBLEDEBTLISTTEST_1)
public class ConvertibleDebtListTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.CONVERTIBLEDEBTLISTTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("ConvertibleDebtListTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("ConvertibleDebtListTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("PARAMS");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        ConvertibleDebtListRequest request = new ConvertibleDebtListRequest();
        request.send(quoteNumbers, new IResponseInfoCallback<ConvertibleDebtListResponse>() {
            @Override
            public void callback(ConvertibleDebtListResponse convertibleDebtListResponse) {
                try {
                    assertNotNull(convertibleDebtListResponse.convertibleDebtItems);
                } catch (AssertionError e) {
                    //                        result.completeExceptionally(e);
                    result.complete(new JSONObject());
                }
                JSONObject uploadObj = new JSONObject();
                // TODO fill uploadObj with QuoteResponse value
                List<ConvertibleDebtItem> list=convertibleDebtListResponse.convertibleDebtItems;
                try {
                    if(list!=null){
                        for (int k=0;k<list.size();k++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("code", list.get(k).code);
                            uploadObj_1.put("name", list.get(k).name);
                            uploadObj_1.put("market", list.get(k).market);
                            uploadObj_1.put("subtype", list.get(k).subtype);
                            uploadObj_1.put("lastPrice", list.get(k).lastPrice);
                            uploadObj_1.put("preClosePrice", list.get(k).preClosePrice);
                            uploadObj_1.put("changeRate", list.get(k).changeRate);
                            uploadObj_1.put("dateTime", list.get(k).dateTime);
                            uploadObj_1.put("zgCode", list.get(k).zgCode);
                            uploadObj_1.put("zgName", list.get(k).zgName);
                            uploadObj_1.put("zgMarket", list.get(k).zgMarket);
                            uploadObj_1.put("zgSubtype", list.get(k).zgSubtype);
                            uploadObj_1.put("zgLastPrice", list.get(k).zgLastPrice);
                            uploadObj_1.put("zgPreClosePrice", list.get(k).zgPreClosePrice);
                            uploadObj_1.put("zgChangeRate", list.get(k).zgChangeRate);
                            uploadObj_1.put("zgDateTime", list.get(k).zgDateTime);
                            uploadObj_1.put("conversionPremiumRate", list.get(k).conversionPremiumRate);
                            uploadObj_1.put("conversionPrice", list.get(k).conversionPrice);
                            uploadObj_1.put("conversionValue", list.get(k).conversionValue);
                            uploadObj.put(String.valueOf(k+1),uploadObj_1);
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
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
        } catch (Exception e) {
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }
    }
}
