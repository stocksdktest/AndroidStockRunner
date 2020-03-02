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
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.OptionExpireRequest;
import com.mitake.core.request.OptionTQuoteRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.OptionExpireResponse;
import com.mitake.core.response.OptionTQuoteResponse;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *期权——T型报价
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName. OPTIONTQUOTETEST_1)
public class OptionTQuoteTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName. OPTIONTQUOTETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d(" OptionTQuoteTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d(" OptionTQuoteTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final String quoteNumbers1 = rule.getParam().optString("EXPIREMONTH");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            OptionTQuoteRequest request = new OptionTQuoteRequest();
            request.send(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<OptionTQuoteResponse>() {
                @Override
                public void callback(OptionTQuoteResponse optionTQuoteResponse) {
                    try {
                        assertNotNull(optionTQuoteResponse.list);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    ArrayList<QuoteItem> list=optionTQuoteResponse.list;
                    JSONObject uploadObj = new JSONObject();
                    try {
                        if(list!=null){
                            for (int i=0;i<list.size();i++){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("status", list.get(i).status);
                                uploadObj_1.put("id", list.get(i).id);
                                uploadObj_1.put("name", list.get(i).name);
                                uploadObj_1.put("datetime", list.get(i).datetime);
                                uploadObj_1.put("market", list.get(i).market);
                                uploadObj_1.put("subtype", list.get(i).subtype);
                                uploadObj_1.put("lastPrice", list.get(i).lastPrice);
                                uploadObj_1.put("highPrice", list.get(i).highPrice);
                                uploadObj_1.put("lowPrice", list.get(i).lowPrice);
                                uploadObj_1.put("openPrice", list.get(i).openPrice);
                                uploadObj_1.put("preClosePrice", list.get(i).preClosePrice);
//                            uploadObj_1.put("changeRate", list.get(i).upDownFlag+list.get(i).changeRate);//ios注意
                                if ("+".equals(list.get(i).upDownFlag)||"-".equals(list.get(i).upDownFlag)){
                                    uploadObj_1.put("changeRate",list.get(i).upDownFlag+list.get(i).changeRate);//加涨跌符号
                                }else {
                                    uploadObj_1.put("changeRate",list.get(i).changeRate);
                                }
                                uploadObj_1.put("volume", list.get(i).volume);
                                uploadObj_1.put("nowVolume", list.get(i).nowVolume);
                                uploadObj_1.put("turnoverRate", list.get(i).turnoverRate);
                                uploadObj_1.put("optionType", list.get(i).optionType);
                                uploadObj_1.put("limitUP", list.get(i).limitUP);
                                uploadObj_1.put("limitDown", list.get(i).limitDown);
                                uploadObj_1.put("averageValue", list.get(i).averageValue);
                                uploadObj_1.put("change", list.get(i).change);
                                uploadObj_1.put("amount", list.get(i).amount);
                                uploadObj_1.put("volumeRatio", list.get(i).volumeRatio);
                                uploadObj_1.put("buyPrice", list.get(i).buyPrice);
                                uploadObj_1.put("sellPrice", list.get(i).sellPrice);
                                uploadObj_1.put("buyVolume", list.get(i).buyVolume);
                                uploadObj_1.put("sellVolume", list.get(i).sellVolume);
                                uploadObj_1.put("totalValue", list.get(i).totalValue);
                                uploadObj_1.put("flowValue", list.get(i).flowValue);
                                uploadObj_1.put("netAsset", list.get(i).netAsset);
                                uploadObj_1.put("pe", list.get(i).pe);
                                uploadObj_1.put("pe2", list.get(i).pe2);
                                uploadObj_1.put("pb", list.get(i).pb);
                                uploadObj_1.put("capitalization", list.get(i).capitalization);
                                uploadObj_1.put("circulatingShares", list.get(i).circulatingShares);
                                uploadObj_1.put("amplitudeRate", list.get(i).amplitudeRate);
                                uploadObj_1.put("receipts", list.get(i).receipts);
                                uploadObj_1.put("orderRatio", list.get(i).orderRatio);
                                uploadObj_1.put("hk_paramStatus", list.get(i).hk_paramStatus);
                                uploadObj_1.put("sumBuy", list.get(i).sumBuy);
                                uploadObj_1.put("sumSell", list.get(i).sumSell);
                                uploadObj_1.put("averageBuy", list.get(i).averageBuy);
                                uploadObj_1.put("averageSell", list.get(i).averageSell);
                                uploadObj_1.put("buy_cancel_count", list.get(i).buy_cancel_count);
                                uploadObj_1.put("buy_cancel_num", list.get(i).buy_cancel_num);
                                uploadObj_1.put("buy_cancel_amount", list.get(i).buy_cancel_amount);
                                uploadObj_1.put("sell_cancel_count", list.get(i).sell_cancel_count);
                                uploadObj_1.put("sell_cancel_num", list.get(i).sell_cancel_num);
                                uploadObj_1.put("sell_cancel_amount", list.get(i).sell_cancel_amount);
                                uploadObj_1.put("vote", list.get(i).vote);
                                uploadObj_1.put("change2", list.get(i).change2);
                                uploadObj_1.put("earningsPerShare", list.get(i).earningsPerShare);
                                uploadObj_1.put("earningsPerShareReportingPeriod", list.get(i).earningsPerShareReportingPeriod);
                                uploadObj_1.put("limitPriceUpperLimit", list.get(i).limitPriceUpperLimit);
                                uploadObj_1.put("limitPriceLowerLimit", list.get(i).limitPriceLowerLimit);
                                uploadObj_1.put("upf", list.get(i).upf);
                                uploadObj_1.put("contractID", list.get(i).contractID);
                                uploadObj_1.put("objectID", list.get(i).objectID);
                                uploadObj_1.put("stockSymble", list.get(i).stockSymble);
                                uploadObj_1.put("stockType", list.get(i).stockType);
                                uploadObj_1.put("stockUnit", list.get(i).stockUnit);
                                uploadObj_1.put("exePrice", list.get(i).exePrice);
                                uploadObj_1.put("startDate", list.get(i).startDate);
                                uploadObj_1.put("endDate", list.get(i).endDate);
                                uploadObj_1.put("exeDate", list.get(i).exeDate);
                                uploadObj_1.put("delDate", list.get(i).delDate);
                                uploadObj_1.put("expDate", list.get(i).expDate);
                                uploadObj_1.put("version", list.get(i).version);
                                uploadObj_1.put("presetPrice", list.get(i).presetPrice);
                                uploadObj_1.put("stockClose", list.get(i).stockClose);
                                uploadObj_1.put("stockLast", list.get(i).stockLast);
                                uploadObj_1.put("isLimit", list.get(i).isLimit);
                                uploadObj_1.put("inValue", list.get(i).inValue);
                                uploadObj_1.put("timeValue", list.get(i).timeValue);
                                uploadObj_1.put("openInterest", list.get(i).openInterest);
                                uploadObj_1.put("remainDate", list.get(i).remainDate);
                                uploadObj_1.put("leverageRatio", list.get(i).leverageRatio);
                                uploadObj_1.put("premiumRate", list.get(i).premiumRate);
                                uploadObj_1.put("impliedVolatility", list.get(i).impliedVolatility);
                                uploadObj_1.put("delta", list.get(i).delta);
                                uploadObj_1.put("gramma", list.get(i).gramma);
                                uploadObj_1.put("theta", list.get(i).theta);
                                uploadObj_1.put("rho", list.get(i).rho);
                                uploadObj_1.put("vega", list.get(i).vega);
                                uploadObj_1.put("realLeverage", list.get(i).realLeverage);
                                uploadObj_1.put("theoreticalPrice", list.get(i).theoreticalPrice);
                                List<String> buyPrices=new ArrayList<>();
                                if (list.get(i).buyPrices!=null&&list.get(i).buyPrices.size()>0){
                                    for (int j=0;j<list.get(i).buyPrices.size();j++){
                                        buyPrices.add(list.get(i).buyPrices.get(j));
                                    }
                                    uploadObj_1.put("bidpx1", list.get(i).buyPrices.get(0));
                                    uploadObj_1.put("buyPrices",new JSONArray(buyPrices));
                                }else {
                                    uploadObj_1.put("bidpx1", "");
                                    uploadObj_1.put("buyPrices",list.get(i).buyPrices);
                                }

                                List<String> buySingleVolumes=new ArrayList<>();
                                if (list.get(i).buySingleVolumes!=null&&list.get(i).buySingleVolumes.size()>0){
                                    for (int j=0;j<list.get(i).buySingleVolumes.size();j++){
                                        buySingleVolumes.add(list.get(i).buySingleVolumes.get(j));
                                    }
                                    uploadObj_1.put("buySingleVolumes",new JSONArray(buySingleVolumes));
                                }else {
                                    uploadObj_1.put("buySingleVolumes",list.get(i).buySingleVolumes);
                                }

                                List<String> buyVolumes=new ArrayList<>();
                                if (list.get(i).buyVolumes!=null&&list.get(i).buyVolumes.size()>0){
                                    for (int j=0;j<list.get(i).buyVolumes.size();j++){
                                        buyVolumes.add(list.get(i).buyVolumes.get(j));
                                    }
                                    uploadObj_1.put("bidvol1", list.get(i).buyVolumes.get(0));
                                    uploadObj_1.put("buyVolumes",new JSONArray(buyVolumes));
                                }else {
                                    uploadObj_1.put("bidvol1", "");
                                    uploadObj_1.put("buyVolumes",list.get(i).buyVolumes);
                                }

                                List<String> sellPrices=new ArrayList<>();
                                if (list.get(i).sellPrices!=null&&list.get(i).sellPrices.size()>0){
                                    for (int j=0;j<list.get(i).sellPrices.size();j++){
                                        sellPrices.add(list.get(i).sellPrices.get(j));
                                    }
                                    uploadObj_1.put("askpx1", list.get(i).sellPrices.get(0));
                                    uploadObj_1.put("sellPrices",new JSONArray(sellPrices));
                                }else {
                                    uploadObj_1.put("askpx1", "");
                                    uploadObj_1.put("sellPrices",list.get(i).sellPrices);
                                }

                                List<String> sellSingleVolumes=new ArrayList<>();
                                if (list.get(i).sellSingleVolumes!=null&&list.get(i).sellSingleVolumes.size()>0){
                                    for (int j=0;j<list.get(i).sellSingleVolumes.size();j++){
                                        sellSingleVolumes.add(list.get(i).sellSingleVolumes.get(j));
                                    }
                                    uploadObj_1.put("sellSingleVolumes",new JSONArray(sellSingleVolumes));
                                }else {
                                    uploadObj_1.put("sellSingleVolumes",list.get(i).sellSingleVolumes);
                                }

                                List<String> sellVolumes=new ArrayList<>();
                                if (list.get(i).sellVolumes!=null&&list.get(i).sellVolumes.size()>0){
                                    for (int j=0;j<list.get(i).sellVolumes.size();j++){
                                        sellVolumes.add(list.get(i).sellVolumes.get(j));
                                    }
                                    uploadObj_1.put("askvol1", list.get(i).sellVolumes.get(0));
                                    uploadObj_1.put("sellVolumes",new JSONArray(sellVolumes));
                                }else {
                                    uploadObj_1.put("askvol1", "");
                                    uploadObj_1.put("sellVolumes",list.get(i).sellVolumes);
                                }
                                uploadObj.put(list.get(i).id,uploadObj_1);
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                //                throw new Exception(e);
                throw new TestcaseException(e,rule.getParam());
            }
//        }
    }
}