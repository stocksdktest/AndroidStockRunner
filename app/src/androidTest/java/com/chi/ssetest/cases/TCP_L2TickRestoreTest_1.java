package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.TickRestoreData;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.listener.ITickRestorePush;
import com.mitake.core.network.NetworkManager;
import com.mitake.core.network.TCPManager;
import com.mitake.core.request.QuoteDetailRequest;
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

//逐笔还原TCP
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.TCP_L2TickRestoreTest_1)
public class TCP_L2TickRestoreTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.TCP_L2TickRestoreTest_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000; //上周开会说的Timeout，可以设置成final类型，TCP设置大一些没关系
    private final static String tTag = "TCPTest";
    int a=0;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("TCP_L2TickRestoreTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("TCP_L2TickRestoreTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE", "");
        final String tcpSeconds = rule.getParam().optString("SECONDS", ""); //设置TCP监听的时间
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        final JSONObject uploadObj = new JSONObject();
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
                TCPManager.getInstance().subscribeTickRestore(quoteResponse.quoteItems.get(0));   // quoteResponse.quoteItems.get(0).id : StockID:600000.sh
                quoteItems.addAll(quoteResponse.quoteItems);
                Log.d("data", uploadObj.toString());
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
                TCPManager.getInstance().unsubscribeTickRestore(unsub.toArray(new String[0]));
                break;
            }else{
                NetworkManager.getInstance().addIPush(new ITickRestorePush(){
                    @Override
                    public void push(String s, TickRestoreData tickRestoreData) {
                        try {
                            if(tickRestoreData!=null){
                                JSONObject uploadObj_1 = new JSONObject();
                                if (tickRestoreData.tickDetailItems.size()<30){
                                    for (int i=0;i<tickRestoreData.tickDetailItems.size();i++){
                                        uploadObj_1.put("time",tickRestoreData.tickDetailItems.get(i).getTransactionTime());
                                        uploadObj_1.put("tradePrice",tickRestoreData.tickDetailItems.get(i).getTransactionPrice());
                                        uploadObj_1.put("tradeVolume",tickRestoreData.tickDetailItems.get(i).getSingleVolume());
                                        uploadObj_1.put("status",tickRestoreData.tickDetailItems.get(i).getTransactionStatus());
                                        uploadObj_1.put("bn",tickRestoreData.tickDetailItems.get(i).getBn());
                                        uploadObj_1.put("on",tickRestoreData.tickDetailItems.get(i).getOn());
                                        uploadObj_1.put("bq",tickRestoreData.tickDetailItems.get(i).getBq());
                                        uploadObj_1.put("oq",tickRestoreData.tickDetailItems.get(i).getOq());
                                        uploadObj.put(tickRestoreData.tickDetailItems.get(i).getTransactionTime(),uploadObj_1);
                                        a=1;
                                    }
                                }else if (tickRestoreData.tickDetailItems.size()>=30){
                                    for (int i=tickRestoreData.tickDetailItems.size()-30;i<tickRestoreData.tickDetailItems.size();i++){
                                        uploadObj_1.put("time",tickRestoreData.tickDetailItems.get(i).getTransactionTime());
                                        uploadObj_1.put("tradePrice",tickRestoreData.tickDetailItems.get(i).getTransactionPrice());
                                        uploadObj_1.put("tradeVolume",tickRestoreData.tickDetailItems.get(i).getSingleVolume());
                                        uploadObj_1.put("status",tickRestoreData.tickDetailItems.get(i).getTransactionStatus());
                                        uploadObj_1.put("bn",tickRestoreData.tickDetailItems.get(i).getBn());
                                        uploadObj_1.put("on",tickRestoreData.tickDetailItems.get(i).getOn());
                                        uploadObj_1.put("bq",tickRestoreData.tickDetailItems.get(i).getBq());
                                        uploadObj_1.put("oq",tickRestoreData.tickDetailItems.get(i).getOq());
                                        uploadObj.put(tickRestoreData.tickDetailItems.get(i).getTransactionTime(),uploadObj_1);
                                        a=1;
                                    }
                                }else if (a==1){
                                    uploadObj_1.put("time",tickRestoreData.tickDetailItems.get(tickRestoreData.tickDetailItems.size()-1).getTransactionTime());
                                    uploadObj_1.put("tradePrice",tickRestoreData.tickDetailItems.get(tickRestoreData.tickDetailItems.size()-1).getTransactionPrice());
                                    uploadObj_1.put("tradeVolume",tickRestoreData.tickDetailItems.get(tickRestoreData.tickDetailItems.size()-1).getSingleVolume());
                                    uploadObj_1.put("status",tickRestoreData.tickDetailItems.get(tickRestoreData.tickDetailItems.size()-1).getTransactionStatus());
                                    uploadObj_1.put("bn",tickRestoreData.tickDetailItems.get(tickRestoreData.tickDetailItems.size()-1).getBn());
                                    uploadObj_1.put("on",tickRestoreData.tickDetailItems.get(tickRestoreData.tickDetailItems.size()-1).getOn());
                                    uploadObj_1.put("bq",tickRestoreData.tickDetailItems.get(tickRestoreData.tickDetailItems.size()-1).getBq());
                                    uploadObj_1.put("oq",tickRestoreData.tickDetailItems.get(tickRestoreData.tickDetailItems.size()-1).getOq());
                                    uploadObj.put(tickRestoreData.tickDetailItems.get(tickRestoreData.tickDetailItems.size()-1).getTransactionTime(),uploadObj_1);
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
