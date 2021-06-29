package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.BrokerInfoItem;
import com.mitake.core.OrderQuantityItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.network.Network;
import com.mitake.core.network.NetworkManager;
import com.mitake.core.network.TCPManager;
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.util.MarketSiteType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//行情快照 方法一 对应的TCP
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.QUOTEDETAILTCPTEST_1)
public class TCP_QuoteDetailTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.QUOTEDETAILTCPTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000; //上周开会说的Timeout，可以设置成final类型，TCP设置大一些没关系
    private final static String tTag = "TCPTest";

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("TCP_QuoteDetailTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("TCP_QuoteDetailTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE", "");
        final String tcpSeconds = rule.getParam().optString("SECONDS", ""); //设置TCP监听的时间
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        final JSONObject uploadObj_6 = new JSONObject();
        final ArrayList<QuoteItem> quoteItems = new ArrayList<>(); //Response的副本

        QuoteDetailRequest request = new QuoteDetailRequest();
        request.send(quoteNumbers, new IResponseInfoCallback<QuoteResponse>() {
            @Override
            public void callback(final QuoteResponse quoteResponse) {
                try {
                    assertNotNull(quoteResponse.quoteItems);
                } catch (AssertionError e) {
                    //                        result.completeExceptionally(e);
                    result.complete(new JSONObject());
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
                                uploadObj.put("status", item.status == null ? "-" : item.status);
                                uploadObj.put("id", item.id == null ? "-" : item.id);
                                uploadObj.put("name", item.name == null ? "-" : item.name);
                                uploadObj.put("datetime", item.datetime == null ? "-" : item.datetime);
//                        uploadObj.put("pinyin", item.pinyin);//ios无
                                uploadObj.put("market", item.market == null ? "-" : item.market);
                                uploadObj.put("subtype", item.subtype == null ? "-" : item.subtype);
                                uploadObj.put("lastPrice", item.lastPrice == null ? "-" : item.lastPrice);
                                uploadObj.put("highPrice", item.highPrice == null ? "-" : item.highPrice);
                                uploadObj.put("lowPrice", item.lowPrice == null ? "-" : item.lowPrice);
                                uploadObj.put("openPrice", item.openPrice == null ? "-" : item.openPrice);
                                uploadObj.put("preClosePrice", item.preClosePrice == null ? "-" : item.preClosePrice);
//                        uploadObj.put("changeRate", item.upDownFlag+item.changeRate);//ios注意
                                if ("+".equals(item.upDownFlag)||"-".equals(item.upDownFlag)){
                                    uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                                }else {
                                    uploadObj.put("changeRate",item.changeRate == null ? "-" : item.changeRate);
                                }
                                uploadObj.put("volume", item.volume == null ? "-" : item.volume);
                                uploadObj.put("nowVolume", item.nowVolume == null ? "-" : item.nowVolume);
                                uploadObj.put("turnoverRate", item.turnoverRate == null ? "-" : item.turnoverRate);
                                uploadObj.put("upDownLimitType", item.upDownLimitType == null ? "-" : item.upDownLimitType);//ios注意
                                uploadObj.put("limitUP", item.limitUP == null ? "-" : item.limitUP);
                                uploadObj.put("limitDown", item.limitDown == null ? "-" : item.limitDown);
                                uploadObj.put("averageValue", item.averageValue == null ? "-" : item.averageValue);//ios无
                                uploadObj.put("change", item.change == null ? "-" : item.change);
                                uploadObj.put("amount", item.amount == null ? "-" : item.amount);
                                uploadObj.put("volumeRatio", item.volumeRatio == null ? "-" : item.volumeRatio);
                                uploadObj.put("buyPrice", item.buyPrice == null ? "-" : item.buyPrice);
                                uploadObj.put("sellPrice", item.sellPrice == null ? "-" : item.sellPrice);
                                uploadObj.put("buyVolume", item.buyVolume == null ? "-" : item.buyVolume);
                                uploadObj.put("sellVolume", item.sellVolume == null ? "-" : item.sellVolume);
                                uploadObj.put("totalValue", item.totalValue == null ? "-" : item.totalValue);
                                uploadObj.put("HKTotalValue", item.HKTotalValue == null ? "-" : item.HKTotalValue);
                                uploadObj.put("flowValue", item.flowValue == null ? "-" : item.flowValue);
                                uploadObj.put("netAsset", item.netAsset == null ? "-" : item.netAsset);
                                uploadObj.put("pe", item.pe == null ? "-" : item.pe);
                                uploadObj.put("pe2", item.pe2 == null ? "-" : item.pe2);
                                uploadObj.put("pb", item.pb == null ? "-" : item.pb);
                                uploadObj.put("capitalization", item.capitalization == null ? "-" : item.capitalization);
                                uploadObj.put("circulatingShares", item.circulatingShares == null ? "-" : item.circulatingShares);

                                List<String> buyPrices=new ArrayList<>();
                                if (item.buyPrices!=null&&item.buyPrices.size()>0){
                                    for (int j=0;j<item.buyPrices.size();j++){
                                        buyPrices.add(item.buyPrices.get(j) == null ? "-" : item.buyPrices.get(j));
                                    }
                                    uploadObj.put("bidpx1", item.buyPrices.get(0) == null ? "-" : item.buyPrices.get(0));
                                    uploadObj.put("buyPrices",new JSONArray(buyPrices));
                                }else {
                                    uploadObj.put("bidpx1", "-");
                                    uploadObj.put("buyPrices",item.buyPrices == null ? "-" : item.buyPrices);
                                }

                                List<String> buySingleVolumes=new ArrayList<>();
                                if (item.buySingleVolumes!=null&&item.buySingleVolumes.size()>0){
                                    for (int j=0;j<item.buySingleVolumes.size();j++){
                                        buySingleVolumes.add(item.buySingleVolumes.get(j) == null ? "-" : item.buySingleVolumes.get(j));
                                    }
                                    uploadObj.put("buySingleVolumes",new JSONArray(buySingleVolumes));
                                }else {
                                    uploadObj.put("buySingleVolumes",item.buySingleVolumes == null ? "-" : item.buySingleVolumes);
                                }

                                List<String> buyVolumes=new ArrayList<>();
                                if (item.buyVolumes!=null&&item.buyVolumes.size()>0){
                                    for (int j=0;j<item.buyVolumes.size();j++){
                                        buyVolumes.add(item.buyVolumes.get(j) == null ? "-" : item.buyVolumes.get(j));
                                    }
                                    uploadObj.put("bidvol1", item.buyVolumes.get(0) == null ? "-" : item.buyVolumes.get(0));
                                    uploadObj.put("buyVolumes",new JSONArray(buyVolumes));
                                }else {
                                    uploadObj.put("bidvol1", "-");
                                    uploadObj.put("buyVolumes",item.buyVolumes == null ? "-" : item.buyVolumes);
                                }

                                List<String> sellPrices=new ArrayList<>();
                                if (item.sellPrices!=null&&item.sellPrices.size()>0){
                                    for (int j=0;j<item.sellPrices.size();j++){
                                        sellPrices.add(item.sellPrices.get(j) == null ? "-" : item.sellPrices.get(j));
                                    }
                                    uploadObj.put("askpx1", item.sellPrices.get(0) == null ? "-" : item.sellPrices.get(0));
                                    uploadObj.put("sellPrices",new JSONArray(sellPrices));
                                }else {
                                    uploadObj.put("askpx1", "-");
                                    uploadObj.put("sellPrices",item.sellPrices == null ? "-" : item.sellPrices);
                                }

                                List<String> sellSingleVolumes=new ArrayList<>();
                                if (item.sellSingleVolumes!=null&&item.sellSingleVolumes.size()>0){
                                    for (int j=0;j<item.sellSingleVolumes.size();j++){
                                        sellSingleVolumes.add(item.sellSingleVolumes.get(j) == null ? "-" : item.sellSingleVolumes.get(j));
                                    }
                                    uploadObj.put("sellSingleVolumes",new JSONArray(sellSingleVolumes));
                                }else {
                                    uploadObj.put("sellSingleVolumes",item.sellSingleVolumes == null ? "-" : item.sellSingleVolumes);
                                }

                                List<String> sellVolumes=new ArrayList<>();
                                if (item.sellVolumes!=null&&item.sellVolumes.size()>0){
                                    for (int j=0;j<item.sellVolumes.size();j++){
                                        sellVolumes.add(item.sellVolumes.get(j) == null ? "-" : item.sellVolumes.get(j));
                                    }
                                    uploadObj.put("askvol1", item.sellVolumes.get(0) == null ? "-" : item.sellVolumes.get(0));
                                    uploadObj.put("sellVolumes",new JSONArray(sellVolumes));
                                }else {
                                    uploadObj.put("askvol1", "-");
                                    uploadObj.put("sellVolumes",item.sellVolumes == null ? "-" : item.sellVolumes);
                                }

                                uploadObj.put("amplitudeRate", item.amplitudeRate == null ? "-" : item.amplitudeRate);
                                uploadObj.put("receipts", item.receipts == null ? "-" : item.receipts);
                                //ios无

                                if (item.tradeTick!=null&&item.tradeTick.length>0){
                                    for (int j=0;j<item.tradeTick.length;j++){
                                        JSONObject uploadObj_1 = new JSONObject();
                                        uploadObj_1.put("type",item.tradeTick[j][0] == null ? "-" : item.tradeTick[j][0]);
                                        uploadObj_1.put("time",item.tradeTick[j][1] == null ? "-" : item.tradeTick[j][1]);
                                        uploadObj_1.put("tradeVolume",item.tradeTick[j][2] == null ? "-" : item.tradeTick[j][2]);
                                        uploadObj_1.put("tradePrice",item.tradeTick[j][3] == null ? "-" : item.tradeTick[j][3]);
                                        uploadObj.put(String.valueOf(j+1),uploadObj_1);
                                    }
                                }else {
                                    uploadObj.put("tradeTick",item.tradeTick == null ? "-" : item.tradeTick);
                                }

                                uploadObj.put("upCount", item.upCount == null ? "-" : item.upCount);
                                uploadObj.put("sameCount", item.sameCount == null ? "-" : item.sameCount);
                                uploadObj.put("downCount", item.downCount == null ? "-" : item.downCount);
                                uploadObj.put("optionType", item.optionType == null ? "-" : item.optionType);
                                uploadObj.put("contractID", item.contractID == null ? "-" : item.contractID);
                                uploadObj.put("objectID", item.objectID == null ? "-" : item.objectID);
                                uploadObj.put("stockSymble", item.stockSymble == null ? "-" : item.stockSymble);
                                uploadObj.put("stockType", item.stockType == null ? "-" : item.stockType);
                                uploadObj.put("stockUnit", item.stockUnit == null ? "-" : item.stockUnit);
                                uploadObj.put("exePrice", item.exePrice == null ? "-" : item.exePrice);
                                uploadObj.put("startDate", item.startDate == null ? "-" : item.startDate);
                                uploadObj.put("endDate", item.endDate == null ? "-" : item.endDate);
                                uploadObj.put("exeDate", item.exeDate == null ? "-" : item.exeDate);
                                uploadObj.put("delDate", item.delDate == null ? "-" : item.delDate);
                                uploadObj.put("expDate", item.expDate == null ? "-" : item.expDate);
                                uploadObj.put("version", item.version == null ? "-" : item.version);
                                uploadObj.put("presetPrice", dwnull(item.presetPrice == null ? "-" : item.presetPrice));

                                uploadObj.put("stockClose", item.stockClose == null ? "-" : item.stockClose);
                                uploadObj.put("stockLast", item.stockLast == null ? "-" : item.stockLast);
                                uploadObj.put("isLimit", item.isLimit == null ? "-" : item.isLimit);
                                uploadObj.put("inValue", item.inValue == null ? "-" : item.inValue);
                                uploadObj.put("timeValue", item.timeValue == null ? "-" : item.timeValue);
                                uploadObj.put("preInterest", item.preInterest == null ? "-" : item.preInterest);
                                uploadObj.put("openInterest", item.openInterest == null ? "-" : item.openInterest);
                                uploadObj.put("remainDate", item.remainDate == null ? "-" : item.remainDate);
                                uploadObj.put("leverageRatio", item.leverageRatio == null ? "-" : item.leverageRatio);
                                uploadObj.put("premiumRate", item.premiumRate == null ? "-" : item.premiumRate);
                                uploadObj.put("impliedVolatility", item.impliedVolatility == null ? "-" : item.impliedVolatility);
                                uploadObj.put("delta", item.delta == null ? "-" : item.delta);
                                uploadObj.put("gramma", item.gramma == null ? "-" : item.gramma);
                                uploadObj.put("theta", item.theta == null ? "-" : item.theta);
                                uploadObj.put("rho", item.rho == null ? "-" : item.rho);
                                uploadObj.put("vega", item.vega == null ? "-" : item.vega);
                                uploadObj.put("realLeverage", item.realLeverage == null ? "-" : item.realLeverage);
                                uploadObj.put("theoreticalPrice", item.theoreticalPrice == null ? "-" : item.theoreticalPrice);
                                //
                                uploadObj.put("exerciseWay", dwnull(item.exerciseWay == null ? "-" : item.exerciseWay));

                                uploadObj.put("orderRatio", item.orderRatio == null ? "-" : item.orderRatio);
                                uploadObj.put("hk_paramStatus", item.hk_paramStatus == null ? "-" : item.hk_paramStatus);//ios无
                                uploadObj.put("fundType", item.fundType == null ? "-" : item.fundType);
                                uploadObj.put("sumBuy", item.sumBuy == null ? "-" : item.sumBuy);
                                uploadObj.put("sumSell", item.sumSell == null ? "-" : item.sumSell);
                                uploadObj.put("averageBuy", item.averageBuy == null ? "-" : item.averageBuy);
                                uploadObj.put("averageSell", item.averageSell == null ? "-" : item.averageSell);
//                        uploadObj.put("upDownFlag", item.upDownFlag);//注意一下IOS android

                                uploadObj.put("zh", dwnull(item.zh == null ? "-" : item.zh));
                                uploadObj.put("hh", dwnull(item.hh == null ? "-" : item.hh));
                                uploadObj.put("st", dwnull(item.st == null ? "-" : item.st));
                                uploadObj.put("bu", dwnull(item.bu == null ? "-" : item.bu));
                                uploadObj.put("su", dwnull(item.su == null ? "-" : item.su));

                                uploadObj.put("hs", dwnull(item.hs == null ? "-" : item.hs));
                                uploadObj.put("ac", dwnull(item.ac == null ? "-" : item.ac));
                                uploadObj.put("qf", dwnull(item.qf == null ? "-" : item.qf));
                                uploadObj.put("qc", dwnull(item.qc == null ? "-" : item.qc));
                                uploadObj.put("ah", dwnull(item.ah == null ? "-" : item.ah));
                                uploadObj.put("VCMFlag", dwnull(item.VCMFlag == null ? "-" : item.VCMFlag));
                                uploadObj.put("CASFlag", dwnull(item.CASFlag == null ? "-" : item.CASFlag));

                                //20210118添加 POSFlag 该字段
                                uploadObj.put("POSFlag", dwnull(item.POSFlag == null ? "-" : item.POSFlag));
                                uploadObj.put("rp", dwnull(item.rp == null ? "-" : item.rp));
                                uploadObj.put("cd", dwnull(item.cd == null ? "-" : item.cd));
                                uploadObj.put("hg", dwnull(item.hg == null ? "-" : item.hg));
                                uploadObj.put("sg", dwnull(item.sg == null ? "-" : item.sg));
                                uploadObj.put("fx", dwnull(item.fx == null ? "-" : item.fx));
                                uploadObj.put("ts", dwnull(item.ts == null ? "-" : item.ts));
                                uploadObj.put("add_option_avg_price", dwnull(item.add_option_avg_price == null ? "-" : item.add_option_avg_price));
                                uploadObj.put("add_option_avg_pb", dwnull(item.add_option_avg_pb == null ? "-" : item.add_option_avg_pb));
                                uploadObj.put("add_option_avg_close", dwnull(item.add_option_avg_close == null ? "-" : item.add_option_avg_close));

                                uploadObj.put("hk_volum_for_every_hand", item.hk_volum_for_every_hand == null ? "-" : item.hk_volum_for_every_hand);
                                //ios无
                                uploadObj.put("buy_cancel_count", item.buy_cancel_count == null ? "-" : item.buy_cancel_count);

                                uploadObj.put("buy_cancel_num", dwnull(item.buy_cancel_num == null ? "-" : item.buy_cancel_num));

                                uploadObj.put("buy_cancel_amount", item.buy_cancel_amount == null ? "-" : item.buy_cancel_amount);
                                uploadObj.put("sell_cancel_count", item.sell_cancel_count == null ? "-" : item.sell_cancel_count);

                                uploadObj.put("sell_cancel_num", dwnull(item.sell_cancel_num == null ? "-" : item.sell_cancel_num));

                                uploadObj.put("sell_cancel_amount", item.sell_cancel_amount == null ? "-" : item.sell_cancel_amount);
                                uploadObj.put("tradingDay", item.tradingDay == null ? "-" : item.tradingDay);
                                uploadObj.put("settlementID", item.settlementID == null ? "-" : item.settlementID);
                                uploadObj.put("settlementGroupID", item.settlementGroupID == null ? "-" : item.settlementGroupID);
                                uploadObj.put("preSettlement", item.preSettlement == null ? "-" : item.preSettlement);
                                uploadObj.put("position_chg", item.position_chg == null ? "-" : item.position_chg);
                                uploadObj.put("close", item.close == null ? "-" : item.close);
                                uploadObj.put("settlement", item.settlement == null ? "-" : item.settlement);
                                uploadObj.put("preDelta", item.preDelta == null ? "-" : item.preDelta);
                                uploadObj.put("currDelta", item.currDelta == null ? "-" : item.currDelta);
                                uploadObj.put("updateMillisec", item.updateMillisec == null ? "-" : item.updateMillisec);
                                uploadObj.put("entrustDiff", item.entrustDiff == null ? "-" : item.entrustDiff);
                                uploadObj.put("posDiff", item.posDiff == null ? "-" : item.posDiff);
                                uploadObj.put("currDiff", item.currDiff == null ? "-" : item.currDiff);
                                uploadObj.put("underlyingType", item.underlyingType == null ? "-" : item.underlyingType);
                                uploadObj.put("underlyingLastPx", item.underlyingLastPx == null ? "-" : item.underlyingLastPx);
                                uploadObj.put("underlyingPreClose", item.underlyingPreClose == null ? "-" : item.underlyingPreClose);
                                uploadObj.put("underlyingchg", item.underlyingchg == null ? "-" : item.underlyingchg);
                                uploadObj.put("underlyingSymbol", item.underlyingSymbol == null ? "-" : item.underlyingSymbol);
                                uploadObj.put("deliveryDay", item.deliveryDay == null ? "-" : item.deliveryDay);
                                uploadObj.put("riskFreeInterestRate", item.riskFreeInterestRate == null ? "-" : item.riskFreeInterestRate);
                                uploadObj.put("intersectionNum", item.intersectionNum == null ? "-" : item.intersectionNum);
                                uploadObj.put("change1", item.change1 == null ? "-" : item.change1);
                                uploadObj.put("totalBid", item.totalBid == null ? "-" : item.totalBid);
                                uploadObj.put("totalAsk", item.totalAsk == null ? "-" : item.totalAsk);
                                //
                                uploadObj.put("IOPV", dwnull(item.IOPV == null ? "-" : item.IOPV));
                                uploadObj.put("preIOPV", dwnull(item.preIOPV == null ? "-" : item.preIOPV));
                                uploadObj.put("stateOfTransfer", dwnull(item.stateOfTransfer == null ? "-" : item.stateOfTransfer));
                                uploadObj.put("typeOfTransfer", dwnull(item.typeOfTransfer == null ? "-" : item.typeOfTransfer));
                                uploadObj.put("exRighitDividend", dwnull(item.exRighitDividend == null ? "-" : item.exRighitDividend));
                                uploadObj.put("securityLevel", dwnull(item.securityLevel == null ? "-" : item.securityLevel));
                                uploadObj.put("rpd", dwnull(item.rpd == null ? "-" : item.rpd));
                                uploadObj.put("cdd", dwnull(item.cdd == null ? "-" : item.cdd));
                                //ios无
                                uploadObj.put("change2", dwnull(item.change2 == null ? "-" : item.change2));

                                uploadObj.put("earningsPerShare", item.earningsPerShare == null ? "-" : item.earningsPerShare);
                                uploadObj.put("earningsPerShareReportingPeriod", item.earningsPerShareReportingPeriod == null ? "-" : item.earningsPerShareReportingPeriod);
                                //
                                uploadObj.put("hkTExchangeFlag", dwnull(item.hkTExchangeFlag == null ? "-" : item.hkTExchangeFlag));

                                uploadObj.put("vote", item.vote == null ? "-" : item.vote);//注意ios
                                uploadObj.put("upf", item.upf == null ? "-" : item.upf);//注意ios
                                uploadObj.put("DRCurrentShare", item.DRCurrentShare == null ? "-" : item.DRCurrentShare);
                                uploadObj.put("DRPreviousClosingShare", item.DRPreviousClosingShare == null ? "-" : item.DRPreviousClosingShare);
                                uploadObj.put("DRConversionBase", item.DRConversionBase == null ? "-" : item.DRConversionBase);

                                uploadObj.put("DRDepositoryInstitutionCode", dwnull(item.DRDepositoryInstitutionCode == null ? "-" : item.DRDepositoryInstitutionCode));
                                uploadObj.put("DRDepositoryInstitutionName", dwnull(item.DRDepositoryInstitutionName == null ? "-" : item.DRDepositoryInstitutionName));
                                uploadObj.put("DRSubjectClosingReferencePrice", dwnull(item.DRSubjectClosingReferencePrice == null ? "-" : item.DRSubjectClosingReferencePrice));
                                uploadObj.put("DR", dwnull(item.DR == null ? "-" : item.DR));
                                uploadObj.put("GDR", dwnull(item.GDR == null ? "-" : item.GDR));
                                uploadObj.put("DRStockCode", dwnull(item.DRStockCode == null ? "-" : item.DRStockCode));
                                uploadObj.put("DRStockName", dwnull(item.DRStockName == null ? "-" : item.DRStockName));

                                uploadObj.put("DRSecuritiesConversionBase", item.DRSecuritiesConversionBase == null ? "-" : item.DRSecuritiesConversionBase);
                                uploadObj.put("DRListingDate", item.DRListingDate == null ? "-" : item.DRListingDate);
                                uploadObj.put("DRFlowStartDate", item.DRFlowStartDate == null ? "-" : item.DRFlowStartDate);
                                uploadObj.put("DRFlowEndDate", item.DRFlowEndDate == null ? "-" : item.DRFlowEndDate);
                                uploadObj.put("changeBP", item.changeBP == null ? "-" : item.changeBP);
                                uploadObj.put("subscribeUpperLimit", item.subscribeUpperLimit == null ? "-" : item.subscribeUpperLimit);
                                uploadObj.put("subscribeLowerLimit", item.subscribeLowerLimit == null ? "-" : item.subscribeLowerLimit);

                                uploadObj.put("afterHoursVolume", dwnull(item.afterHoursVolume == null ? "-" : item.afterHoursVolume));

                                uploadObj.put("afterHoursAmount", item.afterHoursAmount == null ? "-" : item.afterHoursAmount);
                                uploadObj.put("afterHoursTransactionNumber", item.afterHoursTransactionNumber == null ? "-" : item.afterHoursTransactionNumber);
                                uploadObj.put("afterHoursWithdrawBuyCount", item.afterHoursWithdrawBuyCount == null ? "-" : item.afterHoursWithdrawBuyCount);

                                uploadObj.put("afterHoursWithdrawBuyVolume", dwnull(item.afterHoursWithdrawBuyVolume == null ? "-" : item.afterHoursWithdrawBuyVolume));

                                uploadObj.put("afterHoursWithdrawSellCount", item.afterHoursWithdrawSellCount == null ? "-" : item.afterHoursWithdrawSellCount);

                                uploadObj.put("afterHoursWithdrawSellVolume", dwnull(item.afterHoursWithdrawSellVolume == null ? "-" : item.afterHoursWithdrawSellVolume));
                                uploadObj.put("afterHoursBuyVolume", dwnull(item.afterHoursBuyVolume == null ? "-" : item.afterHoursBuyVolume));
                                uploadObj.put("afterHoursSellVolume", dwnull(item.afterHoursSellVolume == null ? "-" : item.afterHoursSellVolume));

                                uploadObj.put("issuedCapital", item.issuedCapital == null ? "-" : item.issuedCapital);
                                uploadObj.put("limitPriceUpperLimit", item.limitPriceUpperLimit == null ? "-" : item.limitPriceUpperLimit);
                                uploadObj.put("limitPriceLowerLimit", item.limitPriceLowerLimit == null ? "-" : item.limitPriceLowerLimit);
                                uploadObj.put("longName", item.longName == null ? "-" : item.longName);
                                //板块指数
                                uploadObj.put("blockChg", item.blockChg == null ? "-" : item.blockChg);
                                uploadObj.put("averageChg", item.averageChg == null ? "-" : item.averageChg);
                                uploadObj.put("indexChg5", item.indexChg5 == null ? "-" : item.indexChg5);
                                uploadObj.put("indexChg10", item.indexChg10 == null ? "-" : item.indexChg10);
                                //3.3.0.002新增字段
                                uploadObj.put("monthChangeRate", item.monthChangeRate == null ? "-" : item.monthChangeRate);
                                uploadObj.put("yearChangeRate", item.yearChangeRate == null ? "-" : item.yearChangeRate);
                                uploadObj.put("recentMonthChangeRate", item.recentMonthChangeRate == null ? "-" : item.recentMonthChangeRate);
                                uploadObj.put("recentYearChangeRate", item.recentYearChangeRate == null ? "-" : item.recentYearChangeRate);
                                //新三板字段   20200828
                                uploadObj.put("listingType", item.listingType == null ? "-" : item.listingType);
                                uploadObj.put("underlyingSecurity", item.underlyingSecurity == null ? "-" : item.underlyingSecurity);
                                uploadObj.put("listDate", item.listDate == null ? "-" : item.listDate);
                                uploadObj.put("valueDate", item.valueDate == null ? "-" : item.valueDate);
                                uploadObj.put("expiringDate", item.expiringDate == null ? "-" : item.expiringDate);
                                uploadObj.put("serviceStatus", item.serviceStatus == null ? "-" : item.serviceStatus);
                                uploadObj.put("suspendedSymbol", item.suspendedSymbol == null ? "-" : item.suspendedSymbol);
                                uploadObj.put("mbxl", item.mbxl == null ? "-" : item.mbxl);
                                uploadObj.put("zxsbsl", item.zxsbsl == null ? "-" : item.zxsbsl);
                                uploadObj.put("en", item.en == null ? "-" : item.en);//期货品种
                                if (item.market.equals("bj")){
                                    uploadObj.put("marketMakerQty", item.marketMakerQty == null ? "-" : item.marketMakerQty);
                                    uploadObj.put("issuePE", item.issuePE == null ? "-" : item.issuePE);
                                    uploadObj.put("unRestrictedShareCapital", item.unRestrictedShareCapital == null ? "-" : item.unRestrictedShareCapital);
                                }
                                //创业板字段
                                uploadObj.put("securityStatus", item.securityStatus == null ? "-" : item.securityStatus);
                                uploadObj.put("buyQtyUpperLimit", item.buyQtyUpperLimit == null ? "-" : item.buyQtyUpperLimit);
                                uploadObj.put("sellQtyUpperLimit", item.sellQtyUpperLimit == null ? "-" : item.sellQtyUpperLimit);
                                uploadObj.put("marketBuyQtyUpperLimit", item.marketBuyQtyUpperLimit == null ? "-" : item.marketBuyQtyUpperLimit);
                                uploadObj.put("marketSellQtyUpperLimit", item.marketSellQtyUpperLimit == null ? "-" : item.marketSellQtyUpperLimit);
                                uploadObj.put("reg", item.reg == null ? "-" : item.reg);
                                uploadObj.put("vie", item.vie == null ? "-" : item.vie);
                                uploadObj.put("mf", item.mf == null ? "-" : item.mf);
                                uploadObj.put("rslf", item.rslf == null ? "-" : item.rslf);
                                uploadObj.put("mmf", item.mmf == null ? "-" : item.mmf);
                                uploadObj.put("buyAuctionRange", item.buyAuctionRange == null ? "-" : "["+ item.buyAuctionRange[0]+","+item.buyAuctionRange[1]+"]");
                                uploadObj.put("sellAuctionRange", item.sellAuctionRange == null ? "-" : "["+ item.sellAuctionRange[0]+","+item.sellAuctionRange[1]+"]");
                                uploadObj.put("afterHoursBuyQtyUpperLimit", item.afterHoursBuyQtyUpperLimit == null ? "-" : item.afterHoursBuyQtyUpperLimit);
                                uploadObj.put("afterHoursSellQtyUpperLimit", item.afterHoursSellQtyUpperLimit == null ? "-" : item.afterHoursSellQtyUpperLimit);
                                //20210603  AppInfo.sdk_version=3.9.0
                                if (item.market.equals("sh")||item.market.equals("sz")){
                                    uploadObj.put("ttm", item.ttm == null ? "-" : item.ttm);
                                    uploadObj.put("roe", item.roe == null ? "-" : item.roe);
                                    uploadObj.put("buyVol1", item.buyVol1 == null ? "-" : item.buyVol1);
                                    uploadObj.put("sellVol1", item.sellVol1 == null ? "-" : item.sellVol1);
                                    uploadObj.put("changeRate5", item.changeRate5 == null ? "-" : item.changeRate5);
                                    uploadObj.put("changeRate10", item.changeRate10 == null ? "-" : item.changeRate10);
                                    uploadObj.put("changeRate20", item.changeRate20 == null ? "-" : item.changeRate20);
                                    uploadObj.put("turnoverRate5", item.turnoverRate5 == null ? "-" : item.turnoverRate5);
                                    uploadObj.put("turnoverRate10", item.turnoverRate10 == null ? "-" : item.turnoverRate10);
                                    uploadObj.put("turnoverRate20", item.turnoverRate20 == null ? "-" : item.turnoverRate20);
                                    uploadObj.put("limitUPChangeRate", item.limitUPChangeRate == null ? "-" : item.limitUPChangeRate);
                                    uploadObj.put("limitDownChangeRate", item.limitDownChangeRate == null ? "-" : item.limitDownChangeRate);

                                }
                                //买卖队列
                                if (buyItems!=null) {
                                    ArrayList<OrderQuantityItem> orderQuantityItem1 = buyItems;
                                    List<JSONObject> buylist = new ArrayList<>();
                                    for (int k = 0; k < orderQuantityItem1.size(); k++) {
                                        JSONObject uploadObj_1 = new JSONObject();
//                               uploadObj_1.put("ID",orderQuantityItem1.get(k).ID_);
                                        uploadObj_1.put("QUANTITY", orderQuantityItem1.get(k).QUANTITY_ == null ? "-" : orderQuantityItem1.get(k).QUANTITY_);
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
                                        uploadObj_1.put("QUANTITY",orderQuantityItem2.get(k).QUANTITY_ == null ? "-" : orderQuantityItem2.get(k).QUANTITY_);
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

//                    @Override
//                    public void pushHttp(QuoteResponse quoteResponse) {
//                        if (quoteResponse == null) {
//                            Log.d(tTag, "quoteResponse Null");
//                        }else{
//                            Log.d(tTag, "quoteResponse not Null");
//                        }
//                    }
                });
            }
        }

        Log.d(tTag,"addIPush End");
        result.complete(uploadObj_6);


        try {
            JSONObject resultObj = (JSONObject) result.get(timeout_ms, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
        } catch (Exception e) {
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }
    }
    public String dwnull(String st){
        if (st.equals("一")){
            st="-";
        }else if (st.equals("")){
            st="-";
        }else if(st==null){
            st="-";
        }else if (st.isEmpty()){
            st="-";
        }
        return  st;
    }
}
