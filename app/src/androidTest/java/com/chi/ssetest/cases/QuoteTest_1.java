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
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.response.IResponseInfoCallback;
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

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//证券行情列表 方法一
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.QUOTETEST_1)
public class QuoteTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.QUOTETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("QuoteTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("QuoteTest_1", "requestWork");
        // TODO get custom args from param
        final String[] quoteNumbers = rule.getParam().optString("CODES", "").split(",");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            QuoteRequest request = new QuoteRequest();
//            System.out.println(CODES+"++++++");
            request.send(quoteNumbers, new IResponseInfoCallback() {
                @Override
                public void callback(Response response) {
                    QuoteResponse quoteResponse = (QuoteResponse) response;
                    try {
                        assertNotNull(quoteResponse.quoteItems);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    JSONObject uploadObj = new JSONObject();
                    try {
                        if(quoteResponse.quoteItems!=null){
                            for (int i=0;i<quoteResponse.quoteItems.size();i++){
                                QuoteItem list=quoteResponse.quoteItems.get(i);
                                JSONObject uploadObj_1 = new JSONObject();
                                // TODO fill uploadObj with QuoteResponse value
                                uploadObj_1.put("status", list.status);
                                uploadObj_1.put("id", list.id);
                                uploadObj_1.put("name", list.name);
                                uploadObj_1.put("datetime", list.datetime);
                                uploadObj_1.put("market", list.market);
                                uploadObj_1.put("subtype", list.subtype);
                                uploadObj_1.put("lastPrice", list.lastPrice);
                                uploadObj_1.put("highPrice", list.highPrice);
                                uploadObj_1.put("lowPrice", list.lowPrice);
                                uploadObj_1.put("openPrice", list.openPrice);
                                uploadObj_1.put("preClosePrice", list.preClosePrice);
//                            uploadObj_1.put("changeRate", list.upDownFlag+list.changeRate);//ios注意
                                if ("+".equals(list.upDownFlag)||"-".equals(list.upDownFlag)){
                                    uploadObj_1.put("changeRate",list.upDownFlag+list.changeRate);//加涨跌符号
                                }else {
                                    uploadObj_1.put("changeRate",list.changeRate);
                                }
                                uploadObj_1.put("volume", list.volume);
                                uploadObj_1.put("nowVolume", list.nowVolume);
                                uploadObj_1.put("turnoverRate", list.turnoverRate);
                                uploadObj_1.put("upDownLimitType", list.upDownLimitType);//ios注意
                                uploadObj_1.put("limitUP", list.limitUP);
                                uploadObj_1.put("limitDown", list.limitDown);
                                uploadObj_1.put("averageValue", list.averageValue);//ios无
                                uploadObj_1.put("change", list.change);
                                uploadObj_1.put("amount", list.amount);
                                uploadObj_1.put("volumeRatio", list.volumeRatio);
                                uploadObj_1.put("buyPrice", list.buyPrice);
                                uploadObj_1.put("sellPrice", list.sellPrice);
                                uploadObj_1.put("buyVolume", list.buyVolume);
                                uploadObj_1.put("sellVolume", list.sellVolume);
                                uploadObj_1.put("totalValue", list.totalValue);
                                uploadObj_1.put("HKTotalValue", list.HKTotalValue);
                                uploadObj_1.put("flowValue", list.flowValue);
                                uploadObj_1.put("netAsset", list.netAsset);
                                uploadObj_1.put("pe", list.pe);
                                uploadObj_1.put("pe2", list.pe2);
                                uploadObj_1.put("pb", list.pb);
                                uploadObj_1.put("capitalization", list.capitalization);
                                uploadObj_1.put("circulatingShares", list.circulatingShares);
                                List<String> buyPrices=new ArrayList<>();
                                if (list.buyPrices!=null&&list.buyPrices.size()>0){
                                    for (int j=0;j<list.buyPrices.size();j++){
                                        buyPrices.add(list.buyPrices.get(j));
                                    }
                                    uploadObj_1.put("bidpx1", list.buyPrices.get(0));
                                    uploadObj_1.put("buyPrices",new JSONArray(buyPrices));
                                }else {
                                    uploadObj_1.put("bidpx1", "");
                                    uploadObj_1.put("buyPrices",list.buyPrices);
                                }

                                List<String> buySingleVolumes=new ArrayList<>();
                                if (list.buySingleVolumes!=null&&list.buySingleVolumes.size()>0){
                                    for (int j=0;j<list.buySingleVolumes.size();j++){
                                        buySingleVolumes.add(list.buySingleVolumes.get(j));
                                    }
                                    uploadObj_1.put("buySingleVolumes",new JSONArray(buySingleVolumes));
                                }else {
                                    uploadObj_1.put("buySingleVolumes",list.buySingleVolumes);
                                }

                                List<String> buyVolumes=new ArrayList<>();
                                if (list.buyVolumes!=null&&list.buyVolumes.size()>0){
                                    for (int j=0;j<list.buyVolumes.size();j++){
                                        buyVolumes.add(list.buyVolumes.get(j));
                                    }
                                    uploadObj_1.put("bidvol1", list.buyVolumes.get(0));
                                    uploadObj_1.put("buyVolumes",new JSONArray(buyVolumes));
                                }else {
                                    uploadObj_1.put("bidvol1", "");
                                    uploadObj_1.put("buyVolumes",list.buyVolumes);
                                }

                                List<String> sellPrices=new ArrayList<>();
                                if (list.sellPrices!=null&&list.sellPrices.size()>0){
                                    for (int j=0;j<list.sellPrices.size();j++){
                                        sellPrices.add(list.sellPrices.get(j));
                                    }
                                    uploadObj_1.put("askpx1", list.sellPrices.get(0));
                                    uploadObj_1.put("sellPrices",new JSONArray(sellPrices));
                                }else {
                                    uploadObj_1.put("askpx1", "");
                                    uploadObj_1.put("sellPrices",list.sellPrices);
                                }

                                List<String> sellSingleVolumes=new ArrayList<>();
                                if (list.sellSingleVolumes!=null&&list.sellSingleVolumes.size()>0){
                                    for (int j=0;j<list.sellSingleVolumes.size();j++){
                                        sellSingleVolumes.add(list.sellSingleVolumes.get(j));
                                    }
                                    uploadObj_1.put("sellSingleVolumes",new JSONArray(sellSingleVolumes));
                                }else {
                                    uploadObj_1.put("sellSingleVolumes",list.sellSingleVolumes);
                                }

                                List<String> sellVolumes=new ArrayList<>();
                                if (list.sellVolumes!=null&&list.sellVolumes.size()>0){
                                    for (int j=0;j<list.sellVolumes.size();j++){
                                        sellVolumes.add(list.sellVolumes.get(j));
                                    }
                                    uploadObj_1.put("askvol1", list.sellVolumes.get(0));
                                    uploadObj_1.put("sellVolumes",new JSONArray(sellVolumes));
                                }else {
                                    uploadObj_1.put("askvol1", "");
                                    uploadObj_1.put("sellVolumes",list.sellVolumes);
                                }

                                uploadObj_1.put("amplitudeRate", list.amplitudeRate);
                                uploadObj_1.put("receipts", list.receipts);

                                uploadObj_1.put("optionType", list.optionType);
                                uploadObj_1.put("contractID", list.contractID);
                                uploadObj_1.put("objectID", list.objectID);
                                uploadObj_1.put("stockSymble", list.stockSymble);
                                uploadObj_1.put("stockType", list.stockType);
                                uploadObj_1.put("stockUnit", list.stockUnit);
                                uploadObj_1.put("exePrice", list.exePrice);
                                uploadObj_1.put("startDate", list.startDate);
                                uploadObj_1.put("endDate", list.endDate);
                                uploadObj_1.put("exeDate", list.exeDate);
                                uploadObj_1.put("delDate", list.delDate);
                                uploadObj_1.put("expDate", list.expDate);
                                uploadObj_1.put("version", list.version);
                                uploadObj_1.put("presetPrice", list.presetPrice);
                                uploadObj_1.put("stockClose", list.stockClose);
                                uploadObj_1.put("stockLast", list.stockLast);
                                uploadObj_1.put("isLimit", list.isLimit);
                                uploadObj_1.put("inValue", list.inValue);
                                uploadObj_1.put("timeValue", list.timeValue);
                                uploadObj_1.put("preInterest", list.preInterest);
                                uploadObj_1.put("openInterest", list.openInterest);
                                uploadObj_1.put("remainDate", list.remainDate);
                                uploadObj_1.put("leverageRatio", list.leverageRatio);
                                uploadObj_1.put("premiumRate", list.premiumRate);
                                uploadObj_1.put("impliedVolatility", list.impliedVolatility);
                                uploadObj_1.put("delta", list.delta);
                                uploadObj_1.put("gramma", list.gramma);
                                uploadObj_1.put("theta", list.theta);
                                uploadObj_1.put("rho", list.rho);
                                uploadObj_1.put("vega", list.vega);
                                uploadObj_1.put("realLeverage", list.realLeverage);
                                uploadObj_1.put("theoreticalPrice", list.theoreticalPrice);
                                //
                                uploadObj_1.put("exerciseWay", list.exerciseWay);
                                uploadObj_1.put("orderRatio", list.orderRatio);
                                uploadObj_1.put("hk_paramStatus", list.hk_paramStatus);//ios无
                                uploadObj_1.put("sumBuy", list.sumBuy);
                                uploadObj_1.put("sumSell", list.sumSell);
                                uploadObj_1.put("averageBuy", list.averageBuy);
                                uploadObj_1.put("averageSell", list.averageSell);
//                        uploadObj_1.put("upDownFlag", list.upDownFlag);//注意一下IOS android
                                uploadObj_1.put("zh", list.zh);
                                uploadObj_1.put("hh", list.hh);
                                uploadObj_1.put("st", list.st);
                                uploadObj_1.put("bu", list.bu);
                                uploadObj_1.put("su", list.su);
                                uploadObj_1.put("hs", list.hs);
                                uploadObj_1.put("ac", list.ac);
                                uploadObj_1.put("qf", list.qf);//ios无
                                uploadObj_1.put("qc", list.qc);//ios无
                                uploadObj_1.put("ah", list.ah);
                                uploadObj_1.put("VCMFlag", list.VCMFlag);
                                uploadObj_1.put("CASFlag", list.CASFlag);
                                uploadObj_1.put("rp", list.rp);
                                uploadObj_1.put("cd", list.cd);
                                uploadObj_1.put("hg", list.hg);
                                uploadObj_1.put("sg", list.sg);
                                uploadObj_1.put("fx", list.fx);
                                uploadObj_1.put("ts", list.ts);
                                uploadObj_1.put("add_option_avg_price", list.add_option_avg_price);
                                uploadObj_1.put("add_option_avg_pb", list.add_option_avg_pb);
                                uploadObj_1.put("add_option_avg_close", list.add_option_avg_close);
                                uploadObj_1.put("hk_volum_for_every_hand", list.hk_volum_for_every_hand);
                                //ios无
                                uploadObj_1.put("buy_cancel_count", list.buy_cancel_count);
                                uploadObj_1.put("buy_cancel_num", list.buy_cancel_num);
                                uploadObj_1.put("buy_cancel_amount", list.buy_cancel_amount);
                                uploadObj_1.put("sell_cancel_count", list.sell_cancel_count);
                                uploadObj_1.put("sell_cancel_num", list.sell_cancel_num);
                                uploadObj_1.put("sell_cancel_amount", list.sell_cancel_amount);
                                uploadObj_1.put("tradingDay", list.tradingDay);
                                uploadObj_1.put("settlementID", list.settlementID);
                                uploadObj_1.put("settlementGroupID", list.settlementGroupID);
                                uploadObj_1.put("preSettlement", list.preSettlement);
                                uploadObj_1.put("position_chg", list.position_chg);
                                uploadObj_1.put("close", list.close);
                                uploadObj_1.put("settlement", list.settlement);
                                uploadObj_1.put("preDelta", list.preDelta);
                                uploadObj_1.put("currDelta", list.currDelta);
                                uploadObj_1.put("updateMillisec", list.updateMillisec);
                                uploadObj_1.put("entrustDiff", list.entrustDiff);
                                uploadObj_1.put("posDiff", list.posDiff);
                                uploadObj_1.put("currDiff", list.currDiff);
                                uploadObj_1.put("underlyingType", list.underlyingType);
                                uploadObj_1.put("underlyingLastPx", list.underlyingLastPx);
                                uploadObj_1.put("underlyingPreClose", list.underlyingPreClose);
                                uploadObj_1.put("underlyingchg", list.underlyingchg);
                                uploadObj_1.put("underlyingSymbol", list.underlyingSymbol);
                                uploadObj_1.put("deliveryDay", list.deliveryDay);
                                uploadObj_1.put("riskFreeInterestRate", list.riskFreeInterestRate);
                                uploadObj_1.put("intersectionNum", list.intersectionNum);
                                uploadObj_1.put("change1", list.change1);
                                uploadObj_1.put("totalBid", list.totalBid);
                                uploadObj_1.put("totalAsk", list.totalAsk);
                                //
                                uploadObj_1.put("IOPV", list.IOPV);
                                uploadObj_1.put("preIOPV", list.preIOPV);
                                uploadObj_1.put("stateOfTransfer", list.stateOfTransfer);
                                uploadObj_1.put("typeOfTransfer", list.typeOfTransfer);
                                uploadObj_1.put("exRighitDividend", list.exRighitDividend);
                                uploadObj_1.put("securityLevel", list.securityLevel);
                                uploadObj_1.put("rpd", list.rpd);
                                uploadObj_1.put("cdd", list.cdd);
                                //ios无
                                uploadObj_1.put("change2", list.change2);
                                uploadObj_1.put("earningsPerShare", list.earningsPerShare);
                                uploadObj_1.put("earningsPerShareReportingPeriod", list.earningsPerShareReportingPeriod);
                                //
                                uploadObj_1.put("hkTExchangeFlag", list.hkTExchangeFlag);//注意ios
                                uploadObj_1.put("vote", list.vote);//注意ios
                                uploadObj_1.put("upf", list.upf);//注意ios
                                uploadObj_1.put("DRCurrentShare", list.DRCurrentShare);
                                uploadObj_1.put("DRPreviousClosingShare", list.DRPreviousClosingShare);
                                uploadObj_1.put("DRConversionBase", list.DRConversionBase);
                                uploadObj_1.put("DRDepositoryInstitutionCode", list.DRDepositoryInstitutionCode);
                                uploadObj_1.put("DRDepositoryInstitutionName", list.DRDepositoryInstitutionName);
                                uploadObj_1.put("DRSubjectClosingReferencePrice", list.DRSubjectClosingReferencePrice);
                                uploadObj_1.put("DR", list.DR);
                                uploadObj_1.put("GDR", list.GDR);
                                uploadObj_1.put("DRStockCode", list.DRStockCode);
                                uploadObj_1.put("DRStockName", list.DRStockName);
                                uploadObj_1.put("DRSecuritiesConversionBase", list.DRSecuritiesConversionBase);
                                uploadObj_1.put("DRListingDate", list.DRListingDate);
                                uploadObj_1.put("DRFlowStartDate", list.DRFlowStartDate);
                                uploadObj_1.put("DRFlowEndDate", list.DRFlowEndDate);
                                uploadObj_1.put("changeBP", list.changeBP);
                                uploadObj_1.put("subscribeUpperLimit", list.subscribeUpperLimit);
                                uploadObj_1.put("subscribeLowerLimit", list.subscribeLowerLimit);
                                uploadObj_1.put("afterHoursVolume", list.afterHoursVolume);
                                uploadObj_1.put("afterHoursAmount", list.afterHoursAmount);
                                uploadObj_1.put("afterHoursTransactionNumber", list.afterHoursTransactionNumber);
                                uploadObj_1.put("afterHoursWithdrawBuyCount", list.afterHoursWithdrawBuyCount);
                                uploadObj_1.put("afterHoursWithdrawBuyVolume", list.afterHoursWithdrawBuyVolume);
                                uploadObj_1.put("afterHoursWithdrawSellCount", list.afterHoursWithdrawSellCount);
                                uploadObj_1.put("afterHoursWithdrawSellVolume", list.afterHoursWithdrawSellVolume);
                                uploadObj_1.put("afterHoursBuyVolume", list.afterHoursBuyVolume);
                                uploadObj_1.put("afterHoursSellVolume", list.afterHoursSellVolume);
                                uploadObj_1.put("issuedCapital", list.issuedCapital);
                                uploadObj_1.put("limitPriceUpperLimit", list.limitPriceUpperLimit);
                                uploadObj_1.put("limitPriceLowerLimit", list.limitPriceLowerLimit);
                                uploadObj_1.put("longName", list.longName);
                                //板块指数
                                uploadObj_1.put("blockChg", list.blockChg);
                                uploadObj_1.put("averageChg", list.averageChg);
                                uploadObj_1.put("indexChg5", list.indexChg5);
                                uploadObj_1.put("indexChg10", list.indexChg10);
//                                //3.3.0.002新增字段
//                                uploadObj_1.put("monthChangeRate", list.monthChangeRate);
//                                uploadObj_1.put("yearChangeRate", list.yearChangeRate);
//                                uploadObj_1.put("recentMonthChangeRate", list.recentMonthChangeRate);
//                                uploadObj_1.put("recentYearChangeRate", list.recentYearChangeRate);
                                //3.1.7.003新增字段
                                uploadObj_1.put("securityStatus", list.securityStatus == null ? "-" : list.securityStatus);
                                uploadObj_1.put("buyQtyUpperLimit", list.buyQtyUpperLimit == null ? "-" : list.buyQtyUpperLimit);
                                uploadObj_1.put("sellQtyUpperLimit", list.sellQtyUpperLimit == null ? "-" : list.sellQtyUpperLimit);
                                uploadObj_1.put("marketBuyQtyUpperLimit", list.marketBuyQtyUpperLimit == null ? "-" : list.marketBuyQtyUpperLimit);
                                uploadObj_1.put("marketSellQtyUpperLimit", list.marketSellQtyUpperLimit == null ? "-" : list.marketSellQtyUpperLimit);
                                uploadObj_1.put("reg", list.reg == null ? "-" : list.reg);
                                uploadObj_1.put("vie", list.vie == null ? "-" : list.vie);
                                uploadObj_1.put("mf", list.mf == null ? "-" : list.mf);
                                uploadObj_1.put("rslf", list.rslf == null ? "-" : list.rslf);
                                uploadObj_1.put("mmf", list.mmf == null ? "-" : list.mmf);
                                uploadObj_1.put("buyAuctionRange", list.buyAuctionRange == null ? "-" : ("["+list.buyAuctionRange[0]+","+list.buyAuctionRange[1])+"]");
                                uploadObj_1.put("sellAuctionRange", list.sellAuctionRange == null ? "-" : ("["+list.sellAuctionRange[0]+","+list.sellAuctionRange[1])+"]");
                                uploadObj_1.put("afterHoursBuyQtyUpperLimit", list.afterHoursBuyQtyUpperLimit == null ? "-" : list.afterHoursBuyQtyUpperLimit);
                                uploadObj_1.put("afterHoursBuyQtyUpperLimit", list.afterHoursBuyQtyUpperLimit == null ? "-" : list.afterHoursBuyQtyUpperLimit);
//                            //增值指标
//                            if (quoteResponse.addValueModel!=null){
//                                ArrayList<AddValueModel> addValueModels=quoteResponse.addValueModel;
//                                for (AddValueModel item : addValueModels) {
//                                    if (item.code.equals(list.id)){
//                                        JSONObject uploadObj_2 = new JSONObject();
//                                        uploadObj_2.put("code",item.code);
//                                        uploadObj_2.put("date",item.date);
//                                        uploadObj_2.put("time",item.time);
//                                        uploadObj_2.put("ultraLargeBuyVolume",item.ultraLargeBuyVolume);
//                                        uploadObj_2.put("ultraLargeSellVolume",item.ultraLargeSellVolume);
//                                        uploadObj_2.put("ultraLargeBuyAmount",item.ultraLargeBuyAmount);
//                                        uploadObj_2.put("ultraLargeSellAmount",item.ultraLargeSellAmount);
//                                        uploadObj_2.put("largeBuyVolume",item.largeBuyVolume);
//                                        uploadObj_2.put("largeSellVolume",item.largeSellVolume);
//                                        uploadObj_2.put("largeBuyAmount",item.largeBuyAmount);
//                                        uploadObj_2.put("largeSellAmount",item.largeSellAmount);
//                                        uploadObj_2.put("mediumBuyVolume",item.mediumBuyVolume);
//                                        uploadObj_2.put("mediumSellVolume",item.mediumSellVolume);
//                                        uploadObj_2.put("mediumBuyAmount",item.mediumBuyAmount);
//                                        uploadObj_2.put("mediumSellAmount",item.mediumSellAmount);
//                                        uploadObj_2.put("smallBuyVolume",item.smallBuyVolume);
//                                        uploadObj_2.put("smallSellVolume",item.smallSellVolume);
//                                        uploadObj_2.put("smallBuyAmount",item.smallBuyAmount);
//                                        uploadObj_2.put("smallSellAmount",item.smallSellAmount);
//                                        uploadObj_2.put("ultraLargeNetInflow",item.ultraLargeNetInflow);
//                                        uploadObj_2.put("largeNetInflow",item.largeNetInflow);
//                                        uploadObj_2.put("netCapitalInflow",item.netCapitalInflow);
//                                        uploadObj_2.put("mediumNetInflow",item.mediumNetInflow);
//                                        uploadObj_2.put("smallNetInflow",item.smallNetInflow);
//
//                                        List<String> fundsInflows=new ArrayList<>();
//                                        if (item.fundsInflows!=null&&item.fundsInflows.length>0){
//                                            for (int j=0;j<item.fundsInflows.length;j++){
//                                                fundsInflows.add(item.fundsInflows[j]);
//                                            }
//                                            uploadObj_2.put("fundsInflows",new JSONArray(fundsInflows));
//                                        }else {
//                                            uploadObj_2.put("fundsInflows",item.fundsInflows);
//                                        }
//
//                                        List<String> fundsOutflows=new ArrayList<>();
//                                        if (item.fundsOutflows!=null&&item.fundsOutflows.length>0){
//                                            for (int j=0;j<item.fundsOutflows.length;j++){
//                                                fundsOutflows.add(item.fundsOutflows[j]);
//                                            }
//                                            uploadObj_2.put("fundsOutflows",new JSONArray(fundsOutflows));
//                                        }else {
//                                            uploadObj_2.put("fundsOutflows",item.fundsOutflows);
//                                        }
//
//                                        uploadObj_2.put("ultraLargeDiffer",item.ultraLargeDiffer);
//                                        uploadObj_2.put("largeDiffer",item.largeDiffer);
//                                        uploadObj_2.put("mediumDiffer",item.mediumDiffer);
//                                        uploadObj_2.put("smallDiffer",item.smallDiffer);
//                                        uploadObj_2.put("largeBuyDealCount",item.largeBuyDealCount);
//                                        uploadObj_2.put("largeSellDealCount",item.largeSellDealCount);
//                                        uploadObj_2.put("dealCountMovingAverage",item.dealCountMovingAverage);
//                                        uploadObj_2.put("buyCount",item.buyCount);
//                                        uploadObj_2.put("sellCount",item.sellCount);
//                                        uploadObj_2.put("BBD",item.BBD);
//                                        uploadObj_2.put("BBD5",item.BBD5);
//                                        uploadObj_2.put("BBD10",item.BBD10);
//                                        uploadObj_2.put("DDX",item.DDX);
//                                        uploadObj_2.put("DDX5",item.DDX5);
//                                        uploadObj_2.put("DDX10",item.DDX10);
//                                        uploadObj_2.put("DDY",item.DDY);
//                                        uploadObj_2.put("DDY5",item.DDY5);
//                                        uploadObj_2.put("DDY10",item.DDY10);
//                                        uploadObj_2.put("DDZ",item.DDZ);
//                                        uploadObj_2.put("RatioBS",item.RatioBS);
//
//                                        List<String> othersFundsInflows=new ArrayList<>();
//                                        if (item.othersFundsInflows!=null&&item.othersFundsInflows.length>0){
//                                            for (int j=0;j<item.othersFundsInflows.length;j++){
//                                                othersFundsInflows.add(item.othersFundsInflows[j]);
//                                            }
//                                            uploadObj_2.put("othersFundsInflows",new JSONArray(othersFundsInflows));
//                                        }else {
//                                            uploadObj_2.put("othersFundsInflows",item.othersFundsInflows);
//                                        }
//
//                                        List<String> othersFundsOutflows=new ArrayList<>();
//                                        if (item.othersFundsOutflows!=null&&item.othersFundsOutflows.length>0){
//                                            for (int j=0;j<item.othersFundsOutflows.length;j++){
//                                                othersFundsOutflows.add(item.othersFundsOutflows[j]);
//                                            }
//                                            uploadObj_2.put("othersFundsOutflows",new JSONArray(othersFundsOutflows));
//                                        }else {
//                                            uploadObj_2.put("othersFundsOutflows",item.othersFundsOutflows);
//                                        }
//
//                                        uploadObj_2.put("fiveMinutesChangeRate",item.fiveMinutesChangeRate);
//                                        uploadObj_2.put("largeOrderNumB",item.largeOrderNumB);
//                                        uploadObj_2.put("largeOrderNumS",item.largeOrderNumS);
//                                        uploadObj_2.put("bigOrderNumB",item.bigOrderNumB);
//                                        uploadObj_2.put("bigOrderNumS",item.bigOrderNumS);
//                                        uploadObj_2.put("midOrderNumB",item.midOrderNumB);
//                                        uploadObj_2.put("midOrderNumS",item.midOrderNumS);
//                                        uploadObj_2.put("smallOrderNumB",item.smallOrderNumB);
//                                        uploadObj_2.put("smallOrderNumS",item.smallOrderNumS);
//                                        uploadObj_2.put("mainforceMoneyNetInflow5",item.mainforceMoneyNetInflow5);
//                                        uploadObj_2.put("mainforceMoneyNetInflow10",item.mainforceMoneyNetInflow10);
//                                        uploadObj_2.put("mainforceMoneyNetInflow20",item.mainforceMoneyNetInflow20);
//                                        uploadObj_2.put("ratioMainforceMoneyNetInflow5",item.ratioMainforceMoneyNetInflow5);
//                                        uploadObj_2.put("ratioMainforceMoneyNetInflow10",item.ratioMainforceMoneyNetInflow10);
//                                        uploadObj_2.put("ratioMainforceMoneyNetInflow20",item.ratioMainforceMoneyNetInflow20);
//                                        uploadObj_1.put("addValue",uploadObj_2);
//                                    }
//                                }
//                            }
                                uploadObj.put(list.id,uploadObj_1);
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
//        }
        try {
            JSONObject resultObj = (JSONObject)result.get(timeout_ms, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
        } catch (Exception e) {
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }
    }
}