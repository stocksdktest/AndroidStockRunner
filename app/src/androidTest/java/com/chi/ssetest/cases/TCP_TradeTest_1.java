package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.OHLCItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.TradeQuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.listener.LinePush;
import com.mitake.core.listener.TradeQuoteItemPush;
import com.mitake.core.network.NetworkManager;
import com.mitake.core.network.TCPManager;
import com.mitake.core.request.ChartRequestV2;
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.request.TradeQuoteRequest;
import com.mitake.core.response.ChartResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;
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

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//交易行情 方法一 对应的TCP
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.TCP_TRADETEST_1)
public class TCP_TradeTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.TCP_TRADETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000; //上周开会说的Timeout，可以设置成final类型，TCP设置大一些没关系
    private final static String tTag = "TCPTest";

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("TCP_TradeTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("TCP_TradeTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final String tcpSeconds = rule.getParam().optString("SECONDS", ""); //设置TCP监听的时间
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        final JSONObject uploadObj_1 = new JSONObject();
        final ArrayList<TradeQuoteItem> tradeQuoteItems=new ArrayList<>();
        TradeQuoteRequest request = new TradeQuoteRequest();
        request.send(quoteNumbers, new IResponseInfoCallback<TradeQuoteResponse>() {

            @Override
            public void callback(TradeQuoteResponse tradeQuoteResponse) {
                try {
                    assertNotNull(tradeQuoteResponse.tradeQuoteItems);
                } catch (AssertionError e) {
                    //                        result.completeExceptionally(e);
                    result.complete(new JSONObject());
                }
                // 准备监听TCP的消息
                TCPManager.getInstance().subscribeTradeItem(tradeQuoteResponse.tradeQuoteItems);   // quoteResponse.quoteItems.get(0).id : StockID:600000.sh
                tradeQuoteItems.addAll(tradeQuoteResponse.tradeQuoteItems);
                Log.d("data", uploadObj_1.toString());
            }

            @Override
            public void exception(ErrorInfo errorInfo) {
                result.completeExceptionally(new Exception(errorInfo.toString()));
            }
        });

        //  订阅商品
        Log.d(tTag,"addIPush Start");

        // while计时
        long t1 = System.currentTimeMillis();
        int timeSec = Integer.parseInt(tcpSeconds);
        while(true){
            long t2 = System.currentTimeMillis();
            if(t2-t1 > timeSec*1000){
                // 解订阅
                ArrayList<String> unsub = new ArrayList<>();
                for(int i=0;i<tradeQuoteItems.size();i++){
                    unsub.add(tradeQuoteItems.get(i).id);
                }
                TCPManager.getInstance().unSubsribeTradeItem(unsub.toArray(new String[0]));
                break;
            }else{
                NetworkManager.getInstance().addIPush(new TradeQuoteItemPush() {

                    @Override
                    public void pushTradeQuoteItem(TradeQuoteItem item) {
                        //                        Log.d("tcp00", item.toString());
                        try {
                            JSONObject uploadObj = new JSONObject();
                            if(item!=null){
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
//                            uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
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
                                //创业板字段  20200828
                                uploadObj.put("buyQtyUpperLimit",item.buyQtyUpperLimit);
                                uploadObj.put("sellQtyUpperLimit",item.sellQtyUpperLimit);
                                uploadObj.put("marketBuyQtyUpperLimit",item.marketBuyQtyUpperLimit);
                                uploadObj.put("marketSellQtyUpperLimit",item.marketSellQtyUpperLimit);
                                uploadObj.put("afterHoursBuyQtyUpperLimit",item.afterHoursBuyQtyUpperLimit);
                                uploadObj.put("afterHoursSellQtyUpperLimit",item.afterHoursSellQtyUpperLimit);
                                uploadObj.put("marketBuyQtyUnit",item.marketBuyQtyUnit);
                                uploadObj.put("marketSellQtyUnit",item.marketSellQtyUnit);
                                uploadObj.put("afterHoursBuyQtyUnit",item.afterHoursBuyQtyUnit);
                                uploadObj.put("afterHoursSellQtyUnit",item.afterHoursSellQtyUnit);
                                uploadObj_1.put(item.id,uploadObj);
                            }
                            Log.d("tcp00", String.valueOf(uploadObj_1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        Log.d(tTag,"addIPush End");
        result.complete(uploadObj_1);


        try {
            JSONObject resultObj = (JSONObject) result.get(timeout_ms, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
        } catch (Exception e) {
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }
    }
}
