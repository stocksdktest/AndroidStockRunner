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
    private static final int timeout_ms = 100000000;
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
        final String quoteNumbers1 = rule.getParam().optString("PARAMS");
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
                            uploadObj_1.put("status", dwnull(list.get(i).status == null ? "-" : list.get(i).status));
                            uploadObj_1.put("id", dwnull(list.get(i).id == null ? "-" : list.get(i).id));
                            uploadObj_1.put("name", dwnull(list.get(i).name == null ? "-" : list.get(i).name));
                            uploadObj_1.put("datetime", dwnull(list.get(i).datetime == null ? "-" : list.get(i).datetime));
                            uploadObj_1.put("market", dwnull(list.get(i).market == null ? "-" : list.get(i).market));
                            uploadObj_1.put("subtype", dwnull(list.get(i).subtype == null ? "-" : list.get(i).subtype));
                            uploadObj_1.put("lastPrice", dwnull(list.get(i).lastPrice == null ? "-" : list.get(i).lastPrice));
                            uploadObj_1.put("highPrice", dwnull(list.get(i).highPrice == null ? "-" : list.get(i).highPrice));
                            uploadObj_1.put("lowPrice", dwnull(list.get(i).lowPrice == null ? "-" : list.get(i).lowPrice));
                            uploadObj_1.put("openPrice", dwnull(list.get(i).openPrice == null ? "-" : list.get(i).openPrice));
                            uploadObj_1.put("preClosePrice", dwnull(list.get(i).preClosePrice == null ? "-" : list.get(i).preClosePrice));
