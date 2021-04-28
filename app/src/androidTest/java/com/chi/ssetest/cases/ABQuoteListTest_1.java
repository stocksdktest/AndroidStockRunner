package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.ab.ABQuoteListItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.ab.ABQuoteListRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.ab.ABQuoteListResponse;

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
//AB股列表接口 方法一
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.ABQUOTELISTTEST_1)
public class ABQuoteListTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.ABQUOTELISTTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("ABQuoteListTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("ABQuoteListTest_1", "requestWork");
        // TODO get custom args from param
//        final String []Params = rule.getParam().optString("param", "").split(";");
        final String Params = rule.getParam().optString("PARAMS", "");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<Params.length;i++){
        ABQuoteListRequest request = new ABQuoteListRequest();
            request.send(Params, new IResponseInfoCallback<ABQuoteListResponse>() {
                @Override
                public void callback(ABQuoteListResponse abQuoteListResponse) {
                    try {
                        assertNotNull(abQuoteListResponse.abQuoteListItems);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    List<ABQuoteListItem> list=abQuoteListResponse.abQuoteListItems;
                    JSONObject uploadObj=new JSONObject();
                    // TODO fill uploadObj with QuoteResponse value
                    try {
                        if(list!=null){
                            for (int k=0;k<list.size();k++){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("codeA", list.get(k).codeA);
//                                uploadObj_1.put("nameA", list.get(k).nameA);
////                                uploadObj_1.put("marketA", list.get(k).marketA);//ios没有该字段
//                                uploadObj_1.put("subtypeA", list.get(k).subtypeA);
//                                uploadObj_1.put("lastPriceA", list.get(k).lastPriceA);
////                                uploadObj_1.put("preClosePriceA", list.get(k).preClosePriceA);//ios没有该字段
//                                uploadObj_1.put("changeA", list.get(k).changeA);
//                                uploadObj_1.put("changeRateA", list.get(k).changeRateA);
//                                uploadObj_1.put("datetimeA", list.get(k).datetimeA);
                                uploadObj_1.put("codeB", list.get(k).codeB);
//                                uploadObj_1.put("nameB", list.get(k).nameB);
////                                uploadObj_1.put("marketB", list.get(k).marketB);//ios没有该字段
//                                uploadObj_1.put("subtypeB", list.get(k).subtypeB);
//                                uploadObj_1.put("lastPriceB", list.get(k).lastPriceB == "一" ? "-":list.get(k).lastPriceB);
////                                uploadObj_1.put("preClosePriceB", list.get(k).preClosePriceB == "一" ? "-":list.get(k).preClosePriceB);//ios没有该字段
//                                uploadObj_1.put("changeB", list.get(k).changeB == null ? "-":list.get(k).changeB);
//                                uploadObj_1.put("changeRateB", list.get(k).changeRateB == null ? "-":list.get(k).changeRateB);
//                                uploadObj_1.put("datetimeB", list.get(k).datetimeB.isEmpty() ? "-":list.get(k).datetimeB);
//                                uploadObj_1.put("premiumRateAB", list.get(k).premiumAB.isEmpty() ? "-":list.get(k).premiumAB);
//                                uploadObj_1.put("premiumRateBA", list.get(k).premiumBA.isEmpty() ? "-":list.get(k).premiumBA);
                                uploadObj.put(list.get(k).codeA,uploadObj_1);
                            }
                        }
//                        Log.d("data", String.valueOf(uploadObj));
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
            } catch (Exception e) {
                //                throw new Exception(e);
                throw new TestcaseException(e,rule.getParam());
            }
//        }
    }
}
