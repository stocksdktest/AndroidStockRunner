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
import com.mitake.core.CateType;
import com.mitake.core.CompanyInfo;
import com.mitake.core.Importantnotice;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.BndNewSharesCalRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.CompanyInfoRequest;
import com.mitake.core.request.FundBasicRequest;
import com.mitake.core.request.ImportantnoticeRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
//import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.F10V2Response;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.ImportantnoticeResponse;
import com.mitake.core.response.MorePriceResponse;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *当日新债
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_BNDNEWSHARESCALTEST_1)
public class F10_BndNewSharesCalTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_BNDNEWSHARESCALTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_BndNewSharesCalTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("F10_BndNewSharesCalTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("DATE");
        final String quoteNumbers1 = rule.getParam().optString("SOURCETYPE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
//        for (int i=0;i<quoteNumbers.length;i++){
            BndNewSharesCalRequest request = new BndNewSharesCalRequest();
            request.send(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<F10V2Response>() {
                @Override
                public void callback(F10V2Response f10V2Response) {
                    try {
                        assertNotNull(f10V2Response.info);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    HashMap<String,Object> info = f10V2Response.info;
                    try {
                        JSONObject uploadObj_2 = new JSONObject();
                        if (info!=null){
                            if (info.get("sglist")!=null){
                                List<HashMap<String,Object>> list1= (List<HashMap<String,Object>>) info.get("sglist");
                                for (int i=0;i<list1.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("APPLYCODE",list1.get(i).get("APPLYCODE"));
                                    uploadObj_1.put("PREFERREDPLACINGCODE",list1.get(i).get("PREFERREDPLACINGCODE"));
                                    uploadObj_1.put("CONVERTPRICE",list1.get(i).get("CONVERTPRICE"));
                                    uploadObj_1.put("STOCKTRADINGCODE",list1.get(i).get("STOCKTRADINGCODE"));
                                    uploadObj_1.put("STOCKSECUABBR",list1.get(i).get("STOCKSECUABBR"));
                                    uploadObj_1.put("SECUABBR",list1.get(i).get("SECUABBR"));
                                    uploadObj_1.put("TRADINGCODE",list1.get(i).get("TRADINGCODE"));
                                    uploadObj_2.put("sglist"+(String) list1.get(i).get("APPLYCODE"),uploadObj_1);
                                }
                            }
                            if (info.get("jjsglist")!=null){
                                List<HashMap<String,Object>> list2= (List<HashMap<String,Object>>) info.get("jjsglist");
                                for (int i=0;i<list2.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("APPLYCODE",list2.get(i).get("APPLYCODE"));
                                    uploadObj_1.put("PREFERREDPLACINGCODE",list2.get(i).get("PREFERREDPLACINGCODE"));
                                    uploadObj_1.put("CONVERTPRICE",list2.get(i).get("CONVERTPRICE"));
                                    uploadObj_1.put("STOCKTRADINGCODE",list2.get(i).get("STOCKTRADINGCODE"));
                                    uploadObj_1.put("STOCKSECUABBR",list2.get(i).get("STOCKSECUABBR"));
                                    uploadObj_1.put("SECUABBR",list2.get(i).get("SECUABBR"));
                                    uploadObj_1.put("TRADINGCODE",list2.get(i).get("TRADINGCODE"));
                                    uploadObj_2.put("jjsglist"+list2.get(i).get("APPLYCODE"),uploadObj_1);
                                }
                            }
                            if (info.get("dsslist")!=null){
                                List<HashMap<String,Object>> list= (List<HashMap<String,Object>>) info.get("dsslist");
                                for (int i=0;i<list.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("LISTINGDATE",list.get(i).get("LISTINGDATE"));
                                    uploadObj_1.put("ALLOTRATEON",list.get(i).get("ALLOTRATEON"));
                                    uploadObj_1.put("TRADINGCODE",list.get(i).get("TRADINGCODE"));
                                    uploadObj_1.put("SECUABBR",list.get(i).get("SECUABBR"));
                                    uploadObj_2.put("dsslist"+list.get(i).get("LISTINGDATE"),uploadObj_1);
                                }
                            }
                        }
                        Log.d("data", String.valueOf(uploadObj_2));
                        result.complete(uploadObj_2);
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