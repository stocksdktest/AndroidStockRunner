package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.OHLCItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.listener.LinePush;
import com.mitake.core.network.NetworkManager;
import com.mitake.core.network.TCPManager;
import com.mitake.core.request.ChartRequestV2;
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.response.ChartResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//走势数据 方法二 对应的TCP  对应走势数据的方法六
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.TCP_CHARTV2TEST_2)
public class TCP_ChartV2Test_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.TCP_CHARTV2TEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000; //上周开会说的Timeout，可以设置成final类型，TCP设置大一些没关系
    private final static String tTag = "TCPTest";

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("TCP_ChartV2Test_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("TCP_ChartV2Test_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().getString("CODE");
        final String Types = rule.getParam().getString("TYPE");
        final String isNeedAfterHours = rule.getParam().getString("RETURNAFDATA");
        final String tcpSeconds = rule.getParam().optString("SECONDS", ""); //设置TCP监听的时间
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        final JSONObject uploadObj = new JSONObject();
        final ArrayList<OHLCItem> historyItems=new ArrayList<>();
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
                final QuoteItem quoteItem=quoteResponse.quoteItems.get(0);
                ChartRequestV2 request = new ChartRequestV2();
                request.send(quoteItem, Types,new IResponseInfoCallback<ChartResponse>() {
                    @Override
                    public void callback(ChartResponse chartResponse) {

                        // 准备监听TCP的消息
                        TCPManager.getInstance().subScribeLines(quoteItem,Types);   // quoteResponse.quoteItems.get(0).id : StockID:600000.sh
                        historyItems.addAll(chartResponse.historyItems);
                        Log.d("data", uploadObj.toString());
                    }

                    @Override
                    public void exception(ErrorInfo errorInfo) {
                        result.completeExceptionally(new Exception(errorInfo.toString()));
                    }
                }, Boolean.parseBoolean(isNeedAfterHours));
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
                TCPManager.getInstance().unSubScribeLines(unsub.toArray(new String[0]),Types);
                break;
            }else{
                NetworkManager.getInstance().addIPush(new LinePush() {

                    @Override
                    public void pushLine(ChartResponse item, String Types, String code) {
//                        Log.d("tcp00", item.toString());
                        try {
                            if(item.historyItems!=null){
                                for (int k=0;k<item.historyItems.size();k++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    //存储到JSON
                                    uploadObj_1.put("datetime",item.historyItems.get(k).datetime);
                                    uploadObj_1.put("closePrice",item.historyItems.get(k).closePrice);
                                    uploadObj_1.put("tradeVolume",item.historyItems.get(k).tradeVolume);
                                    uploadObj_1.put("averagePrice",item.historyItems.get(k).averagePrice);
                                    uploadObj_1.put("md",item.historyItems.get(k).getMd());
                                    uploadObj_1.put("openInterest",item.historyItems.get(k).openInterest);
                                    uploadObj_1.put("iopv",item.historyItems.get(k).iopv);
                                    uploadObj_1.put("iopvPre",item.historyItems.get(k).iopvPre);
                                    uploadObj_1.put("volRatio",item.historyItems.get(k).volRatio);
                                    uploadObj.put(item.historyItems.get(k).datetime,uploadObj_1);
                                }
                                if (Boolean.parseBoolean(isNeedAfterHours)){
                                    for (int k=0;k<item.afterHoursChartResponse.historyItems.size();k++){
                                        JSONObject uploadObj_1 = new JSONObject();
                                        //存储到JSON
                                        uploadObj_1.put("datetime",item.afterHoursChartResponse.historyItems.get(k).datetime);
                                        uploadObj_1.put("closePrice",item.afterHoursChartResponse.historyItems.get(k).closePrice);
                                        uploadObj_1.put("tradeVolume",item.afterHoursChartResponse.historyItems.get(k).tradeVolume);
                                        uploadObj_1.put("averagePrice",item.afterHoursChartResponse.historyItems.get(k).averagePrice);
                                        uploadObj_1.put("md",item.afterHoursChartResponse.historyItems.get(k).getMd());
                                        uploadObj_1.put("openInterest",item.afterHoursChartResponse.historyItems.get(k).openInterest);
                                        uploadObj_1.put("iopv",item.afterHoursChartResponse.historyItems.get(k).iopv);
                                        uploadObj_1.put("iopvPre",item.afterHoursChartResponse.historyItems.get(k).iopvPre);
                                        uploadObj_1.put("volRatio",item.afterHoursChartResponse.historyItems.get(k).volRatio);
                                        uploadObj.put(item.afterHoursChartResponse.historyItems.get(k).datetime,uploadObj_1);
                                    }
                                }
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
