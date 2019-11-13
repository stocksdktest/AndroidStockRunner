package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.AHQuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AHQuoteListRequest;
import com.mitake.core.response.AHQuoteListResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.Response;

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
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//AH股列表 方法二
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.AHLISTTEST_2)
public class AHListTest_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.AHLISTTEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("AHListTest_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("AHListTest_2", "requestWork");
        // TODO get custom args from param
        final String Params = rule.getParam().optString("param", "");
        final CompletableFuture result = new CompletableFuture<JSONObject>();

//        for (int i=0;i<Params.length;i++){
            AHQuoteListRequest request = new AHQuoteListRequest();
            request.sendV2(Params, new IResponseInfoCallback() {
                @Override
                public void callback(Response response) {
                    AHQuoteListResponse ahQuoteListResponse = (AHQuoteListResponse) response;
                    try {
                        assertNotNull(ahQuoteListResponse.mAHQuoteItems);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    List<AHQuoteItem> list=ahQuoteListResponse.mAHQuoteItems;
                    JSONObject uploadObj=new JSONObject();
                    // TODO fill uploadObj with QuoteResponse value
                    try {
                        for (int k=0;k<list.size();k++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("name", list.get(k).name);
                            uploadObj_1.put("codeA", list.get(k).codeA);
                            uploadObj_1.put("lastPriceA", list.get(k).lastPriceA);
                            uploadObj_1.put("preClosePriceA", list.get(k).preClosePriceA);
                            uploadObj_1.put("datetimeA", list.get(k).datetimeA);
                            uploadObj_1.put("codeH", list.get(k).codeH);
                            uploadObj_1.put("lastPriceH", list.get(k).lastPriceH);
                            uploadObj_1.put("preClosePriceH", list.get(k).preClosePriceH);
                            uploadObj_1.put("datetimeH", list.get(k).datetimeH);
                            uploadObj_1.put("premiumAH", list.get(k).premiumAH);
                            uploadObj_1.put("changeRateA", list.get(k).changeRateA);
                            uploadObj_1.put("changeRateH", list.get(k).changeRateH);
                            Log.d("data", String.valueOf(uploadObj_1));
                            uploadObj.put(list.get(k).datetimeA,uploadObj_1);
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}
