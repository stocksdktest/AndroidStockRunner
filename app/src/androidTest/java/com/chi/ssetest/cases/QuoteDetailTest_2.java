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
import com.mitake.core.keys.quote.AddValueCustomField;
import com.mitake.core.keys.quote.QuoteCustomField;
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.response.IResponseCallback;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteDetailResponse;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;

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
//行情快照 方法二
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.QUOTEDETAILTEST_2)
public class QuoteDetailTest_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.QUOTEDETAILTEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("QuoteDetailTest_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("QuoteDetailTest_2", "requestWork");
        // TODO get custom args from param

        final String quoteNumbers = rule.getParam().optString("CODE", "");
        final String count = rule.getParam().optString("TICKCOUNT", "");
        final String[] INTS1 = rule.getParam().optString("STOCKFIELDS","").split(",");
        final String[] INTS2 = rule.getParam().optString("FIELDS", "").split(",");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
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
//        QuoteCustomField;
//        AddValueCustomField
        QuoteDetailRequest request = new QuoteDetailRequest();
        request.send(quoteNumbers,count,ints1,ints2, new IResponseInfoCallback<QuoteResponse>() {
            @Override
            public void callback(QuoteResponse quoteResponse) {
                try {
                    assertNotNull(quoteResponse.quoteItems);
                } catch (AssertionError e) {
                    //                        result.completeExceptionally(e);
                    result.complete(new JSONObject());
                }
                QuoteItem list=quoteResponse.quoteItems.get(0);
                JSONObject uploadObj = new JSONObject();
                // TODO fill uploadObj with QuoteResponse value
                try {
                    if(list!=null){
                        uploadObj.put("status", dwnull(list.status == null ? "-" : list.status));
                        uploadObj.put("id", dwnull(list.id == null ? "-" : list.id));
                        uploadObj.put("name", dwnull(list.name == null ? "-" : list.name));
                        uploadObj.put("datetime", dwnull(list.datetime == null ? "-" : list.datetime));
//                        uploadObj.put("pinyin", list.pinyin);//ios无
                        uploadObj.put("market", dwnull(list.market == null ? "-" : list.market));
                        uploadObj.put("subtype", dwnull(list.subtype == null ? "-" : list.subtype));
                        uploadObj.put("lastPrice", dwnull(list.lastPrice == null ? "-" : list.lastPrice));
                        uploadObj.put("highPrice", dwnull(list.highPrice == null ? "-" : list.highPrice));
                        uploadObj.put("lowPrice", dwnull(list.lowPrice == null ? "-" : list.lowPrice));
                        uploadObj.put("openPrice", dwnull(list.openPrice == null ? "-" : list.openPrice));
                        uploadObj.put("preClosePrice", dwnull(list.preClosePrice == null ? "-" : list.preClosePrice));
//                        uploadObj.put("changeRate", list.upDownFlag+list.changeRate);//ios注意
                        if ("+".equals(list.upDownFlag)||"-".equals(list.upDownFlag)){
                            uploadObj.put("changeRate",list.upDownFlag+list.changeRate);//加涨跌符号
                        }else {
                            uploadObj.put("changeRate",dwnull(list.changeRate == null ? "-" : list.changeRate));
                        }
                        uploadObj.put("volume", dwnull(list.volume == null ? "-" : list.volume));
                        uploadObj.put("nowVolume", dwnull(list.nowVolume == null ? "-" : list.nowVolume));
                        uploadObj.put("turnoverRate", dwnull(list.turnoverRate == null ? "-" : list.turnoverRate));
                        uploadObj.put("upDownLimitType", list.upDownLimitType == null ? "-" : list.upDownLimitType);//ios注意
                        uploadObj.put("limitUP", list.limitUP == null ? "-" : list.limitUP);
                        uploadObj.put("limitDown", list.limitDown == null ? "-" : list.limitDown);
                        uploadObj.put("averageValue", list.averageValue == null ? "-" : list.averageValue);//ios无
                        uploadObj.put("change", list.change == null ? "-" : list.change);
                        uploadObj.put("amount", list.amount == null ? "-" : list.amount);
                        uploadObj.put("volumeRatio", list.volumeRatio == null ? "-" : list.volumeRatio);
                        uploadObj.put("buyPrice", list.buyPrice == null ? "-" : list.buyPrice);
                        uploadObj.put("sellPrice", list.sellPrice == null ? "-" : list.sellPrice);
                        uploadObj.put("buyVolume", list.buyVolume == null ? "-" : list.buyVolume);
                        uploadObj.put("sellVolume", list.sellVolume == null ? "-" : list.sellVolume);
                        uploadObj.put("totalValue", list.totalValue == null ? "-" : list.totalValue);
                        uploadObj.put("HKTotalValue", list.HKTotalValue == null ? "-" : list.HKTotalValue);
                        uploadObj.put("flowValue", list.flowValue == null ? "-" : list.flowValue);
                        uploadObj.put("netAsset", list.netAsset == null ? "-" : list.netAsset);
                        uploadObj.put("pe", list.pe == null ? "-" : list.pe);
                        uploadObj.put("pe2", list.pe2 == null ? "-" : list.pe2);
                        uploadObj.put("pb", list.pb == null ? "-" : list.pb);
                        uploadObj.put("capitalization", list.capitalization == null ? "-" : list.capitalization);
                        uploadObj.put("circulatingShares", list.circulatingShares == null ? "-" : list.circulatingShares);

                        List<String> buyPrices=new ArrayList<>();
                        if (list.buyPrices!=null&&list.buyPrices.size()>0){
                            for (int j=0;j<list.buyPrices.size();j++){
                                buyPrices.add(list.buyPrices.get(j) == null ? "-" : list.buyPrices.get(j));
                            }
                            uploadObj.put("bidpx1", list.buyPrices.get(0) == null ? "-" : list.buyPrices.get(0));
                            uploadObj.put("buyPrices",new JSONArray(buyPrices));
                        }else {
                            uploadObj.put("bidpx1", "-");
                            uploadObj.put("buyPrices",list.buyPrices == null ? "-" : list.buyPrices);
                        }

                        List<String> buySingleVolumes=new ArrayList<>();
                        if (list.buySingleVolumes!=null&&list.buySingleVolumes.size()>0){
                            for (int j=0;j<list.buySingleVolumes.size();j++){
                                buySingleVolumes.add(list.buySingleVolumes.get(j) == null ? "-" : list.buySingleVolumes.get(j));
                            }
                            uploadObj.put("buySingleVolumes",new JSONArray(buySingleVolumes));
                        }else {
                            uploadObj.put("buySingleVolumes",list.buySingleVolumes == null ? "-" : list.buySingleVolumes);
                        }

                        List<String> buyVolumes=new ArrayList<>();
                        if (list.buyVolumes!=null&&list.buyVolumes.size()>0){
                            for (int j=0;j<list.buyVolumes.size();j++){
                                buyVolumes.add(list.buyVolumes.get(j) == null ? "-" : list.buyVolumes.get(j));
                            }
                            uploadObj.put("bidvol1", list.buyVolumes.get(0) == null ? "-" : list.buyVolumes.get(0));
                            uploadObj.put("buyVolumes",new JSONArray(buyVolumes));
                        }else {
                            uploadObj.put("bidvol1", "-");
                            uploadObj.put("buyVolumes",list.buyVolumes == null ? "-" : list.buyVolumes);
                        }

                        List<String> sellPrices=new ArrayList<>();
                        if (list.sellPrices!=null&&list.sellPrices.size()>0){
                            for (int j=0;j<list.sellPrices.size();j++){
                                sellPrices.add(list.sellPrices.get(j) == null ? "-" : list.sellPrices.get(j));
                            }
                            uploadObj.put("askpx1", list.sellPrices.get(0) == null ? "-" : list.sellPrices.get(0));
                            uploadObj.put("sellPrices",new JSONArray(sellPrices));
                        }else {
                            uploadObj.put("askpx1", "-");
                            uploadObj.put("sellPrices",list.sellPrices == null ? "-" : list.sellPrices);
                        }

                        List<String> sellSingleVolumes=new ArrayList<>();
                        if (list.sellSingleVolumes!=null&&list.sellSingleVolumes.size()>0){
                            for (int j=0;j<list.sellSingleVolumes.size();j++){
                                sellSingleVolumes.add(list.sellSingleVolumes.get(j) == null ? "-" : list.sellSingleVolumes.get(j));
                            }
                            uploadObj.put("sellSingleVolumes",new JSONArray(sellSingleVolumes));
                        }else {
                            uploadObj.put("sellSingleVolumes",list.sellSingleVolumes == null ? "-" : list.sellSingleVolumes);
                        }

                        List<String> sellVolumes=new ArrayList<>();
                        if (list.sellVolumes!=null&&list.sellVolumes.size()>0){
                            for (int j=0;j<list.sellVolumes.size();j++){
                                sellVolumes.add(list.sellVolumes.get(j) == null ? "-" : list.sellVolumes.get(j));
                            }
                            uploadObj.put("askvol1", list.sellVolumes.get(0) == null ? "-" : list.sellVolumes.get(0));
                            uploadObj.put("sellVolumes",new JSONArray(sellVolumes));
                        }else {
                            uploadObj.put("askvol1", "-");
                            uploadObj.put("sellVolumes",list.sellVolumes == null ? "-" : list.sellVolumes);
                        }

                        uploadObj.put("amplitudeRate", list.amplitudeRate == null ? "-" : list.amplitudeRate);
                        uploadObj.put("receipts", list.receipts == null ? "-" : list.receipts);
                        //ios无

                        if (list.tradeTick!=null&&list.tradeTick.length>0){
                            for (int j=0;j<list.tradeTick.length;j++){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("type",list.tradeTick[j][0] == null ? "-" : list.tradeTick[j][0]);
                                uploadObj_1.put("time",list.tradeTick[j][1] == null ? "-" : list.tradeTick[j][1]);
                                uploadObj_1.put("tradeVolume",list.tradeTick[j][2] == null ? "-" : list.tradeTick[j][2]);
                                uploadObj_1.put("tradePrice",list.tradeTick[j][3] == null ? "-" : list.tradeTick[j][3]);
                                uploadObj.put(String.valueOf(j+1),uploadObj_1);
                            }
                        }else {
                            uploadObj.put("tradeTick",list.tradeTick == null ? "-" : list.tradeTick);
                        }

                        uploadObj.put("upCount", list.upCount == null ? "-" : list.upCount);
                        uploadObj.put("sameCount", list.sameCount == null ? "-" : list.sameCount);
                        uploadObj.put("downCount", list.downCount == null ? "-" : list.downCount);
                        uploadObj.put("optionType", list.optionType == null ? "-" : list.optionType);
                        uploadObj.put("contractID", list.contractID == null ? "-" : list.contractID);
                        uploadObj.put("objectID", list.objectID == null ? "-" : list.objectID);
                        uploadObj.put("stockSymble", list.stockSymble == null ? "-" : list.stockSymble);
                        uploadObj.put("stockType", list.stockType == null ? "-" : list.stockType);
                        uploadObj.put("stockUnit", list.stockUnit == null ? "-" : list.stockUnit);
                        uploadObj.put("exePrice", list.exePrice == null ? "-" : list.exePrice);
                        uploadObj.put("startDate", list.startDate == null ? "-" : list.startDate);
                        uploadObj.put("endDate", list.endDate == null ? "-" : list.endDate);
                        uploadObj.put("exeDate", list.exeDate == null ? "-" : list.exeDate);
                        uploadObj.put("delDate", list.delDate == null ? "-" : list.delDate);
                        uploadObj.put("expDate", list.expDate == null ? "-" : list.expDate);
                        uploadObj.put("version", list.version == null ? "-" : list.version);

                        uploadObj.put("presetPrice", dwnull(list.presetPrice == null ? "-" : list.presetPrice));

                        uploadObj.put("stockClose", list.stockClose == null ? "-" : list.stockClose);
                        uploadObj.put("stockLast", list.stockLast == null ? "-" : list.stockLast);
                        uploadObj.put("isLimit", list.isLimit == null ? "-" : list.isLimit);
                        uploadObj.put("inValue", list.inValue == null ? "-" : list.inValue);
                        uploadObj.put("timeValue", list.timeValue == null ? "-" : list.timeValue);
                        uploadObj.put("preInterest", list.preInterest == null ? "-" : list.preInterest);
                        uploadObj.put("openInterest", list.openInterest == null ? "-" : list.openInterest);
                        uploadObj.put("remainDate", list.remainDate == null ? "-" : list.remainDate);
                        uploadObj.put("leverageRatio", list.leverageRatio == null ? "-" : list.leverageRatio);
                        uploadObj.put("premiumRate", list.premiumRate == null ? "-" : list.premiumRate);
                        uploadObj.put("impliedVolatility", list.impliedVolatility == null ? "-" : list.impliedVolatility);
                        uploadObj.put("delta", list.delta == null ? "-" : list.delta);
                        uploadObj.put("gramma", list.gramma == null ? "-" : list.gramma);
                        uploadObj.put("theta", list.theta == null ? "-" : list.theta);
                        uploadObj.put("rho", list.rho == null ? "-" : list.rho);
                        uploadObj.put("vega", list.vega == null ? "-" : list.vega);
                        uploadObj.put("realLeverage", list.realLeverage == null ? "-" : list.realLeverage);
                        uploadObj.put("theoreticalPrice", list.theoreticalPrice == null ? "-" : list.theoreticalPrice);
                        //
                        uploadObj.put("exerciseWay", dwnull(list.exerciseWay == null ? "-" : list.exerciseWay));
//                        if (list.exerciseWay.isEmpty()){
//                            uploadObj.put("exerciseWay", "-");
//                        }else {
//                            uploadObj.put("exerciseWay", dwnull(list.exerciseWay == null ? "-" : list.exerciseWay));
//                        }
                        uploadObj.put("orderRatio", list.orderRatio == null ? "-" : list.orderRatio);
                        uploadObj.put("hk_paramStatus", list.hk_paramStatus == null ? "-" : list.hk_paramStatus);//ios无
                        uploadObj.put("fundType", list.fundType == null ? "-" : list.fundType);
                        uploadObj.put("sumBuy", list.sumBuy == null ? "-" : list.sumBuy);
                        uploadObj.put("sumSell", list.sumSell == null ? "-" : list.sumSell);
                        uploadObj.put("averageBuy", list.averageBuy == null ? "-" : list.averageBuy);
                        uploadObj.put("averageSell", list.averageSell == null ? "-" : list.averageSell);
//                        uploadObj.put("upDownFlag", list.upDownFlag);//注意一下IOS android

                        uploadObj.put("zh", dwnull(list.zh == null ? "-" : list.zh));
                        uploadObj.put("hh", dwnull(list.hh == null ? "-" : list.hh));
                        uploadObj.put("st", dwnull(list.st == null ? "-" : list.st));
                        uploadObj.put("bu", dwnull(list.bu == null ? "-" : list.bu));
                        uploadObj.put("su", dwnull(list.su == null ? "-" : list.su));

                        uploadObj.put("hs", dwnull(list.hs == null ? "-" : list.hs));
                        uploadObj.put("ac", dwnull(list.ac == null ? "-" : list.ac));
                        uploadObj.put("qf", dwnull(list.qf == null ? "-" : list.qf));
                        uploadObj.put("qc", dwnull(list.qc == null ? "-" : list.qc));
                        uploadObj.put("ah", dwnull(list.ah == null ? "-" : list.ah));
                        uploadObj.put("VCMFlag", dwnull(list.VCMFlag == null ? "-" : list.VCMFlag));
                        uploadObj.put("CASFlag", dwnull(list.CASFlag == null ? "-" : list.CASFlag));

                        //20210118添加 POSFlag 该字段
                        uploadObj.put("POSFlag", dwnull(list.POSFlag == null ? "-" : list.POSFlag));
                        uploadObj.put("rp", dwnull(list.rp == null ? "-" : list.rp));
                        uploadObj.put("cd", dwnull(list.cd == null ? "-" : list.cd));
                        uploadObj.put("hg", dwnull(list.hg == null ? "-" : list.hg));
                        uploadObj.put("sg", dwnull(list.sg == null ? "-" : list.sg));
                        uploadObj.put("fx", dwnull(list.fx == null ? "-" : list.fx));
                        uploadObj.put("ts", dwnull(list.ts == null ? "-" : list.ts));
                        uploadObj.put("add_option_avg_price", dwnull(list.add_option_avg_price == null ? "-" : list.add_option_avg_price));
                        uploadObj.put("add_option_avg_pb", dwnull(list.add_option_avg_pb == null ? "-" : list.add_option_avg_pb));
                        uploadObj.put("add_option_avg_close", dwnull(list.add_option_avg_close == null ? "-" : list.add_option_avg_close));

                        uploadObj.put("hk_volum_for_every_hand", list.hk_volum_for_every_hand == null ? "-" : list.hk_volum_for_every_hand);
                        //ios无
                        uploadObj.put("buy_cancel_count", list.buy_cancel_count == null ? "-" : list.buy_cancel_count);

                        uploadObj.put("buy_cancel_num", dwnull(list.buy_cancel_num == null ? "-" : list.buy_cancel_num));

                        uploadObj.put("buy_cancel_amount", list.buy_cancel_amount == null ? "-" : list.buy_cancel_amount);
                        uploadObj.put("sell_cancel_count", list.sell_cancel_count == null ? "-" : list.sell_cancel_count);

                        uploadObj.put("sell_cancel_num", dwnull(list.sell_cancel_num == null ? "-" : list.sell_cancel_num));

                        uploadObj.put("sell_cancel_amount", list.sell_cancel_amount == null ? "-" : list.sell_cancel_amount);
                        uploadObj.put("tradingDay", list.tradingDay == null ? "-" : list.tradingDay);
                        uploadObj.put("settlementID", list.settlementID == null ? "-" : list.settlementID);
                        uploadObj.put("settlementGroupID", list.settlementGroupID == null ? "-" : list.settlementGroupID);
                        uploadObj.put("preSettlement", list.preSettlement == null ? "-" : list.preSettlement);
                        uploadObj.put("position_chg", list.position_chg == null ? "-" : list.position_chg);
                        uploadObj.put("close", list.close == null ? "-" : list.close);
                        uploadObj.put("settlement", list.settlement == null ? "-" : list.settlement);
                        uploadObj.put("preDelta", list.preDelta == null ? "-" : list.preDelta);
                        uploadObj.put("currDelta", list.currDelta == null ? "-" : list.currDelta);
                        uploadObj.put("updateMillisec", list.updateMillisec == null ? "-" : list.updateMillisec);
                        uploadObj.put("entrustDiff", list.entrustDiff == null ? "-" : list.entrustDiff);
                        uploadObj.put("posDiff", list.posDiff == null ? "-" : list.posDiff);
                        uploadObj.put("currDiff", list.currDiff == null ? "-" : list.currDiff);
                        uploadObj.put("underlyingType", list.underlyingType == null ? "-" : list.underlyingType);
                        uploadObj.put("underlyingLastPx", list.underlyingLastPx == null ? "-" : list.underlyingLastPx);
                        uploadObj.put("underlyingPreClose", list.underlyingPreClose == null ? "-" : list.underlyingPreClose);
                        uploadObj.put("underlyingchg", list.underlyingchg == null ? "-" : list.underlyingchg);
                        uploadObj.put("underlyingSymbol", list.underlyingSymbol == null ? "-" : list.underlyingSymbol);
                        uploadObj.put("deliveryDay", list.deliveryDay == null ? "-" : list.deliveryDay);
                        uploadObj.put("riskFreeInterestRate", list.riskFreeInterestRate == null ? "-" : list.riskFreeInterestRate);
                        uploadObj.put("intersectionNum", list.intersectionNum == null ? "-" : list.intersectionNum);
                        uploadObj.put("change1", list.change1 == null ? "-" : list.change1);
                        uploadObj.put("totalBid", list.totalBid == null ? "-" : list.totalBid);
                        uploadObj.put("totalAsk", list.totalAsk == null ? "-" : list.totalAsk);
                        //
                        uploadObj.put("IOPV", dwnull(list.IOPV == null ? "-" : list.IOPV));
                        uploadObj.put("preIOPV", dwnull(list.preIOPV == null ? "-" : list.preIOPV));

                        uploadObj.put("stateOfTransfer", dwnull(list.stateOfTransfer == null ? "-" : list.stateOfTransfer));
                        uploadObj.put("typeOfTransfer", dwnull(list.typeOfTransfer == null ? "-" : list.typeOfTransfer));
                        uploadObj.put("exRighitDividend", dwnull(list.exRighitDividend == null ? "-" : list.exRighitDividend));
                        uploadObj.put("securityLevel", dwnull(list.securityLevel == null ? "-" : list.securityLevel));
                        uploadObj.put("rpd", dwnull(list.rpd == null ? "-" : list.rpd));
                        uploadObj.put("cdd", dwnull(list.cdd == null ? "-" : list.cdd));

                        //ios无
                        uploadObj.put("change2", dwnull(list.change2 == null ? "-" : list.change2));
                        uploadObj.put("earningsPerShare", list.earningsPerShare == null ? "-" : list.earningsPerShare);
                        uploadObj.put("earningsPerShareReportingPeriod", list.earningsPerShareReportingPeriod == null ? "-" : list.earningsPerShareReportingPeriod);
                        //
                        uploadObj.put("hkTExchangeFlag", dwnull(list.hkTExchangeFlag == null ? "-" : list.hkTExchangeFlag));

                        uploadObj.put("vote", list.vote == null ? "-" : list.vote);//注意ios
                        uploadObj.put("upf", list.upf == null ? "-" : list.upf);//注意ios
                        uploadObj.put("DRCurrentShare", list.DRCurrentShare == null ? "-" : list.DRCurrentShare);
                        uploadObj.put("DRPreviousClosingShare", list.DRPreviousClosingShare == null ? "-" : list.DRPreviousClosingShare);
                        uploadObj.put("DRConversionBase", list.DRConversionBase == null ? "-" : list.DRConversionBase);

                        uploadObj.put("DRDepositoryInstitutionCode", dwnull(list.DRDepositoryInstitutionCode == null ? "-" : list.DRDepositoryInstitutionCode));
                        uploadObj.put("DRDepositoryInstitutionName", dwnull(list.DRDepositoryInstitutionName == null ? "-" : list.DRDepositoryInstitutionName));
                        uploadObj.put("DRSubjectClosingReferencePrice", dwnull(list.DRSubjectClosingReferencePrice == null ? "-" : list.DRSubjectClosingReferencePrice));
                        uploadObj.put("DR", dwnull(list.DR == null ? "-" : list.DR));
                        uploadObj.put("GDR", dwnull(list.GDR == null ? "-" : list.GDR));
                        uploadObj.put("DRStockCode", dwnull(list.DRStockCode == null ? "-" : list.DRStockCode));
                        uploadObj.put("DRStockName", dwnull(list.DRStockName == null ? "-" : list.DRStockName));

                        uploadObj.put("DRSecuritiesConversionBase", list.DRSecuritiesConversionBase == null ? "-" : list.DRSecuritiesConversionBase);
                        uploadObj.put("DRListingDate", list.DRListingDate == null ? "-" : list.DRListingDate);
                        uploadObj.put("DRFlowStartDate", list.DRFlowStartDate == null ? "-" : list.DRFlowStartDate);
                        uploadObj.put("DRFlowEndDate", list.DRFlowEndDate == null ? "-" : list.DRFlowEndDate);
                        uploadObj.put("changeBP", list.changeBP == null ? "-" : list.changeBP);
                        uploadObj.put("subscribeUpperLimit", list.subscribeUpperLimit == null ? "-" : list.subscribeUpperLimit);
                        uploadObj.put("subscribeLowerLimit", list.subscribeLowerLimit == null ? "-" : list.subscribeLowerLimit);
                        uploadObj.put("afterHoursVolume", dwnull(list.afterHoursVolume == null ? "-" : list.afterHoursVolume));

                        uploadObj.put("afterHoursAmount", list.afterHoursAmount == null ? "-" : list.afterHoursAmount);
                        uploadObj.put("afterHoursTransactionNumber", list.afterHoursTransactionNumber == null ? "-" : list.afterHoursTransactionNumber);
                        uploadObj.put("afterHoursWithdrawBuyCount", list.afterHoursWithdrawBuyCount == null ? "-" : list.afterHoursWithdrawBuyCount);

                        uploadObj.put("afterHoursWithdrawBuyVolume", dwnull(list.afterHoursWithdrawBuyVolume == null ? "-" : list.afterHoursWithdrawBuyVolume));

                        uploadObj.put("afterHoursWithdrawSellCount", list.afterHoursWithdrawSellCount == null ? "-" : list.afterHoursWithdrawSellCount);

                        uploadObj.put("afterHoursWithdrawSellVolume", dwnull(list.afterHoursWithdrawSellVolume == null ? "-" : list.afterHoursWithdrawSellVolume));
                        uploadObj.put("afterHoursBuyVolume", dwnull(list.afterHoursBuyVolume == null ? "-" : list.afterHoursBuyVolume));
                        uploadObj.put("afterHoursSellVolume", dwnull(list.afterHoursSellVolume == null ? "-" : list.afterHoursSellVolume));

                        uploadObj.put("issuedCapital", list.issuedCapital == null ? "-" : list.issuedCapital);
                        uploadObj.put("limitPriceUpperLimit", list.limitPriceUpperLimit == null ? "-" : list.limitPriceUpperLimit);
                        uploadObj.put("limitPriceLowerLimit", list.limitPriceLowerLimit == null ? "-" : list.limitPriceLowerLimit);
                        uploadObj.put("longName", list.longName == null ? "-" : list.longName);
                        //板块指数
                        uploadObj.put("blockChg", list.blockChg == null ? "-" : list.blockChg);
                        uploadObj.put("averageChg", list.averageChg == null ? "-" : list.averageChg);
                        uploadObj.put("indexChg5", list.indexChg5 == null ? "-" : list.indexChg5);
                        uploadObj.put("indexChg10", list.indexChg10 == null ? "-" : list.indexChg10);
                        //3.3.0.002新增字段
                        uploadObj.put("monthChangeRate", list.monthChangeRate == null ? "-" : list.monthChangeRate);
                        uploadObj.put("yearChangeRate", list.yearChangeRate == null ? "-" : list.yearChangeRate);
                        uploadObj.put("recentMonthChangeRate", list.recentMonthChangeRate == null ? "-" : list.recentMonthChangeRate);
                        uploadObj.put("recentYearChangeRate", list.recentYearChangeRate == null ? "-" : list.recentYearChangeRate);
                        //新三板字段   20200828
                        uploadObj.put("listingType", list.listingType == null ? "-" : list.listingType);
                        uploadObj.put("underlyingSecurity", list.underlyingSecurity == null ? "-" : list.underlyingSecurity);
                        uploadObj.put("listDate", list.listDate == null ? "-" : list.listDate);
                        uploadObj.put("valueDate", list.valueDate == null ? "-" : list.valueDate);
                        uploadObj.put("expiringDate", list.expiringDate == null ? "-" : list.expiringDate);
                        uploadObj.put("serviceStatus", list.serviceStatus == null ? "-" : list.serviceStatus);
                        uploadObj.put("suspendedSymbol", list.suspendedSymbol == null ? "-" : list.suspendedSymbol);
                        uploadObj.put("mbxl", list.mbxl == null ? "-" : list.mbxl);
                        uploadObj.put("zxsbsl", list.zxsbsl == null ? "-" : list.zxsbsl);
                        uploadObj.put("en", list.en == null ? "-" : list.en);//期货品种
                        if (list.market.equals("bj")){
                            uploadObj.put("marketMakerQty", list.marketMakerQty == null ? "-" : list.marketMakerQty);
                            uploadObj.put("issuePE", list.issuePE == null ? "-" : list.issuePE);
                            uploadObj.put("unRestrictedShareCapital", list.unRestrictedShareCapital == null ? "-" : list.unRestrictedShareCapital);
                        }
                        //创业板字段
                        uploadObj.put("securityStatus", list.securityStatus == null ? "-" : list.securityStatus);
                        uploadObj.put("buyQtyUpperLimit", list.buyQtyUpperLimit == null ? "-" : list.buyQtyUpperLimit);
                        uploadObj.put("sellQtyUpperLimit", list.sellQtyUpperLimit == null ? "-" : list.sellQtyUpperLimit);
                        uploadObj.put("marketBuyQtyUpperLimit", list.marketBuyQtyUpperLimit == null ? "-" : list.marketBuyQtyUpperLimit);
                        uploadObj.put("marketSellQtyUpperLimit", list.marketSellQtyUpperLimit == null ? "-" : list.marketSellQtyUpperLimit);
                        uploadObj.put("reg", list.reg == null ? "-" : list.reg);
                        uploadObj.put("vie", list.vie == null ? "-" : list.vie);
                        uploadObj.put("mf", list.mf == null ? "-" : list.mf);
                        uploadObj.put("rslf", list.rslf == null ? "-" : list.rslf);
                        uploadObj.put("mmf", list.mmf == null ? "-" : list.mmf);
                        uploadObj.put("buyAuctionRange", list.buyAuctionRange == null ? "-" : "["+ list.buyAuctionRange[0]+","+list.buyAuctionRange[1]+"]");
                        uploadObj.put("sellAuctionRange", list.sellAuctionRange == null ? "-" : "["+ list.sellAuctionRange[0]+","+list.sellAuctionRange[1]+"]");
                        uploadObj.put("afterHoursBuyQtyUpperLimit", list.afterHoursBuyQtyUpperLimit == null ? "-" : list.afterHoursBuyQtyUpperLimit);
                        uploadObj.put("afterHoursSellQtyUpperLimit", list.afterHoursSellQtyUpperLimit == null ? "-" : list.afterHoursSellQtyUpperLimit);
                        //20210603  AppInfo.sdk_version=3.9.0
                        if (list.market.equals("sh")||list.market.equals("sz")){
                            uploadObj.put("ttm", list.ttm == null ? "-" : list.ttm);
                            uploadObj.put("roe", list.roe == null ? "-" : list.roe);
                            uploadObj.put("buyVol1", list.buyVol1 == null ? "-" : list.buyVol1);
                            uploadObj.put("sellVol1", list.sellVol1 == null ? "-" : list.sellVol1);
                            uploadObj.put("changeRate5", list.changeRate5 == null ? "-" : list.changeRate5);
                            uploadObj.put("changeRate10", list.changeRate10 == null ? "-" : list.changeRate10);
                            uploadObj.put("changeRate20", list.changeRate20 == null ? "-" : list.changeRate20);
                            uploadObj.put("turnoverRate5", list.turnoverRate5 == null ? "-" : list.turnoverRate5);
                            uploadObj.put("turnoverRate10", list.turnoverRate10 == null ? "-" : list.turnoverRate10);
                            uploadObj.put("turnoverRate20", list.turnoverRate20 == null ? "-" : list.turnoverRate20);
                            uploadObj.put("limitUPChangeRate", list.limitUPChangeRate == null ? "-" : list.limitUPChangeRate);
                            uploadObj.put("limitDownChangeRate", list.limitDownChangeRate == null ? "-" : list.limitDownChangeRate);
                        }
                        //买卖队列
                        if (quoteResponse.OrderQuantityBuyList!=null) {
                            ArrayList<OrderQuantityItem> orderQuantityItem1 = quoteResponse.OrderQuantityBuyList;
                            List<JSONObject> buylist = new ArrayList<>();
                            for (int k = 0; k < orderQuantityItem1.size(); k++) {
                                JSONObject uploadObj_1 = new JSONObject();
//                               uploadObj_1.put("ID",orderQuantityItem1.get(k).ID_);
                                uploadObj_1.put("QUANTITY", orderQuantityItem1.get(k).QUANTITY_ == null ? "-" : orderQuantityItem1.get(k).QUANTITY_);
                                buylist.add(uploadObj_1);
                            }
                            uploadObj.put("buylist", new JSONArray(buylist));
                        }
                        if (quoteResponse.OrderQuantitySellList!=null){
                            ArrayList<OrderQuantityItem> orderQuantityItem2=quoteResponse.OrderQuantitySellList;
                            List<JSONObject> selllist=new ArrayList<>();
                            for (int k=0;k<orderQuantityItem2.size();k++){
                                JSONObject uploadObj_1 = new JSONObject();
//                               uploadObj_1.put("ID",orderQuantityItem2.get(k).ID_);
                                uploadObj_1.put("QUANTITY",orderQuantityItem2.get(k).QUANTITY_ == null ? "-" : orderQuantityItem2.get(k).QUANTITY_);
                                selllist.add(uploadObj_1);
                            }
                            uploadObj.put("selllist",new JSONArray(selllist));
                        }
                        //经纪席位
                        if (quoteResponse.BrokerInfoListBuy!=null){
                            ArrayList<BrokerInfoItem> brokerInfoItems1=quoteResponse.BrokerInfoListBuy;
                            for (int i=0;i<brokerInfoItems1.size();i++) {
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("corp",brokerInfoItems1.get(i).corp == null ? "-" : brokerInfoItems1.get(i).corp);
                                uploadObj_1.put("corporation",brokerInfoItems1.get(i).corporation == null ? "-" : brokerInfoItems1.get(i).corporation);
                                uploadObj_1.put("state",brokerInfoItems1.get(i).state);
                                uploadObj.put(brokerInfoItems1.get(i).state+"_"+String.valueOf(i+1),uploadObj_1);
                            }
                        }
                        if (quoteResponse.BrokerInfoListSell!=null){
                            ArrayList<BrokerInfoItem> brokerInfoItems2=quoteResponse.BrokerInfoListSell;
                            for (int i=0;i<brokerInfoItems2.size();i++) {
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("corp",brokerInfoItems2.get(i).corp == null ? "-" : brokerInfoItems2.get(i).corp);
                                uploadObj_1.put("corporation",brokerInfoItems2.get(i).corporation == null ? "-" : brokerInfoItems2.get(i).corporation);
                                uploadObj_1.put("state",brokerInfoItems2.get(i).state);
                                uploadObj.put(brokerInfoItems2.get(i).state+"_"+String.valueOf(i+1),uploadObj_1);
                            }
                        }
//                        //增值指标
//                        if (quoteResponse.addValueModel!=null){
//                            ArrayList<AddValueModel> addValueModel=quoteResponse.addValueModel;
//                            for (AddValueModel item : addValueModel) {
//                                JSONObject uploadObj_1 = new JSONObject();
//                                uploadObj_1.put("code",item.code == null ? "-" : item.code);
//                                uploadObj_1.put("date",item.date == null ? "-" : item.date);
//                                uploadObj_1.put("time",item.time == null ? "-" : item.time);
//                                uploadObj_1.put("ultraLargeBuyVolume",item.ultraLargeBuyVolume == null ? "-" : item.ultraLargeBuyVolume);
//                                uploadObj_1.put("ultraLargeSellVolume",item.ultraLargeSellVolume == null ? "-" : item.ultraLargeSellVolume);
//                                uploadObj_1.put("ultraLargeBuyAmount",item.ultraLargeBuyAmount == null ? "-" : item.ultraLargeBuyAmount);
//                                uploadObj_1.put("ultraLargeSellAmount",item.ultraLargeSellAmount == null ? "-" : item.ultraLargeSellAmount);
//                                uploadObj_1.put("largeBuyVolume",item.largeBuyVolume == null ? "-" : item.largeBuyVolume);
//                                uploadObj_1.put("largeSellVolume",item.largeSellVolume == null ? "-" : item.largeSellVolume);
//                                uploadObj_1.put("largeBuyAmount",item.largeBuyAmount == null ? "-" : item.largeBuyAmount);
//                                uploadObj_1.put("largeSellAmount",item.largeSellAmount == null ? "-" : item.largeSellAmount);
//                                uploadObj_1.put("mediumBuyVolume",item.mediumBuyVolume == null ? "-" : item.mediumBuyVolume);
//                                uploadObj_1.put("mediumSellVolume",item.mediumSellVolume == null ? "-" : item.mediumSellVolume);
//                                uploadObj_1.put("mediumBuyAmount",item.mediumBuyAmount == null ? "-" : item.mediumBuyAmount);
//                                uploadObj_1.put("mediumSellAmount",item.mediumSellAmount == null ? "-" : item.mediumSellAmount);
//                                uploadObj_1.put("smallBuyVolume",item.smallBuyVolume == null ? "-" : item.smallBuyVolume);
//                                uploadObj_1.put("smallSellVolume",item.smallSellVolume == null ? "-" : item.smallSellVolume);
//                                uploadObj_1.put("smallBuyAmount",item.smallBuyAmount == null ? "-" : item.smallBuyAmount);
//                                uploadObj_1.put("smallSellAmount",item.smallSellAmount == null ? "-" : item.smallSellAmount);
//                                uploadObj_1.put("ultraLargeNetInflow",item.ultraLargeNetInflow == null ? "-" : item.ultraLargeNetInflow);
//                                uploadObj_1.put("largeNetInflow",item.largeNetInflow == null ? "-" : item.largeNetInflow);
//                                uploadObj_1.put("netCapitalInflow",item.netCapitalInflow == null ? "-" : item.netCapitalInflow);
//                                uploadObj_1.put("mediumNetInflow",item.mediumNetInflow == null ? "-" : item.mediumNetInflow);
//                                uploadObj_1.put("smallNetInflow",item.smallNetInflow == null ? "-" : item.smallNetInflow);
//
//                                List<String> fundsInflows=new ArrayList<>();
//                                if (item.fundsInflows!=null&&item.fundsInflows.length>0){
//                                    for (int j=0;j<item.fundsInflows.length;j++){
//                                        fundsInflows.add(item.fundsInflows[j] == null ? "-" : item.fundsInflows[j]);
//                                    }
//                                    uploadObj_1.put("fundsInflows",new JSONArray(fundsInflows));
//                                }else {
//                                    uploadObj_1.put("fundsInflows",item.fundsInflows == null ? "-" : item.fundsInflows);
//                                }
//
//                                List<String> fundsOutflows=new ArrayList<>();
//                                if (item.fundsOutflows!=null&&item.fundsOutflows.length>0){
//                                    for (int j=0;j<item.fundsOutflows.length;j++){
//                                        fundsOutflows.add(item.fundsOutflows[j] == null ? "-" : item.fundsOutflows[j]);
//                                    }
//                                    uploadObj_1.put("fundsOutflows",new JSONArray(fundsOutflows));
//                                }else {
//                                    uploadObj_1.put("fundsOutflows",item.fundsOutflows == null ? "-" : item.fundsOutflows);
//                                }
//
//                                uploadObj_1.put("ultraLargeDiffer",item.ultraLargeDiffer == null ? "-" : item.ultraLargeDiffer);
//                                uploadObj_1.put("largeDiffer",item.largeDiffer == null ? "-" : item.largeDiffer);
//                                uploadObj_1.put("mediumDiffer",item.mediumDiffer == null ? "-" : item.mediumDiffer);
//                                uploadObj_1.put("smallDiffer",item.smallDiffer == null ? "-" : item.smallDiffer);
//                                uploadObj_1.put("largeBuyDealCount",item.largeBuyDealCount == null ? "-" : item.largeBuyDealCount);
//                                uploadObj_1.put("largeSellDealCount",item.largeSellDealCount == null ? "-" : item.largeSellDealCount);
//                                uploadObj_1.put("dealCountMovingAverage",item.dealCountMovingAverage == null ? "-" : item.dealCountMovingAverage);
//                                uploadObj_1.put("buyCount",item.buyCount == null ? "-" : item.buyCount);
//                                uploadObj_1.put("sellCount",item.sellCount == null ? "-" : item.sellCount);
//                                uploadObj_1.put("BBD",item.BBD == null ? "-" : item.BBD);
//                                uploadObj_1.put("BBD5",item.BBD5 == null ? "-" : item.BBD5);
//                                uploadObj_1.put("BBD10",item.BBD10 == null ? "-" : item.BBD10);
//                                uploadObj_1.put("DDX",item.DDX == null ? "-" : item.DDX);
//                                uploadObj_1.put("DDX5",item.DDX5 == null ? "-" : item.DDX5);
//                                uploadObj_1.put("DDX10",item.DDX10 == null ? "-" : item.DDX10);
//                                uploadObj_1.put("DDY",item.DDY == null ? "-" : item.DDY);
//                                uploadObj_1.put("DDY5",item.DDY5 == null ? "-" : item.DDY5);
//                                uploadObj_1.put("DDY10",item.DDY10 == null ? "-" : item.DDY10);
//                                uploadObj_1.put("DDZ",item.DDZ == null ? "-" : item.DDZ);
//                                uploadObj_1.put("RatioBS",item.RatioBS == null ? "-" : item.RatioBS);
//
//                                List<String> othersFundsInflows=new ArrayList<>();
//                                if (item.othersFundsInflows!=null&&item.othersFundsInflows.length>0){
//                                    for (int j=0;j<item.othersFundsInflows.length;j++){
//                                        othersFundsInflows.add(item.othersFundsInflows[j] == null ? "-" : item.othersFundsInflows[j]);
//                                    }
//                                    uploadObj_1.put("othersFundsInflows",new JSONArray(othersFundsInflows));
//                                }else {
//                                    uploadObj_1.put("othersFundsInflows",item.othersFundsInflows == null ? "-" : item.othersFundsInflows);
//                                }
//
//                                List<String> othersFundsOutflows=new ArrayList<>();
//                                if (item.othersFundsOutflows!=null&&item.othersFundsOutflows.length>0){
//                                    for (int j=0;j<item.othersFundsOutflows.length;j++){
//                                        othersFundsOutflows.add(item.othersFundsOutflows[j] == null ? "-" : item.othersFundsOutflows[j]);
//                                    }
//                                    uploadObj_1.put("othersFundsOutflows",new JSONArray(othersFundsOutflows));
//                                }else {
//                                    uploadObj_1.put("othersFundsOutflows",item.othersFundsOutflows == null ? "-" : item.othersFundsOutflows);
//                                }
//
//                                uploadObj_1.put("fiveMinutesChangeRate",item.fiveMinutesChangeRate == null ? "-" : item.fiveMinutesChangeRate);
//                                uploadObj_1.put("largeOrderNumB",item.largeOrderNumB == null ? "-" : item.largeOrderNumB);
//                                uploadObj_1.put("largeOrderNumS",item.largeOrderNumS == null ? "-" : item.largeOrderNumS);
//                                uploadObj_1.put("bigOrderNumB",item.bigOrderNumB == null ? "-" : item.bigOrderNumB);
//                                uploadObj_1.put("bigOrderNumS",item.bigOrderNumS == null ? "-" : item.bigOrderNumS);
//                                uploadObj_1.put("midOrderNumB",item.midOrderNumB == null ? "-" : item.midOrderNumB);
//                                uploadObj_1.put("midOrderNumS",item.midOrderNumS == null ? "-" : item.midOrderNumS);
//                                uploadObj_1.put("smallOrderNumB",item.smallOrderNumB == null ? "-" : item.smallOrderNumB);
//                                uploadObj_1.put("smallOrderNumS",item.smallOrderNumS == null ? "-" : item.smallOrderNumS);
//                                uploadObj_1.put("mainforceMoneyNetInflow5",item.mainforceMoneyNetInflow5 == null ? "-" : item.mainforceMoneyNetInflow5);
//                                uploadObj_1.put("mainforceMoneyNetInflow10",item.mainforceMoneyNetInflow10 == null ? "-" : item.mainforceMoneyNetInflow10);
//                                uploadObj_1.put("mainforceMoneyNetInflow20",item.mainforceMoneyNetInflow20 == null ? "-" : item.mainforceMoneyNetInflow20);
//                                uploadObj_1.put("ratioMainforceMoneyNetInflow5",item.ratioMainforceMoneyNetInflow5 == null ? "-" : item.ratioMainforceMoneyNetInflow5);
//                                uploadObj_1.put("ratioMainforceMoneyNetInflow10",item.ratioMainforceMoneyNetInflow10 == null ? "-" : item.ratioMainforceMoneyNetInflow10);
//                                uploadObj_1.put("ratioMainforceMoneyNetInflow20",item.ratioMainforceMoneyNetInflow20 == null ? "-" : item.ratioMainforceMoneyNetInflow20);
//                                uploadObj.put("addValue",uploadObj_1);
//                            }
//                        }
                    }
                } catch (JSONException e) {
                    result.completeExceptionally(e);
                }
//                    for (QuoteItem item : quoteResponse.quoteItems) {
//                        Log.d("StockUnittest", item.toString());
//                    }
//                Log.d("data",uploadObj.toString());
                result.complete(uploadObj);
            }

            @Override
            public void exception(ErrorInfo errorInfo) {
               result.completeExceptionally(new Exception(errorInfo.toString()));
            }
        });
//        }
        try {
            JSONObject resultObj = (JSONObject)result.get(timeout_ms, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
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
