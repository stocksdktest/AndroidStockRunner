package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.HKTItem;
import com.mitake.core.request.chart.TongLineRequest;
import com.mitake.core.response.IResponseCallback;
import com.mitake.core.response.Response;
import com.mitake.core.response.chart.TongLineResponse;

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
 *港股通/沪深股通额度走势
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.TONGLINETEST_1)
public class TongLineTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.TONGLINETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("TongLineTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("TongLineTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("TYPE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        TongLineRequest tongLineRequest = new  TongLineRequest();
        tongLineRequest.send(quoteNumbers,new IResponseCallback() {
            @Override
            public void callback(Response response) {
                try {
                    assertNotNull(response);
                } catch (AssertionError e) {
                    //                        result.completeExceptionally(e);
                    result.complete(new JSONObject());
                }
                JSONObject uploadObj = new JSONObject();
                try {
                    List<HKTItem> hktItems = ((TongLineResponse) response).hktItems;
                    for (int i=0;i<hktItems.size();i++){
                        JSONObject uploadObj_1 = new JSONObject();
                        uploadObj_1.put("datetime",hktItems.get(i).datetime);
                        uploadObj_1.put("SHInitialAmount",hktItems.get(i).shInitAmount);
                        uploadObj_1.put("SHRemainingAmount",hktItems.get(i).shRemainingAmount);
                        uploadObj_1.put("SHInflowAmount",hktItems.get(i).shInflowAmount);
                        uploadObj_1.put("SZInitialAmount",hktItems.get(i).szInitAmount);
                        uploadObj_1.put("SZRemainingAmount",hktItems.get(i).szRemainingAmount);
                        uploadObj_1.put("SZInflowAmount",hktItems.get(i).szInflowAmount);
                        uploadObj_1.put("SHSZInflowAmount",hktItems.get(i).shSzInflowAmount);
                        uploadObj.put(hktItems.get(i).datetime,uploadObj_1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                    Log.d("data", String.valueOf(uploadObj));
                result.complete(uploadObj);
            }

            @Override
            public void exception(int i, String s) {
                result.completeExceptionally(new Exception(s));
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
