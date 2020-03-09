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
import com.mitake.core.CateType;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.keys.quote.SortType;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CateSortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CateSortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.MorePriceResponse;
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
 *排序接口1
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.CATESORTINGTEST_1)
public class CateSortingTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.CATESORTINGTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("  CateSortingTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
   // SortType
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("  CateSortingTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CateType");
        final String quoteNumbers1 = rule.getParam().optString("param");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            CateSortingRequest request = new  CateSortingRequest();
            request.send(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<CateSortingResponse>() {
                @Override
                public void callback(CateSortingResponse cateSortingResponse) {
                    try {
                        assertNotNull(cateSortingResponse.list);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    ArrayList<QuoteItem> list=cateSortingResponse.list;
                    JSONObject uploadObj = new JSONObject();
                    try {
                        if (list!=null){
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
                                uploadObj_1.put("limitUP", list.get(i).limitUP);
                                uploadObj_1.put("limitDown", list.get(i).limitDown);
                                uploadObj_1.put("averageValue", list.get(i).averageValue);//ios无  500
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
//                                uploadObj_1.put("pb", list.get(i).pb);
                                uploadObj_1.put("capitalization", list.get(i).capitalization);
                                uploadObj_1.put("circulatingShares", list.get(i).circulatingShares);
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

                                uploadObj_1.put("amplitudeRate", list.get(i).amplitudeRate);
                                uploadObj_1.put("receipts", list.get(i).receipts);

                                uploadObj_1.put("upCount", list.get(i).upCount);
                                uploadObj_1.put("sameCount", list.get(i).sameCount);
                                uploadObj_1.put("downCount", list.get(i).downCount);
                                uploadObj_1.put("optionType", list.get(i).optionType);
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
                                uploadObj_1.put("preInterest", list.get(i).preInterest);
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
                                //
                                uploadObj_1.put("exerciseWay", list.get(i).exerciseWay);
                                uploadObj_1.put("orderRatio", list.get(i).orderRatio);
                                uploadObj_1.put("hk_paramStatus", list.get(i).hk_paramStatus);//ios无
                                uploadObj_1.put("fundType", list.get(i).fundType);
                                uploadObj_1.put("sumBuy", list.get(i).sumBuy);
                                uploadObj_1.put("sumSell", list.get(i).sumSell);
                                uploadObj_1.put("averageBuy", list.get(i).averageBuy);
                                uploadObj_1.put("averageSell", list.get(i).averageSell);
//                        uploadObj_1.put("upDownFlag", list.get(i).upDownFlag);//注意一下IOS android
                                uploadObj_1.put("zh", list.get(i).zh);
                                uploadObj_1.put("hh", list.get(i).hh);
                                uploadObj_1.put("st", list.get(i).st);
                                uploadObj_1.put("bu", list.get(i).bu);
                                uploadObj_1.put("su", list.get(i).su);
                                uploadObj_1.put("hs", list.get(i).hs);
                                uploadObj_1.put("ac", list.get(i).ac);
                                uploadObj_1.put("qf", list.get(i).qf);//ios无
                                uploadObj_1.put("qc", list.get(i).qc);//ios无
                                uploadObj_1.put("ah", list.get(i).ah);
                                uploadObj_1.put("VCMFlag", list.get(i).VCMFlag);
                                uploadObj_1.put("CASFlag", list.get(i).CASFlag);
                                uploadObj_1.put("rp", list.get(i).rp);
                                uploadObj_1.put("cd", list.get(i).cd);
                                uploadObj_1.put("hg", list.get(i).hg);
                                uploadObj_1.put("sg", list.get(i).sg);
                                uploadObj_1.put("fx", list.get(i).fx);
                                uploadObj_1.put("ts", list.get(i).ts);
                                uploadObj_1.put("add_option_avg_price", list.get(i).add_option_avg_price);
                                uploadObj_1.put("add_option_avg_pb", list.get(i).add_option_avg_pb);
                                uploadObj_1.put("add_option_avg_close", list.get(i).add_option_avg_close);
                                uploadObj_1.put("hk_volum_for_every_hand", list.get(i).hk_volum_for_every_hand);
                                //ios无
                                uploadObj_1.put("buy_cancel_count", list.get(i).buy_cancel_count);
                                uploadObj_1.put("buy_cancel_num", list.get(i).buy_cancel_num);
                                uploadObj_1.put("buy_cancel_amount", list.get(i).buy_cancel_amount);
                                uploadObj_1.put("sell_cancel_count", list.get(i).sell_cancel_count);
                                uploadObj_1.put("sell_cancel_num", list.get(i).sell_cancel_num);
                                uploadObj_1.put("sell_cancel_amount", list.get(i).sell_cancel_amount);
                                uploadObj_1.put("tradingDay", list.get(i).tradingDay);
                                uploadObj_1.put("settlementID", list.get(i).settlementID);
                                uploadObj_1.put("settlementGroupID", list.get(i).settlementGroupID);
                                uploadObj_1.put("preSettlement", list.get(i).preSettlement);
                                uploadObj_1.put("position_chg", list.get(i).position_chg);
                                uploadObj_1.put("close", list.get(i).close);
                                uploadObj_1.put("settlement", list.get(i).settlement);
                                uploadObj_1.put("preDelta", list.get(i).preDelta);
                                uploadObj_1.put("currDelta", list.get(i).currDelta);
                                uploadObj_1.put("updateMillisec", list.get(i).updateMillisec);
                                uploadObj_1.put("entrustDiff", list.get(i).entrustDiff);
                                uploadObj_1.put("posDiff", list.get(i).posDiff);
                                uploadObj_1.put("currDiff", list.get(i).currDiff);
                                uploadObj_1.put("underlyingType", list.get(i).underlyingType);
                                uploadObj_1.put("underlyingLastPx", list.get(i).underlyingLastPx);
                                uploadObj_1.put("underlyingPreClose", list.get(i).underlyingPreClose);
                                uploadObj_1.put("underlyingchg", list.get(i).underlyingchg);
                                uploadObj_1.put("underlyingSymbol", list.get(i).underlyingSymbol);
                                uploadObj_1.put("deliveryDay", list.get(i).deliveryDay);
                                uploadObj_1.put("riskFreeInterestRate", list.get(i).riskFreeInterestRate);
                                uploadObj_1.put("intersectionNum", list.get(i).intersectionNum);
                                uploadObj_1.put("change1", list.get(i).change1);
                                uploadObj_1.put("totalBid", list.get(i).totalBid);
                                uploadObj_1.put("totalAsk", list.get(i).totalAsk);
                                //
                                uploadObj_1.put("IOPV", list.get(i).IOPV);
                                uploadObj_1.put("preIOPV", list.get(i).preIOPV);
                                uploadObj_1.put("stateOfTransfer", list.get(i).stateOfTransfer);
                                uploadObj_1.put("typeOfTransfer", list.get(i).typeOfTransfer);
                                uploadObj_1.put("exRighitDividend", list.get(i).exRighitDividend);
                                uploadObj_1.put("securityLevel", list.get(i).securityLevel);
                                uploadObj_1.put("rpd", list.get(i).rpd);
                                uploadObj_1.put("cdd", list.get(i).cdd);
                                //ios无
                                uploadObj_1.put("change2", list.get(i).change2);
                                uploadObj_1.put("earningsPerShare", list.get(i).earningsPerShare);
                                uploadObj_1.put("earningsPerShareReportingPeriod", list.get(i).earningsPerShareReportingPeriod);
                                //
                                uploadObj_1.put("hkTExchangeFlag", list.get(i).hkTExchangeFlag);//注意ios
                                uploadObj_1.put("vote", list.get(i).vote);//注意ios
                                uploadObj_1.put("upf", list.get(i).upf);//注意ios
                                uploadObj_1.put("DRCurrentShare", list.get(i).DRCurrentShare);
                                uploadObj_1.put("DRPreviousClosingShare", list.get(i).DRPreviousClosingShare);
                                uploadObj_1.put("DRConversionBase", list.get(i).DRConversionBase);
                                uploadObj_1.put("DRDepositoryInstitutionCode", list.get(i).DRDepositoryInstitutionCode);
                                uploadObj_1.put("DRDepositoryInstitutionName", list.get(i).DRDepositoryInstitutionName);
                                uploadObj_1.put("DRSubjectClosingReferencePrice", list.get(i).DRSubjectClosingReferencePrice);
                                uploadObj_1.put("DR", list.get(i).DR);
                                uploadObj_1.put("GDR", list.get(i).GDR);
                                uploadObj_1.put("DRStockCode", list.get(i).DRStockCode);
                                uploadObj_1.put("DRStockName", list.get(i).DRStockName);
                                uploadObj_1.put("DRSecuritiesConversionBase", list.get(i).DRSecuritiesConversionBase);
                                uploadObj_1.put("DRListingDate", list.get(i).DRListingDate);
                                uploadObj_1.put("DRFlowStartDate", list.get(i).DRFlowStartDate);
                                uploadObj_1.put("DRFlowEndDate", list.get(i).DRFlowEndDate);
                                uploadObj_1.put("changeBP", list.get(i).changeBP);
                                uploadObj_1.put("subscribeUpperLimit", list.get(i).subscribeUpperLimit);
                                uploadObj_1.put("subscribeLowerLimit", list.get(i).subscribeLowerLimit);
                                uploadObj_1.put("afterHoursVolume", list.get(i).afterHoursVolume);
                                uploadObj_1.put("afterHoursAmount", list.get(i).afterHoursAmount);
                                uploadObj_1.put("afterHoursTransactionNumber", list.get(i).afterHoursTransactionNumber);
                                uploadObj_1.put("afterHoursWithdrawBuyCount", list.get(i).afterHoursWithdrawBuyCount);
                                uploadObj_1.put("afterHoursWithdrawBuyVolume", list.get(i).afterHoursWithdrawBuyVolume);
                                uploadObj_1.put("afterHoursWithdrawSellCount", list.get(i).afterHoursWithdrawSellCount);
                                uploadObj_1.put("afterHoursWithdrawSellVolume", list.get(i).afterHoursWithdrawSellVolume);
                                uploadObj_1.put("afterHoursBuyVolume", list.get(i).afterHoursBuyVolume);
                                uploadObj_1.put("afterHoursSellVolume", list.get(i).afterHoursSellVolume);
                                uploadObj_1.put("issuedCapital", list.get(i).issuedCapital);
//                                uploadObj_1.put("limitPriceUpperLimit", list.get(i).limitPriceUpperLimit);
//                                uploadObj_1.put("limitPriceLowerLimit", list.get(i).limitPriceLowerLimit);
//                                uploadObj_1.put("longName", list.get(i).longName);
//                                //板块指数
//                                uploadObj_1.put("blockChg", list.get(i).blockChg);
//                                uploadObj_1.put("averageChg", list.get(i).averageChg);
//                                uploadObj_1.put("indexChg5", list.get(i).indexChg5);
//                                uploadObj_1.put("indexChg10", list.get(i).indexChg10);
//                                //3.3.0.002新增字段
//                                uploadObj.put("monthChangeRate", list.get(i).monthChangeRate);
//                                uploadObj.put("yearChangeRate", list.get(i).yearChangeRate);
//                                uploadObj.put("recentMonthChangeRate", list.get(i).recentMonthChangeRate);
//                                uploadObj.put("recentYearChangeRate", list.get(i).recentYearChangeRate);
                                //增值指标
                                if (cateSortingResponse.addValueModel!=null){
                                    ArrayList<AddValueModel> addValueModels=cateSortingResponse.addValueModel;
                                    for (AddValueModel item : addValueModels) {
                                        if (item.code.equals(list.get(i).id)){
                                            JSONObject uploadObj_2 = new JSONObject();
                                            uploadObj_2.put("code",item.code);
                                            uploadObj_2.put("date",item.date);
                                            uploadObj_2.put("time",item.time);
                                            uploadObj_2.put("ultraLargeBuyVolume",item.ultraLargeBuyVolume);
                                            uploadObj_2.put("ultraLargeSellVolume",item.ultraLargeSellVolume);
                                            uploadObj_2.put("ultraLargeBuyAmount",item.ultraLargeBuyAmount);
                                            uploadObj_2.put("ultraLargeSellAmount",item.ultraLargeSellAmount);
                                            uploadObj_2.put("largeBuyVolume",item.largeBuyVolume);
                                            uploadObj_2.put("largeSellVolume",item.largeSellVolume);
                                            uploadObj_2.put("largeBuyAmount",item.largeBuyAmount);
                                            uploadObj_2.put("largeSellAmount",item.largeSellAmount);
                                            uploadObj_2.put("mediumBuyVolume",item.mediumBuyVolume);
                                            uploadObj_2.put("mediumSellVolume",item.mediumSellVolume);
                                            uploadObj_2.put("mediumBuyAmount",item.mediumBuyAmount);
                                            uploadObj_2.put("mediumSellAmount",item.mediumSellAmount);
                                            uploadObj_2.put("smallBuyVolume",item.smallBuyVolume);
                                            uploadObj_2.put("smallSellVolume",item.smallSellVolume);
                                            uploadObj_2.put("smallBuyAmount",item.smallBuyAmount);
                                            uploadObj_2.put("smallSellAmount",item.smallSellAmount);
                                            uploadObj_2.put("ultraLargeNetInflow",item.ultraLargeNetInflow);
                                            uploadObj_2.put("largeNetInflow",item.largeNetInflow);
                                            uploadObj_2.put("netCapitalInflow",item.netCapitalInflow);
                                            uploadObj_2.put("mediumNetInflow",item.mediumNetInflow);
                                            uploadObj_2.put("smallNetInflow",item.smallNetInflow);

                                            List<String> fundsInflows=new ArrayList<>();
                                            if (item.fundsInflows!=null&&item.fundsInflows.length>0){
                                                for (int j=0;j<item.fundsInflows.length;j++){
                                                    fundsInflows.add(item.fundsInflows[j]);
                                                }
                                                uploadObj_2.put("fundsInflows",new JSONArray(fundsInflows));
                                            }else {
                                                uploadObj_2.put("fundsInflows",item.fundsInflows);
                                            }

                                            List<String> fundsOutflows=new ArrayList<>();
                                            if (item.fundsOutflows!=null&&item.fundsOutflows.length>0){
                                                for (int j=0;j<item.fundsOutflows.length;j++){
                                                    fundsOutflows.add(item.fundsOutflows[j]);
                                                }
                                                uploadObj_2.put("fundsOutflows",new JSONArray(fundsOutflows));
                                            }else {
                                                uploadObj_2.put("fundsOutflows",item.fundsOutflows);
                                            }

                                            uploadObj_2.put("ultraLargeDiffer",item.ultraLargeDiffer);
                                            uploadObj_2.put("largeDiffer",item.largeDiffer);
                                            uploadObj_2.put("mediumDiffer",item.mediumDiffer);
                                            uploadObj_2.put("smallDiffer",item.smallDiffer);
                                            uploadObj_2.put("largeBuyDealCount",item.largeBuyDealCount);
                                            uploadObj_2.put("largeSellDealCount",item.largeSellDealCount);
                                            uploadObj_2.put("dealCountMovingAverage",item.dealCountMovingAverage);
                                            uploadObj_2.put("buyCount",item.buyCount);
                                            uploadObj_2.put("sellCount",item.sellCount);
                                            uploadObj_2.put("BBD",item.BBD);
                                            uploadObj_2.put("BBD5",item.BBD5);
                                            uploadObj_2.put("BBD10",item.BBD10);
                                            uploadObj_2.put("DDX",item.DDX);
                                            uploadObj_2.put("DDX5",item.DDX5);
                                            uploadObj_2.put("DDX10",item.DDX10);
                                            uploadObj_2.put("DDY",item.DDY);
                                            uploadObj_2.put("DDY5",item.DDY5);
                                            uploadObj_2.put("DDY10",item.DDY10);
                                            uploadObj_2.put("DDZ",item.DDZ);
                                            uploadObj_2.put("RatioBS",item.RatioBS);

                                            List<String> othersFundsInflows=new ArrayList<>();
                                            if (item.othersFundsInflows!=null&&item.othersFundsInflows.length>0){
                                                for (int j=0;j<item.othersFundsInflows.length;j++){
                                                    othersFundsInflows.add(item.othersFundsInflows[j]);
                                                }
                                                uploadObj_2.put("othersFundsInflows",new JSONArray(othersFundsInflows));
                                            }else {
                                                uploadObj_2.put("othersFundsInflows",item.othersFundsInflows);
                                            }

                                            List<String> othersFundsOutflows=new ArrayList<>();
                                            if (item.othersFundsOutflows!=null&&item.othersFundsOutflows.length>0){
                                                for (int j=0;j<item.othersFundsOutflows.length;j++){
                                                    othersFundsOutflows.add(item.othersFundsOutflows[j]);
                                                }
                                                uploadObj_2.put("othersFundsOutflows",new JSONArray(othersFundsOutflows));
                                            }else {
                                                uploadObj_2.put("othersFundsOutflows",item.othersFundsOutflows);
                                            }

                                            uploadObj_2.put("fiveMinutesChangeRate",item.fiveMinutesChangeRate);
                                            uploadObj_2.put("largeOrderNumB",item.largeOrderNumB);
                                            uploadObj_2.put("largeOrderNumS",item.largeOrderNumS);
                                            uploadObj_2.put("bigOrderNumB",item.bigOrderNumB);
                                            uploadObj_2.put("bigOrderNumS",item.bigOrderNumS);
                                            uploadObj_2.put("midOrderNumB",item.midOrderNumB);
                                            uploadObj_2.put("midOrderNumS",item.midOrderNumS);
                                            uploadObj_2.put("smallOrderNumB",item.smallOrderNumB);
                                            uploadObj_2.put("smallOrderNumS",item.smallOrderNumS);
                                            uploadObj_2.put("mainforceMoneyNetInflow5",item.mainforceMoneyNetInflow5);
                                            uploadObj_2.put("mainforceMoneyNetInflow10",item.mainforceMoneyNetInflow10);
                                            uploadObj_2.put("mainforceMoneyNetInflow20",item.mainforceMoneyNetInflow20);
                                            uploadObj_2.put("ratioMainforceMoneyNetInflow5",item.ratioMainforceMoneyNetInflow5);
                                            uploadObj_2.put("ratioMainforceMoneyNetInflow10",item.ratioMainforceMoneyNetInflow10);
                                            uploadObj_2.put("ratioMainforceMoneyNetInflow20",item.ratioMainforceMoneyNetInflow20);
                                            uploadObj_1.put("addValue",uploadObj_2);
                                        }
                                    }
                                }
//                            Log.d("data", String.valueOf(uploadObj_1));
                                uploadObj.put(String.valueOf(i+1),uploadObj_1);
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
            } catch (Exception e) {
                //                throw new Exception(e);
                throw new TestcaseException(e,rule.getParam());
            }
//        }
    }
}