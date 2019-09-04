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
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.response.IResponseCallback;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteDetailResponse;
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
//行情快照 方法二
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.QUOTEDETAILTEST_2)
public class QuoteDetailTest_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.QUOTEDETAILTEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("QuoteDetailTest_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("QuoteDetailTest_2", "requestWork");
        // TODO get custom args from param

        final String []quoteNumbers = rule.getParam().optString("CODES", "").split(",");
        final String []count = rule.getParam().optString("COUNTS", "").split(",");
        final String []INTS1 = rule.getParam().optString("INTS1", "").split(",");
        final String []INTS2 = rule.getParam().optString("INTS2", "").split(",");

        final CompletableFuture result = new CompletableFuture<JSONObject>();

        for (int i=0;i<quoteNumbers.length;i++){
            QuoteDetailRequest request = new QuoteDetailRequest();
            request.send(quoteNumbers[i],count[i],null,null, new IResponseInfoCallback<QuoteResponse>() {
                @Override
                public void callback(QuoteResponse quoteResponse) {
                    assertNotNull(quoteResponse.quoteItems);
                    JSONObject uploadObj = new JSONObject();
                    // TODO fill uploadObj with QuoteResponse value
                    try {
                        uploadObj.put("fake_result", quoteNumbers);
//                    uploadObj.put
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
        }

        try {
            JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, resultObj);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