//                            uploadObj_1.put("changeRate", list.get(i).upDownFlag+list.get(i).changeRate);//ios注意
                            if ("+".equals(list.get(i).upDownFlag)||"-".equals(list.get(i).upDownFlag)){
                                uploadObj_1.put("changeRate",list.get(i).upDownFlag+list.get(i).changeRate);//加涨跌符号
                            }else {
                                uploadObj_1.put("changeRate",dwnull(list.get(i).changeRate == null ? "-" : list.get(i).changeRate));
                            }
                            uploadObj_1.put("volume", dwnull(list.get(i).volume == null ? "-" : list.get(i).volume));
                            uploadObj_1.put("nowVolume", dwnull(list.get(i).nowVolume == null ? "-" : list.get(i).nowVolume));
                            uploadObj_1.put("turnoverRate", dwnull(list.get(i).turnoverRate == null ? "-" : list.get(i).turnoverRate));
                            uploadObj_1.put("limitUP", dwnull(list.get(i).limitUP == null ? "-" : list.get(i).limitUP));
                            uploadObj_1.put("limitDown", dwnull(list.get(i).limitDown == null ? "-" : list.get(i).limitDown));
                            uploadObj_1.put("averageValue", dwnull(list.get(i).averageValue == null ? "-" : list.get(i).averageValue));//ios无  500
                            uploadObj_1.put("change", dwnull(list.get(i).change == null ? "-" : list.get(i).change));
                            uploadObj_1.put("amount", dwnull(list.get(i).amount == null ? "-" : list.get(i).amount));
                            uploadObj_1.put("volumeRatio", dwnull(list.get(i).volumeRatio == null ? "-" : list.get(i).volumeRatio));
                            uploadObj_1.put("buyPrice", dwnull(list.get(i).buyPrice == null ? "-" : list.get(i).buyPrice));
                            uploadObj_1.put("sellPrice", dwnull(list.get(i).sellPrice == null ? "-" : list.get(i).sellPrice));
                            uploadObj_1.put("buyVolume", dwnull(list.get(i).buyVolume == null ? "-" : list.get(i).buyVolume));
                            uploadObj_1.put("sellVolume", dwnull(list.get(i).sellVolume == null ? "-" : list.get(i).sellVolume));
                            uploadObj_1.put("totalValue", dwnull(list.get(i).totalValue == null ? "-" : list.get(i).totalValue));
                            uploadObj_1.put("flowValue", dwnull(list.get(i).flowValue == null ? "-" : list.get(i).flowValue));
                            uploadObj_1.put("netAsset", dwnull(list.get(i).netAsset == null ? "-" : list.get(i).netAsset));
                            uploadObj_1.put("pe", dwnull(list.get(i).pe == null ? "-" : list.get(i).pe));
                            uploadObj_1.put("pe2", dwnull(list.get(i).pe2 == null ? "-" : list.get(i).pe2));
                            uploadObj_1.put("pb", dwnull(list.get(i).pb == null ? "-" : list.get(i).pb));
                            uploadObj_1.put("capitalization", dwnull(list.get(i).capitalization == null ? "-" : list.get(i).capitalization));
                            uploadObj_1.put("circulatingShares", dwnull(list.get(i).circulatingShares == null ? "-" : list.get(i).circulatingShares));
                            List<String> buyPrices=new ArrayList<>();
                            if (list.get(i).buyPrices!=null&&list.get(i).buyPrices.size()>0){
                                for (int j=0;j<list.get(i).buyPrices.size();j++){
                                    buyPrices.add(dwnull(list.get(i).buyPrices.get(j) == null ? "-" : list.get(i).buyPrices.get(j)));
                                }
                                uploadObj_1.put("bidpx1", dwnull(list.get(i).buyPrices.get(0) == null ? "-" : list.get(i).buyPrices.get(0)));
                                uploadObj_1.put("buyPrices",new JSONArray(buyPrices));
                            }else {
                                uploadObj_1.put("bidpx1", "");
                                uploadObj_1.put("buyPrices",list.get(i).buyPrices);
                            }

                            List<String> buySingleVolumes=new ArrayList<>();
                            if (list.get(i).buySingleVolumes!=null&&list.get(i).buySingleVolumes.size()>0){
                                for (int j=0;j<list.get(i).buySingleVolumes.size();j++){
                                    buySingleVolumes.add(dwnull(list.get(i).buySingleVolumes.get(j) == null ? "-" : list.get(i).buySingleVolumes.get(j)));
                                }
                                uploadObj_1.put("buySingleVolumes",new JSONArray(buySingleVolumes));
                            }else {
                                uploadObj_1.put("buySingleVolumes",list.get(i).buySingleVolumes);
                            }

                            List<String> buyVolumes=new ArrayList<>();
                            if (list.get(i).buyVolumes!=null&&list.get(i).buyVolumes.size()>0){
                                for (int j=0;j<list.get(i).buyVolumes.size();j++){
                                    buyVolumes.add(dwnull(list.get(i).buyVolumes.get(j) == null ? "-" : list.get(i).buyVolumes.get(j)));
                                }
                                uploadObj_1.put("bidvol1", dwnull(list.get(i).buyVolumes.get(0) == null ? "-" : list.get(i).buyVolumes.get(0)));
                                uploadObj_1.put("buyVolumes",new JSONArray(buyVolumes));
                            }else {
                                uploadObj_1.put("bidvol1", "");
                                uploadObj_1.put("buyVolumes",list.get(i).buyVolumes);
                            }

                            List<String> sellPrices=new ArrayList<>();
                            if (list.get(i).sellPrices!=null&&list.get(i).sellPrices.size()>0){
                                for (int j=0;j<list.get(i).sellPrices.size();j++){
                                    sellPrices.add(dwnull(list.get(i).sellPrices.get(j) == null ? "-" : list.get(i).sellPrices.get(j)));
                                }
                                uploadObj_1.put("askpx1", dwnull(list.get(i).sellPrices.get(0) == null ? "-" : list.get(i).sellPrices.get(0)));
                                uploadObj_1.put("sellPrices",new JSONArray(sellPrices));
                            }else {
                                uploadObj_1.put("askpx1", "");
                                uploadObj_1.put("sellPrices",list.get(i).sellPrices);
                            }

                            List<String> sellSingleVolumes=new ArrayList<>();
                            if (list.get(i).sellSingleVolumes!=null&&list.get(i).sellSingleVolumes.size()>0){
                                for (int j=0;j<list.get(i).sellSingleVolumes.size();j++){
                                    sellSingleVolumes.add(dwnull(list.get(i).sellSingleVolumes.get(j) == null ? "-" : list.get(i).sellSingleVolumes.get(j)));
                                }
                                uploadObj_1.put("sellSingleVolumes",new JSONArray(sellSingleVolumes));
                            }else {
                                uploadObj_1.put("sellSingleVolumes",list.get(i).sellSingleVolumes);
                            }

                            List<String> sellVolumes=new ArrayList<>();
                            if (list.get(i).sellVolumes!=null&&list.get(i).sellVolumes.size()>0){
                                for (int j=0;j<list.get(i).sellVolumes.size();j++){
                                    sellVolumes.add(dwnull(list.get(i).sellVolumes.get(j) == null ? "-" : list.get(i).sellVolumes.get(j)));
                                }
                                uploadObj_1.put("askvol1", dwnull(list.get(i).sellVolumes.get(0) == null ? "-" : list.get(i).sellVolumes.get(0)));
                                uploadObj_1.put("sellVolumes",new JSONArray(sellVolumes));
                            }else {
                                uploadObj_1.put("askvol1", "");
                                uploadObj_1.put("sellVolumes",list.get(i).sellVolumes);
                            }

                            uploadObj_1.put("amplitudeRate", dwnull(list.get(i).amplitudeRate == null ? "-" : list.get(i).amplitudeRate));
                            uploadObj_1.put("receipts", dwnull(list.get(i).receipts == null ? "-" : list.get(i).receipts));

                            uploadObj_1.put("upCount", dwnull(list.get(i).upCount == null ? "-" : list.get(i).upCount));
                            uploadObj_1.put("sameCount", dwnull(list.get(i).sameCount == null ? "-" : list.get(i).sameCount));
                            uploadObj_1.put("downCount", dwnull(list.get(i).downCount == null ? "-" : list.get(i).downCount));
                            uploadObj_1.put("optionType", dwnull(list.get(i).optionType == null ? "-" : list.get(i).optionType));
                            uploadObj_1.put("contractID", dwnull(list.get(i).contractID == null ? "-" : list.get(i).contractID));
                            uploadObj_1.put("objectID", dwnull(list.get(i).objectID == null ? "-" : list.get(i).objectID));
                            uploadObj_1.put("stockSymble", dwnull(list.get(i).stockSymble == null ? "-" : list.get(i).stockSymble));
                            uploadObj_1.put("stockType", dwnull(list.get(i).stockType == null ? "-" : list.get(i).stockType));
                            uploadObj_1.put("stockUnit", dwnull(list.get(i).stockUnit == null ? "-" : list.get(i).stockUnit));
                            uploadObj_1.put("exePrice", dwnull(list.get(i).exePrice == null ? "-" : list.get(i).exePrice));
                            uploadObj_1.put("startDate", dwnull(list.get(i).startDate == null ? "-" : list.get(i).startDate));
                            uploadObj_1.put("endDate", dwnull(list.get(i).endDate == null ? "-" : list.get(i).endDate));
                            uploadObj_1.put("exeDate", dwnull(list.get(i).exeDate == null ? "-" : list.get(i).exeDate));
                            uploadObj_1.put("delDate", dwnull(list.get(i).delDate == null ? "-" : list.get(i).delDate));
                            uploadObj_1.put("expDate", dwnull(list.get(i).expDate == null ? "-" : list.get(i).expDate));
                            uploadObj_1.put("version", dwnull(list.get(i).version == null ? "-" : list.get(i).version));
                            uploadObj_1.put("presetPrice", dwnull(list.get(i).presetPrice == null ? "-" : list.get(i).presetPrice));
                            uploadObj_1.put("stockClose", dwnull(list.get(i).stockClose == null ? "-" : list.get(i).stockClose));
                            uploadObj_1.put("stockLast", dwnull(list.get(i).stockLast == null ? "-" : list.get(i).stockLast));
                            uploadObj_1.put("isLimit", dwnull(list.get(i).isLimit == null ? "-" : list.get(i).isLimit));
                            uploadObj_1.put("inValue", dwnull(list.get(i).inValue == null ? "-" : list.get(i).inValue));
                            uploadObj_1.put("timeValue", dwnull(list.get(i).timeValue == null ? "-" : list.get(i).timeValue));
                            uploadObj_1.put("preInterest", dwnull(list.get(i).preInterest == null ? "-" : list.get(i).preInterest));
                            uploadObj_1.put("openInterest", dwnull(list.get(i).openInterest == null ? "-" : list.get(i).openInterest));
                            uploadObj_1.put("remainDate", dwnull(list.get(i).remainDate == null ? "-" : list.get(i).remainDate));
                            uploadObj_1.put("leverageRatio", dwnull(list.get(i).leverageRatio == null ? "-" : list.get(i).leverageRatio));
                            uploadObj_1.put("premiumRate", dwnull(list.get(i).premiumRate == null ? "-" : list.get(i).premiumRate));
                            uploadObj_1.put("impliedVolatility", dwnull(list.get(i).impliedVolatility == null ? "-" : list.get(i).impliedVolatility));
                            uploadObj_1.put("delta", dwnull(list.get(i).delta == null ? "-" : list.get(i).delta));
                            uploadObj_1.put("gramma", dwnull(list.get(i).gramma == null ? "-" : list.get(i).gramma));
                            uploadObj_1.put("theta", dwnull(list.get(i).theta == null ? "-" : list.get(i).theta));
                            uploadObj_1.put("rho", dwnull(list.get(i).rho == null ? "-" : list.get(i).rho));
                            uploadObj_1.put("vega", dwnull(list.get(i).vega == null ? "-" : list.get(i).vega));
                            uploadObj_1.put("realLeverage", dwnull(list.get(i).realLeverage == null ? "-" : list.get(i).realLeverage));
                            uploadObj_1.put("theoreticalPrice", dwnull(list.get(i).theoreticalPrice == null ? "-" : list.get(i).theoreticalPrice));
                            //
                            uploadObj_1.put("exerciseWay", dwnull(list.get(i).exerciseWay == null ? "-" : list.get(i).exerciseWay));
                            uploadObj_1.put("orderRatio", dwnull(list.get(i).orderRatio == null ? "-" : list.get(i).orderRatio));
                            uploadObj_1.put("hk_paramStatus", dwnull(list.get(i).hk_paramStatus == null ? "-" : list.get(i).hk_paramStatus));//ios无
                            uploadObj_1.put("fundType", dwnull(list.get(i).fundType == null ? "-" : list.get(i).fundType));
                            uploadObj_1.put("sumBuy", dwnull(list.get(i).sumBuy == null ? "-" : list.get(i).sumBuy));
                            uploadObj_1.put("sumSell", dwnull(list.get(i).sumSell == null ? "-" : list.get(i).sumSell));
                            uploadObj_1.put("averageBuy", dwnull(list.get(i).averageBuy == null ? "-" : list.get(i).averageBuy));
                            uploadObj_1.put("averageSell", dwnull(list.get(i).averageSell == null ? "-" : list.get(i).averageSell));
