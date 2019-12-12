package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.CateType;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.bean.quote.ConvertibleBoundItem;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.ConvertibleDebtRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.ConvertibleBoundResponse;
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
 *可转债溢价查询
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.CONVERTIBLETEST_1)
public class ConvertibleDebTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.CONVERTIBLETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("ConvertibleDebTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("ConvertibleDebTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            ConvertibleDebtRequest request = new  ConvertibleDebtRequest();
            request.send(quoteNumbers,new IResponseInfoCallback<ConvertibleBoundResponse>() {
                @Override
                public void callback(ConvertibleBoundResponse convertibleBoundResponse) {
                    try {
                        assertNotNull(convertibleBoundResponse.items);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    // TODO fill uploadObj with QuoteResponse value
                    try {
                        for (ConvertibleBoundItem item :convertibleBoundResponse.items) {
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("code",item.code);
                            uploadObj_1.put("name",item.name);
//                            uploadObj_1.put("market",item.market);
//                            uploadObj_1.put("subtype",item.subtype);
                            uploadObj_1.put("lastPrice",item.lastPrice);
//                            uploadObj_1.put("preClosePrice",item.preClosePrice);
                            uploadObj_1.put("premium",item.premium);
//                            uploadObj_1.put("upDownFlag",item.upDownFlag);
//                            uploadObj_1.put("changeRate",item.upDownFlag+item.changeRate);
                            if ("+".equals(item.upDownFlag)||"-".equals(item.upDownFlag)){
                                uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                            }else {
                                uploadObj.put("changeRate",item.changeRate);
                            }
                            uploadObj_1.put("change",item.change);
                            Log.d("data", String.valueOf(uploadObj_1));
                            uploadObj.put(item.code,uploadObj_1);
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}