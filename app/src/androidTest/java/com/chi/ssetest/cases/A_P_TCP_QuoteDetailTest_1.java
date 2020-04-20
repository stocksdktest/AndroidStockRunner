package com.chi.ssetest.cases;

import android.provider.Settings;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
@StockTestcase(StockTestcaseName.A_P_TCP_QuoteDetailTest_1)
public class A_P_TCP_QuoteDetailTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.A_P_TCP_QuoteDetailTest_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000; //上周开会说的Timeout，可以设置成final类型，TCP设置大一些没关系
    private final static String tTag = "TCPTest";

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("A_P_TCP_QuoteDetailTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("A_P_TCP_QuoteDetailTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE_A", "");
        //设置TCP监听的时间  final String tcpSeconds = rule.getParam().optString("SECONDS", "");
        String tcpSeconds = null;
        String endTime = (rule.getParam().optString("ENDDATE", "")).replace("-","");
        String reg = "(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})";
        endTime = endTime.replaceAll(reg, "$1-$2-$3 $4:$5:$6");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(endTime);
        long nowTime= System.currentTimeMillis();
        long setTime= date.getTime();
        if (setTime>nowTime){
            long chaTime=setTime-nowTime;
            tcpSeconds= String.valueOf(chaTime/1000);
            tcpQuote(quoteNumbers,tcpSeconds);
        }else {
            quote(quoteNumbers);
        }

    }

    private void quote(final String quoteNumbers) throws TestcaseException {
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        final JSONObject uploadObj_6 = new JSONObject();
        QuoteDetailRequest request = new QuoteDetailRequest();
        request.send(quoteNumbers, new IResponseInfoCallback<QuoteResponse>() {

            @Override
            public void callback(QuoteResponse quoteResponse) {
                try {
                    assertNotNull(quoteResponse.quoteItems);
                } catch (AssertionError e) {
                    //                        result.completeExceptionally(e);
                    result.complete(new JSONObject());
                }
                QuoteItem item=quoteResponse.quoteItems.get(0);
                System.out.println("++++++++++++++++"+item.subtype+""+item.id+"_________");
                try {
                    JSONObject uploadObj= new JSONObject();
                    if(item!=null){
                        //沪深期权
                        if (item.subtype.equals("3002")){
                            System.out.println("沪深期权--------------"+item.subtype);
                            uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                            uploadObj.put("averageValue", item.averageValue == "一" ? "-" : item.averageValue);//ios无
                            if ("-".equals(item.upDownFlag)){
                                uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                            }else {
                                uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                            }
                            uploadObj.put("change", item.change == "一" ? "-" : item.change);
                            uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                            uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                            uploadObj.put("openInterest", item.openInterest == "一" ? "-" : item.openInterest);
                            uploadObj.put("position_chg", item.position_chg == "一" ? "-" : item.position_chg);//不返回字段 数据库里面没有该字段
                            uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                            uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                            uploadObj.put("setPrice", item.setPrice == "一" ? "-" : item.setPrice);//新加  当日结算价
                            uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                            uploadObj.put("presetPrice", item.presetPrice == "一" ? "-" : item.presetPrice);
                            uploadObj.put("stockUnit", item.stockUnit == "一" ? "-" : item.stockUnit);
                            uploadObj.put("limitUP", item.limitUP == "一" ? "-" : item.limitUP);
                            uploadObj.put("limitDown", item.limitDown == "一" ? "-" : item.limitDown);
                            uploadObj.put("exePrice", item.exePrice == "一" ? "-" : item.exePrice);//执行价格
//                                  uploadObj.put("excercisePx", item.excercisePx == "一" ? "-" : item.excercisePx);//新加  行权价  没有此字段 使用执行价格
                            uploadObj.put("remainDate", item.remainDate == "一" ? "-" : item.remainDate);
                            uploadObj.put("inValue", item.inValue == "一" ? "-" : item.inValue);
                            uploadObj.put("premiumRate", item.premiumRate == "一" ? "-" : item.premiumRate);
                            uploadObj.put("impliedVolatility", item.impliedVolatility == "一" ? "-" : item.impliedVolatility);
                            uploadObj.put("delta", item.delta == "一" ? "-" : item.delta);
                            uploadObj.put("gramma", item.gramma == "一" ? "-" : item.gramma);
                            uploadObj.put("theta", item.theta == "一" ? "-" : item.theta);
                            uploadObj.put("vega", item.vega == "一" ? "-" : item.vega);
                            uploadObj.put("rho", item.rho == "一" ? "-" : item.rho);
                            //五档卖价
                            if (item.sellPrices.size()>0){
                                for (int i=0;i<item.sellPrices.size();i++){
                                    uploadObj.put("sellPrices"+(i+1),item.sellPrices.get(i)== "一" ? "-" : item.sellPrices.get(i));
                                }
                            }else {
                                uploadObj.put("sellPrices1", "-");
                                uploadObj.put("sellPrices2", "-");
                                uploadObj.put("sellPrices3", "-");
                                uploadObj.put("sellPrices4", "-");
                                uploadObj.put("sellPrices5", "-");
                            }
                            //五档卖量
                            if (item.sellVolumes.size()>0){
                                for (int i=0;i<item.sellVolumes.size();i++){
                                    uploadObj.put("sellVolumes"+(i+1),item.sellVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.sellVolumes.get(i)))));
                                }
                            }else {
                                uploadObj.put("sellVolumes1", "-");
                                uploadObj.put("sellVolumes2", "-");
                                uploadObj.put("sellVolumes3", "-");
                                uploadObj.put("sellVolumes4", "-");
                                uploadObj.put("sellVolumes5", "-");
                            }
                            //五档买价
                            if (item.buyPrices.size()>0){
                                for (int i=item.buyPrices.size()-1;i>=0;i--){
                                    uploadObj.put("buyPrices"+(5-i),item.buyPrices.get(i)== "一" ? "-" : item.buyPrices.get(i));
                                }
                            }else {
                                uploadObj.put("buyPrices1", "-");
                                uploadObj.put("buyPrices2", "-");
                                uploadObj.put("buyPrices3", "-");
                                uploadObj.put("buyPrices4", "-");
                                uploadObj.put("buyPrices5", "-");
                            }
                            //五档买量
                            if (item.buyVolumes.size()>0){
                                for (int i=item.buyVolumes.size()-1;i>=0;i--){
                                    uploadObj.put("buyVolumes"+(5-i),item.buyVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.buyVolumes.get(i)))));
                                }
                            }else {
                                uploadObj.put("buyVolumes1", "-");
                                uploadObj.put("buyVolumes2", "-");
                                uploadObj.put("buyVolumes3", "-");
                                uploadObj.put("buyVolumes4", "-");
                                uploadObj.put("buyVolumes5", "-");
                            }
                        }
                        //沪深市场
                        if (item.market.equals("sh")||item.market.equals("sz")){
                            if (item.subtype.equals("1000")||item.subtype.equals("1001")||item.subtype.equals("1002")||item.subtype.equals("1003")||item.subtype.equals("1004")||item.subtype.equals("1005")
                                    ||item.subtype.equals("1011")||item.subtype.equals("1012")||item.subtype.equals("1500")||item.subtype.equals("1510")||item.subtype.equals("1520")||item.subtype.equals("1530")
                                    ||item.subtype.equals("1540")||item.subtype.equals("1600")){
                                System.out.println("沪深市场--------------"+item.subtype);
                                uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                                uploadObj.put("averageValue", item.averageValue == "一" ? "-" : item.averageValue);//ios无
                                if ("-".equals(item.upDownFlag)){
                                    uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                                }else {
                                    uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                                }
                                uploadObj.put("change", item.change == "一" ? "-" : item.change);
                                uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                                uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                                if (item.turnoverRate.isEmpty()){
                                    uploadObj.put("turnoverRate","-");
                                }else {
                                    uploadObj.put("turnoverRate", item.turnoverRate == "一" ? "-" : item.turnoverRate);
                                }
                                if (item.volumeRatio.isEmpty()){
                                    uploadObj.put("volumeRatio","-");
                                }else {
                                    uploadObj.put("volumeRatio",item.volumeRatio=="一" ? "-" : String.format("%.2f",Float.parseFloat(item.volumeRatio)));
                                }
                                uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                                uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                                uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                                uploadObj.put("preClosePrice", item.preClosePrice == "一" ? "-" : item.preClosePrice);
                                uploadObj.put("limitUP", item.limitUP == "一" ? "-" : item.limitUP);
                                uploadObj.put("limitDown", item.limitDown == "一" ? "-" : item.limitDown);
                                uploadObj.put("buyVolume", item.buyVolume == "一" ? "-" : item.buyVolume);
                                uploadObj.put("sellVolume", item.sellVolume == "一" ? "-" : item.sellVolume);
                                uploadObj.put("orderRatio", item.orderRatio == "一" ? "-" : item.orderRatio);
                                uploadObj.put("amplitudeRate", item.amplitudeRate == "一" ? "-" : item.amplitudeRate);
                                uploadObj.put("pe", item.pe == "一" ? "-" : item.pe);
                                uploadObj.put("pe2", item.pe2 == "一" ? "-" : item.pe2);
                                uploadObj.put("netAsset", item.netAsset == "一" ? "-" : item.netAsset);
                                uploadObj.put("pb", item.pb == "一" ? "-" : item.pb);
                                uploadObj.put("capitalization", item.capitalization == "一" ? "-" : item.capitalization);
                                uploadObj.put("totalValue", item.totalValue == "一" ? "-" : item.totalValue);
                                uploadObj.put("circulatingShares", item.circulatingShares == "一" ? "-" : item.circulatingShares);
                                uploadObj.put("flowValue", item.flowValue == "一" ? "-" : item.flowValue);
                                //五档卖价
                                if (item.sellPrices.size()>0){
                                    for (int i=0;i<item.sellPrices.size();i++){
                                        uploadObj.put("sellPrices"+(i+1),item.sellPrices.get(i)== "一" ? "-" : item.sellPrices.get(i));
                                    }
                                }else {
                                    uploadObj.put("sellPrices1", "-");
                                    uploadObj.put("sellPrices2", "-");
                                    uploadObj.put("sellPrices3", "-");
                                    uploadObj.put("sellPrices4", "-");
                                    uploadObj.put("sellPrices5", "-");
                                }
                                //五档卖量
                                if (item.sellVolumes.size()>0){
                                    for (int i=0;i<item.sellVolumes.size();i++){
                                        uploadObj.put("sellVolumes"+(i+1),item.sellVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.sellVolumes.get(i)))));
                                    }
                                }else {
                                    uploadObj.put("sellVolumes1", "-");
                                    uploadObj.put("sellVolumes2", "-");
                                    uploadObj.put("sellVolumes3", "-");
                                    uploadObj.put("sellVolumes4", "-");
                                    uploadObj.put("sellVolumes5", "-");
                                }
                                //五档买价
                                if (item.buyPrices.size()>0){
                                    for (int i=item.buyPrices.size()-1;i>=0;i--){
                                        uploadObj.put("buyPrices"+(5-i),item.buyPrices.get(i)== "一" ? "-" : item.buyPrices.get(i));
                                    }
                                }else {
                                    uploadObj.put("buyPrices1", "-");
                                    uploadObj.put("buyPrices2", "-");
                                    uploadObj.put("buyPrices3", "-");
                                    uploadObj.put("buyPrices4", "-");
                                    uploadObj.put("buyPrices5", "-");
                                }
                                //五档买量
                                if (item.buyVolumes.size()>0){
                                    for (int i=item.buyVolumes.size()-1;i>=0;i--){
                                        uploadObj.put("buyVolumes"+(5-i),item.buyVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.buyVolumes.get(i)))));
                                    }
                                }else {
                                    uploadObj.put("buyVolumes1", "-");
                                    uploadObj.put("buyVolumes2", "-");
                                    uploadObj.put("buyVolumes3", "-");
                                    uploadObj.put("buyVolumes4", "-");
                                    uploadObj.put("buyVolumes5", "-");
                                }
                            }
                        }
                        //期货市场
                        if (item.market.equals("cff")||item.market.equals("dce")||item.market.equals("czce")||item.market.equals("shfe")||item.market.equals("ine")){
                            System.out.println("期货市场--------------"+item.market+"++"+item.datetime);
                            uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                            uploadObj.put("averageValue", item.averageValue == "一" ? "-" : item.averageValue);//ios无
                            uploadObj.put("change", item.change == "一" ? "-" : item.change);
                            if ("-".equals(item.upDownFlag)){
                                uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                            }else {
                                uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                            }
                            uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                            uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                            uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                            uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                            uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                            uploadObj.put("buyVolume", item.buyVolume == "一" ? "-" : item.buyVolume);
                            uploadObj.put("sellVolume", item.sellVolume == "一" ? "-" : item.sellVolume);
                            uploadObj.put("openInterest", item.openInterest == "一" ? "-" : item.openInterest);
                            uploadObj.put("preSettlement", item.preSettlement == "一" ? "-" : item.preSettlement);
                            uploadObj.put("position_chg", item.position_chg == "一" ? "-" : item.position_chg);
                            uploadObj.put("askpx1", item.sellPrices.get(0) == "一" ? "-" : item.sellPrices.get(0));
                            uploadObj.put("askvol1", item.sellVolumes.get(0) == "一" ? "-" : item.sellVolumes.get(0));
                            uploadObj.put("bidpx1", item.buyPrices.get(0) == "一" ? "-" : item.buyPrices.get(0));
                            uploadObj.put("bidvol1", item.buyVolumes.get(0) == "一" ? "-" : item.buyVolumes.get(0));
                            uploadObj.put("turnoverRate", item.turnoverRate == "一" ? "-" : item.turnoverRate);//不返回字段 数据库里面没有该字段
                            uploadObj.put("limitUP", item.limitUP == "一" ? "-" : item.limitUP);
                            uploadObj.put("limitDown", item.limitDown == "一" ? "-" : item.limitDown);
                        }
                        //指数
                        if (item.subtype.equals("1400")){
                            System.out.println("指数--------------"+item.subtype);
                            uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                            if ("-".equals(item.upDownFlag)){
                                uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                            }else {
                                uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                            }
                            uploadObj.put("change", item.change == "一" ? "-" : item.change);
                            uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                            uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                            uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                            uploadObj.put("preClosePrice", item.preClosePrice == "一" ? "-" : item.preClosePrice);
                            uploadObj.put("amplitudeRate", item.amplitudeRate == "一" ? "-" : item.amplitudeRate);
                            uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                            if (item.turnoverRate.isEmpty()){
                                uploadObj.put("turnoverRate","-");
                            }else {
                                uploadObj.put("turnoverRate", item.turnoverRate == "一" ? "-" : item.turnoverRate);
                            }
                            if (item.volumeRatio.isEmpty()){
                                uploadObj.put("volumeRatio","-");
                            }else {
                                uploadObj.put("volumeRatio",item.volumeRatio=="一" ? "-" : String.format("%.2f",Float.parseFloat(item.volumeRatio)));
                            }
                            uploadObj.put("upCount", item.upCount == "一" ? "-" : item.upCount);
                            uploadObj.put("sameCount", item.sameCount == "一" ? "-" : item.sameCount);
                            uploadObj.put("downCount", item.downCount == "一" ? "-" : item.downCount);
                            uploadObj.put("averageValue", item.averageValue == "一" ? "-" : item.averageValue);//ios无
                            uploadObj.put("orderRatio", item.orderRatio == "一" ? "-" : item.orderRatio);
                            uploadObj.put("buyVolume", item.buyVolume == "一" ? "-" : item.buyVolume);
                            uploadObj.put("sellVolume", item.sellVolume == "一" ? "-" : item.sellVolume);
                        }

                        //港股
                        if (item.market.equals("hk")&&!item.subtype.equals("1300")){
                            System.out.println("港股--------------"+item.subtype);
                            uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                            uploadObj.put("averageValue", item.averageValue == "一" ? "-" : item.averageValue);//ios无
                            if ("-".equals(item.upDownFlag)){
                                uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                            }else {
                                uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                            }
                            uploadObj.put("change", item.change == "一" ? "-" : item.change);
                            uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                            uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                            if (item.turnoverRate.isEmpty()){
                                uploadObj.put("turnoverRate","-");
                            }else {
                                uploadObj.put("turnoverRate", item.turnoverRate == "一" ? "-" : item.turnoverRate);
                            }
                            uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                            uploadObj.put("preClosePrice", item.preClosePrice == "一" ? "-" : item.preClosePrice);
                            uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                            uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                            uploadObj.put("buyVolume", item.buyVolume == "一" ? "-" : item.buyVolume);
                            uploadObj.put("sellVolume", item.sellVolume == "一" ? "-" : item.sellVolume);
                            uploadObj.put("pe", item.pe == "一" ? "-" : item.pe);
                            uploadObj.put("netAsset", item.netAsset == "一" ? "-" : item.netAsset);
                            uploadObj.put("pb", item.pb == "一" ? "-" : item.pb);
                            uploadObj.put("capitalization", item.capitalization == "一" ? "-" : item.capitalization);
                            uploadObj.put("totalValue", item.totalValue == "一" ? "-" : item.totalValue);
                            if (item.hs.isEmpty()){
                                uploadObj.put("hs", "-");
                            }else {
                                uploadObj.put("hs", item.hs == "一" ? "-" : item.hs);
                            }
                            uploadObj.put("HKTotalValue", item.HKTotalValue == "一" ? "-" : item.HKTotalValue);
                        }
                        //基金
                        if (item.subtype.equals("1100")||item.subtype.equals("1110")||item.subtype.equals("1120")||item.subtype.equals("1130")||item.subtype.equals("1131")
                                ||item.subtype.equals("1132")||item.subtype.equals("1133")||item.subtype.equals("1140")){
                            System.out.println("基金--------------"+item.subtype);
                            uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                            uploadObj.put("change", item.change == "一" ? "-" : item.change);
                            if ("-".equals(item.upDownFlag)){
                                uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                            }else {
                                uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                            }
                            if (item.turnoverRate.isEmpty()){
                                uploadObj.put("turnoverRate","-");
                            }else {
                                uploadObj.put("turnoverRate", item.turnoverRate == "一" ? "-" : item.turnoverRate);
                            }
                            uploadObj.put("limitUP", item.limitUP == "一" ? "-" : item.limitUP);
                            uploadObj.put("limitDown", item.limitDown == "一" ? "-" : item.limitDown);
                            uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                            uploadObj.put("preClosePrice", item.preClosePrice == "一" ? "-" : item.preClosePrice);
                            uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                            uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                            if (item.volumeRatio.isEmpty()){
                                uploadObj.put("volumeRatio","-");
                            }else {
                                uploadObj.put("volumeRatio",item.volumeRatio=="一" ? "-" : String.format("%.2f",Float.parseFloat(item.volumeRatio)));
                            }
                            uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                            uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                            uploadObj.put("IOPV", item.IOPV == "一" ? "-" : item.IOPV);
                            //五档卖价
                            if (item.sellPrices.size()>0){
                                for (int i=0;i<item.sellPrices.size();i++){
                                    uploadObj.put("sellPrices"+(i+1),item.sellPrices.get(i)== "一" ? "-" : item.sellPrices.get(i));
                                }
                            }else {
                                uploadObj.put("sellPrices1", "-");
                                uploadObj.put("sellPrices2", "-");
                                uploadObj.put("sellPrices3", "-");
                                uploadObj.put("sellPrices4", "-");
                                uploadObj.put("sellPrices5", "-");
                            }
                            //五档卖量
                            if (item.sellVolumes.size()>0){
                                for (int i=0;i<item.sellVolumes.size();i++){
                                    uploadObj.put("sellVolumes"+(i+1),item.sellVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.sellVolumes.get(i)))));
                                }
                            }else {
                                uploadObj.put("sellVolumes1", "-");
                                uploadObj.put("sellVolumes2", "-");
                                uploadObj.put("sellVolumes3", "-");
                                uploadObj.put("sellVolumes4", "-");
                                uploadObj.put("sellVolumes5", "-");
                            }
                            //五档买价
                            if (item.buyPrices.size()>0){
                                for (int i=item.buyPrices.size()-1;i>=0;i--){
                                    uploadObj.put("buyPrices"+(5-i),item.buyPrices.get(i)== "一" ? "-" : item.buyPrices.get(i));
                                }
                            }else {
                                uploadObj.put("buyPrices1", "-");
                                uploadObj.put("buyPrices2", "-");
                                uploadObj.put("buyPrices3", "-");
                                uploadObj.put("buyPrices4", "-");
                                uploadObj.put("buyPrices5", "-");
                            }
                            //五档买量
                            if (item.buyVolumes.size()>0){
                                for (int i=item.buyVolumes.size()-1;i>=0;i--){
                                    uploadObj.put("buyVolumes"+(5-i),item.buyVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.buyVolumes.get(i)))));
                                }
                            }else {
                                uploadObj.put("buyVolumes1", "-");
                                uploadObj.put("buyVolumes2", "-");
                                uploadObj.put("buyVolumes3", "-");
                                uploadObj.put("buyVolumes4", "-");
                                uploadObj.put("buyVolumes5", "-");
                            }
                        }
                        //债券
                        if (item.subtype.equals("1300")||item.subtype.equals("1311")||item.subtype.equals("1312")||item.subtype.equals("1313")||item.subtype.equals("1314")||item.subtype.equals("1321")||item.subtype.equals("1322")){
                            System.out.println("债券--------------"+item.subtype);
                            uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                            uploadObj.put("change", item.change == "一" ? "-" : item.change);
                            if ("-".equals(item.upDownFlag)){
                                uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                            }else {
                                uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                            }
                            uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                            if (item.volumeRatio.isEmpty()){
                                uploadObj.put("volumeRatio","-");
                            }else {
                                uploadObj.put("volumeRatio",item.volumeRatio=="一" ? "-" : String.format("%.2f",Float.parseFloat(item.volumeRatio)));
                            }
                            uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                            uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                            uploadObj.put("preClosePrice", item.preClosePrice == "一" ? "-" : item.preClosePrice);
                            uploadObj.put("buyVolume", item.buyVolume == "一" ? "-" : item.buyVolume);
                            uploadObj.put("sellVolume", item.sellVolume == "一" ? "-" : item.sellVolume);
                            //五档卖价
                            if (item.sellPrices.size()>0){
                                for (int i=0;i<item.sellPrices.size();i++){
                                    uploadObj.put("sellPrices"+(i+1),item.sellPrices.get(i)== "一" ? "-" : item.sellPrices.get(i));
                                }
                            }else {
                                uploadObj.put("sellPrices1", "-");
                                uploadObj.put("sellPrices2", "-");
                                uploadObj.put("sellPrices3", "-");
                                uploadObj.put("sellPrices4", "-");
                                uploadObj.put("sellPrices5", "-");
                            }
                            //五档卖量
                            if (item.sellVolumes.size()>0){
                                for (int i=0;i<item.sellVolumes.size();i++){
                                    uploadObj.put("sellVolumes"+(i+1),item.sellVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.sellVolumes.get(i)))));
                                }
                            }else {
                                uploadObj.put("sellVolumes1", "-");
                                uploadObj.put("sellVolumes2", "-");
                                uploadObj.put("sellVolumes3", "-");
                                uploadObj.put("sellVolumes4", "-");
                                uploadObj.put("sellVolumes5", "-");
                            }
                            //五档买价
                            if (item.buyPrices.size()>0){
                                for (int i=item.buyPrices.size()-1;i>=0;i--){
                                    uploadObj.put("buyPrices"+(5-i),item.buyPrices.get(i)== "一" ? "-" : item.buyPrices.get(i));
                                }
                            }else {
                                uploadObj.put("buyPrices1", "-");
                                uploadObj.put("buyPrices2", "-");
                                uploadObj.put("buyPrices3", "-");
                                uploadObj.put("buyPrices4", "-");
                                uploadObj.put("buyPrices5", "-");
                            }
                            //五档买量
                            if (item.buyVolumes.size()>0){
                                for (int i=item.buyVolumes.size()-1;i>=0;i--){
                                    uploadObj.put("buyVolumes"+(5-i),item.buyVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.buyVolumes.get(i)))));
                                }
                            }else {
                                uploadObj.put("buyVolumes1", "-");
                                uploadObj.put("buyVolumes2", "-");
                                uploadObj.put("buyVolumes3", "-");
                                uploadObj.put("buyVolumes4", "-");
                                uploadObj.put("buyVolumes5", "-");
                            }
                        }
                        //科创板
                        if (item.market.equals("sh")){
                            if (item.subtype.equals("1006")||item.subtype.equals("1521")){
                                System.out.println("科创板--------------"+item.subtype);
                                uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                                uploadObj.put("averageValue", item.averageValue == "一" ? "-" : item.averageValue);//ios无
                                if ("-".equals(item.upDownFlag)){
                                    uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                                }else {
                                    uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                                }
                                uploadObj.put("change", item.change == "一" ? "-" : item.change);
                                uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                                uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                                if (item.turnoverRate.isEmpty()){
                                    uploadObj.put("turnoverRate","-");
                                }else {
                                    uploadObj.put("turnoverRate", item.turnoverRate == "一" ? "-" : item.turnoverRate);
                                }
                                if (item.volumeRatio.isEmpty()){
                                    uploadObj.put("volumeRatio","-");
                                }else {
                                    uploadObj.put("volumeRatio",item.volumeRatio=="一" ? "-" : String.format("%.2f",Float.parseFloat(item.volumeRatio)));
                                }
                                uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                                uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                                uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                                uploadObj.put("preClosePrice", item.preClosePrice == "一" ? "-" : item.preClosePrice);
                                uploadObj.put("limitUP", item.limitUP == "一" ? "-" : item.limitUP);
                                uploadObj.put("limitDown", item.limitDown == "一" ? "-" : item.limitDown);
                                uploadObj.put("buyVolume", item.buyVolume == "一" ? "-" : item.buyVolume);
                                uploadObj.put("sellVolume", item.sellVolume == "一" ? "-" : item.sellVolume);
                                uploadObj.put("orderRatio", item.orderRatio == "一" ? "-" : item.orderRatio);
                                uploadObj.put("amplitudeRate", item.amplitudeRate == "一" ? "-" : item.amplitudeRate);
                                uploadObj.put("afterHoursVolume", item.afterHoursVolume == "一" ? "-" : item.afterHoursVolume);
                                uploadObj.put("afterHoursAmount", item.afterHoursAmount == "" ? "-" : item.afterHoursAmount);//不返回字段 数据库里面没有该字段

                                uploadObj.put("pe", item.pe == "一" ? "-" : item.pe);
                                uploadObj.put("pe2", item.pe2 == "一" ? "-" : item.pe2);
                                uploadObj.put("netAsset", item.netAsset == "一" ? "-" : item.netAsset);
                                uploadObj.put("pb", item.pb == "一" ? "-" : item.pb);
                                uploadObj.put("vote", item.vote == "一" ? "-" : item.vote);//注意ios
                                uploadObj.put("capitalization", item.capitalization == "一" ? "-" : item.capitalization);
                                uploadObj.put("totalValue", item.totalValue == "一" ? "-" : item.totalValue);
                                uploadObj.put("circulatingShares", item.circulatingShares == "一" ? "-" : item.circulatingShares);
                                uploadObj.put("flowValue", item.flowValue == "一" ? "-" : item.flowValue);
                                uploadObj.put("issuedCapital", item.issuedCapital == "一" ? "-" : item.issuedCapital);
                                uploadObj.put("upf", item.upf == "一" ? "-" : item.upf);//注意ios
                                //五档卖价
                                if (item.sellPrices.size()>0){
                                    for (int i=0;i<item.sellPrices.size();i++){
                                        uploadObj.put("sellPrices"+(i+1),item.sellPrices.get(i)== "一" ? "-" : item.sellPrices.get(i));
                                    }
                                }else {
                                    uploadObj.put("sellPrices1", "-");
                                    uploadObj.put("sellPrices2", "-");
                                    uploadObj.put("sellPrices3", "-");
                                    uploadObj.put("sellPrices4", "-");
                                    uploadObj.put("sellPrices5", "-");
                                }
                                //五档卖量
                                if (item.sellVolumes.size()>0){
                                    for (int i=0;i<item.sellVolumes.size();i++){
                                        uploadObj.put("sellVolumes"+(i+1),item.sellVolumes.get(i)== "一" ? "-" : item.sellVolumes.get(i));
                                    }
                                }else {
                                    uploadObj.put("sellVolumes1", "-");
                                    uploadObj.put("sellVolumes2", "-");
                                    uploadObj.put("sellVolumes3", "-");
                                    uploadObj.put("sellVolumes4", "-");
                                    uploadObj.put("sellVolumes5", "-");
                                }
                                //五档买价
                                if (item.buyPrices.size()>0){
                                    for (int i=item.buyPrices.size()-1;i>=0;i--){
                                        uploadObj.put("buyPrices"+(5-i),item.buyPrices.get(i)== "一" ? "-" : item.buyPrices.get(i));
                                    }
                                }else {
                                    uploadObj.put("buyPrices1", "-");
                                    uploadObj.put("buyPrices2", "-");
                                    uploadObj.put("buyPrices3", "-");
                                    uploadObj.put("buyPrices4", "-");
                                    uploadObj.put("buyPrices5", "-");
                                }
                                //五档买量
                                if (item.buyVolumes.size()>0){
                                    for (int i=item.buyVolumes.size()-1;i>=0;i--){
                                        uploadObj.put("buyVolumes"+(5-i),item.buyVolumes.get(i)== "一" ? "-" : item.buyVolumes.get(i));
                                    }
                                }else {
                                    uploadObj.put("buyVolumes1", "-");
                                    uploadObj.put("buyVolumes2", "-");
                                    uploadObj.put("buyVolumes3", "-");
                                    uploadObj.put("buyVolumes4", "-");
                                    uploadObj.put("buyVolumes5", "-");
                                }
                            }
                        }
                        uploadObj_6.put(item.datetime,uploadObj);
                    }
                    result.complete(uploadObj_6);
