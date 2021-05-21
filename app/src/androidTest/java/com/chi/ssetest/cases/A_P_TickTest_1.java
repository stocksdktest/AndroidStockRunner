package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.TickItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.TickRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.Response;
import com.mitake.core.response.TickResponse;

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
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//分时明细对比竞品 方法一   最多返回100条数据
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.A_P_TickTest_1)
public class A_P_TickTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.A_P_TickTest_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000;
    final CompletableFuture result = new CompletableFuture<JSONObject>();
    private static JSONObject uploadObj = new JSONObject();
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("A_P_TickTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("A_P_TickTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE_A", "");
        final String Pages = rule.getParam().optString("COUNT", "");
        final String SubTypes = rule.getParam().optString("SUBTYPE_C", "");

        TickRequest request = new TickRequest();
        request.send(quoteNumbers,"0,"+Pages+",-1",SubTypes, new IResponseInfoCallback() {
            @Override
            public void callback(Response response) {
                TickResponse tickResponse = (TickResponse) response;
                try {
                    assertNotNull(tickResponse.tickItems);
                } catch (AssertionError e) {
                    //                        result.completeExceptionally(e);
                    result.complete(new JSONObject());
                }
                List<TickItem> list=tickResponse.tickItems;
                try {
                    if(list!=null){
                        for (int k=0;k<100;k++){
                            JSONObject uploadObj_1=new JSONObject();
                            String transactionTime=(list.get(k).getTransactionTime()).substring(0,6);
                            uploadObj_1.put("transactionTime",transactionTime);
                            uploadObj_1.put("singleVolume", String.valueOf(Math.round(Float.parseFloat(list.get(k).getSingleVolume()))));
                            uploadObj_1.put("transactionPrice", list.get(k).getTransactionPrice());
                            uploadObj_1.put("OpenInterestDiff", list.get(k).getOpenInterestDiff());
//                            Log.d("data", String.valueOf(uploadObj_1));
                            uploadObj.put(transactionTime,uploadObj_1);
                        }
                    }
//                    Log.d("data", String.valueOf(uploadObj));
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
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
        } catch (Exception e) {
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }
    }
}
