package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.listener.KLinePush;
import com.mitake.core.network.NetworkManager;
import com.mitake.core.network.TCPManager;
import com.mitake.core.response.OHLCResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Example local unit test, which will execute on the development machine (host).
 *TCP k线接口
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.TCP_OHLCV3TEST_1)
public class TCP_OHLCV3Test_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.TCP_OHLCV3TEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000; //上周开会说的Timeout，可以设置成final类型，TCP设置大一些没关系
    private final static String tTag = "TCPTest";

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("TCP_OHLCV3Test_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("TCP_OHLCV3Test_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().getString("CODE");
        final String SUB = rule.getParam().getString("SUB");
        final String TYPES = rule.getParam().getString("TYPES");
        final String tcpSeconds = rule.getParam().optString("SECONDS", ""); //设置TCP监听的时间
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        final JSONObject uploadObj = new JSONObject();
//        final ArrayList<OHLCItem> historyItems=new ArrayList<>();
//        final ArrayList<QuoteItem> quoteItems = new ArrayList<>(); //Response的副本

        TCPManager.getInstance().subScribeKLines(quoteNumbers,Integer.parseInt(SUB),TYPES);
        Log.d("data", uploadObj.toString());

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
//                for(int i=0;i<quoteItems.size();i++){
                    unsub.add(quoteNumbers);
//                }
                TCPManager.getInstance().unSubScribeKlines(unsub.toArray(new String[0]),Integer.parseInt(SUB),TYPES);
                break;
            }else{
                NetworkManager.getInstance().addIPush(new KLinePush() {
                    @Override
                    public void pushKLine(OHLCResponse ohlcResponse, String s, int i, String s1) {

                        try {
                            if (ohlcResponse.historyItems!=null){
                                JSONObject uploadObj_1 = new JSONObject();
                                String timedata;
                                if (TYPES.equals("dayk")||TYPES.equals("weekk")||TYPES.equals("monthk")||TYPES.equals("yeark")){
                                    timedata=ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).datetime;
                                }else {
                                    timedata=ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).datetime+ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).time;
                                }
                                uploadObj_1.put("datetime",timedata);
                                uploadObj_1.put("openPrice",ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).openPrice == null ? "-" : ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).openPrice);
                                uploadObj_1.put("highPrice",ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).highPrice == null ? "-" : ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).highPrice);
                                uploadObj_1.put("lowPrice",ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).lowPrice == null ? "-" : ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).lowPrice);
                                uploadObj_1.put("closePrice",ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).closePrice == null ? "-" : ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).closePrice);
                                uploadObj_1.put("tradeVolume",ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).tradeVolume == null ? "-" : ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).tradeVolume);
                                uploadObj_1.put("averagePrice",ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).averagePrice == null ? "-" : ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).averagePrice);
                                uploadObj_1.put("reference_price",ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).reference_price == null ? "-" : ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).reference_price);
                                uploadObj_1.put("transaction_price",ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).transaction_price == null ? "-" : ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).transaction_price);
                                uploadObj_1.put("openInterest",ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).openInterest == null ? "-" : ohlcResponse.historyItems.get(ohlcResponse.historyItems.size()-1).openInterest);
                                uploadObj.put(timedata,uploadObj_1);
                            }
                            Log.d("tcp00", String.valueOf(uploadObj));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }

        Log.d(tTag,"addIPush End");
        result.complete(uploadObj);


        try {
            JSONObject resultObj = (JSONObject) result.get(timeout_ms, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
        } catch (Exception e) {
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }
    }
}
