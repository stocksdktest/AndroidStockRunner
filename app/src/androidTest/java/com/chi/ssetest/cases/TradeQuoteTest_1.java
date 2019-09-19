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
import com.mitake.core.TradeQuoteItem;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.TradeQuoteRequest;
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
import com.mitake.core.response.TradeQuoteResponse;

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
 *交易行情
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.TRADEQUOTETEST_1)
public class TradeQuoteTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.TRADEQUOTETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("TradeQuoteTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("TradeQuoteTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
//        for (int i=0;i<quoteNumbers.length;i++){
            TradeQuoteRequest request = new TradeQuoteRequest();
            request.send(quoteNumbers,new IResponseInfoCallback<TradeQuoteResponse>() {
                @Override
                public void callback(TradeQuoteResponse tradeQuoteResponse) {
                    try {
                        assertNotNull(tradeQuoteResponse.tradeQuoteItems);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    for (TradeQuoteItem item :tradeQuoteResponse.tradeQuoteItems) {
                        try {
                            uploadObj.put("id", item.id);
                            uploadObj.put("name", item.name);
                            uploadObj.put("market",item.market);
                            uploadObj.put("subtype",item.subtype);
                            List<JSONObject> buyPrices=new ArrayList<>();
                            if (item.buyPrices!=null&&item.buyPrices.size()>0){
                                for (int j=0;j<item.buyPrices.size();j++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("buyprice"+j,item.buyPrices.get(j));
                                    buyPrices.add(uploadObj_1);
                                }
                                uploadObj.put("buyPrices",new JSONArray(buyPrices));
                            }else {
                                uploadObj.put("buyPrices",item.buyPrices);
                            }

                            List<JSONObject> buyVolumes=new ArrayList<>();
                            if (item.buyVolumes!=null&&item.buyVolumes.size()>0){
                                for (int j=0;j<item.buyVolumes.size();j++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("buyVolumes"+j,item.buyVolumes.get(j));
                                    buyVolumes.add(uploadObj_1);
                                }
                                uploadObj.put("buyVolumes",new JSONArray(buyVolumes));
                            }else {
                                uploadObj.put("buyVolumes",item.buyVolumes);
                            }
                            List<JSONObject> sellPrices=new ArrayList<>();
                            if (item.sellPrices!=null&&item.sellPrices.size()>0){
                                for (int j=0;j<item.sellPrices.size();j++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("sellPrices"+j,item.sellPrices.get(j));
                                    sellPrices.add(uploadObj_1);
                                }
                                uploadObj.put("sellPrices",new JSONArray(sellPrices));
                            }else {
                                uploadObj.put("sellPrices",item.sellPrices);
                            }
                            List<JSONObject> sellVolumes=new ArrayList<>();
                            if (item.sellVolumes!=null&&item.sellVolumes.size()>0){
                                for (int j=0;j<item.sellVolumes.size();j++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("sellVolumes"+j,item.sellVolumes.get(j));
                                    sellVolumes.add(uploadObj_1);
                                }
                                uploadObj.put("sellVolumes",new JSONArray(sellVolumes));
                            }else {
                                uploadObj.put("sellVolumes",item.sellVolumes);
                            }
                            uploadObj.put("lastPrice",item.lastPrice);
                            uploadObj.put("change",item.change);
                            uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                            uploadObj.put("preClosePrice",item.preClosePrice);
//                            uploadObj.put("upDownFlag",item.upDownFlag);
                            uploadObj.put("limitDown",item.limitDown);
                            uploadObj.put("limitUP",item.limitUP);
                            uploadObj.put("pricePosition",item.pricePosition);
                            uploadObj.put("quantityUnitBuy",item.quantityUnitBuy);
                            uploadObj.put("quantityUnitSell",item.quantityUnitSell);
                            uploadObj.put("hkPriceDifferenceCategory",item.hkPriceDifferenceCategory);
                        } catch (JSONException e) {
                            result.completeExceptionally(e);
                        }
                    }
                    Log.d("data", String.valueOf(uploadObj));
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