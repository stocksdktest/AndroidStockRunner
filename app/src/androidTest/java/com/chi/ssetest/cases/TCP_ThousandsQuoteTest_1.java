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
import com.mitake.core.bean.ThousandsData;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.listener.IThousandsPush;
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

//千档行情TCP
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.TCP_ThousandsQuoteTest_1)
public class TCP_ThousandsQuoteTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.TCP_ThousandsQuoteTest_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 100000000; //上周开会说的Timeout，可以设置成final类型，TCP设置大一些没关系
    private final static String tTag = "TCPTest";
    int a=0;
    int b=0;
    int c=0;
    int d=0;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("TCP_ThousandsQuoteTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("TCP_ThousandsQuoteTest_1", "requestWork");
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
                TCPManager.getInstance().subscribeThousands(quoteResponse.quoteItems.get(0));   // quoteResponse.quoteItems.get(0).id : StockID:600000.sh
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
                TCPManager.getInstance().unsubscribeThousands(unsub.get(0));
                break;
            }else{
                NetworkManager.getInstance().addIPush(new IThousandsPush(){
                    @Override
                    public void push(String s, ThousandsData thousandsData) {

                        try {
                            if(thousandsData.buyItems!=null){
                                JSONObject uploadObj_1 = new JSONObject();
                                JSONObject uploadObj_2 = new JSONObject();
                                if (thousandsData.buyItems.size()<50){
                                    for (int i=0;i<thousandsData.buyItems.size();i++){
                                        uploadObj_1.put("price",thousandsData.buyItems.get(i).price);
                                        uploadObj_1.put("volume",thousandsData.buyItems.get(i).volume);
                                        uploadObj_1.put("originPrice",thousandsData.buyItems.get(i).originPrice);
                                        uploadObj_1.put("bs",thousandsData.buyItems.get(i).bs);
                                        uploadObj_1.put("count",thousandsData.buyItems.get(i).count);
                                        uploadObj_2.put("B"+(i+1),uploadObj_1);
                                        a=1;
                                        c=i+1;
                                    }
                                }else if (thousandsData.buyItems.size()>=50){
                                    for (int i=thousandsData.buyItems.size()-50;i<thousandsData.buyItems.size();i++){
                                        uploadObj_1.put("price",thousandsData.buyItems.get(i).price);
                                        uploadObj_1.put("volume",thousandsData.buyItems.get(i).volume);
                                        uploadObj_1.put("originPrice",thousandsData.buyItems.get(i).originPrice);
                                        uploadObj_1.put("bs",thousandsData.buyItems.get(i).bs);
                                        uploadObj_1.put("count",thousandsData.buyItems.get(i).count);
                                        uploadObj_2.put("B"+(i+1),uploadObj_1);
                                        a=1;
                                        c=i+1;
                                    }
                                }else if (a==1){
                                    uploadObj_1.put("price",thousandsData.buyItems.get(thousandsData.buyItems.size()-1).price);
                                    uploadObj_1.put("volume",thousandsData.buyItems.get(thousandsData.buyItems.size()-1).volume);
                                    uploadObj_1.put("originPrice",thousandsData.buyItems.get(thousandsData.buyItems.size()-1).originPrice);
                                    uploadObj_1.put("bs",thousandsData.buyItems.get(thousandsData.buyItems.size()-1).bs);
                                    uploadObj_1.put("count",thousandsData.buyItems.get(thousandsData.buyItems.size()-1).count);
                                    c=c+1;
                                    uploadObj_2.put("B"+c,uploadObj_1);
                                }
                                uploadObj.put("buylist",uploadObj_2);
                            }else {
                                uploadObj.put("buylist","");
                            }

                            if(thousandsData.sellItems!=null){
                                JSONObject uploadObj_3 = new JSONObject();
                                JSONObject uploadObj_4 = new JSONObject();
                                if (thousandsData.sellItems.size()<50){
                                    for (int i=0;i<thousandsData.sellItems.size();i++){
                                        uploadObj_3.put("price",thousandsData.sellItems.get(i).price);
                                        uploadObj_3.put("volume",thousandsData.sellItems.get(i).volume);
                                        uploadObj_3.put("originPrice",thousandsData.sellItems.get(i).originPrice);
                                        uploadObj_3.put("bs",thousandsData.sellItems.get(i).bs);
                                        uploadObj_3.put("count",thousandsData.sellItems.get(i).count);
                                        uploadObj_4.put("S"+(i+1),uploadObj_3);
                                        b=1;
                                        d=i+1;
                                    }
                                }else if (thousandsData.sellItems.size()>=50){
                                    for (int i=thousandsData.sellItems.size()-50;i<thousandsData.sellItems.size();i++){
                                        uploadObj_3.put("price",thousandsData.sellItems.get(i).price);
                                        uploadObj_3.put("volume",thousandsData.sellItems.get(i).volume);
                                        uploadObj_3.put("originPrice",thousandsData.sellItems.get(i).originPrice);
                                        uploadObj_3.put("bs",thousandsData.sellItems.get(i).bs);
                                        uploadObj_3.put("count",thousandsData.sellItems.get(i).count);
                                        uploadObj_4.put("S"+(i+1),uploadObj_3);
                                        b=1;
                                        d=i+1;
                                    }
                                }else if (b==1){
                                    uploadObj_3.put("price",thousandsData.sellItems.get(thousandsData.sellItems.size()-1).price);
                                    uploadObj_3.put("volume",thousandsData.sellItems.get(thousandsData.sellItems.size()-1).volume);
                                    uploadObj_3.put("originPrice",thousandsData.sellItems.get(thousandsData.sellItems.size()-1).originPrice);
                                    uploadObj_3.put("bs",thousandsData.sellItems.get(thousandsData.sellItems.size()-1).bs);
                                    uploadObj_3.put("count",thousandsData.sellItems.get(thousandsData.sellItems.size()-1).count);
                                    d=d+1;
                                    uploadObj_4.put("S"+d,uploadObj_3);
                                }
                                uploadObj.put("selllist",uploadObj_4);
                            }else {
                                uploadObj.put("selllist","");
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
