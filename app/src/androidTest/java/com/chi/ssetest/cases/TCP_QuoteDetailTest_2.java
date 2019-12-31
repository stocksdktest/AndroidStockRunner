package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.OrderQuantityItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.network.NetworkManager;
import com.mitake.core.network.TCPManager;
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;

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
//行情快照 方法二 对应的TCP
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.QUOTEDETAILTCPTEST_2)
public class TCP_QuoteDetailTest_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.QUOTEDETAILTCPTEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000; //上周开会说的Timeout，可以设置成final类型，TCP设置大一些没关系
    private final static String tTag = "TCPTest";

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("TCP_QuoteDetailTest_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("TCP_QuoteDetailTest_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE", "");
        final String count = rule.getParam().optString("TICKCOUNT", "");
        final String[] INTS1 = rule.getParam().optString("STOCKFIELDS","").split(",");
        final String[] INTS2 = rule.getParam().optString("FIELDS", "").split(",");
        final String tcpSeconds = rule.getParam().optString("SECONDS", ""); //设置TCP监听的时间
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        final JSONObject uploadObj_6 = new JSONObject();
        final ArrayList<QuoteItem> quoteItems = new ArrayList<>(); //Response的副本

        int[] ints1 = new int[INTS1.length];
        int[] ints2 = new int[INTS2.length];
        if (INTS1[0].equals("null")){
            ints1=null;
        }else {
            for (int i=0;i<INTS1.length;i++){
                ints1[i]=Integer.parseInt(INTS1[i]);
            }
        }
        if (INTS2[0].equals("null")){
            ints2=null;
        }else {
            for (int i=0;i<INTS2.length;i++){
                ints2[i]=Integer.parseInt(INTS2[i]);
            }
        }
        QuoteDetailRequest request = new QuoteDetailRequest();
        request.send(quoteNumbers,count,ints1,ints2, new IResponseInfoCallback<QuoteResponse>() {
            @Override
            public void callback(final QuoteResponse quoteResponse) {
                try {
                    assertNotNull(quoteResponse.quoteItems);
                } catch (AssertionError e) {
                    result.completeExceptionally(e);
                }
                // 准备监听TCP的消息
                TCPManager.getInstance().subscribe(quoteResponse.quoteItems);   // quoteResponse.quoteItems.get(0).id : StockID:600000.sh
                quoteItems.addAll(quoteResponse.quoteItems);
                Log.d("data", uploadObj_6.toString());
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
                for(int i=0;i<quoteItems.size();i++){
                    unsub.add(quoteItems.get(i).id);
                }
                TCPManager.getInstance().unsubscribe(unsub.toArray(new String[0]));
                break;
            }else{
                NetworkManager.getInstance().addIPush(new NetworkManager.IPush() {
                    @Override
                    public void push(QuoteItem item, ArrayList sellItems, ArrayList buyItems) {
//                        Log.d("tcp00", item.toString());
                        try {
                            JSONObject uploadObj= new JSONObject();
                            if(item!=null){
                                uploadObj.put("status", item.status);
                                uploadObj.put("id", item.id);
                                uploadObj.put("name", item.name);
                                uploadObj.put("datetime", item.datetime);
//                        uploadObj.put("pinyin", item.pinyin);//ios无
                                uploadObj.put("market", item.market);
                                uploadObj.put("subtype", item.subtype);
                                uploadObj.put("lastPrice", item.lastPrice);
                                uploadObj.put("highPrice", item.highPrice);
                                uploadObj.put("lowPrice", item.lowPrice);
                                uploadObj.put("openPrice", item.openPrice);
                                uploadObj.put("preClosePrice", item.preClosePrice);

                                if ("+".equals(item.upDownFlag)||"-".equals(item.upDownFlag)){
                                    uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                                }else {
                                    uploadObj.put("changeRate",item.changeRate);
                                }
//                            uploadObj.put("changeRate", item.upDownFlag+item.changeRate);//ios注意
                                uploadObj.put("volume", item.volume);
                                uploadObj.put("nowVolume", item.nowVolume);
                                uploadObj.put("turnoverRate", item.turnoverRate);
//                                uploadObj.put("upDownLimitType", item.upDownLimitType);//ios注意
                                uploadObj.put("limitUP", item.limitUP);
                                uploadObj.put("limitDown", item.limitDown);
                                uploadObj.put("averageValue", item.averageValue);//ios无
                                uploadObj.put("change", item.change);
                                uploadObj.put("amount", item.amount);
                                uploadObj.put("volumeRatio", item.volumeRatio);
                                uploadObj.put("buyPrice", item.buyPrice);
                                uploadObj.put("sellPrice", item.sellPrice);
                                uploadObj.put("buyVolume", item.buyVolume);
                                uploadObj.put("sellVolume", item.sellVolume);
                                uploadObj.put("totalValue", item.totalValue);
                                uploadObj.put("HKTotalValue", item.HKTotalValue);
                                uploadObj.put("flowValue", item.flowValue);
                                uploadObj.put("netAsset", item.netAsset);
                                uploadObj.put("pe", item.pe);
                                uploadObj.put("pe2", item.pe2);
//                                uploadObj.put("pb", item.pb);
                                uploadObj.put("capitalization", item.capitalization);
                                uploadObj.put("circulatingShares", item.circulatingShares);

                                List<String> buyPrices=new ArrayList<>();
                                if (item.buyPrices!=null&&item.buyPrices.size()>0){
                                    for (int j=0;j<item.buyPrices.size();j++){
                                        buyPrices.add(item.buyPrices.get(j));
                                    }
                                    uploadObj.put("bidpx1", item.buyPrices.get(0));
                                    uploadObj.put("buyPrices",new JSONArray(buyPrices));
                                }else {
                                    uploadObj.put("bidpx1", "");
                                    uploadObj.put("buyPrices",item.buyPrices);
                                }

                                List<String> buySingleVolumes=new ArrayList<>();
                                if (item.buySingleVolumes!=null&&item.buySingleVolumes.size()>0){
                                    for (int j=0;j<item.buySingleVolumes.size();j++){
                                        buySingleVolumes.add(item.buySingleVolumes.get(j));
                                    }
                                    uploadObj.put("buySingleVolumes",new JSONArray(buySingleVolumes));
                                }else {
                                    uploadObj.put("buySingleVolumes",item.buySingleVolumes);
                                }

                                List<String> buyVolumes=new ArrayList<>();
                                if (item.buyVolumes!=null&&item.buyVolumes.size()>0){
                                    for (int j=0;j<item.buyVolumes.size();j++){
                                        buyVolumes.add(item.buyVolumes.get(j));
                                    }
                                    uploadObj.put("bidvol1", item.buyVolumes.get(0));
                                    uploadObj.put("buyVolumes",new JSONArray(buyVolumes));
                                }else {
                                    uploadObj.put("bidvol1", "");
                                    uploadObj.put("buyVolumes",item.buyVolumes);
                                }

                                List<String> sellPrices=new ArrayList<>();
                                if (item.sellPrices!=null&&item.sellPrices.size()>0){
                                    for (int j=0;j<item.sellPrices.size();j++){
                                        sellPrices.add(item.sellPrices.get(j));
                                    }
                                    uploadObj.put("askpx1", item.sellPrices.get(0));
                                    uploadObj.put("sellPrices",new JSONArray(sellPrices));
                                }else {
                                    uploadObj.put("askpx1", "");
                                    uploadObj.put("sellPrices",item.sellPrices);
                                }

                                List<String> sellSingleVolumes=new ArrayList<>();
                                if (item.sellSingleVolumes!=null&&item.sellSingleVolumes.size()>0){
                                    for (int j=0;j<item.sellSingleVolumes.size();j++){
                                        sellSingleVolumes.add(item.sellSingleVolumes.get(j));
                                    }
                                    uploadObj.put("sellSingleVolumes",new JSONArray(sellSingleVolumes));
                                }else {
                                    uploadObj.put("sellSingleVolumes",item.sellSingleVolumes);
                                }

                                List<String> sellVolumes=new ArrayList<>();
                                if (item.sellVolumes!=null&&item.sellVolumes.size()>0){
                                    for (int j=0;j<item.sellVolumes.size();j++){
                                        sellVolumes.add(item.sellVolumes.get(j));
                                    }
                                    uploadObj.put("askvol1", item.sellVolumes.get(0));
                                    uploadObj.put("sellVolumes",new JSONArray(sellVolumes));
                                }else {
                                    uploadObj.put("askvol1", "");
                                    uploadObj.put("sellVolumes",item.sellVolumes);
                                }

                                uploadObj.put("amplitudeRate", item.amplitudeRate);
                                uploadObj.put("receipts", item.receipts);
                                //ios无

                                if (item.tradeTick!=null&&item.tradeTick.length>0){
                                    for (int j=0;j<item.tradeTick.length;j++){
                                        JSONObject uploadObj_1 = new JSONObject();
                                        uploadObj_1.put("type",item.tradeTick[j][0]);
                                        uploadObj_1.put("time",item.tradeTick[j][1]);
                                        uploadObj_1.put("tradeVolume",item.tradeTick[j][2]);
                                        uploadObj_1.put("tradePrice",item.tradeTick[j][3]);
                                        uploadObj.put(String.valueOf(j+1),uploadObj_1);
                                    }
                                }else {
                                    uploadObj.put("tradeTick",item.tradeTick);
                                }

                                uploadObj.put("upCount", item.upCount);
                                uploadObj.put("sameCount", item.sameCount);
                                uploadObj.put("downCount", item.downCount);
                                uploadObj.put("optionType", item.optionType);
                                uploadObj.put("contractID", item.contractID);
                                uploadObj.put("objectID", item.objectID);
                                uploadObj.put("stockSymble", item.stockSymble);
                                uploadObj.put("stockType", item.stockType);
                                uploadObj.put("stockUnit", item.stockUnit);
                                uploadObj.put("exePrice", item.exePrice);
                                uploadObj.put("startDate", item.startDate);
                                uploadObj.put("endDate", item.endDate);
                                uploadObj.put("exeDate", item.exeDate);
                                uploadObj.put("delDate", item.delDate);
                                uploadObj.put("expDate", item.expDate);
                                uploadObj.put("version", item.version);
                                uploadObj.put("presetPrice", item.presetPrice);
                                uploadObj.put("stockClose", item.stockClose);
                                uploadObj.put("stockLast", item.stockLast);
                                uploadObj.put("isLimit", item.isLimit);
                                uploadObj.put("inValue", item.inValue);
                                uploadObj.put("timeValue", item.timeValue);
                                uploadObj.put("preInterest", item.preInterest);
                                uploadObj.put("openInterest", item.openInterest);
                                uploadObj.put("remainDate", item.remainDate);
                                uploadObj.put("leverageRatio", item.leverageRatio);
                                uploadObj.put("premiumRate", item.premiumRate);
                                uploadObj.put("impliedVolatility", item.impliedVolatility);
                                uploadObj.put("delta", item.delta);
                                uploadObj.put("gramma", item.gramma);
                                uploadObj.put("theta", item.theta);
                                uploadObj.put("rho", item.rho);
                                uploadObj.put("vega", item.vega);
                                uploadObj.put("realLeverage", item.realLeverage);
                                uploadObj.put("theoreticalPrice", item.theoreticalPrice);
                                //
                                uploadObj.put("exerciseWay", item.exerciseWay);
                                uploadObj.put("orderRatio", item.orderRatio);
                                uploadObj.put("hk_paramStatus", item.hk_paramStatus);//ios无
                                uploadObj.put("fundType", item.fundType);
                                uploadObj.put("sumBuy", item.sumBuy);
                                uploadObj.put("sumSell", item.sumSell);
                                uploadObj.put("averageBuy", item.averageBuy);
                                uploadObj.put("averageSell", item.averageSell);
//                        uploadObj.put("upDownFlag", item.upDownFlag);//注意一下IOS android
                                uploadObj.put("zh", item.zh);
                                uploadObj.put("hh", item.hh);
                                uploadObj.put("st", item.st);
                                uploadObj.put("bu", item.bu);
                                uploadObj.put("su", item.su);
                                uploadObj.put("hs", item.hs);
                                uploadObj.put("ac", item.ac);
                                uploadObj.put("qf", item.qf);//ios无
                                uploadObj.put("qc", item.qc);//ios无
                                uploadObj.put("ah", item.ah);
                                uploadObj.put("VCMFlag", item.VCMFlag);
                                uploadObj.put("CASFlag", item.CASFlag);
                                uploadObj.put("rp", item.rp);
                                uploadObj.put("cd", item.cd);
                                uploadObj.put("hg", item.hg);
                                uploadObj.put("sg", item.sg);
                                uploadObj.put("fx", item.fx);
                                uploadObj.put("ts", item.ts);
                                uploadObj.put("add_option_avg_price", item.add_option_avg_price);
                                uploadObj.put("add_option_avg_pb", item.add_option_avg_pb);
                                uploadObj.put("add_option_avg_close", item.add_option_avg_close);

                                uploadObj.put("hk_volum_for_every_hand", item.hk_volum_for_every_hand);
                                //ios无
                                uploadObj.put("buy_cancel_count", item.buy_cancel_count);
                                uploadObj.put("buy_cancel_num", item.buy_cancel_num);
                                uploadObj.put("buy_cancel_amount", item.buy_cancel_amount);
                                uploadObj.put("sell_cancel_count", item.sell_cancel_count);
                                uploadObj.put("sell_cancel_num", item.sell_cancel_num);
                                uploadObj.put("sell_cancel_amount", item.sell_cancel_amount);
                                uploadObj.put("tradingDay", item.tradingDay);
                                uploadObj.put("settlementID", item.settlementID);
                                uploadObj.put("settlementGroupID", item.settlementGroupID);
                                uploadObj.put("preSettlement", item.preSettlement);
                                uploadObj.put("position_chg", item.position_chg);
                                uploadObj.put("close", item.close);
                                uploadObj.put("settlement", item.settlement);
                                uploadObj.put("preDelta", item.preDelta);
                                uploadObj.put("currDelta", item.currDelta);
                                uploadObj.put("updateMillisec", item.updateMillisec);
                                uploadObj.put("entrustDiff", item.entrustDiff);
                                uploadObj.put("posDiff", item.posDiff);
                                uploadObj.put("currDiff", item.currDiff);
                                uploadObj.put("underlyingType", item.underlyingType);
                                uploadObj.put("underlyingLastPx", item.underlyingLastPx);
                                uploadObj.put("underlyingPreClose", item.underlyingPreClose);
                                uploadObj.put("underlyingchg", item.underlyingchg);
                                uploadObj.put("underlyingSymbol", item.underlyingSymbol);
                                uploadObj.put("deliveryDay", item.deliveryDay);
                                uploadObj.put("riskFreeInterestRate", item.riskFreeInterestRate);
                                uploadObj.put("intersectionNum", item.intersectionNum);
                                uploadObj.put("change1", item.change1);
                                uploadObj.put("totalBid", item.totalBid);
                                uploadObj.put("totalAsk", item.totalAsk);
                                //
                                uploadObj.put("IOPV", item.IOPV);
                                uploadObj.put("preIOPV", item.preIOPV);
                                uploadObj.put("stateOfTransfer", item.stateOfTransfer);
                                uploadObj.put("typeOfTransfer", item.typeOfTransfer);
                                uploadObj.put("exRighitDividend", item.exRighitDividend);
                                uploadObj.put("securityLevel", item.securityLevel);
                                uploadObj.put("rpd", item.rpd);
                                uploadObj.put("cdd", item.cdd);
                                //ios无
                                uploadObj.put("change2", item.change2);
                                uploadObj.put("earningsPerShare", item.earningsPerShare);
                                uploadObj.put("earningsPerShareReportingPeriod", item.earningsPerShareReportingPeriod);
                                //
                                uploadObj.put("hkTExchangeFlag", item.hkTExchangeFlag);//注意ios
                                uploadObj.put("vote", item.vote);//注意ios
                                uploadObj.put("upf", item.upf);//注意ios
                                uploadObj.put("DRCurrentShare", item.DRCurrentShare);
                                uploadObj.put("DRPreviousClosingShare", item.DRPreviousClosingShare);
                                uploadObj.put("DRConversionBase", item.DRConversionBase);
                                uploadObj.put("DRDepositoryInstitutionCode", item.DRDepositoryInstitutionCode);
                                uploadObj.put("DRDepositoryInstitutionName", item.DRDepositoryInstitutionName);
                                uploadObj.put("DRSubjectClosingReferencePrice", item.DRSubjectClosingReferencePrice);
                                uploadObj.put("DR", item.DR);
                                uploadObj.put("GDR", item.GDR);
                                uploadObj.put("DRStockCode", item.DRStockCode);
                                uploadObj.put("DRStockName", item.DRStockName);
                                uploadObj.put("DRSecuritiesConversionBase", item.DRSecuritiesConversionBase);
                                uploadObj.put("DRListingDate", item.DRListingDate);
                                uploadObj.put("DRFlowStartDate", item.DRFlowStartDate);
                                uploadObj.put("DRFlowEndDate", item.DRFlowEndDate);
                                uploadObj.put("changeBP", item.changeBP);
                                uploadObj.put("subscribeUpperLimit", item.subscribeUpperLimit);
                                uploadObj.put("subscribeLowerLimit", item.subscribeLowerLimit);
                                uploadObj.put("afterHoursVolume", item.afterHoursVolume);
                                uploadObj.put("afterHoursAmount", item.afterHoursAmount);
                                uploadObj.put("afterHoursTransactionNumber", item.afterHoursTransactionNumber);
                                uploadObj.put("afterHoursWithdrawBuyCount", item.afterHoursWithdrawBuyCount);
                                uploadObj.put("afterHoursWithdrawBuyVolume", item.afterHoursWithdrawBuyVolume);
                                uploadObj.put("afterHoursWithdrawSellCount", item.afterHoursWithdrawSellCount);
                                uploadObj.put("afterHoursWithdrawSellVolume", item.afterHoursWithdrawSellVolume);
                                uploadObj.put("afterHoursBuyVolume", item.afterHoursBuyVolume);
                                uploadObj.put("afterHoursSellVolume", item.afterHoursSellVolume);
                                uploadObj.put("issuedCapital", item.issuedCapital);
//                                uploadObj.put("limitPriceUpperLimit", item.limitPriceUpperLimit);
//                                uploadObj.put("limitPriceLowerLimit", item.limitPriceLowerLimit);
//                                uploadObj.put("longName", item.longName);
//                                //板块指数
//                                uploadObj.put("blockChg", item.blockChg);
//                                uploadObj.put("averageChg", item.averageChg);
//                                uploadObj.put("indexChg5", item.indexChg5);
//                                uploadObj.put("indexChg10", item.indexChg10);
                                //买卖队列
                                if (buyItems!=null) {
                                    ArrayList<OrderQuantityItem> orderQuantityItem1 = buyItems;
                                    List<JSONObject> buylist = new ArrayList<>();
                                    for (int k = 0; k < orderQuantityItem1.size(); k++) {
                                        JSONObject uploadObj_1 = new JSONObject();
//                               uploadObj_1.put("ID",orderQuantityItem1.get(k).ID_);
                                        uploadObj_1.put("QUANTITY", orderQuantityItem1.get(k).QUANTITY_);
                                        buylist.add(uploadObj_1);
                                    }
                                    uploadObj.put("buylist", new JSONArray(buylist));
                                }
                                if (sellItems!=null){
                                    ArrayList<OrderQuantityItem> orderQuantityItem2=sellItems;
                                    List<JSONObject> selllist=new ArrayList<>();
                                    for (int k=0;k<orderQuantityItem2.size();k++){
                                        JSONObject uploadObj_1 = new JSONObject();
//                               uploadObj_1.put("ID",orderQuantityItem2.get(k).ID_);
                                        uploadObj_1.put("QUANTITY",orderQuantityItem2.get(k).QUANTITY_);
                                        selllist.add(uploadObj_1);
                                    }
                                    uploadObj.put("selllist",new JSONArray(selllist));
                                }
                                uploadObj_6.put(item.datetime,uploadObj);
                            }
                            Log.d("tcp00", String.valueOf(uploadObj_6));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void pushHttp(QuoteResponse quoteResponse) {
                        if (quoteResponse == null) {
                            Log.d(tTag, "quoteResponse Null");
                        }else{
                            Log.d(tTag, "quoteResponse not Null");
                        }
                    }
                });
            }
        }

        Log.d(tTag,"addIPush End");
        result.complete(uploadObj_6);


        try {
            JSONObject resultObj = (JSONObject) result.get(timeout_ms, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
