package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.model.TradeDateModel;
import com.mitake.core.request.TradeDateRequest;
import com.mitake.core.response.IResponseCallback;
import com.mitake.core.response.Response;
import com.mitake.core.response.TradeDateResponse;
import com.mitake.core.response.TradeDateResponseV2;

import org.json.JSONException;
import org.json.JSONObject;
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
 *市场当年交易日1
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.TRADEDATETEST_1)
public class TradeDateTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.TRADEDATETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("TradeDateSampleTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("TradeDateSampleTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("markets");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
           final TradeDateRequest request = new TradeDateRequest();
            request.downLoad(quoteNumbers, new IResponseCallback() {

                @Override
                public void callback(Response response) {
                    assertNotNull(response);
                    JSONObject uploadObj = new JSONObject();
                    try {
                        uploadObj.put("status",response.status);
                        uploadObj.put("message",response.message);
                        uploadObj.put("extra",response.extra);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("data", String.valueOf(uploadObj));
                    result.complete(uploadObj);
                }

                @Override
                public void exception(int i, String s) {
                    result.completeExceptionally(new Exception(s));
                }
            });
            try {
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}