//package com.chi.ssetest.cases;
//
//import android.support.test.runner.AndroidJUnit4;
//import android.util.Log;
//
//import com.chi.ssetest.protos.SetupConfig;
//import com.chi.ssetest.setup.RunnerSetup;
//import com.chi.ssetest.StockTestcase;
//import com.chi.ssetest.StockTestcaseName;
//import com.chi.ssetest.setup.TestcaseConfigRule;
//import com.mitake.core.AddValueModel;
//import com.mitake.core.CateType;
//import com.mitake.core.QuoteItem;
//import com.mitake.core.bean.MorePriceItem;
//import com.mitake.core.bean.log.ErrorInfo;
//import com.mitake.core.bean.offer.OfferQuoteBean;
//import com.mitake.core.request.AddValueRequest;
//import com.mitake.core.request.BankuaisortingRequest;
//import com.mitake.core.request.CategoryType;
//import com.mitake.core.request.CatequoteRequest;
//import com.mitake.core.request.MorePriceRequest;
//import com.mitake.core.request.QuoteRequest;
//import com.mitake.core.request.offer.OfferQuoteRequest;
//import com.mitake.core.request.offer.OfferQuoteSort;
//import com.mitake.core.response.AddValueResponse;
//import com.mitake.core.response.BankuaiRankingResponse;
//import com.mitake.core.response.Bankuaisorting;
//import com.mitake.core.response.BankuaisortingResponse;
//import com.mitake.core.response.CatequoteResponse;
//import com.mitake.core.response.IResponseInfoCallback;
//import com.mitake.core.response.MorePriceResponse;
//import com.mitake.core.response.OptionQuoteResponse;
//import com.mitake.core.response.QuoteResponse;
//import com.mitake.core.response.Response;
//import com.mitake.core.response.offer.OfferQuoteResponse;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.Assert.*;
//
///**要约收购接口请求2
// * Example local unit test, which will execute on the development machine (host).
// *
// * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// */
//@RunWith(AndroidJUnit4.class)
//@StockTestcase(StockTestcaseName.OFFERQUOTETEST_2)
//public class OfferQuoteTest_2 {
//    private static final StockTestcaseName testcaseName = StockTestcaseName.OFFERQUOTETEST_2;
//    private static SetupConfig.TestcaseConfig testcaseConfig;
//    private static final int timeout_ms = 1000000;
//    @BeforeClass
//    public static void setup() throws Exception {
//        Log.d("OfferQuoteTest_2", "Setup");
//        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
//        if (testcaseConfig == null ) {
//            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
//        }
//    }
//    @Rule
//    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
//    // OfferQuoteSort.SortField
//    @Test(timeout = timeout_ms)
//    public void requestWork() throws Exception {
//        Log.d("OfferQuoteTest_2", "requestWork");
//        // TODO get custom args from param
//        final String quoteNumbers = rule.getParam().optString("PAGEINDEX");
//        final String quoteNumbers1 = rule.getParam().optString("PAGESIZE");
//        final String quoteNumbers2 = rule.getParam().optString("FIELD");
//        final String quoteNumbers3 = rule.getParam().optString("ASCENDING");
//        final CompletableFuture result = new CompletableFuture<JSONObject>();
////        OfferQuoteSort.SortField
////        for (int i=0;i<quoteNumbers.length;i++){
//            OfferQuoteRequest request = new OfferQuoteRequest();
//            request.send(Integer.parseInt(quoteNumbers),Integer.parseInt(quoteNumbers1),Integer.parseInt(quoteNumbers2),quoteNumbers3,new IResponseInfoCallback<OfferQuoteResponse>() {
//                @Override
//                public void callback(OfferQuoteResponse offerQuoteResponse) {
//                    try {
//                        assertNotNull(offerQuoteResponse.offerQuoteList);
//                    } catch (AssertionError e) {
//                        result.completeExceptionally(e);
//                    }
//                    List<OfferQuoteBean> list=offerQuoteResponse.offerQuoteList;
//                    JSONObject uploadObj = new JSONObject();
//                    try {
//                        if(list!=null){
//                            for (int i=0;i<list.size();i++) {
//                                JSONObject uploadObj_1 = new JSONObject();
//                                uploadObj_1.put("code",list.get(i).code);
//                                uploadObj_1.put("name",list.get(i).name);
//                                uploadObj_1.put("offerId",list.get(i).offerId);
//                                uploadObj_1.put("offerName",list.get(i).offerName);
//                                uploadObj_1.put("price",list.get(i).price);
//                                uploadObj_1.put("startDate",list.get(i).startDate);
//                                uploadObj_1.put("endDate",list.get(i).endDate);
////                            Log.d("data", String.valueOf(uploadObj_1));
//                                uploadObj.put(String.valueOf(i+1),uploadObj_1);
//                            }
//                        }
////                        Log.d("data", String.valueOf(uploadObj));
//                        result.complete(uploadObj);
//                    } catch (JSONException e) {
//                        result.completeExceptionally(e);
//                    }
//                }
//                @Override
//                public void exception(ErrorInfo errorInfo) {
//                    result.completeExceptionally(new Exception(errorInfo.toString()));
//                }
//            });
//            try {
//                JSONObject resultObj = (JSONObject)result.get(timeout_ms, TimeUnit.MILLISECONDS);
//                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
//            } catch (Exception e) {
//                throw new Exception(e);
//            }
////        }
//    }
//}