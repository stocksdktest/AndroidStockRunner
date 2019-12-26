package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.TradeQuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.TradeQuoteRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.TradeQuoteResponse;

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
 *交易行情
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.TRADEQUOTETEST_1)
public class TradeQuoteTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.TRADEQUOTETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
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
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("TradeQuoteTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
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
                    JSONObject uploadObj_1 = new JSONObject();
                    try {
                        if(tradeQuoteResponse.tradeQuoteItems!=null){
                            for (TradeQuoteItem item :tradeQuoteResponse.tradeQuoteItems) {
                                JSONObject uploadObj = new JSONObject();
                                uploadObj.put("id", item.id);
                                uploadObj.put("name", item.name);
                                uploadObj.put("subtype",item.subtype);
                                List<String> buyPrices=new ArrayList<>();
                                if (item.buyPrices!=null&&item.buyPrices.size()>0){
                                    for (int j=0;j<item.buyPrices.size();j++){
                                        buyPrices.add(item.buyPrices.get(j));
                                    }
                                    uploadObj.put("buyPrices",new JSONArray(buyPrices));
                                }else {
                                    uploadObj.put("buyPrices",item.buyPrices);
                                }

                                List<String> buyVolumes=new ArrayList<>();
                                if (item.buyVolumes!=null&&item.buyVolumes.size()>0){
                                    for (int j=0;j<item.buyVolumes.size();j++){
                                        buyVolumes.add(item.buyVolumes.get(j));
                                    }
                                    uploadObj.put("buyVolumes",new JSONArray(buyVolumes));
                                }else {
                                    uploadObj.put("buyVolumes",item.buyVolumes);
                                }
                                List<String> sellPrices=new ArrayList<>();
                                if (item.sellPrices!=null&&item.sellPrices.size()>0){
                                    for (int j=0;j<item.sellPrices.size();j++){
                                        sellPrices.add(item.sellPrices.get(j));
                                    }
                                    uploadObj.put("sellPrices",new JSONArray(sellPrices));
                                }else {
                                    uploadObj.put("sellPrices",item.sellPrices);
                                }
                                List<String> sellVolumes=new ArrayList<>();
                                if (item.sellVolumes!=null&&item.sellVolumes.size()>0){
                                    for (int j=0;j<item.sellVolumes.size();j++){
                                        sellVolumes.add(item.sellVolumes.get(j));
                                    }
                                    uploadObj.put("sellVolumes",new JSONArray(sellVolumes));
                                }else {
                                    uploadObj.put("sellVolumes",item.sellVolumes);
                                }
                                uploadObj.put("lastPrice",item.lastPrice);
                                uploadObj.put("change",item.change);
                                if ("+".equals(item.upDownFlag)||"-".equals(item.upDownFlag)){
                                    uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                                }else {
                                    uploadObj.put("changeRate",item.changeRate);
                                }
                                uploadObj.put("preClosePrice",item.preClosePrice);
//                            uploadObj.put("upDownFlag",item.upDownFlag);
                                uploadObj.put("limitDown",item.limitDown);
                                uploadObj.put("limitUP",item.limitUP);
                                uploadObj.put("pricePosition",item.pricePosition);
                                uploadObj.put("quantityUnitBuy",item.quantityUnitBuy);
                                uploadObj.put("quantityUnitSell",item.quantityUnitSell);
                                uploadObj.put("hkPriceDifferenceCategory",item.hkPriceDifferenceCategory);
                                uploadObj_1.put(item.id,uploadObj);
                            }
                        }
//                        Log.d("data", String.valueOf(uploadObj_1));
                        result.complete(uploadObj_1);
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
                throw new Exception(e);
            }
//        }
    }
}