//                    Log.d("tcp00", String.valueOf(uploadObj_6));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void exception(ErrorInfo errorInfo) {
                result.completeExceptionally(new Exception(errorInfo.toString()));
            }
        });

        try {
            JSONObject resultObj = (JSONObject) result.get(timeout_ms, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
        } catch (Exception e) {
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }

    }

    private void tcpQuote(String quoteNumbers, String tcpSeconds) throws TestcaseException {
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
                                //沪深期权
                                if (item.subtype.equals("3002")){
                                    System.out.println("沪深期权++++++++++"+item.subtype);
                                    uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                                    uploadObj.put("averageValue", item.averageValue == "一" ? "-" : item.averageValue);//ios无
                                    if ("-".equals(item.upDownFlag)){
                                        uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                                    }else {
                                        uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                                    }
                                    uploadObj.put("change", item.change == "一" ? "-" : item.change);
                                    uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                                    uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                                    uploadObj.put("openInterest", item.openInterest == "一" ? "-" : item.openInterest);
                                    uploadObj.put("position_chg", item.position_chg == "一" ? "-" : item.position_chg);//不返回字段 数据库里面没有该字段
                                    uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                                    uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                                    uploadObj.put("setPrice", item.setPrice == "一" ? "-" : item.setPrice);//新加  当日结算价
                                    uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                                    uploadObj.put("presetPrice", item.presetPrice == "一" ? "-" : item.presetPrice);
                                    uploadObj.put("stockUnit", item.stockUnit == "一" ? "-" : item.stockUnit);
                                    uploadObj.put("limitUP", item.limitUP == "一" ? "-" : item.limitUP);
                                    uploadObj.put("limitDown", item.limitDown == "一" ? "-" : item.limitDown);
                                    uploadObj.put("exePrice", item.exePrice == "一" ? "-" : item.exePrice);//执行价格
//                                  uploadObj.put("excercisePx", item.excercisePx == "一" ? "-" : item.excercisePx);//新加  行权价  没有此字段 使用执行价格
                                    uploadObj.put("remainDate", item.remainDate == "一" ? "-" : item.remainDate);
                                    uploadObj.put("inValue", item.inValue == "一" ? "-" : item.inValue);
                                    uploadObj.put("premiumRate", item.premiumRate == "一" ? "-" : item.premiumRate);
                                    uploadObj.put("impliedVolatility", item.impliedVolatility == "一" ? "-" : item.impliedVolatility);
                                    uploadObj.put("delta", item.delta == "一" ? "-" : item.delta);
                                    uploadObj.put("gramma", item.gramma == "一" ? "-" : item.gramma);
                                    uploadObj.put("theta", item.theta == "一" ? "-" : item.theta);
                                    uploadObj.put("vega", item.vega == "一" ? "-" : item.vega);
                                    uploadObj.put("rho", item.rho == "一" ? "-" : item.rho);
                                    //五档卖价
                                    if (item.sellPrices.size()>0){
                                        for (int i=0;i<item.sellPrices.size();i++){
                                            uploadObj.put("sellPrices"+(i+1),item.sellPrices.get(i)== "一" ? "-" : item.sellPrices.get(i));
                                        }
                                    }else {
                                        uploadObj.put("sellPrices1", "-");
                                        uploadObj.put("sellPrices2", "-");
                                        uploadObj.put("sellPrices3", "-");
                                        uploadObj.put("sellPrices4", "-");
                                        uploadObj.put("sellPrices5", "-");
                                    }
                                    //五档卖量
                                    if (item.sellVolumes.size()>0){
                                        for (int i=0;i<item.sellVolumes.size();i++){
                                            uploadObj.put("sellVolumes"+(i+1),item.sellVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.sellVolumes.get(i)))));
                                        }
                                    }else {
                                        uploadObj.put("sellVolumes1", "-");
                                        uploadObj.put("sellVolumes2", "-");
                                        uploadObj.put("sellVolumes3", "-");
                                        uploadObj.put("sellVolumes4", "-");
                                        uploadObj.put("sellVolumes5", "-");
                                    }
                                    //五档买价
                                    if (item.buyPrices.size()>0){
                                        for (int i=item.buyPrices.size()-1;i>=0;i--){
                                            uploadObj.put("buyPrices"+(5-i),item.buyPrices.get(i)== "一" ? "-" : item.buyPrices.get(i));
                                        }
                                    }else {
                                        uploadObj.put("buyPrices1", "-");
                                        uploadObj.put("buyPrices2", "-");
                                        uploadObj.put("buyPrices3", "-");
                                        uploadObj.put("buyPrices4", "-");
                                        uploadObj.put("buyPrices5", "-");
                                    }
                                    //五档买量
                                    if (item.buyVolumes.size()>0){
                                        for (int i=item.buyVolumes.size()-1;i>=0;i--){
                                            uploadObj.put("buyVolumes"+(5-i),item.buyVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.buyVolumes.get(i)))));
                                        }
                                    }else {
                                        uploadObj.put("buyVolumes1", "-");
                                        uploadObj.put("buyVolumes2", "-");
                                        uploadObj.put("buyVolumes3", "-");
                                        uploadObj.put("buyVolumes4", "-");
                                        uploadObj.put("buyVolumes5", "-");
                                    }
                                }
                                //沪深市场
                                if (item.market.equals("sh")||item.market.equals("sz")){
                                    if (item.subtype.equals("1000")||item.subtype.equals("1001")||item.subtype.equals("1002")||item.subtype.equals("1003")||item.subtype.equals("1004")||item.subtype.equals("1005")
                                            ||item.subtype.equals("1011")||item.subtype.equals("1012")||item.subtype.equals("1500")||item.subtype.equals("1510")||item.subtype.equals("1520")||item.subtype.equals("1530")
                                            ||item.subtype.equals("1540")||item.subtype.equals("1600")){
                                        System.out.println("沪深市场++++++++++++++"+item.subtype);
                                        uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                                        uploadObj.put("averageValue", item.averageValue == "一" ? "-" : item.averageValue);//ios无
                                        if ("-".equals(item.upDownFlag)){
                                            uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                                        }else {
                                            uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                                        }
                                        uploadObj.put("change", item.change == "一" ? "-" : item.change);
                                        uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                                        uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                                        if (item.turnoverRate.isEmpty()){
                                            uploadObj.put("turnoverRate","-");
                                        }else {
                                            uploadObj.put("turnoverRate", item.turnoverRate == "一" ? "-" : item.turnoverRate);
                                        }
                                        if (item.volumeRatio.isEmpty()){
                                            uploadObj.put("volumeRatio","-");
                                        }else {
                                            uploadObj.put("volumeRatio",item.volumeRatio=="一" ? "-" : String.format("%.2f",Float.parseFloat(item.volumeRatio)));
                                        }
                                        uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                                        uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                                        uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                                        uploadObj.put("preClosePrice", item.preClosePrice == "一" ? "-" : item.preClosePrice);
                                        uploadObj.put("limitUP", item.limitUP == "一" ? "-" : item.limitUP);
                                        uploadObj.put("limitDown", item.limitDown == "一" ? "-" : item.limitDown);
                                        uploadObj.put("buyVolume", item.buyVolume == "一" ? "-" : item.buyVolume);
                                        uploadObj.put("sellVolume", item.sellVolume == "一" ? "-" : item.sellVolume);
                                        uploadObj.put("orderRatio", item.orderRatio == "一" ? "-" : item.orderRatio);
                                        uploadObj.put("amplitudeRate", item.amplitudeRate == "一" ? "-" : item.amplitudeRate);
                                        uploadObj.put("pe", item.pe == "一" ? "-" : item.pe);
                                        uploadObj.put("pe2", item.pe2 == "一" ? "-" : item.pe2);
                                        uploadObj.put("netAsset", item.netAsset == "一" ? "-" : item.netAsset);
                                        uploadObj.put("pb", item.pb == "一" ? "-" : item.pb);
                                        uploadObj.put("capitalization", item.capitalization == "一" ? "-" : item.capitalization);
                                        uploadObj.put("totalValue", item.totalValue == "一" ? "-" : item.totalValue);
                                        uploadObj.put("circulatingShares", item.circulatingShares == "一" ? "-" : item.circulatingShares);
                                        uploadObj.put("flowValue", item.flowValue == "一" ? "-" : item.flowValue);
                                        //五档卖价
                                        if (item.sellPrices.size()>0){
                                            for (int i=0;i<item.sellPrices.size();i++){
                                                uploadObj.put("sellPrices"+(i+1),item.sellPrices.get(i)== "一" ? "-" : item.sellPrices.get(i));
                                            }
                                        }else {
                                            uploadObj.put("sellPrices1", "-");
                                            uploadObj.put("sellPrices2", "-");
                                            uploadObj.put("sellPrices3", "-");
                                            uploadObj.put("sellPrices4", "-");
                                            uploadObj.put("sellPrices5", "-");
                                        }
                                        //五档卖量
                                        if (item.sellVolumes.size()>0){
                                            for (int i=0;i<item.sellVolumes.size();i++){
                                                uploadObj.put("sellVolumes"+(i+1),item.sellVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.sellVolumes.get(i)))));
                                            }
                                        }else {
                                            uploadObj.put("sellVolumes1", "-");
                                            uploadObj.put("sellVolumes2", "-");
                                            uploadObj.put("sellVolumes3", "-");
                                            uploadObj.put("sellVolumes4", "-");
                                            uploadObj.put("sellVolumes5", "-");
                                        }
                                        //五档买价
                                        if (item.buyPrices.size()>0){
                                            for (int i=item.buyPrices.size()-1;i>=0;i--){
                                                uploadObj.put("buyPrices"+(5-i),item.buyPrices.get(i)== "一" ? "-" : item.buyPrices.get(i));
                                            }
                                        }else {
                                            uploadObj.put("buyPrices1", "-");
                                            uploadObj.put("buyPrices2", "-");
                                            uploadObj.put("buyPrices3", "-");
                                            uploadObj.put("buyPrices4", "-");
                                            uploadObj.put("buyPrices5", "-");
                                        }
                                        //五档买量
                                        if (item.buyVolumes.size()>0){
                                            for (int i=item.buyVolumes.size()-1;i>=0;i--){
                                                uploadObj.put("buyVolumes"+(5-i),item.buyVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.buyVolumes.get(i)))));
                                            }
                                        }else {
                                            uploadObj.put("buyVolumes1", "-");
                                            uploadObj.put("buyVolumes2", "-");
                                            uploadObj.put("buyVolumes3", "-");
                                            uploadObj.put("buyVolumes4", "-");
                                            uploadObj.put("buyVolumes5", "-");
                                        }
                                    }
                                }
                                //期货市场
                                if (item.market.equals("cff")||item.market.equals("dce")||item.market.equals("czce")||item.market.equals("shfe")||item.market.equals("ine")){
                                    System.out.println("期货市场++++++++++++++++"+item.market+"++"+item.datetime);
                                    uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                                    uploadObj.put("averageValue", item.averageValue == "一" ? "-" : item.averageValue);//ios无
                                    uploadObj.put("change", item.change == "一" ? "-" : item.change);
                                    if ("-".equals(item.upDownFlag)){
                                        uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                                    }else {
                                        uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                                    }
                                    uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                                    uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                                    uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                                    uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                                    uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                                    uploadObj.put("buyVolume", item.buyVolume == "一" ? "-" : item.buyVolume);
                                    uploadObj.put("sellVolume", item.sellVolume == "一" ? "-" : item.sellVolume);
                                    uploadObj.put("openInterest", item.openInterest == "一" ? "-" : item.openInterest);
                                    uploadObj.put("preSettlement", item.preSettlement == "一" ? "-" : item.preSettlement);
                                    uploadObj.put("position_chg", item.position_chg == "一" ? "-" : item.position_chg);
                                    uploadObj.put("askpx1", item.sellPrices.get(0) == "一" ? "-" : item.sellPrices.get(0));
                                    uploadObj.put("askvol1", item.sellVolumes.get(0) == "一" ? "-" : item.sellVolumes.get(0));
                                    uploadObj.put("bidpx1", item.buyPrices.get(0) == "一" ? "-" : item.buyPrices.get(0));
                                    uploadObj.put("bidvol1", item.buyVolumes.get(0) == "一" ? "-" : item.buyVolumes.get(0));
                                    uploadObj.put("turnoverRate", item.turnoverRate == "一" ? "-" : item.turnoverRate);//不返回字段 数据库里面没有该字段
                                    uploadObj.put("limitUP", item.limitUP == "一" ? "-" : item.limitUP);
                                    uploadObj.put("limitDown", item.limitDown == "一" ? "-" : item.limitDown);
                                }
                                //指数
                                if (item.subtype.equals("1400")){
                                    System.out.println("指数++++++++++++++"+item.subtype);
                                    uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                                    if ("-".equals(item.upDownFlag)){
                                        uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                                    }else {
                                        uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                                    }
                                    uploadObj.put("change", item.change == "一" ? "-" : item.change);
                                    uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                                    uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                                    uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                                    uploadObj.put("preClosePrice", item.preClosePrice == "一" ? "-" : item.preClosePrice);
                                    uploadObj.put("amplitudeRate", item.amplitudeRate == "一" ? "-" : item.amplitudeRate);
                                    uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                                    if (item.turnoverRate.isEmpty()){
                                        uploadObj.put("turnoverRate","-");
                                    }else {
                                        uploadObj.put("turnoverRate", item.turnoverRate == "一" ? "-" : item.turnoverRate);
                                    }
                                    if (item.volumeRatio.isEmpty()){
                                        uploadObj.put("volumeRatio","-");
                                    }else {
                                        uploadObj.put("volumeRatio",item.volumeRatio=="一" ? "-" : String.format("%.2f",Float.parseFloat(item.volumeRatio)));
                                    }
                                    uploadObj.put("upCount", item.upCount == "一" ? "-" : item.upCount);
                                    uploadObj.put("sameCount", item.sameCount == "一" ? "-" : item.sameCount);
                                    uploadObj.put("downCount", item.downCount == "一" ? "-" : item.downCount);
                                    uploadObj.put("averageValue", item.averageValue == "一" ? "-" : item.averageValue);//ios无
                                    uploadObj.put("orderRatio", item.orderRatio == "一" ? "-" : item.orderRatio);
                                    uploadObj.put("buyVolume", item.buyVolume == "一" ? "-" : item.buyVolume);
                                    uploadObj.put("sellVolume", item.sellVolume == "一" ? "-" : item.sellVolume);
                                }

                                //港股
                                if (item.market.equals("hk")){
                                    System.out.println("港股++++++++++++++++"+item.subtype);
                                    uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                                    uploadObj.put("averageValue", item.averageValue == "一" ? "-" : item.averageValue);//ios无
                                    if ("-".equals(item.upDownFlag)){
                                        uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                                    }else {
                                        uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                                    }
                                    uploadObj.put("change", item.change == "一" ? "-" : item.change);
                                    uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                                    uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                                    if (item.turnoverRate.isEmpty()){
                                        uploadObj.put("turnoverRate","-");
                                    }else {
                                        uploadObj.put("turnoverRate", item.turnoverRate == "一" ? "-" : item.turnoverRate);
                                    }
                                    uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                                    uploadObj.put("preClosePrice", item.preClosePrice == "一" ? "-" : item.preClosePrice);
                                    uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                                    uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                                    uploadObj.put("buyVolume", item.buyVolume == "一" ? "-" : item.buyVolume);
                                    uploadObj.put("sellVolume", item.sellVolume == "一" ? "-" : item.sellVolume);
                                    uploadObj.put("pe", item.pe == "一" ? "-" : item.pe);
                                    uploadObj.put("netAsset", item.netAsset == "一" ? "-" : item.netAsset);
                                    uploadObj.put("pb", item.pb == "一" ? "-" : item.pb);
                                    uploadObj.put("capitalization", item.capitalization == "一" ? "-" : item.capitalization);
                                    uploadObj.put("totalValue", item.totalValue == "一" ? "-" : item.totalValue);
                                    if (item.hs.isEmpty()){
                                        uploadObj.put("hs", "-");
                                    }else {
                                        uploadObj.put("hs", item.hs == "一" ? "-" : item.hs);
                                    }
                                    uploadObj.put("HKTotalValue", item.HKTotalValue == "一" ? "-" : item.HKTotalValue);
                                }
                                //基金
                                if (item.subtype.equals("1100")||item.subtype.equals("1110")||item.subtype.equals("1120")||item.subtype.equals("1130")||item.subtype.equals("1131")
                                        ||item.subtype.equals("1132")||item.subtype.equals("1133")||item.subtype.equals("1140")){
                                    System.out.println("基金++++++++++++++"+item.subtype);
                                    uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                                    uploadObj.put("change", item.change == "一" ? "-" : item.change);
                                    if ("-".equals(item.upDownFlag)){
                                        uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                                    }else {
                                        uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                                    }
                                    if (item.turnoverRate.isEmpty()){
                                        uploadObj.put("turnoverRate","-");
                                    }else {
                                        uploadObj.put("turnoverRate", item.turnoverRate == "一" ? "-" : item.turnoverRate);
                                    }
                                    uploadObj.put("limitUP", item.limitUP == "一" ? "-" : item.limitUP);
                                    uploadObj.put("limitDown", item.limitDown == "一" ? "-" : item.limitDown);
                                    uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                                    uploadObj.put("preClosePrice", item.preClosePrice == "一" ? "-" : item.preClosePrice);
                                    uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                                    uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                                    if (item.volumeRatio.isEmpty()){
                                        uploadObj.put("volumeRatio","-");
                                    }else {
                                        uploadObj.put("volumeRatio",item.volumeRatio=="一" ? "-" : String.format("%.2f",Float.parseFloat(item.volumeRatio)));
                                    }
                                    uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                                    uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                                    uploadObj.put("IOPV", item.IOPV == "一" ? "-" : item.IOPV);
                                    //五档卖价
                                    if (item.sellPrices.size()>0){
                                        for (int i=0;i<item.sellPrices.size();i++){
                                            uploadObj.put("sellPrices"+(i+1),item.sellPrices.get(i)== "一" ? "-" : item.sellPrices.get(i));
                                        }
                                    }else {
                                        uploadObj.put("sellPrices1", "-");
                                        uploadObj.put("sellPrices2", "-");
                                        uploadObj.put("sellPrices3", "-");
                                        uploadObj.put("sellPrices4", "-");
                                        uploadObj.put("sellPrices5", "-");
                                    }
                                    //五档卖量
                                    if (item.sellVolumes.size()>0){
                                        for (int i=0;i<item.sellVolumes.size();i++){
                                            uploadObj.put("sellVolumes"+(i+1),item.sellVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.sellVolumes.get(i)))));
                                        }
                                    }else {
                                        uploadObj.put("sellVolumes1", "-");
                                        uploadObj.put("sellVolumes2", "-");
                                        uploadObj.put("sellVolumes3", "-");
                                        uploadObj.put("sellVolumes4", "-");
                                        uploadObj.put("sellVolumes5", "-");
                                    }
                                    //五档买价
                                    if (item.buyPrices.size()>0){
                                        for (int i=item.buyPrices.size()-1;i>=0;i--){
                                            uploadObj.put("buyPrices"+(5-i),item.buyPrices.get(i)== "一" ? "-" : item.buyPrices.get(i));
                                        }
                                    }else {
                                        uploadObj.put("buyPrices1", "-");
                                        uploadObj.put("buyPrices2", "-");
                                        uploadObj.put("buyPrices3", "-");
                                        uploadObj.put("buyPrices4", "-");
                                        uploadObj.put("buyPrices5", "-");
                                    }
                                    //五档买量
                                    if (item.buyVolumes.size()>0){
                                        for (int i=item.buyVolumes.size()-1;i>=0;i--){
                                            uploadObj.put("buyVolumes"+(5-i),item.buyVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.buyVolumes.get(i)))));
                                        }
                                    }else {
                                        uploadObj.put("buyVolumes1", "-");
                                        uploadObj.put("buyVolumes2", "-");
                                        uploadObj.put("buyVolumes3", "-");
                                        uploadObj.put("buyVolumes4", "-");
                                        uploadObj.put("buyVolumes5", "-");
                                    }
                                }
                                //债券
                                if (item.subtype.equals("1300")||item.subtype.equals("1311")||item.subtype.equals("1312")||item.subtype.equals("1313")||item.subtype.equals("1314")||item.subtype.equals("1321")||item.subtype.equals("1322")){
                                    System.out.println("债券++++++++++++++++"+item.subtype);
                                    uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                                    uploadObj.put("change", item.change == "一" ? "-" : item.change);
                                    if ("-".equals(item.upDownFlag)){
                                        uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                                    }else {
                                        uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                                    }
                                    uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                                    if (item.volumeRatio.isEmpty()){
                                        uploadObj.put("volumeRatio","-");
                                    }else {
                                        uploadObj.put("volumeRatio",item.volumeRatio=="一" ? "-" : String.format("%.2f",Float.parseFloat(item.volumeRatio)));
                                    }
                                    uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                                    uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                                    uploadObj.put("preClosePrice", item.preClosePrice == "一" ? "-" : item.preClosePrice);
                                    uploadObj.put("buyVolume", item.buyVolume == "一" ? "-" : item.buyVolume);
                                    uploadObj.put("sellVolume", item.sellVolume == "一" ? "-" : item.sellVolume);
                                    //五档卖价
                                    if (item.sellPrices.size()>0){
                                        for (int i=0;i<item.sellPrices.size();i++){
                                            uploadObj.put("sellPrices"+(i+1),item.sellPrices.get(i)== "一" ? "-" : item.sellPrices.get(i));
                                        }
                                    }else {
                                        uploadObj.put("sellPrices1", "-");
                                        uploadObj.put("sellPrices2", "-");
                                        uploadObj.put("sellPrices3", "-");
                                        uploadObj.put("sellPrices4", "-");
                                        uploadObj.put("sellPrices5", "-");
                                    }
                                    //五档卖量
                                    if (item.sellVolumes.size()>0){
                                        for (int i=0;i<item.sellVolumes.size();i++){
                                            uploadObj.put("sellVolumes"+(i+1),item.sellVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.sellVolumes.get(i)))));
                                        }
                                    }else {
                                        uploadObj.put("sellVolumes1", "-");
                                        uploadObj.put("sellVolumes2", "-");
                                        uploadObj.put("sellVolumes3", "-");
                                        uploadObj.put("sellVolumes4", "-");
                                        uploadObj.put("sellVolumes5", "-");
                                    }
                                    //五档买价
                                    if (item.buyPrices.size()>0){
                                        for (int i=item.buyPrices.size()-1;i>=0;i--){
                                            uploadObj.put("buyPrices"+(5-i),item.buyPrices.get(i)== "一" ? "-" : item.buyPrices.get(i));
                                        }
                                    }else {
                                        uploadObj.put("buyPrices1", "-");
                                        uploadObj.put("buyPrices2", "-");
                                        uploadObj.put("buyPrices3", "-");
                                        uploadObj.put("buyPrices4", "-");
                                        uploadObj.put("buyPrices5", "-");
                                    }
                                    //五档买量
                                    if (item.buyVolumes.size()>0){
                                        for (int i=item.buyVolumes.size()-1;i>=0;i--){
                                            uploadObj.put("buyVolumes"+(5-i),item.buyVolumes.get(i)== "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.buyVolumes.get(i)))));
                                        }
                                    }else {
                                        uploadObj.put("buyVolumes1", "-");
                                        uploadObj.put("buyVolumes2", "-");
                                        uploadObj.put("buyVolumes3", "-");
                                        uploadObj.put("buyVolumes4", "-");
                                        uploadObj.put("buyVolumes5", "-");
                                    }
                                }
                                //科创板
                                if (item.market.equals("sh")){
                                    if (item.subtype.equals("1006")||item.subtype.equals("1521")){
                                        System.out.println("科创板+++++++++++++++"+item.subtype);
                                        uploadObj.put("lastPrice", item.lastPrice == "一" ? "-" : item.lastPrice);
                                        uploadObj.put("averageValue", item.averageValue == "一" ? "-" : item.averageValue);//ios无
                                        if ("-".equals(item.upDownFlag)){
                                            uploadObj.put("changeRate",item.upDownFlag+item.changeRate);//加涨跌符号
                                        }else {
                                            uploadObj.put("changeRate",item.changeRate == "一" ? "-" : item.changeRate);
                                        }
                                        uploadObj.put("change", item.change == "一" ? "-" : item.change);
                                        uploadObj.put("volume", item.volume == "一" ? "-" : String.valueOf(Math.round(Float.parseFloat(item.volume))));
                                        uploadObj.put("amount", item.amount == "一" ? "-" : item.amount);
                                        if (item.turnoverRate.isEmpty()){
                                            uploadObj.put("turnoverRate","-");
                                        }else {
                                            uploadObj.put("turnoverRate", item.turnoverRate == "一" ? "-" : item.turnoverRate);
                                        }
                                        if (item.volumeRatio.isEmpty()){
                                            uploadObj.put("volumeRatio","-");
                                        }else {
                                            uploadObj.put("volumeRatio",item.volumeRatio=="一" ? "-" : String.format("%.2f",Float.parseFloat(item.volumeRatio)));
                                        }
                                        uploadObj.put("highPrice", item.highPrice == "一" ? "-" : item.highPrice);
                                        uploadObj.put("lowPrice", item.lowPrice == "一" ? "-" : item.lowPrice);
                                        uploadObj.put("openPrice", item.openPrice == "一" ? "-" : item.openPrice);
                                        uploadObj.put("preClosePrice", item.preClosePrice == "一" ? "-" : item.preClosePrice);
                                        uploadObj.put("limitUP", item.limitUP == "一" ? "-" : item.limitUP);
                                        uploadObj.put("limitDown", item.limitDown == "一" ? "-" : item.limitDown);
                                        uploadObj.put("buyVolume", item.buyVolume == "一" ? "-" : item.buyVolume);
                                        uploadObj.put("sellVolume", item.sellVolume == "一" ? "-" : item.sellVolume);
                                        uploadObj.put("orderRatio", item.orderRatio == "一" ? "-" : item.orderRatio);
                                        uploadObj.put("amplitudeRate", item.amplitudeRate == "一" ? "-" : item.amplitudeRate);
                                        uploadObj.put("afterHoursVolume", item.afterHoursVolume == "一" ? "-" : item.afterHoursVolume);
                                        uploadObj.put("afterHoursAmount", item.afterHoursAmount == "" ? "-" : item.afterHoursAmount);//不返回字段 数据库里面没有该字段

                                        uploadObj.put("pe", item.pe == "一" ? "-" : item.pe);
                                        uploadObj.put("pe2", item.pe2 == "一" ? "-" : item.pe2);
                                        uploadObj.put("netAsset", item.netAsset == "一" ? "-" : item.netAsset);
                                        uploadObj.put("pb", item.pb == "一" ? "-" : item.pb);
                                        uploadObj.put("vote", item.vote == "一" ? "-" : item.vote);//注意ios
                                        uploadObj.put("capitalization", item.capitalization == "一" ? "-" : item.capitalization);
                                        uploadObj.put("totalValue", item.totalValue == "一" ? "-" : item.totalValue);
                                        uploadObj.put("circulatingShares", item.circulatingShares == "一" ? "-" : item.circulatingShares);
                                        uploadObj.put("flowValue", item.flowValue == "一" ? "-" : item.flowValue);
                                        uploadObj.put("issuedCapital", item.issuedCapital == "一" ? "-" : item.issuedCapital);
                                        uploadObj.put("upf", item.upf == "一" ? "-" : item.upf);//注意ios
                                        //五档卖价
                                        if (item.sellPrices.size()>0){
                                            for (int i=0;i<item.sellPrices.size();i++){
                                                uploadObj.put("sellPrices"+(i+1),item.sellPrices.get(i)== "一" ? "-" : item.sellPrices.get(i));
                                            }
                                        }else {
                                            uploadObj.put("sellPrices1", "-");
                                            uploadObj.put("sellPrices2", "-");
                                            uploadObj.put("sellPrices3", "-");
                                            uploadObj.put("sellPrices4", "-");
                                            uploadObj.put("sellPrices5", "-");
                                        }
                                        //五档卖量
                                        if (item.sellVolumes.size()>0){
                                            for (int i=0;i<item.sellVolumes.size();i++){
                                                uploadObj.put("sellVolumes"+(i+1),item.sellVolumes.get(i)== "一" ? "-" : item.sellVolumes.get(i));
                                            }
                                        }else {
                                            uploadObj.put("sellVolumes1", "-");
                                            uploadObj.put("sellVolumes2", "-");
                                            uploadObj.put("sellVolumes3", "-");
                                            uploadObj.put("sellVolumes4", "-");
                                            uploadObj.put("sellVolumes5", "-");
                                        }
                                        //五档买价
                                        if (item.buyPrices.size()>0){
                                            for (int i=item.buyPrices.size()-1;i>=0;i--){
                                                uploadObj.put("buyPrices"+(5-i),item.buyPrices.get(i)== "一" ? "-" : item.buyPrices.get(i));
                                            }
                                        }else {
                                            uploadObj.put("buyPrices1", "-");
                                            uploadObj.put("buyPrices2", "-");
                                            uploadObj.put("buyPrices3", "-");
                                            uploadObj.put("buyPrices4", "-");
                                            uploadObj.put("buyPrices5", "-");
                                        }
                                        //五档买量
                                        if (item.buyVolumes.size()>0){
                                            for (int i=item.buyVolumes.size()-1;i>=0;i--){
                                                uploadObj.put("buyVolumes"+(5-i),item.buyVolumes.get(i)== "一" ? "-" : item.buyVolumes.get(i));
                                            }
                                        }else {
                                            uploadObj.put("buyVolumes1", "-");
                                            uploadObj.put("buyVolumes2", "-");
                                            uploadObj.put("buyVolumes3", "-");
                                            uploadObj.put("buyVolumes4", "-");
                                            uploadObj.put("buyVolumes5", "-");
                                        }
                                    }
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
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }
    }
}
