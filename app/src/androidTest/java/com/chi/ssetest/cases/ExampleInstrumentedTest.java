package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.QUOTE_REQUEST_EXAMPLE)
public class ExampleInstrumentedTest {
    private static final StockTestcaseName testcaseName = StockTestcaseName.QUOTE_REQUEST_EXAMPLE;
    private static SetupConfig.TestcaseConfig testcaseConfig;

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("ExampleInstrumentedTest", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("ExampleInstrumentedTest", "requestWork");
        // TODO get custom args from param
        final String []quoteNumbers = rule.getParam().optString("QUOTE_NUMBERS", "").split(",");
        final CompletableFuture result = new CompletableFuture<JSONObject>();

        QuoteRequest request = new QuoteRequest();
        request.send(quoteNumbers, new IResponseInfoCallback() {
            @Override
            public void callback(Response response) {
                QuoteResponse quoteResponse = (QuoteResponse) response;
                assertNotNull(quoteResponse.quoteItems);
                JSONObject uploadObj = new JSONObject();
                // TODO fill uploadObj with QuoteResponse value
                try {
                    uploadObj.put("fake_result", quoteNumbers);

                } catch (JSONException e) {
                    result.completeExceptionally(e);
                }
                for (QuoteItem item : quoteResponse.quoteItems) {
                    Log.d("StockUnittest", item.toString());
                }
                result.complete(uploadObj);
            }
            @Override
            public void exception(ErrorInfo errorInfo) {
                result.completeExceptionally(new Exception(errorInfo.toString()));
            }
        });
        try {
            JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}