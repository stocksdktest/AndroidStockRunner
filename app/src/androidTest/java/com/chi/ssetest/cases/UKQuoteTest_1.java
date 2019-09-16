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
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.UKQuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.UKQuoteRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.MorePriceResponse;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;
import com.mitake.core.response.UKQuoteResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
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
 *uk市场快照单独接口，可支持多商品
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.UKQUOTETEST_1)
public class UKQuoteTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.UKQUOTETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d(" UKQuoteTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("UKQuoteTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
//        for (int i=0;i<quoteNumbers.length;i++){
            UKQuoteRequest request = new UKQuoteRequest();
            request.send(quoteNumbers,new IResponseInfoCallback<UKQuoteResponse>() {
                @Override
                public void callback(UKQuoteResponse ukQuoteResponse) {
                    assertNotNull(ukQuoteResponse.ukQuoteItems);
                    List<JSONObject> items=new ArrayList<>();
                    JSONObject uploadObj = new JSONObject();
                    for (UKQuoteItem item : ukQuoteResponse.ukQuoteItems) {
                        JSONObject uploadObj_1 = new JSONObject();
                        try {
                            uploadObj_1.put("code",item.code);
                            uploadObj_1.put("name",item.name);
                            uploadObj_1.put("subtype",item.subtype);
                            uploadObj_1.put("lastPrice",item.lastPrice);
                            uploadObj_1.put("openPrice",item.openPrice);
                            uploadObj_1.put("datetime",item.datetime);
                            uploadObj_1.put("volume",item.volume);
                            uploadObj_1.put("amount",item.amount);
                            uploadObj_1.put("transactionNumber",item.transactionNumber);
                            uploadObj_1.put("closeBuyPrice",item.closeBuyPrice);
                            uploadObj_1.put("closeSellPrice",item.closeSellPrice);
                            uploadObj_1.put("highPriceDayAuto",item.highPriceDayAuto);
                            uploadObj_1.put("highPriceDayNonAuto",item.highPriceDayNonAuto);
                            uploadObj_1.put("lowPriceDayAuto",item.lowPriceDayAuto);
                            uploadObj_1.put("lowPriceDayNonAuto",item.lowPriceDayNonAuto);
                            uploadObj_1.put("highPriceYearAuto",item.highPriceYearAuto);
                            uploadObj_1.put("highPriceYearNonAuto",item.highPriceYearNonAuto);
                            uploadObj_1.put("lowPriceYearAuto",item.lowPriceYearAuto);
                            uploadObj_1.put("lowPriceYearNonAuto",item.lowPriceYearNonAuto);
                            uploadObj_1.put("highPriceTimeYearAuto",item.highPriceTimeYearAuto);
                            uploadObj_1.put("highPriceTimeYearNonAuto",item.highPriceTimeYearNonAuto);
                            uploadObj_1.put("lowPriceTimeYearAuto",item.lowPriceTimeYearAuto);
                            uploadObj_1.put("lowPriceTimeYearNonAuto",item.lowPriceTimeYearNonAuto);
                            uploadObj_1.put("averageValue",item.averageValue);
                            uploadObj_1.put("currency",item.currency);
                            uploadObj_1.put("listingDate",item.listingDate);
                            uploadObj_1.put("conversionBase",item.conversionBase);
                            uploadObj_1.put("securitiesConversionBase",item.securitiesConversionBase);
                            uploadObj_1.put("GDR",item.GDR);
                            uploadObj_1.put("CDR",item.CDR);
                            uploadObj_1.put("stockCode",item.stockCode);
                            uploadObj_1.put("stockName",item.stockName);
                            uploadObj_1.put("subtypes",item.subtypes);
                            uploadObj_1.put("subjectClosingReferencePrice",item.subjectClosingReferencePrice);
                            uploadObj_1.put("premium",item.premium);
                            items.add(uploadObj_1);
                        } catch (JSONException e) {
                            result.completeExceptionally(e);
                        }
                    }
                    try {
                        uploadObj.put("items",new JSONArray(items));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //解析输出JSON
                    try {
                        JSONArray jsonArray = uploadObj.getJSONArray("items");
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Log.d("data", String.valueOf(jsonObject));
//                            System.out.println(jsonObject.optString("code")+","+jsonObject.optString("datetime"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    result.complete(uploadObj);
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