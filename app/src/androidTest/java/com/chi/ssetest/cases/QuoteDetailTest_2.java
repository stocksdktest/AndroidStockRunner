package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
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

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("QuoteDetailTest_2", "requestWork");
        // TODO get custom args from param

        final String quoteNumbers = rule.getParam().optString("CODES", "");
        final String count = rule.getParam().optString("COUNTS", "");
        final String[] INTS1 = rule.getParam().optString("INTS1","").split(",");
        final String[] INTS2 = rule.getParam().optString("INTS2", "").split(",");
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
                    result.completeExceptionally(e);
                }
                QuoteItem list=quoteResponse.quoteItems.get(0);
                JSONObject uploadObj = new JSONObject();
                // TODO fill uploadObj with QuoteResponse value
                try {
                    uploadObj.put("status", list.status);
                    uploadObj.put("id", list.id);
                    uploadObj.put("name", list.name);
                    uploadObj.put("datetime", list.datetime);
//                        uploadObj.put("pinyin", list.pinyin);//ios无
                    uploadObj.put("market", list.market);
                    uploadObj.put("subtype", list.subtype);
                    uploadObj.put("lastPrice", list.lastPrice);
                    uploadObj.put("highPrice", list.highPrice);
                    uploadObj.put("lowPrice", list.lowPrice);
                    uploadObj.put("openPrice", list.openPrice);
                    uploadObj.put("preClosePrice", list.preClosePrice);
                    uploadObj.put("changeRate", list.upDownFlag+list.changeRate);//ios注意
                    uploadObj.put("volume", list.volume);
                    uploadObj.put("nowVolume", list.nowVolume);
                    uploadObj.put("turnoverRate", list.turnoverRate);
                    uploadObj.put("upDownLimitType", list.upDownLimitType);//ios注意
                    uploadObj.put("limitUP", list.limitUP);
                    uploadObj.put("limitDown", list.limitDown);
                    uploadObj.put("averageValue", list.averageValue);//ios无
                    uploadObj.put("change", list.change);
                    uploadObj.put("amount", list.amount);
                    uploadObj.put("volumeRatio", list.volumeRatio);
                    uploadObj.put("buyPrice", list.buyPrice);
                    uploadObj.put("sellPrice", list.sellPrice);
                    uploadObj.put("buyVolume", list.buyVolume);
                    uploadObj.put("sellVolume", list.sellVolume);
                    uploadObj.put("totalValue", list.totalValue);
                    uploadObj.put("HKTotalValue", list.HKTotalValue);
                    uploadObj.put("flowValue", list.flowValue);
                    uploadObj.put("netAsset", list.netAsset);
                    uploadObj.put("pe", list.pe);
                    uploadObj.put("pe2", list.pe2);
                    uploadObj.put("pb", list.pb);
                    uploadObj.put("capitalization", list.capitalization);
                    uploadObj.put("circulatingShares", list.circulatingShares);
                    List<JSONObject> buyPrices=new ArrayList<>();
                    if (list.buyPrices!=null&&list.buyPrices.size()>0){
                        for (int j=0;j<list.buyPrices.size();j++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("buyprices"+(j+1),list.buyPrices.get(j));
                            buyPrices.add(uploadObj_1);
                        }
                        uploadObj.put("bidpx1", list.buyPrices.get(0));
                        uploadObj.put("buyPrices",new JSONArray(buyPrices));
                    }else {
                        uploadObj.put("bidpx1", "");
                        uploadObj.put("buyPrices",list.buyPrices);
                    }

                    List<JSONObject> buySingleVolumes=new ArrayList<>();
                    if (list.buySingleVolumes!=null&&list.buySingleVolumes.size()>0){
                        for (int j=0;j<list.buySingleVolumes.size();j++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("buySingleVolumes"+(j+1),list.buySingleVolumes.get(j));
                            buySingleVolumes.add(uploadObj_1);
                        }
                        uploadObj.put("buySingleVolumes",new JSONArray(buySingleVolumes));
                    }else {
                        uploadObj.put("buySingleVolumes",list.buySingleVolumes);
                    }

                    List<JSONObject> buyVolumes=new ArrayList<>();
                    if (list.buyVolumes!=null&&list.buyVolumes.size()>0){
                        for (int j=0;j<list.buyVolumes.size();j++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("buyVolumes"+(j+1),list.buyVolumes.get(j));
                            buyVolumes.add(uploadObj_1);
                        }
                        uploadObj.put("bidvol1", list.buyVolumes.get(0));
                        uploadObj.put("buyVolumes",new JSONArray(buyVolumes));
                    }else {
                        uploadObj.put("bidvol1", "");
                        uploadObj.put("buyVolumes",list.buyVolumes);
                    }

                    List<JSONObject> sellPrices=new ArrayList<>();
                    if (list.sellPrices!=null&&list.sellPrices.size()>0){
                        for (int j=0;j<list.sellPrices.size();j++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("sellPrices"+(j+1),list.sellPrices.get(j));
                            sellPrices.add(uploadObj_1);
                        }
                        uploadObj.put("askpx1", list.sellPrices.get(0));
                        uploadObj.put("sellPrices",new JSONArray(sellPrices));
                    }else {
                        uploadObj.put("askpx1", "");
                        uploadObj.put("sellPrices",list.sellPrices);
                    }

                    List<JSONObject> sellSingleVolumes=new ArrayList<>();
                    if (list.sellSingleVolumes!=null&&list.sellSingleVolumes.size()>0){
                        for (int j=0;j<list.sellSingleVolumes.size();j++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("sellSingleVolumes"+(j+1),list.sellSingleVolumes.get(j));
                            sellSingleVolumes.add(uploadObj_1);
                        }
                        uploadObj.put("sellSingleVolumes",new JSONArray(sellSingleVolumes));
                    }else {
                        uploadObj.put("sellSingleVolumes",list.sellSingleVolumes);
                    }

                    List<JSONObject> sellVolumes=new ArrayList<>();
                    if (list.sellVolumes!=null&&list.sellVolumes.size()>0){
                        for (int j=0;j<list.sellVolumes.size();j++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("sellVolumes"+(j+1),list.sellVolumes.get(j));
                            sellVolumes.add(uploadObj_1);
                        }
                        uploadObj.put("askvol1", list.sellVolumes.get(0));
                        uploadObj.put("sellVolumes",new JSONArray(sellVolumes));
                    }else {
                        uploadObj.put("askvol1", "");
                        uploadObj.put("sellVolumes",list.sellVolumes);
                    }

                    uploadObj.put("amplitudeRate", list.amplitudeRate);
                    uploadObj.put("receipts", list.receipts);
                    //ios无
                    List<JSONObject> tradeTick=new ArrayList<>();
                    if (list.tradeTick!=null&&list.tradeTick.length>0){
                        for (int j=0;j<10;j++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("type",list.tradeTick[j][0]);
                            uploadObj_1.put("time",list.tradeTick[j][1]);
                            uploadObj_1.put("tradeVolume",list.tradeTick[j][2]);
                            uploadObj_1.put("tradePrice",list.tradeTick[j][3]);
                            tradeTick.add(uploadObj_1);
                        }
                        uploadObj.put("tradeTick",new JSONArray(tradeTick));
                    }else {
                        uploadObj.put("tradeTick",list.tradeTick);
                    }

                    uploadObj.put("upCount", list.upCount);
                    uploadObj.put("sameCount", list.sameCount);
                    uploadObj.put("downCount", list.downCount);
                    uploadObj.put("optionType", list.optionType);
                    uploadObj.put("contractID", list.contractID);
                    uploadObj.put("objectID", list.objectID);
                    uploadObj.put("stockSymble", list.stockSymble);
                    uploadObj.put("stockType", list.stockType);
                    uploadObj.put("stockUnit", list.stockUnit);
                    uploadObj.put("exePrice", list.exePrice);
                    uploadObj.put("startDate", list.startDate);
                    uploadObj.put("endDate", list.endDate);
                    uploadObj.put("exeDate", list.exeDate);
                    uploadObj.put("delDate", list.delDate);
                    uploadObj.put("expDate", list.expDate);
                    uploadObj.put("version", list.version);
                    uploadObj.put("presetPrice", list.presetPrice);
                    uploadObj.put("stockClose", list.stockClose);
                    uploadObj.put("stockLast", list.stockLast);
                    uploadObj.put("isLimit", list.isLimit);
                    uploadObj.put("inValue", list.inValue);
                    uploadObj.put("timeValue", list.timeValue);
                    uploadObj.put("preInterest", list.preInterest);
                    uploadObj.put("openInterest", list.openInterest);
                    uploadObj.put("remainDate", list.remainDate);
                    uploadObj.put("leverageRatio", list.leverageRatio);
                    uploadObj.put("premiumRate", list.premiumRate);
                    uploadObj.put("impliedVolatility", list.impliedVolatility);
                    uploadObj.put("delta", list.delta);
                    uploadObj.put("gramma", list.gramma);
                    uploadObj.put("theta", list.theta);
                    uploadObj.put("rho", list.rho);
                    uploadObj.put("vega", list.vega);
                    uploadObj.put("realLeverage", list.realLeverage);
                    uploadObj.put("theoreticalPrice", list.theoreticalPrice);
                    //
                    uploadObj.put("exerciseWay", list.exerciseWay);
                    uploadObj.put("orderRatio", list.orderRatio);
                    uploadObj.put("hk_paramStatus", list.hk_paramStatus);//ios无
                    uploadObj.put("fundType", list.fundType);
                    uploadObj.put("sumBuy", list.sumBuy);
                    uploadObj.put("sumSell", list.sumSell);
                    uploadObj.put("averageBuy", list.averageBuy);
                    uploadObj.put("averageSell", list.averageSell);
//                        uploadObj.put("upDownFlag", list.upDownFlag);//注意一下IOS android
                    uploadObj.put("zh", list.zh);
                    uploadObj.put("hh", list.hh);
                    uploadObj.put("st", list.st);
                    uploadObj.put("bu", list.bu);
                    uploadObj.put("su", list.su);
                    uploadObj.put("hs", list.hs);
                    uploadObj.put("ac", list.ac);
                    uploadObj.put("qf", list.qf);//ios无
                    uploadObj.put("qc", list.qc);//ios无
                    uploadObj.put("ah", list.ah);
                    uploadObj.put("VCMFlag", list.VCMFlag);
                    uploadObj.put("CASFlag", list.CASFlag);
                    uploadObj.put("rp", list.rp);
                    uploadObj.put("cd", list.cd);
                    uploadObj.put("hg", list.hg);
                    uploadObj.put("sg", list.sg);
                    uploadObj.put("fx", list.fx);
                    uploadObj.put("ts", list.ts);
                    uploadObj.put("add_option_avg_price", list.add_option_avg_price);
                    uploadObj.put("add_option_avg_pb", list.add_option_avg_pb);
                    uploadObj.put("add_option_avg_close", list.add_option_avg_close);

                    uploadObj.put("hk_volum_for_every_hand", list.hk_volum_for_every_hand);
                    //ios无
                    uploadObj.put("buy_cancel_count", list.buy_cancel_count);
                    uploadObj.put("buy_cancel_num", list.buy_cancel_num);
                    uploadObj.put("buy_cancel_amount", list.buy_cancel_amount);
                    uploadObj.put("sell_cancel_count", list.sell_cancel_count);
                    uploadObj.put("sell_cancel_num", list.sell_cancel_num);
                    uploadObj.put("sell_cancel_amount", list.sell_cancel_amount);
                    uploadObj.put("tradingDay", list.tradingDay);
                    uploadObj.put("settlementID", list.settlementID);
                    uploadObj.put("settlementGroupID", list.settlementGroupID);
                    uploadObj.put("preSettlement", list.preSettlement);
                    uploadObj.put("position_chg", list.position_chg);
                    uploadObj.put("close", list.close);
                    uploadObj.put("settlement", list.settlement);
                    uploadObj.put("preDelta", list.preDelta);
                    uploadObj.put("currDelta", list.currDelta);
                    uploadObj.put("updateMillisec", list.updateMillisec);
                    uploadObj.put("entrustDiff", list.entrustDiff);
                    uploadObj.put("posDiff", list.posDiff);
                    uploadObj.put("currDiff", list.currDiff);
                    uploadObj.put("underlyingType", list.underlyingType);
                    uploadObj.put("underlyingLastPx", list.underlyingLastPx);
                    uploadObj.put("underlyingPreClose", list.underlyingPreClose);
                    uploadObj.put("underlyingchg", list.underlyingchg);
                    uploadObj.put("underlyingSymbol", list.underlyingSymbol);
                    uploadObj.put("deliveryDay", list.deliveryDay);
                    uploadObj.put("riskFreeInterestRate", list.riskFreeInterestRate);
                    uploadObj.put("intersectionNum", list.intersectionNum);
                    uploadObj.put("change1", list.change1);
                    uploadObj.put("totalBid", list.totalBid);
                    uploadObj.put("totalAsk", list.totalAsk);
                    //
                    uploadObj.put("IOPV", list.IOPV);
                    uploadObj.put("preIOPV", list.preIOPV);
                    uploadObj.put("stateOfTransfer", list.stateOfTransfer);
                    uploadObj.put("typeOfTransfer", list.typeOfTransfer);
                    uploadObj.put("exRighitDividend", list.exRighitDividend);
                    uploadObj.put("securityLevel", list.securityLevel);
                    uploadObj.put("rpd", list.rpd);
                    uploadObj.put("cdd", list.cdd);
                    //ios无
                    uploadObj.put("change2", list.change2);
                    uploadObj.put("earningsPerShare", list.earningsPerShare);
                    uploadObj.put("earningsPerShareReportingPeriod", list.earningsPerShareReportingPeriod);
                    //
                    uploadObj.put("hkTExchangeFlag", list.hkTExchangeFlag);//注意ios
                    uploadObj.put("vote", list.vote);//注意ios
                    uploadObj.put("upf", list.upf);//注意ios
                    uploadObj.put("DRCurrentShare", list.DRCurrentShare);
                    uploadObj.put("DRPreviousClosingShare", list.DRPreviousClosingShare);
                    uploadObj.put("DRConversionBase", list.DRConversionBase);
                    uploadObj.put("DRDepositoryInstitutionCode", list.DRDepositoryInstitutionCode);
                    uploadObj.put("DRDepositoryInstitutionName", list.DRDepositoryInstitutionName);
                    uploadObj.put("DRSubjectClosingReferencePrice", list.DRSubjectClosingReferencePrice);
                    uploadObj.put("DR", list.DR);
                    uploadObj.put("GDR", list.GDR);
                    uploadObj.put("DRStockCode", list.DRStockCode);
                    uploadObj.put("DRStockName", list.DRStockName);
                    uploadObj.put("DRSecuritiesConversionBase", list.DRSecuritiesConversionBase);
                    uploadObj.put("DRListingDate", list.DRListingDate);
                    uploadObj.put("DRFlowStartDate", list.DRFlowStartDate);
                    uploadObj.put("DRFlowEndDate", list.DRFlowEndDate);
                    uploadObj.put("changeBP", list.changeBP);
                    uploadObj.put("subscribeUpperLimit", list.subscribeUpperLimit);
                    uploadObj.put("subscribeLowerLimit", list.subscribeLowerLimit);
                    uploadObj.put("afterHoursVolume", list.afterHoursVolume);
                    uploadObj.put("afterHoursAmount", list.afterHoursAmount);
                    uploadObj.put("afterHoursTransactionNumber", list.afterHoursTransactionNumber);
                    uploadObj.put("afterHoursWithdrawBuyCount", list.afterHoursWithdrawBuyCount);
                    uploadObj.put("afterHoursWithdrawBuyVolume", list.afterHoursWithdrawBuyVolume);
                    uploadObj.put("afterHoursWithdrawSellCount", list.afterHoursWithdrawSellCount);
                    uploadObj.put("afterHoursWithdrawSellVolume", list.afterHoursWithdrawSellVolume);
                    uploadObj.put("afterHoursBuyVolume", list.afterHoursBuyVolume);
                    uploadObj.put("afterHoursSellVolume", list.afterHoursSellVolume);
                    uploadObj.put("issuedCapital", list.issuedCapital);
                    uploadObj.put("limitPriceUpperLimit", list.limitPriceUpperLimit);
                    uploadObj.put("limitPriceLowerLimit", list.limitPriceLowerLimit);
                    uploadObj.put("longName", list.longName);
                } catch (JSONException e) {
                    result.completeExceptionally(e);
                }
//                    for (QuoteItem item : quoteResponse.quoteItems) {
//                        Log.d("StockUnittest", item.toString());
//                    }
                Log.d("data",uploadObj.toString());
                result.complete(uploadObj);
            }

            @Override
            public void exception(ErrorInfo errorInfo) {
               result.completeExceptionally(new Exception(errorInfo.toString()));
            }
        });
//        }
        try {
            JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
