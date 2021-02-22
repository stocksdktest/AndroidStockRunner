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
import com.mitake.core.request.ab.ABQuoteRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.ab.ABQuoteResponse;

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
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//AB股联动接口 方法一
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.ABQUOTETEST_1)
public class ABQuoteTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.ABQUOTETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("ABQuoteTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("ABQuoteTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE", "");
        final CompletableFuture result = new CompletableFuture<JSONObject>();

//        for (int i=0;i<quoteNumbers.length;i++){
            ABQuoteRequest request = new ABQuoteRequest();
            request.send(quoteNumbers, new IResponseInfoCallback<ABQuoteResponse>() {
                @Override
                public void callback(ABQuoteResponse abQuoteResponse) {
                    try {
                        assertNotNull(abQuoteResponse);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    JSONObject uploadObj = new JSONObject();
                    // TODO fill uploadObj with QuoteResponse value
                    try {
                        if (abQuoteResponse!=null){
                            uploadObj.put("code",abQuoteResponse.abQuoteItem.code);
                            uploadObj.put("name",abQuoteResponse.abQuoteItem.name);
//                            uploadObj.put("market",abQuoteResponse.abQuoteItem.market);//ios没有该字段
                            uploadObj.put("subtype",abQuoteResponse.abQuoteItem.subtype);
                            uploadObj.put("lastPrice",abQuoteResponse.abQuoteItem.lastPrice == "一" ? "-":abQuoteResponse.abQuoteItem.lastPrice);
//                            uploadObj.put("preClosePrice",abQuoteResponse.abQuoteItem.preClosePrice == "一" ? "-":abQuoteResponse.abQuoteItem.preClosePrice);//ios没有该字段
                            uploadObj.put("change",abQuoteResponse.abQuoteItem.change == null ? "-":abQuoteResponse.abQuoteItem.change);
                            //涨跌幅
                            uploadObj.put("changeRate",abQuoteResponse.abQuoteItem.changeRate == null ? "-":abQuoteResponse.abQuoteItem.changeRate);
                            uploadObj.put("premiumRateAB",abQuoteResponse.abQuoteItem.premiumRateAB.isEmpty() ? "-":abQuoteResponse.abQuoteItem.premiumRateAB);
                            uploadObj.put("premiumRateBA",abQuoteResponse.abQuoteItem.premiumRateBA.isEmpty() ? "-":abQuoteResponse.abQuoteItem.premiumRateBA);
                        }
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
//                    Log.d("data",uploadObj.toString());
                    result.complete(uploadObj);
                }

                @Override
                public void exception(ErrorInfo errorInfo) {
                    result.completeExceptionally(new Exception(errorInfo.toString()));
                }
            });
//        }
        try {
            JSONObject resultObj = (JSONObject)result.get(timeout_ms, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
        } catch (Exception e) {
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }
    }
}
