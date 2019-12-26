package com.chi.ssetest.cases;


import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AHQuoteRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.response.AHQuoteResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;

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
//AH股联动 方法一
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.AHQUOTETEST_1)
public class AHQuoteTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.AHQUOTETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("AHQuoteTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("AHQuoteTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE", "");
        final CompletableFuture result = new CompletableFuture<JSONObject>();

//        for (int i=0;i<quoteNumbers.length;i++){
            AHQuoteRequest request = new AHQuoteRequest();
            request.send(quoteNumbers, new IResponseInfoCallback() {
                @Override
                public void callback(Response response) {
                    AHQuoteResponse ahQuoteResponse = (AHQuoteResponse) response;
                    try {
                        assertNotNull(ahQuoteResponse);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    // TODO fill uploadObj with QuoteResponse value
                    try {
                        if (ahQuoteResponse!=null){
                            uploadObj.put("code",ahQuoteResponse.code);
                            uploadObj.put("name",ahQuoteResponse.name);
                            uploadObj.put("lastPrice",ahQuoteResponse.lastPrice);
                            uploadObj.put("premium",ahQuoteResponse.premium);
                            uploadObj.put("preClosePrice",ahQuoteResponse.preClosePrice);
                            //涨跌幅
                            uploadObj.put("changeRate",ahQuoteResponse.changeRate);
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
            throw new Exception(e);
        }
    }
}
