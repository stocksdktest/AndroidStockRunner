package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.bean.TickDetailItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.L2TickDetailRequestV2;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.L2TickDetailResponseV2;
import com.mitake.core.response.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//L2逐笔  最多返回100条数据
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.L2TICKDETAILV2TEST_2)
public class L2TickDetailV2Test_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.L2TICKDETAILV2TEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 2000000000;
    String sttime="";
    int i=1;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("L2TickDetailV2Test_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("L2TickDetailV2Test_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE", "");
        final String Pages = rule.getParam().optString("page", "");
        final String SubTypes = rule.getParam().optString("SUBTYPE", "");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<Params.length;i++){
        L2TickDetailRequestV2 request = new L2TickDetailRequestV2();
        request.send(quoteNumbers,Pages,SubTypes, new IResponseInfoCallback() {
            @Override
            public void callback(Response response) {
                L2TickDetailResponseV2 l2TickDetailResponseV2 = (L2TickDetailResponseV2) response;
                List<TickDetailItem> list =l2TickDetailResponseV2.tickDetailItems;
                try {
                    assertNotNull(l2TickDetailResponseV2.tickDetailItems);
                } catch (AssertionError e) {
                    //                        result.completeExceptionally(e);
                    result.complete(new JSONObject());
                }
                JSONObject uploadObj=new JSONObject();
                // TODO fill uploadObj with QuoteResponse value
                try {
                    if(list!=null){
                        for (int k=0;k<list.size();k++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("type", list.get(k).getTransactionStatus());
                            uploadObj_1.put("time", list.get(k).getTransactionTime());
                            uploadObj_1.put("tradeVolume", list.get(k).getSingleVolume());
                            uploadObj_1.put("tradePrice", list.get(k).getTransactionPrice());
//                            Log.d("data", String.valueOf(uploadObj_1));
                            if (sttime.equals(list.get(k).getTransactionTime())){
                                uploadObj.put(list.get(k).getTransactionTime()+i,uploadObj_1);
                                i++;
                            }else {
                                sttime=list.get(k).getTransactionTime();
                                uploadObj.put(list.get(k).getTransactionTime(),uploadObj_1);
                                i=1;
                            }
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
