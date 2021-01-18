package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
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

                                if (item.presetPrice.equals("一")){
                                    uploadObj.put("presetPrice", item.presetPrice == "一" ? "-" : item.presetPrice);
                                }else {
                                    uploadObj.put("presetPrice", item.presetPrice == null ? "-" : item.presetPrice);
                                }
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

                                if (item.exerciseWay.isEmpty()){
                                    uploadObj.put("exerciseWay", "-");
                                }else {
                                    uploadObj.put("exerciseWay", item.exerciseWay == null ? "-" : item.exerciseWay);
                                }
                                uploadObj.put("orderRatio", item.orderRatio == null ? "-" : item.orderRatio);
                                uploadObj.put("hk_paramStatus", item.hk_paramStatus == null ? "-" : item.hk_paramStatus);//ios无
                                uploadObj.put("fundType", item.fundType == null ? "-" : item.fundType);
                                uploadObj.put("sumBuy", item.sumBuy == null ? "-" : item.sumBuy);
                                uploadObj.put("sumSell", item.sumSell == null ? "-" : item.sumSell);
                                uploadObj.put("averageBuy", item.averageBuy == null ? "-" : item.averageBuy);
                                uploadObj.put("averageSell", item.averageSell == null ? "-" : item.averageSell);
//                        uploadObj.put("upDownFlag", item.upDownFlag);//注意一下IOS android

                                if (item.zh.equals("")){
                                    uploadObj.put("zh", item.zh == "" ? "-" : item.zh);
                                }else {
                                    uploadObj.put("zh", item.zh == null ? "-" : item.zh);
                                }
                                if (item.hh.equals("")){
                                    uploadObj.put("hh", item.hh == "" ? "-" : item.hh);
                                }else {
                                    uploadObj.put("hh", item.hh == null ? "-" : item.hh);
                                }
                                uploadObj.put("st", item.st == null ? "-" : item.st);
                                uploadObj.put("bu", item.bu == null ? "-" : item.bu);
                                uploadObj.put("su", item.su == null ? "-" : item.su);

                                if (item.hs.equals("")){
                                    uploadObj.put("hs", item.hs == "" ? "-" : item.hs);
                                }else {
                                    uploadObj.put("hs", item.hs == null ? "-" : item.hs);
                                }

                                if (item.ac.equals("")){
                                    uploadObj.put("ac", item.ac == "" ? "-" : item.ac);
                                }else {
                                    uploadObj.put("ac", item.ac == null ? "-" : item.ac);
                                }
                                uploadObj.put("qf", item.qf == null ? "-" : item.qf);//ios无
                                uploadObj.put("qc", item.qc == null ? "-" : item.qc);//ios无

                                if (item.ah.equals("")){
                                    uploadObj.put("ah", item.ah == "" ? "-" : item.ah);
                                }else {
                                    uploadObj.put("ah", item.ah == null ? "-" : item.ah);
                                }

                                if (item.VCMFlag.equals("")){
                                    uploadObj.put("VCMFlag", item.VCMFlag == "" ? "-" : item.VCMFlag);
                                }else {
                                    uploadObj.put("VCMFlag", item.VCMFlag == null ? "-" : item.VCMFlag);
                                }

                                if (item.CASFlag.equals("")){
                                    uploadObj.put("CASFlag", item.CASFlag == "" ? "-" : item.CASFlag);
                                }else {
                                    uploadObj.put("CASFlag", item.CASFlag == null ? "-" : item.CASFlag);
                                }
                                //20210118添加 POSFlag 该字段
                                if (item.POSFlag.equals("")){
                                    uploadObj.put("POSFlag", item.POSFlag == "" ? "-" : item.POSFlag);
                                }else {
                                    uploadObj.put("POSFlag", item.POSFlag == null ? "-" : item.POSFlag);
                                }

                                if (item.rp.equals("")){
                                    uploadObj.put("rp", item.rp == "" ? "-" : item.rp);
                                }else {
                                    uploadObj.put("rp", item.rp == null ? "-" : item.rp);
                                }

                                if (item.cd.equals("")){
                                    uploadObj.put("cd", item.cd == "" ? "-" : item.cd);
                                }else {
                                    uploadObj.put("cd", item.cd == null ? "-" : item.cd);
                                }
                                uploadObj.put("hg", item.hg == null ? "-" : item.hg);

                                if (item.sg.equals("")){
                                    uploadObj.put("sg", item.sg == "" ? "-" : item.sg);
                                }else {
                                    uploadObj.put("sg", item.sg == null ? "-" : item.sg);
                                }

                                if (item.fx.equals("")){
                                    uploadObj.put("fx", item.fx == "" ? "-" : item.fx);
                                }else {
                                    uploadObj.put("fx", item.fx == null ? "-" : item.fx);
                                }

                                if (item.ts.equals("")){
                                    uploadObj.put("ts", item.ts == "" ? "-" : item.ts);
                                }else {
                                    uploadObj.put("ts", item.ts == null ? "-" : item.ts);
                                }

                                if (item.add_option_avg_price.equals("一")){
                                    uploadObj.put("add_option_avg_price", item.add_option_avg_price == "一" ? "-" : item.add_option_avg_price);
                                }else {
                                    uploadObj.put("add_option_avg_price", item.add_option_avg_price == null ? "-" : item.add_option_avg_price);
                                }

                                if (item.add_option_avg_pb.equals("一")){
                                    uploadObj.put("add_option_avg_pb", item.add_option_avg_pb == "一" ? "-" : item.add_option_avg_pb);
                                }else {
                                    uploadObj.put("add_option_avg_pb", item.add_option_avg_pb == null ? "-" : item.add_option_avg_pb);
                                }

                                if (item.add_option_avg_close.equals("一")){
                                    uploadObj.put("add_option_avg_close", item.add_option_avg_close == "一" ? "-" : item.add_option_avg_close);
                                }else {
                                    uploadObj.put("add_option_avg_close", item.add_option_avg_close == null ? "-" : item.add_option_avg_close);
                                }
                                uploadObj.put("hk_volum_for_every_hand", item.hk_volum_for_every_hand == null ? "-" : item.hk_volum_for_every_hand);
                                //ios无
                                uploadObj.put("buy_cancel_count", item.buy_cancel_count == null ? "-" : item.buy_cancel_count);

                                if (item.buy_cancel_num.equals("一")){
                                    uploadObj.put("buy_cancel_num", item.buy_cancel_num == "一" ? "-" : item.buy_cancel_num);
                                }else {
                                    uploadObj.put("buy_cancel_num", item.buy_cancel_num == null ? "-" : item.buy_cancel_num);
                                }
                                uploadObj.put("buy_cancel_amount", item.buy_cancel_amount == null ? "-" : item.buy_cancel_amount);
                                uploadObj.put("sell_cancel_count", item.sell_cancel_count == null ? "-" : item.sell_cancel_count);

                                if (item.sell_cancel_num.equals("一")){
                                    uploadObj.put("sell_cancel_num", item.sell_cancel_num == "一" ? "-" : item.sell_cancel_num);
                                }else {
                                    uploadObj.put("sell_cancel_num", item.sell_cancel_num == null ? "-" : item.sell_cancel_num);
                                }
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

                                if (item.IOPV.equals("一")){
                                    uploadObj.put("IOPV", item.IOPV == "一" ? "-" : item.IOPV);
                                }else {
                                    uploadObj.put("IOPV", item.IOPV == null ? "-" : item.IOPV);
                                }

                                if (item.preIOPV.equals("一")){
                                    uploadObj.put("preIOPV", item.preIOPV == "一" ? "-" : item.preIOPV);
                                }else {
                                    uploadObj.put("preIOPV", item.preIOPV == null ? "-" : item.preIOPV);
                                }

                                if (item.stateOfTransfer.equals("")){
                                    uploadObj.put("stateOfTransfer", item.stateOfTransfer == "" ? "-" : item.stateOfTransfer);
                                }else {
                                    uploadObj.put("stateOfTransfer", item.stateOfTransfer == null ? "-" : item.stateOfTransfer);
                                }

                                if (item.typeOfTransfer.equals("")){
                                    uploadObj.put("typeOfTransfer", item.typeOfTransfer == "" ? "-" : item.typeOfTransfer);
                                }else {
                                    uploadObj.put("typeOfTransfer", item.typeOfTransfer == null ? "-" : item.typeOfTransfer);
                                }

                                if (item.exRighitDividend.equals("")){
                                    uploadObj.put("exRighitDividend", item.exRighitDividend == "" ? "-" : item.exRighitDividend);
                                }else {
                                    uploadObj.put("exRighitDividend", item.exRighitDividend == null ? "-" : item.exRighitDividend);
                                }

                                if (item.securityLevel.equals("")){
                                    uploadObj.put("securityLevel", item.securityLevel == "" ? "-" : item.securityLevel);
                                }else {
                                    uploadObj.put("securityLevel", item.securityLevel == null ? "-" : item.securityLevel);
                                }

                                if (item.rpd.equals("")){
                                    uploadObj.put("rpd", item.rpd == "" ? "-" : item.rpd);
                                }else {
                                    uploadObj.put("rpd", item.rpd == null ? "-" : item.rpd);
                                }

                                if (item.cdd.equals("")){
                                    uploadObj.put("cdd", item.cdd == "" ? "-" : item.cdd);
                                }else {
                                    uploadObj.put("cdd", item.cdd == null ? "-" : item.cdd);
                                }
                                //ios无

                                if (item.change2.equals("一")){
                                    uploadObj.put("change2", item.change2 == "一" ? "-" : item.change2);
                                }else {
                                    uploadObj.put("change2", item.change2 == null ? "-" : item.change2);
                                }
                                uploadObj.put("earningsPerShare", item.earningsPerShare == null ? "-" : item.earningsPerShare);
                                uploadObj.put("earningsPerShareReportingPeriod", item.earningsPerShareReportingPeriod == null ? "-" : item.earningsPerShareReportingPeriod);
                                //

                                if (item.hkTExchangeFlag.equals("")){
                                    uploadObj.put("hkTExchangeFlag", item.hkTExchangeFlag == "" ? "-" : item.hkTExchangeFlag);//注意ios
                                }else {
                                    uploadObj.put("hkTExchangeFlag", item.hkTExchangeFlag == null ? "-" : item.hkTExchangeFlag);//注意ios
                                }
                                uploadObj.put("vote", item.vote == null ? "-" : item.vote);//注意ios
                                uploadObj.put("upf", item.upf == null ? "-" : item.upf);//注意ios
                                uploadObj.put("DRCurrentShare", item.DRCurrentShare == null ? "-" : item.DRCurrentShare);
                                uploadObj.put("DRPreviousClosingShare", item.DRPreviousClosingShare == null ? "-" : item.DRPreviousClosingShare);
                                uploadObj.put("DRConversionBase", item.DRConversionBase == null ? "-" : item.DRConversionBase);

                                if (item.DRDepositoryInstitutionCode.isEmpty()){
                                    uploadObj.put("DRDepositoryInstitutionCode", "-");
                                }else {
                                    uploadObj.put("DRDepositoryInstitutionCode", item.DRDepositoryInstitutionCode == null ? "-" : item.DRDepositoryInstitutionCode);
                                }

                                if (item.DRDepositoryInstitutionName.isEmpty()){
                                    uploadObj.put("DRDepositoryInstitutionName", "-");
                                }else {
                                    uploadObj.put("DRDepositoryInstitutionName", item.DRDepositoryInstitutionName == null ? "-" : item.DRDepositoryInstitutionName);
                                }

                                if (item.DRSubjectClosingReferencePrice.isEmpty()){
                                    uploadObj.put("DRSubjectClosingReferencePrice","-");
                                }else {
                                    uploadObj.put("DRSubjectClosingReferencePrice", item.DRSubjectClosingReferencePrice == null ? "-" : item.DRSubjectClosingReferencePrice);
                                }

                                if (item.DR.equals("")){
                                    uploadObj.put("DR", item.DR == "" ? "-" : item.DR);
                                }else {
                                    uploadObj.put("DR", item.DR == null ? "-" : item.DR);
                                }

                                if (item.GDR.equals("")){
                                    uploadObj.put("GDR", item.GDR == "" ? "-" : item.GDR);
                                }else {
                                    uploadObj.put("GDR", item.GDR == null ? "-" : item.GDR);
                                }

                                if (item.DRStockCode.equals("")){
                                    uploadObj.put("DRStockCode", item.DRStockCode == "" ? "-" : item.DRStockCode);
                                }else {
                                    uploadObj.put("DRStockCode", item.DRStockCode == null ? "-" : item.DRStockCode);
                                }

                                if (item.DRStockName.equals("")){
                                    uploadObj.put("DRStockName", item.DRStockName == "" ? "-" : item.DRStockName);
                                }else {
                                    uploadObj.put("DRStockName", item.DRStockName == null ? "-" : item.DRStockName);
                                }
                                uploadObj.put("DRSecuritiesConversionBase", item.DRSecuritiesConversionBase == null ? "-" : item.DRSecuritiesConversionBase);
                                uploadObj.put("DRListingDate", item.DRListingDate == null ? "-" : item.DRListingDate);
                                uploadObj.put("DRFlowStartDate", item.DRFlowStartDate == null ? "-" : item.DRFlowStartDate);
                                uploadObj.put("DRFlowEndDate", item.DRFlowEndDate == null ? "-" : item.DRFlowEndDate);
                                uploadObj.put("changeBP", item.changeBP == null ? "-" : item.changeBP);
                                uploadObj.put("subscribeUpperLimit", item.subscribeUpperLimit == null ? "-" : item.subscribeUpperLimit);
                                uploadObj.put("subscribeLowerLimit", item.subscribeLowerLimit == null ? "-" : item.subscribeLowerLimit);

                                if (item.afterHoursVolume.equals("一")){
                                    uploadObj.put("afterHoursVolume", item.afterHoursVolume == "一" ? "-" : item.afterHoursVolume);
                                }else {
                                    uploadObj.put("afterHoursVolume", item.afterHoursVolume == null ? "-" : item.afterHoursVolume);
                                }
                                uploadObj.put("afterHoursAmount", item.afterHoursAmount == null ? "-" : item.afterHoursAmount);
                                uploadObj.put("afterHoursTransactionNumber", item.afterHoursTransactionNumber == null ? "-" : item.afterHoursTransactionNumber);
                                uploadObj.put("afterHoursWithdrawBuyCount", item.afterHoursWithdrawBuyCount == null ? "-" : item.afterHoursWithdrawBuyCount);

                                if (item.afterHoursWithdrawBuyVolume.equals("一")){
                                    uploadObj.put("afterHoursWithdrawBuyVolume", item.afterHoursWithdrawBuyVolume == "一" ? "-" : item.afterHoursWithdrawBuyVolume);
                                }else {
                                    uploadObj.put("afterHoursWithdrawBuyVolume", item.afterHoursWithdrawBuyVolume == null ? "-" : item.afterHoursWithdrawBuyVolume);
                                }
                                uploadObj.put("afterHoursWithdrawSellCount", item.afterHoursWithdrawSellCount == null ? "-" : item.afterHoursWithdrawSellCount);

                                if (item.afterHoursWithdrawSellVolume.equals("一")){
                                    uploadObj.put("afterHoursWithdrawSellVolume", item.afterHoursWithdrawSellVolume == "一" ? "-" : item.afterHoursWithdrawSellVolume);
                                }else {
                                    uploadObj.put("afterHoursWithdrawSellVolume", item.afterHoursWithdrawSellVolume == null ? "-" : item.afterHoursWithdrawSellVolume);
                                }

                                if (item.afterHoursBuyVolume.equals("一")){
                                    uploadObj.put("afterHoursBuyVolume", item.afterHoursBuyVolume == "一" ? "-" : item.afterHoursBuyVolume);
                                }else {
                                    uploadObj.put("afterHoursBuyVolume", item.afterHoursBuyVolume == null ? "-" : item.afterHoursBuyVolume);
                                }

                                if (item.afterHoursSellVolume.equals("一")){
                                    uploadObj.put("afterHoursSellVolume", item.afterHoursSellVolume == "一" ? "-" : item.afterHoursSellVolume);
                                }else {
                                    uploadObj.put("afterHoursSellVolume", item.afterHoursSellVolume == null ? "-" : item.afterHoursSellVolume);
                                }
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
}
