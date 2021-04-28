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
import com.mitake.core.bean.ThousandsQueueData;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.listener.IThousandsQueuePush;
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

//全息队列TCP
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.TCP_ThousandsQueueTest_1)
public class TCP_ThousandsQueueTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.TCP_ThousandsQueueTest_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000; //上周开会说的Timeout，可以设置成final类型，TCP设置大一些没关系
    private final static String tTag = "TCPTest";
    int a=0;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("TCP_ThousandsQueueTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("TCP_ThousandsQueueTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE", "");
        final String quoteNumbers1 = rule.getParam().optString("PRICE1", "");
        final String quoteNumbers2 = rule.getParam().optString("PRICE2", "");
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
                TCPManager.getInstance().subscribeThousandsQueue(quoteResponse.quoteItems.get(0),quoteNumbers1,quoteNumbers2);   // quoteResponse.quoteItems.get(0).id : StockID:600000.sh
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
                TCPManager.getInstance().unsubscribeTickEntrust(unsub.toArray(new String[0]));
                break;
            }else{
                NetworkManager.getInstance().addIPush(new IThousandsQueuePush(){
                    @Override
                    public void push(String s, String s1, ThousandsQueueData thousandsQueueData) {

                        try {
                            if(thousandsQueueData!=null){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj.put("price",thousandsQueueData.price);
                                for (int i=0;i<thousandsQueueData.thousandsQueueItems.size();i++){
                                    uploadObj_1.put("sn",thousandsQueueData.thousandsQueueItems.get(i).sn);
                                    uploadObj_1.put("volume",thousandsQueueData.thousandsQueueItems.get(i).volume);
                                    uploadObj_1.put("bs",thousandsQueueData.thousandsQueueItems.get(i).bs);
                                    uploadObj.put(thousandsQueueData.thousandsQueueItems.get(i).sn,uploadObj_1);
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
