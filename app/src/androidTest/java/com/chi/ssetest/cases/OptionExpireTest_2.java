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
import com.mitake.core.request.OptionExpireRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.OptionExpireResponse;

import org.json.JSONArray;
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

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *期权——交割月方法二
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.OPTIONEXPIRETEST_2)
public class OptionExpireTest_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.OPTIONEXPIRETEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("OptionExpireTest_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("OptionExpireTest_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final String quoteNumbers1 = rule.getParam().optString("adjusted");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            OptionExpireRequest request = new OptionExpireRequest();
            request.send(quoteNumbers, Boolean.parseBoolean(quoteNumbers1),new IResponseInfoCallback<OptionExpireResponse>() {
                @Override
                public void callback(OptionExpireResponse optionExpireResponse) {
                    try {
                        assertNotNull(optionExpireResponse.list);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    try {
                        JSONObject uploadObj = new JSONObject();
                        if (optionExpireResponse.list!=null&&optionExpireResponse.list.length>0){
                            if (Boolean.parseBoolean(quoteNumbers1)){
                                //分割
                                List<String> datas=new ArrayList<>();
                                JSONObject uploadObj_1 = new JSONObject();
                                for (int i=0;i<optionExpireResponse.datas.length;i++){
                                    uploadObj_1.put("time",optionExpireResponse.datas[i][0]);
                                    uploadObj_1.put("day",optionExpireResponse.datas[i][1]);
                                    uploadObj.put(String.valueOf(i+1),uploadObj_1);
                                }
                            }else {
                                //未分割
                                List<String> list=new ArrayList<>();
                                for (int i=0;i<optionExpireResponse.list.length;i++){
                                    list.add(optionExpireResponse.list[i]);
                                }
                                uploadObj.put("list",new JSONArray(list));
                            }
                        }
                        Log.d("data", String.valueOf(uploadObj));
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