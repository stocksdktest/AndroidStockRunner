package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.UpdownsItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.UpdownsRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;
import com.mitake.core.response.UpdownsResponse;
import com.mitake.widget.UpDownInOutFiveView;

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
 *沪深A股及指数涨跌平家数
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.UPDOWNSTEST_1)
public class UpdownsTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.UPDOWNSTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("UpdownsTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("UpdownsTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("UDCODE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            UpdownsRequest request = new UpdownsRequest();
            request.send(quoteNumbers,new IResponseInfoCallback<UpdownsResponse>() {
                @Override
                public void callback(UpdownsResponse updownsResponse) {
                    try {
                        assertNotNull(updownsResponse.mUpdownsItem);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    JSONObject uploadObj = new JSONObject();
                    UpdownsItem list = updownsResponse.mUpdownsItem;
                    // TODO fill uploadObj with QuoteResponse value
                    try {
                        if(list!=null){
                            uploadObj.put("upCount",list.upCount);
                            uploadObj.put("downCount",list.downCount);
                            uploadObj.put("sameCount",list.sameCount);
                        }
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
//                    Log.d("data", String.valueOf(uploadObj));
                    result.complete(uploadObj);
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
