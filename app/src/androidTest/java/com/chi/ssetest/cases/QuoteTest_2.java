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

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//证券行情列表 方法二
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.QUOTETEST_2)
public class QuoteTest_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.QUOTETEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("QuoteTest_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("QuoteTest_2", "requestWork");
        // TODO get custom args from param
        final String []quoteNumbers = rule.getParam().optString("CODES", "").split(",");
        final String[] INTS1 = rule.getParam().optString("INTS1","").split(";");
        final String[] INTS2 = rule.getParam().optString("INTS2", "").split(";");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        String[] IN1;
        String[] IN2;
        for (int i=0;i<INTS1.length;i++){
            IN1=INTS1[i].split(",");
            IN2=INTS2[i].split(",");
            int[] ints1 = new int[IN1.length];
            int[] ints2 = new int[IN2.length];
            if (INTS1[i].equals("null")){
                ints1=null;
            }else {
                for (int k=0;k<IN1.length;k++){
                    ints1[k]=Integer.parseInt(IN1[k]);
                }
            }
            if (INTS2[i].equals("null")){
                ints2=null;
            }else {
                for (int k=0;k<IN2.length;k++){
                    ints2[k]=Integer.parseInt(IN2[k]);
                }
            }
            QuoteRequest request = new QuoteRequest();
            request.send(quoteNumbers,ints1,ints2, new IResponseInfoCallback() {
                @Override
                public void callback(Response response) {
                    QuoteResponse quoteResponse = (QuoteResponse) response;
                    assertNotNull(quoteResponse.quoteItems);
                    List<JSONObject> items = new ArrayList<>();
                    JSONObject uploadObj = new JSONObject();
                    for (int i=0;i<quoteResponse.quoteItems.size();i++){
                        QuoteItem list=quoteResponse.quoteItems.get(i);
                        JSONObject uploadObj_1 = new JSONObject();
                        // TODO fill uploadObj with QuoteResponse value
                        try {
                            uploadObj_1.put("status", list.status);
                            uploadObj_1.put("id", list.id);
                            uploadObj_1.put("name", list.name);
                            uploadObj_1.put("datetime", list.datetime);
                            uploadObj_1.put("pinyin", list.pinyin);//ios无
                            uploadObj_1.put("market", list.market);
                            uploadObj_1.put("subtype", list.subtype);
                            uploadObj_1.put("lastPrice", list.lastPrice);
                            uploadObj_1.put("highPrice", list.highPrice);
                            uploadObj_1.put("lowPrice", list.lowPrice);
                            uploadObj_1.put("openPrice", list.openPrice);
                            uploadObj_1.put("preClosePrice", list.preClosePrice);
                            uploadObj_1.put("changeRate", list.upDownFlag+list.changeRate);//ios注意
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
                            List<JSONObject> buyPrices=new ArrayList<>();
                            if (list.buyPrices!=null&&list.buyPrices.size()>0){
                                for (int j=0;j<list.buyPrices.size();j++){
                                    JSONObject uploadObj_1_1 = new JSONObject();
                                    uploadObj_1_1.put("buyPrices"+j,list.buyPrices.get(j));
                                    buyPrices.add(uploadObj_1_1);
                                }
                                uploadObj_1.put("bidpx1", list.buyPrices.get(0));
                                uploadObj_1.put("buyPrices",new JSONArray(buyPrices));
                            }else {
                                uploadObj_1.put("bidpx1", "");
                                uploadObj_1.put("buyPrices",list.buyPrices);
                            }

                            List<JSONObject> buySingleVolumes=new ArrayList<>();
                            if (list.buySingleVolumes!=null&&list.buySingleVolumes.size()>0){
                                for (int j=0;j<list.buySingleVolumes.size();j++){
                                    JSONObject uploadObj_1_1 = new JSONObject();
                                    uploadObj_1_1.put("buySingleVolumes"+j,list.buySingleVolumes.get(j));
                                    buySingleVolumes.add(uploadObj_1_1);
                                }
                                uploadObj_1.put("buySingleVolumes",new JSONArray(buySingleVolumes));
                            }else {
                                uploadObj_1.put("buySingleVolumes",list.buySingleVolumes);
                            }

                            List<JSONObject> buyVolumes=new ArrayList<>();
                            if (list.buyVolumes!=null&&list.buyVolumes.size()>0){
                                for (int j=0;j<list.buyVolumes.size();j++){
                                    JSONObject uploadObj_1_1 = new JSONObject();
                                    uploadObj_1_1.put("buyVolumes"+j,list.buyVolumes.get(j));
                                    buyVolumes.add(uploadObj_1_1);
                                }
                                uploadObj_1.put("bidvol1", list.buyVolumes.get(0));
                                uploadObj_1.put("buyVolumes",new JSONArray(buyVolumes));
                            }else {
                                uploadObj_1.put("bidvol1", "");
                                uploadObj_1.put("buyVolumes",list.buyVolumes);
                            }

                            List<JSONObject> sellPrices=new ArrayList<>();
                            if (list.sellPrices!=null&&list.sellPrices.size()>0){
                                for (int j=0;j<list.sellPrices.size();j++){
                                    JSONObject uploadObj_1_1 = new JSONObject();
                                    uploadObj_1_1.put("sellPrices"+j,list.sellPrices.get(j));
                                    sellPrices.add(uploadObj_1_1);
                                }
                                uploadObj_1.put("askpx1", list.sellPrices.get(0));
                                uploadObj_1.put("sellPrices",new JSONArray(sellPrices));
                            }else {
                                uploadObj_1.put("askpx1", "");
                                uploadObj_1.put("sellPrices",list.sellPrices);
                            }

                            List<JSONObject> sellSingleVolumes=new ArrayList<>();
                            if (list.sellSingleVolumes!=null&&list.sellSingleVolumes.size()>0){
                                for (int j=0;j<list.sellSingleVolumes.size();j++){
                                    JSONObject uploadObj_1_1 = new JSONObject();
                                    uploadObj_1_1.put("sellSingleVolumes"+j,list.sellSingleVolumes.get(j));
                                    sellSingleVolumes.add(uploadObj_1_1);
                                }
                                uploadObj_1.put("sellSingleVolumes",new JSONArray(sellSingleVolumes));
                            }else {
                                uploadObj_1.put("sellSingleVolumes",list.sellSingleVolumes);
                            }

                            List<JSONObject> sellVolumes=new ArrayList<>();
                            if (list.sellVolumes!=null&&list.sellVolumes.size()>0){
                                for (int j=0;j<list.sellVolumes.size();j++){
                                    JSONObject uploadObj_1_1 = new JSONObject();
                                    uploadObj_1_1.put("sellVolumes"+j,list.sellVolumes.get(j));
                                    sellVolumes.add(uploadObj_1_1);
                                }
                                uploadObj_1.put("askvol1", list.sellVolumes.get(0));
                                uploadObj_1.put("sellVolumes",new JSONArray(sellVolumes));
                            }else {
                                uploadObj_1.put("askvol1", "");
                                uploadObj_1.put("sellVolumes",list.sellVolumes);
                            }

                            uploadObj_1.put("amplitudeRate", list.amplitudeRate);
                            uploadObj_1.put("receipts", list.receipts);
                            //ios无
                            List<JSONObject> tradeTick=new ArrayList<>();
                            if (list.tradeTick!=null&&list.tradeTick.length>0){
                                for (int j=0;j<list.tradeTick.length;j++){
                                    for (int k=0;k<list.tradeTick[j].length;k++){
                                        JSONObject uploadObj_1_1 = new JSONObject();
                                        uploadObj_1_1.put("type",list.tradeTick[j][0]);
                                        uploadObj_1_1.put("time",list.tradeTick[j][1]);
                                        uploadObj_1_1.put("tradeVolume",list.tradeTick[j][2]);
                                        uploadObj_1_1.put("tradePrice",list.tradeTick[j][3]);
                                        tradeTick.add(uploadObj_1_1);
                                    }
                                }
                                uploadObj_1.put("tradeTick",new JSONArray(tradeTick));
                            }else {
                                uploadObj_1.put("tradeTick",list.tradeTick);
                            }

                            uploadObj_1.put("upCount", list.upCount);
                            uploadObj_1.put("sameCount", list.sameCount);
                            uploadObj_1.put("downCount", list.downCount);
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
                            uploadObj_1.put("setPrice", list.setPrice);
                            uploadObj_1.put("stockClose", list.stockClose);
                            uploadObj_1.put("stockLast", list.stockLast);
                            uploadObj_1.put("isLimit", list.isLimit);
                            uploadObj_1.put("marginUnit", list.marginUnit);
                            uploadObj_1.put("roundLot", list.roundLot);
                            uploadObj_1.put("inValue", list.inValue);
                            uploadObj_1.put("timeValue", list.timeValue);
                            uploadObj_1.put("preInterest", list.preInterest);
                            uploadObj_1.put("openInterest", list.openInterest);
                            uploadObj_1.put("tradePhase", list.tradePhase);
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
                            uploadObj_1.put("fundTyp", list.fundType);
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
                            uploadObj_1.put("pe2_unit", list.pe2_unit);//ios无
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
                            uploadObj_1.put("masukura", list.masukura);
                            //
                            uploadObj_1.put("hkTExchangeFlag", list.hkTExchangeFlag);//注意ios
                            uploadObj_1.put("zgConvertCodes", list.zgConvertCodes); //ios无
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
                            items.add(uploadObj_1);
//                        uploadObj_1.put("minVolume", list.minVolume);//android没有
//                        uploadObj_1.put("index", list.index);
//                        uploadObj_1.put("hongKong", list.hongKong);
//                        uploadObj_1.put("bond", list.bond);
//                        uploadObj_1.put("fund", list.fund);
//                        uploadObj_1.put("wrnt", list.wrnt);
//                        uploadObj_1.put("option", list.option);
//                        uploadObj_1.put("addValueItem", list.addValueItem);//有单独的接口
                        } catch (JSONException e) {
                            result.completeExceptionally(e);
                        }
                    }
                    try {
                        //把数组存储到JSON
                        uploadObj.put("items", new JSONArray(items));
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
//                    for (QuoteItem item : quoteResponse.quoteItems) {
//                        Log.d("StockUnittest", item.toString());
//                    }
                    result.complete(uploadObj);
                }
                @Override
                public void exception(ErrorInfo errorInfo) {
                    result.completeExceptionally(new Exception(errorInfo.toString()));
                }
            });
        }
        try {
            JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
