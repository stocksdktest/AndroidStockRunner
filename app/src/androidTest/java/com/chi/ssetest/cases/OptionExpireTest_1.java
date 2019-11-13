package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
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

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *期权——交割月
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.OPTIONEXPIRETEST_1)
public class OptionExpireTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.OPTIONEXPIRETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("OptionExpireTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("OptionExpireTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("stockID");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            OptionExpireRequest request = new OptionExpireRequest();
            request.send(quoteNumbers,new IResponseInfoCallback<OptionExpireResponse>() {
                @Override
                public void callback(OptionExpireResponse optionExpireResponse) {
                    try {
                        assertNotNull(optionExpireResponse.list);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    try {
                        JSONObject uploadObj = new JSONObject();
                        if (optionExpireResponse.list!=null&&optionExpireResponse.list.length>0){
                            List<String> list=new ArrayList<>();
                            for (int i=0;i<optionExpireResponse.list.length;i++){
                                list.add(optionExpireResponse.list[i]);
                            }
                            uploadObj.put("list",new JSONArray(list));
                            Log.d("data", String.valueOf(uploadObj));
                        }
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
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}