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
import com.mitake.core.bean.TickData;
import com.mitake.core.bean.TickDetailItem;
import com.mitake.core.bean.TickItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.listener.AbstractTickPush;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//分笔L2 方法一 对应的TCP
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.TCP_TICKTEST_1)
public class TCP_TickTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.TCP_TICKTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000; //上周开会说的Timeout，可以设置成final类型，TCP设置大一些没关系
    private final static String tTag = "TCPTest";

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("TCP_TickTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("TCP_TickTest_1", "requestWork");
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
                Log.d("quoteItems", quoteResponse.quoteItems.toString());
                Log.d("quoteResponse", quoteResponse.toString());
                if (quoteResponse.quoteItems == null || quoteResponse.quoteItems.size() == 0) {
                    JSONObject uploadObj_1 = new JSONObject();
                    try {
                        uploadObj_1.put("quoteItems", "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 准备监听TCP的消息
                    TCPManager.getInstance().subscribeTick(quoteResponse.quoteItems.get(0));   // quoteResponse.quoteItems.get(0).id : StockID:600000.sh
                    quoteItems.addAll(quoteResponse.quoteItems);
                    Log.d("data", uploadObj.toString());


                }

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
                TCPManager.getInstance().unsubscribeTick(unsub.toArray(new String[0]));
//                TCPManager.getInstance().unsubscribeTick(new String[]{quoteNumbers});
                break;
            }else{
                NetworkManager.getInstance().addIPush(new AbstractTickPush() {
                    @Override
                    public void pushTick(String s, TickData tickData) {

                        String code=s;
                        Log.d("tickData", tickData.toString());
                        try {
//                            uploadObj.put("code",code);
                            if(tickData!=null&&tickData.tickItems.size()!=0){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("time",tickData.tickItems.get(tickData.tickItems.size()-1).getTransactionTime());
                                uploadObj_1.put("tradePrice",tickData.tickItems.get(tickData.tickItems.size()-1).getTransactionPrice());
                                uploadObj_1.put("tradeVolume",tickData.tickItems.get(tickData.tickItems.size()-1).getSingleVolume());
                                uploadObj_1.put("type",tickData.tickItems.get(tickData.tickItems.size()-1).getTransactionStatus());
                                uploadObj.put(tickData.tickItems.get(tickData.tickItems.size()-1).getTransactionTime(),uploadObj_1);
                            }
                            Log.d("tcp00", String.valueOf(uploadObj));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

//                    @Override
//                    public void pushTick(Map<String, TickData>data) {
//                        try {
//                            String code;
//                            TickData tickItems;
//                            for (Map.Entry<String, TickData> entry : data.entrySet()) {
//                                code = entry.getKey();
//                                tickItems = entry.getValue();
////                                uploadObj.put("code",code);
//                                if(tickItems!=null){
////                                    for (TickItem tickItem : tickItems) {
//                                        JSONObject uploadObj_1 = new JSONObject();
//                                        uploadObj_1.put("time",tickItems.tickItems.get(tickItems.tickItems.size()-1).getTransactionTime());
//                                        uploadObj_1.put("tradePrice",tickItems.tickItems.get(tickItems.tickItems.size()-1).getTransactionPrice());
//                                        uploadObj_1.put("tradeVolume",tickItems.tickItems.get(tickItems.tickItems.size()-1).getSingleVolume());
//                                        uploadObj_1.put("type",tickItems.tickItems.get(tickItems.tickItems.size()-1).getTransactionStatus());
//                                        uploadObj.put(tickItems.tickItems.get(tickItems.tickItems.size()-1).getTransactionTime(),uploadObj_1);
////                                    }
//                                }
//                            }
//                            Log.d("tcp00", String.valueOf(uploadObj));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
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
