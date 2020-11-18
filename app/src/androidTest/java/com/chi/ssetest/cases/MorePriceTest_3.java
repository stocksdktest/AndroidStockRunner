package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.MorePriceResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *分价——沪深港 sendV2  方法三
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName. MOREPRICETEST_3)
public class MorePriceTest_3 {
    private static final StockTestcaseName testcaseName = StockTestcaseName. MOREPRICETEST_3;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass

    public static void setup() throws Exception {
        Log.d(" MorePriceTest_3", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d(" MorePriceTest_3", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final String quoteNumbers1 = rule.getParam().optString("SUBTYPE");
        final String quoteNumbers2 = rule.getParam().optString("PARAMS");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
//        for (int i=0;i<quoteNumbers.length;i++){
           MorePriceRequest request = new  MorePriceRequest();
            request.sendV2(quoteNumbers,quoteNumbers1,quoteNumbers2,new IResponseInfoCallback<MorePriceResponse>() {
                @Override
                public void callback(MorePriceResponse morePriceResponse) {
                    try {
                        assertNotNull(morePriceResponse.strs);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    try {
                        JSONObject uploadObj = new JSONObject();
                        if(morePriceResponse.strs!=null){
                            for (int i=0;i<morePriceResponse.strs.length;i++){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("price",morePriceResponse.strs[i][0]);
                                uploadObj_1.put("volume",morePriceResponse.strs[i][1]== "一" ? "-" :morePriceResponse.strs[i][1]);
                                uploadObj_1.put("buyVolume",morePriceResponse.strs[i][2]== "一" ? "-" :morePriceResponse.strs[i][2]);
                                uploadObj_1.put("sellVolume",morePriceResponse.strs[i][3]== "一" ? "-" :morePriceResponse.strs[i][3]);
                                uploadObj_1.put("unknownVolume",morePriceResponse.strs[i][4]== "一" ? "-" :morePriceResponse.strs[i][4]);
                                uploadObj_1.put("tradeCount",morePriceResponse.strs[i][5].isEmpty() ? "-" :morePriceResponse.strs[i][5]);
                                uploadObj_1.put("buyCount",morePriceResponse.strs[i][6].isEmpty() ? "-" :morePriceResponse.strs[i][6]);
                                uploadObj_1.put("sellCount",morePriceResponse.strs[i][7].isEmpty() ? "-" :morePriceResponse.strs[i][7]);
                                uploadObj_1.put("unknownCount",morePriceResponse.strs[i][8].isEmpty() ? "-" :morePriceResponse.strs[i][8]);
//                                Log.d("data", String.valueOf(uploadObj_1));
                                uploadObj.put(String.valueOf(i+1),uploadObj_1);
                            }
                        }
//                            Log.d("data", String.valueOf(uploadObj));
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
//        }
    }
}