//                        uploadObj_1.put("upDownFlag", list.get(i).upDownFlag);//注意一下IOS android
                            uploadObj_1.put("zh", dwnull(list.get(i).zh == null ? "-" : list.get(i).zh));
                            uploadObj_1.put("hh", dwnull(list.get(i).hh == null ? "-" : list.get(i).hh));
                            uploadObj_1.put("st", dwnull(list.get(i).st == null ? "-" : list.get(i).st));
                            uploadObj_1.put("bu", dwnull(list.get(i).bu == null ? "-" : list.get(i).bu));
                            uploadObj_1.put("su", dwnull(list.get(i).su == null ? "-" : list.get(i).su));
                            uploadObj_1.put("hs", dwnull(list.get(i).hs == null ? "-" : list.get(i).hs));
                            uploadObj_1.put("ac", dwnull(list.get(i).ac == null ? "-" : list.get(i).ac));
                            uploadObj_1.put("qf", dwnull(list.get(i).qf == null ? "-" : list.get(i).qf));//ios无
                            uploadObj_1.put("qc", dwnull(list.get(i).qc == null ? "-" : list.get(i).qc));//ios无
                            uploadObj_1.put("ah", dwnull(list.get(i).ah == null ? "-" : list.get(i).ah));
                            uploadObj_1.put("VCMFlag", dwnull(list.get(i).VCMFlag == null ? "-" : list.get(i).VCMFlag));
                            uploadObj_1.put("CASFlag", dwnull(list.get(i).CASFlag == null ? "-" : list.get(i).CASFlag));
                            uploadObj_1.put("rp", dwnull(list.get(i).rp == null ? "-" : list.get(i).rp));
                            uploadObj_1.put("cd", dwnull(list.get(i).cd == null ? "-" : list.get(i).cd));
                            uploadObj_1.put("hg", dwnull(list.get(i).hg == null ? "-" : list.get(i).hg));
                            uploadObj_1.put("sg", dwnull(list.get(i).sg == null ? "-" : list.get(i).sg));
                            uploadObj_1.put("fx", dwnull(list.get(i).fx == null ? "-" : list.get(i).fx));
                            uploadObj_1.put("ts", dwnull(list.get(i).ts == null ? "-" : list.get(i).ts));
                            uploadObj_1.put("add_option_avg_price", dwnull(list.get(i).add_option_avg_price == null ? "-" : list.get(i).add_option_avg_price));
                            uploadObj_1.put("add_option_avg_pb", dwnull(list.get(i).add_option_avg_pb == null ? "-" : list.get(i).add_option_avg_pb));
                            uploadObj_1.put("add_option_avg_close", dwnull(list.get(i).add_option_avg_close == null ? "-" : list.get(i).add_option_avg_close));
                            uploadObj_1.put("hk_volum_for_every_hand", dwnull(list.get(i).hk_volum_for_every_hand == null ? "-" : list.get(i).hk_volum_for_every_hand));
                            //ios无
                            uploadObj_1.put("buy_cancel_count", dwnull(list.get(i).buy_cancel_count == null ? "-" : list.get(i).buy_cancel_count));
                            uploadObj_1.put("buy_cancel_num", dwnull(list.get(i).buy_cancel_num == null ? "-" : list.get(i).buy_cancel_num));
                            uploadObj_1.put("buy_cancel_amount", dwnull(list.get(i).buy_cancel_amount == null ? "-" : list.get(i).buy_cancel_amount));
                            uploadObj_1.put("sell_cancel_count", dwnull(list.get(i).sell_cancel_count == null ? "-" : list.get(i).sell_cancel_count));
                            uploadObj_1.put("sell_cancel_num", dwnull(list.get(i).sell_cancel_num == null ? "-" : list.get(i).sell_cancel_num));
                            uploadObj_1.put("sell_cancel_amount", dwnull(list.get(i).sell_cancel_amount == null ? "-" : list.get(i).sell_cancel_amount));
                            uploadObj_1.put("tradingDay", dwnull(list.get(i).tradingDay == null ? "-" : list.get(i).tradingDay));
                            uploadObj_1.put("settlementID", dwnull(list.get(i).settlementID == null ? "-" : list.get(i).settlementID));
                            uploadObj_1.put("settlementGroupID", dwnull(list.get(i).settlementGroupID == null ? "-" : list.get(i).settlementGroupID));
                            uploadObj_1.put("preSettlement", dwnull(list.get(i).preSettlement == null ? "-" : list.get(i).preSettlement));
                            uploadObj_1.put("position_chg", dwnull(list.get(i).position_chg == null ? "-" : list.get(i).position_chg));
                            uploadObj_1.put("close", dwnull(list.get(i).close == null ? "-" : list.get(i).close));
                            uploadObj_1.put("settlement", dwnull(list.get(i).settlement == null ? "-" : list.get(i).settlement));
                            uploadObj_1.put("preDelta", dwnull(list.get(i).preDelta == null ? "-" : list.get(i).preDelta));
                            uploadObj_1.put("currDelta", dwnull(list.get(i).currDelta == null ? "-" : list.get(i).currDelta));
                            uploadObj_1.put("updateMillisec", dwnull(list.get(i).updateMillisec == null ? "-" : list.get(i).updateMillisec));
                            uploadObj_1.put("entrustDiff", dwnull(list.get(i).entrustDiff == null ? "-" : list.get(i).entrustDiff));
                            uploadObj_1.put("posDiff", dwnull(list.get(i).posDiff == null ? "-" : list.get(i).posDiff));
                            uploadObj_1.put("currDiff", dwnull(list.get(i).currDiff == null ? "-" : list.get(i).currDiff));
                            uploadObj_1.put("underlyingType", dwnull(list.get(i).underlyingType == null ? "-" : list.get(i).underlyingType));
                            uploadObj_1.put("underlyingLastPx", dwnull(list.get(i).underlyingLastPx == null ? "-" : list.get(i).underlyingLastPx));
                            uploadObj_1.put("underlyingPreClose", dwnull(list.get(i).underlyingPreClose == null ? "-" : list.get(i).underlyingPreClose));
                            uploadObj_1.put("underlyingchg", dwnull(list.get(i).underlyingchg == null ? "-" : list.get(i).underlyingchg));
                            uploadObj_1.put("underlyingSymbol", dwnull(list.get(i).underlyingSymbol == null ? "-" : list.get(i).underlyingSymbol));
                            uploadObj_1.put("deliveryDay", dwnull(list.get(i).deliveryDay == null ? "-" : list.get(i).deliveryDay));
                            uploadObj_1.put("riskFreeInterestRate", dwnull(list.get(i).riskFreeInterestRate == null ? "-" : list.get(i).riskFreeInterestRate));
                            uploadObj_1.put("intersectionNum", dwnull(list.get(i).intersectionNum == null ? "-" : list.get(i).intersectionNum));
                            uploadObj_1.put("change1", dwnull(list.get(i).change1 == null ? "-" : list.get(i).change1));
                            uploadObj_1.put("totalBid", dwnull(list.get(i).totalBid == null ? "-" : list.get(i).totalBid));
                            uploadObj_1.put("totalAsk", dwnull(list.get(i).totalAsk == null ? "-" : list.get(i).totalAsk));
                            //
                            uploadObj_1.put("IOPV", dwnull(list.get(i).IOPV == null ? "-" : list.get(i).IOPV));
                            uploadObj_1.put("preIOPV", dwnull(list.get(i).preIOPV == null ? "-" : list.get(i).preIOPV));
                            uploadObj_1.put("stateOfTransfer", dwnull(list.get(i).stateOfTransfer == null ? "-" : list.get(i).stateOfTransfer));
                            uploadObj_1.put("typeOfTransfer", dwnull(list.get(i).typeOfTransfer == null ? "-" : list.get(i).typeOfTransfer));
                            uploadObj_1.put("exRighitDividend", dwnull(list.get(i).exRighitDividend == null ? "-" : list.get(i).exRighitDividend));
                            uploadObj_1.put("securityLevel", dwnull(list.get(i).securityLevel == null ? "-" : list.get(i).securityLevel));
                            uploadObj_1.put("rpd", dwnull(list.get(i).rpd == null ? "-" : list.get(i).rpd));
                            uploadObj_1.put("cdd", dwnull(list.get(i).cdd == null ? "-" : list.get(i).cdd));
                            //ios无
                            uploadObj_1.put("change2", dwnull(list.get(i).change2 == null ? "-" : list.get(i).change2));
                            uploadObj_1.put("earningsPerShare", dwnull(list.get(i).earningsPerShare == null ? "-" : list.get(i).earningsPerShare));
                            uploadObj_1.put("earningsPerShareReportingPeriod", dwnull(list.get(i).earningsPerShareReportingPeriod == null ? "-" : list.get(i).earningsPerShareReportingPeriod));
                            //
                            uploadObj_1.put("hkTExchangeFlag", dwnull(list.get(i).hkTExchangeFlag == null ? "-" : list.get(i).hkTExchangeFlag));//注意ios
                            uploadObj_1.put("vote", dwnull(list.get(i).vote == null ? "-" : list.get(i).vote));//注意ios
                            uploadObj_1.put("upf", dwnull(list.get(i).upf == null ? "-" : list.get(i).upf));//注意ios
                            uploadObj_1.put("DRCurrentShare", dwnull(list.get(i).DRCurrentShare == null ? "-" : list.get(i).DRCurrentShare));
                            uploadObj_1.put("DRPreviousClosingShare", dwnull(list.get(i).DRPreviousClosingShare == null ? "-" : list.get(i).DRPreviousClosingShare));
                            uploadObj_1.put("DRConversionBase", dwnull(list.get(i).DRConversionBase == null ? "-" : list.get(i).DRConversionBase));
                            uploadObj_1.put("DRDepositoryInstitutionCode", dwnull(list.get(i).DRDepositoryInstitutionCode == null ? "-" : list.get(i).DRDepositoryInstitutionCode));
                            uploadObj_1.put("DRDepositoryInstitutionName", dwnull(list.get(i).DRDepositoryInstitutionName == null ? "-" : list.get(i).DRDepositoryInstitutionName));
                            uploadObj_1.put("DRSubjectClosingReferencePrice", dwnull(list.get(i).DRSubjectClosingReferencePrice == null ? "-" : list.get(i).DRSubjectClosingReferencePrice));
                            uploadObj_1.put("DR", dwnull(list.get(i).DR == null ? "-" : list.get(i).DR));
                            uploadObj_1.put("GDR", dwnull(list.get(i).GDR == null ? "-" : list.get(i).GDR));
                            uploadObj_1.put("DRStockCode", dwnull(list.get(i).DRStockCode == null ? "-" : list.get(i).DRStockCode));
                            uploadObj_1.put("DRStockName", dwnull(list.get(i).DRStockName == null ? "-" : list.get(i).DRStockName));
                            uploadObj_1.put("DRSecuritiesConversionBase", dwnull(list.get(i).DRSecuritiesConversionBase == null ? "-" : list.get(i).DRSecuritiesConversionBase));
                            uploadObj_1.put("DRListingDate", dwnull(list.get(i).DRListingDate == null ? "-" : list.get(i).DRListingDate));
                            uploadObj_1.put("DRFlowStartDate", dwnull(list.get(i).DRFlowStartDate == null ? "-" : list.get(i).DRFlowStartDate));
                            uploadObj_1.put("DRFlowEndDate", dwnull(list.get(i).DRFlowEndDate == null ? "-" : list.get(i).DRFlowEndDate));
                            uploadObj_1.put("changeBP", dwnull(list.get(i).changeBP == null ? "-" : list.get(i).changeBP));
                            uploadObj_1.put("subscribeUpperLimit", dwnull(list.get(i).subscribeUpperLimit == null ? "-" : list.get(i).subscribeUpperLimit));
                            uploadObj_1.put("subscribeLowerLimit", dwnull(list.get(i).subscribeLowerLimit == null ? "-" : list.get(i).subscribeLowerLimit));
                            uploadObj_1.put("afterHoursVolume", dwnull(list.get(i).afterHoursVolume == null ? "-" : list.get(i).afterHoursVolume));
                            uploadObj_1.put("afterHoursAmount", dwnull(list.get(i).afterHoursAmount == null ? "-" : list.get(i).afterHoursAmount));
                            uploadObj_1.put("afterHoursTransactionNumber", dwnull(list.get(i).afterHoursTransactionNumber == null ? "-" : list.get(i).afterHoursTransactionNumber));
                            uploadObj_1.put("afterHoursWithdrawBuyCount", dwnull(list.get(i).afterHoursWithdrawBuyCount == null ? "-" : list.get(i).afterHoursWithdrawBuyCount));
                            uploadObj_1.put("afterHoursWithdrawBuyVolume", dwnull(list.get(i).afterHoursWithdrawBuyVolume == null ? "-" : list.get(i).afterHoursWithdrawBuyVolume));
                            uploadObj_1.put("afterHoursWithdrawSellCount", dwnull(list.get(i).afterHoursWithdrawSellCount == null ? "-" : list.get(i).afterHoursWithdrawSellCount));
                            uploadObj_1.put("afterHoursWithdrawSellVolume", dwnull(list.get(i).afterHoursWithdrawSellVolume == null ? "-" : list.get(i).afterHoursWithdrawSellVolume));
                            uploadObj_1.put("afterHoursBuyVolume", dwnull(list.get(i).afterHoursBuyVolume == null ? "-" : list.get(i).afterHoursBuyVolume));
                            uploadObj_1.put("afterHoursSellVolume", dwnull(list.get(i).afterHoursSellVolume == null ? "-" : list.get(i).afterHoursSellVolume));
                            uploadObj_1.put("issuedCapital", dwnull(list.get(i).issuedCapital == null ? "-" : list.get(i).issuedCapital));
                            uploadObj_1.put("limitPriceUpperLimit", dwnull(list.get(i).limitPriceUpperLimit == null ? "-" : list.get(i).limitPriceUpperLimit));
                            uploadObj_1.put("limitPriceLowerLimit", dwnull(list.get(i).limitPriceLowerLimit == null ? "-" : list.get(i).limitPriceLowerLimit));
                            uploadObj_1.put("longName", dwnull(list.get(i).longName == null ? "-" : list.get(i).longName));
                            //板块指数
                            uploadObj_1.put("blockChg", dwnull(list.get(i).blockChg == null ? "-" : list.get(i).blockChg));
                            uploadObj_1.put("averageChg", dwnull(list.get(i).averageChg == null ? "-" : list.get(i).averageChg));
                            uploadObj_1.put("indexChg5", dwnull(list.get(i).indexChg5 == null ? "-" : list.get(i).indexChg5));
                            uploadObj_1.put("indexChg10", dwnull(list.get(i).indexChg10 == null ? "-" : list.get(i).indexChg10));
                            //3.3.0.002新增字段
                            uploadObj_1.put("monthChangeRate", dwnull(list.get(i).monthChangeRate == null ? "-" : list.get(i).monthChangeRate));
                            uploadObj_1.put("yearChangeRate", dwnull(list.get(i).yearChangeRate == null ? "-" : list.get(i).yearChangeRate));
                            uploadObj_1.put("recentMonthChangeRate", dwnull(list.get(i).recentMonthChangeRate == null ? "-" : list.get(i).recentMonthChangeRate));
                            uploadObj_1.put("recentYearChangeRate", dwnull(list.get(i).recentYearChangeRate == null ? "-" : list.get(i).recentYearChangeRate));
                            //20210603  AppInfo.sdk_version=3.9.0
                            if (list.get(i).market.equals("sh")||list.get(i).market.equals("sz")){
                                uploadObj_1.put("listDate", dwnull(list.get(i).listDate == null ? "-" : list.get(i).listDate));
                                uploadObj_1.put("ttm", dwnull(list.get(i).ttm == null ? "-" : list.get(i).ttm));
                                uploadObj_1.put("roe", dwnull(list.get(i).roe == null ? "-" : list.get(i).roe));
                                uploadObj_1.put("buyVol1", dwnull(list.get(i).buyVol1 == null ? "-" : list.get(i).buyVol1));
                                uploadObj_1.put("sellVol1", dwnull(list.get(i).sellVol1 == null ? "-" : list.get(i).sellVol1));
                                uploadObj_1.put("changeRate5", dwnull(list.get(i).changeRate5 == null ? "-" : list.get(i).changeRate5));
                                uploadObj_1.put("changeRate10", dwnull(list.get(i).changeRate10 == null ? "-" : list.get(i).changeRate10));
                                uploadObj_1.put("changeRate20", dwnull(list.get(i).changeRate20 == null ? "-" : list.get(i).changeRate20));
                                uploadObj_1.put("turnoverRate5", dwnull(list.get(i).turnoverRate5 == null ? "-" : list.get(i).turnoverRate5));
                                uploadObj_1.put("turnoverRate10", dwnull(list.get(i).turnoverRate10 == null ? "-" : list.get(i).turnoverRate10));
                                uploadObj_1.put("turnoverRate20", dwnull(list.get(i).turnoverRate20 == null ? "-" : list.get(i).turnoverRate20));
                            }
                            //增值指标
                            if (cateSortingResponse.addValueModel!=null){
                                ArrayList<AddValueModel> addValueModels=cateSortingResponse.addValueModel;
                                for (AddValueModel item : addValueModels) {

                                    uploadObj_1.put("code",dwnull(item.code == null ? "-" : item.code));
                                    uploadObj_1.put("date",dwnull(item.date == null ? "-" : item.date));
                                    uploadObj_1.put("time",dwnull(item.time == null ? "-" : item.time));
                                    uploadObj_1.put("ultraLargeBuyVolume",dwnull(item.ultraLargeBuyVolume == null ? "-" : item.ultraLargeBuyVolume));
                                    uploadObj_1.put("ultraLargeSellVolume",dwnull(item.ultraLargeSellVolume == null ? "-" : item.ultraLargeSellVolume));
                                    uploadObj_1.put("ultraLargeBuyAmount",dwnull(item.ultraLargeBuyAmount == null ? "-" : item.ultraLargeBuyAmount));
                                    uploadObj_1.put("ultraLargeSellAmount",dwnull(item.ultraLargeSellAmount == null ? "-" : item.ultraLargeSellAmount));
                                    uploadObj_1.put("largeBuyVolume",dwnull(item.largeBuyVolume == null ? "-" : item.largeBuyVolume));
                                    uploadObj_1.put("largeSellVolume",dwnull(item.largeSellVolume == null ? "-" : item.largeSellVolume));
                                    uploadObj_1.put("largeBuyAmount",dwnull(item.largeBuyAmount == null ? "-" : item.largeBuyAmount));
                                    uploadObj_1.put("largeSellAmount",dwnull(item.largeSellAmount == null ? "-" : item.largeSellAmount));
                                    uploadObj_1.put("mediumBuyVolume",dwnull(item.mediumBuyVolume == null ? "-" : item.mediumBuyVolume));
                                    uploadObj_1.put("mediumSellVolume",dwnull(item.mediumSellVolume == null ? "-" : item.mediumSellVolume));
                                    uploadObj_1.put("mediumBuyAmount",dwnull(item.mediumBuyAmount == null ? "-" : item.mediumBuyAmount));
                                    uploadObj_1.put("mediumSellAmount",dwnull(item.mediumSellAmount == null ? "-" : item.mediumSellAmount));
                                    uploadObj_1.put("smallBuyVolume",dwnull(item.smallBuyVolume == null ? "-" : item.smallBuyVolume));
                                    uploadObj_1.put("smallSellVolume",dwnull(item.smallSellVolume == null ? "-" : item.smallSellVolume));
                                    uploadObj_1.put("smallBuyAmount",dwnull(item.smallBuyAmount == null ? "-" : item.smallBuyAmount));
                                    uploadObj_1.put("smallSellAmount",dwnull(item.smallSellAmount == null ? "-" : item.smallSellAmount));
                                    uploadObj_1.put("ultraLargeNetInflow",dwnull(item.ultraLargeNetInflow == null ? "-" : item.ultraLargeNetInflow));
                                    uploadObj_1.put("largeNetInflow",dwnull(item.largeNetInflow == null ? "-" : item.largeNetInflow));
                                    uploadObj_1.put("netCapitalInflow",dwnull(item.netCapitalInflow == null ? "-" : item.netCapitalInflow));
                                    uploadObj_1.put("mediumNetInflow",dwnull(item.mediumNetInflow == null ? "-" : item.mediumNetInflow));
                                    uploadObj_1.put("smallNetInflow",dwnull(item.smallNetInflow == null ? "-" : item.smallNetInflow));

                                    List<String> fundsInflows=new ArrayList<>();
                                    if (item.fundsInflows!=null&&item.fundsInflows.length>0){
                                        for (int j=0;j<item.fundsInflows.length;j++){
                                            fundsInflows.add(dwnull(item.fundsInflows[j] == null ? "-" : item.fundsInflows[j]));
                                        }
                                        uploadObj_1.put("fundsInflows",new JSONArray(fundsInflows));
                                    }else {
                                        uploadObj_1.put("fundsInflows",item.fundsInflows);
                                    }

                                    List<String> fundsOutflows=new ArrayList<>();
                                    if (item.fundsOutflows!=null&&item.fundsOutflows.length>0){
                                        for (int j=0;j<item.fundsOutflows.length;j++){
                                            fundsOutflows.add(dwnull(item.fundsOutflows[j] == null ? "-" : item.fundsOutflows[j]));
                                        }
                                        uploadObj_1.put("fundsOutflows",new JSONArray(fundsOutflows));
                                    }else {
                                        uploadObj_1.put("fundsOutflows",item.fundsOutflows);
                                    }

                                    uploadObj_1.put("ultraLargeDiffer",dwnull(item.ultraLargeDiffer == null ? "-" : item.ultraLargeDiffer));
                                    uploadObj_1.put("largeDiffer",dwnull(item.largeDiffer == null ? "-" : item.largeDiffer));
                                    uploadObj_1.put("mediumDiffer",dwnull(item.mediumDiffer == null ? "-" : item.mediumDiffer));
                                    uploadObj_1.put("smallDiffer",dwnull(item.smallDiffer == null ? "-" : item.smallDiffer));
                                    uploadObj_1.put("largeBuyDealCount",dwnull(item.largeBuyDealCount == null ? "-" : item.largeBuyDealCount));
                                    uploadObj_1.put("largeSellDealCount",dwnull(item.largeSellDealCount == null ? "-" : item.largeSellDealCount));
                                    uploadObj_1.put("dealCountMovingAverage",dwnull(item.dealCountMovingAverage == null ? "-" : item.dealCountMovingAverage));
                                    uploadObj_1.put("buyCount",dwnull(item.buyCount == null ? "-" : item.buyCount));
                                    uploadObj_1.put("sellCount",dwnull(item.sellCount == null ? "-" : item.sellCount));
                                    uploadObj_1.put("BBD",dwnull(item.BBD == null ? "-" : item.BBD));
                                    uploadObj_1.put("BBD5",dwnull(item.BBD5 == null ? "-" : item.BBD5));
                                    uploadObj_1.put("BBD10",dwnull(item.BBD10 == null ? "-" : item.BBD10));
                                    uploadObj_1.put("DDX",dwnull(item.DDX == null ? "-" : item.DDX));
                                    uploadObj_1.put("DDX5",dwnull(item.DDX5 == null ? "-" : item.DDX5));
                                    uploadObj_1.put("DDX10",dwnull(item.DDX10 == null ? "-" : item.DDX10));
                                    uploadObj_1.put("DDY",dwnull(item.DDY == null ? "-" : item.DDY));
                                    uploadObj_1.put("DDY5",dwnull(item.DDY5 == null ? "-" : item.DDY5));
                                    uploadObj_1.put("DDY10",dwnull(item.DDY10 == null ? "-" : item.DDY10));
                                    uploadObj_1.put("DDZ",dwnull(item.DDZ == null ? "-" : item.DDZ));
                                    uploadObj_1.put("RatioBS",dwnull(item.RatioBS == null ? "-" : item.RatioBS));

                                    List<String> othersFundsInflows=new ArrayList<>();
                                    if (item.othersFundsInflows!=null&&item.othersFundsInflows.length>0){
                                        for (int j=0;j<item.othersFundsInflows.length;j++){
                                            othersFundsInflows.add(dwnull(item.othersFundsInflows[j] == null ? "-" : item.othersFundsInflows[j]));
                                        }
                                        uploadObj_1.put("othersFundsInflows",new JSONArray(othersFundsInflows));
                                    }else {
                                        uploadObj_1.put("othersFundsInflows",item.othersFundsInflows);
                                    }

                                    List<String> othersFundsOutflows=new ArrayList<>();
                                    if (item.othersFundsOutflows!=null&&item.othersFundsOutflows.length>0){
                                        for (int j=0;j<item.othersFundsOutflows.length;j++){
                                            othersFundsOutflows.add(dwnull(item.othersFundsOutflows[j] == null ? "-" : item.othersFundsOutflows[j]));
                                        }
                                        uploadObj_1.put("othersFundsOutflows",new JSONArray(othersFundsOutflows));
                                    }else {
                                        uploadObj_1.put("othersFundsOutflows",item.othersFundsOutflows);
                                    }

                                    uploadObj_1.put("fiveMinutesChangeRate",dwnull(item.fiveMinutesChangeRate == null ? "-" : item.fiveMinutesChangeRate));
                                    uploadObj_1.put("largeOrderNumB",dwnull(item.largeOrderNumB == null ? "-" : item.largeOrderNumB));
                                    uploadObj_1.put("largeOrderNumS",dwnull(item.largeOrderNumS == null ? "-" : item.largeOrderNumS));
                                    uploadObj_1.put("bigOrderNumB",dwnull(item.bigOrderNumB == null ? "-" : item.bigOrderNumB));
                                    uploadObj_1.put("bigOrderNumS",dwnull(item.bigOrderNumS == null ? "-" : item.bigOrderNumS));
                                    uploadObj_1.put("midOrderNumB",dwnull(item.midOrderNumB == null ? "-" : item.midOrderNumB));
                                    uploadObj_1.put("midOrderNumS",dwnull(item.midOrderNumS == null ? "-" : item.midOrderNumS));
                                    uploadObj_1.put("smallOrderNumB",dwnull(item.smallOrderNumB == null ? "-" : item.smallOrderNumB));
                                    uploadObj_1.put("smallOrderNumS",dwnull(item.smallOrderNumS == null ? "-" : item.smallOrderNumS));
                                    uploadObj_1.put("mainforceMoneyNetInflow5",dwnull(item.mainforceMoneyNetInflow5 == null ? "-" : item.mainforceMoneyNetInflow5));
                                    uploadObj_1.put("mainforceMoneyNetInflow10",dwnull(item.mainforceMoneyNetInflow10 == null ? "-" : item.mainforceMoneyNetInflow10));
                                    uploadObj_1.put("mainforceMoneyNetInflow20",dwnull(item.mainforceMoneyNetInflow20 == null ? "-" : item.mainforceMoneyNetInflow20));
                                    uploadObj_1.put("ratioMainforceMoneyNetInflow5",dwnull(item.ratioMainforceMoneyNetInflow5 == null ? "-" : item.ratioMainforceMoneyNetInflow5));
                                    uploadObj_1.put("ratioMainforceMoneyNetInflow10",dwnull(item.ratioMainforceMoneyNetInflow10 == null ? "-" : item.ratioMainforceMoneyNetInflow10));
                                    uploadObj_1.put("ratioMainforceMoneyNetInflow20",dwnull(item.ratioMainforceMoneyNetInflow20 == null ? "-" : item.ratioMainforceMoneyNetInflow20));

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
    public String dwnull(String st){
        if (st.equals("一")){
            st="0";
        }else if (st.equals("")){
            st="0";
        }else if(st==null){
            st="0";
        }else if (st.isEmpty()){
            st="0";
        }
        else if (st.equals("-")){
            st="0";
        }     else if (st.equals("+0.00")){
            st="0";
        }     else if (st.equals("-0.00")){
            st="0";
        }
        return  st;
    }
}