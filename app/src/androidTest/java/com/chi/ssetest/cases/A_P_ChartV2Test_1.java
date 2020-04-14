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
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.ChartRequestV2;
import com.mitake.core.response.ChartResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//走势数据对比竞品 方法一  传入code补全
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.A_P_ChartV2Test_1)
public class A_P_ChartV2Test_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.A_P_ChartV2Test_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("A_P_ChartV2Test_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("A_P_ChartV2Test_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().getString("CODE_A");
        final String Types = rule.getParam().getString("TYPE");
//        ChartType
        final CompletableFuture result = new CompletableFuture<JSONObject>();

//        for (int i=0;i<quoteNumbers.length;i++){
        ChartRequestV2 request = new ChartRequestV2();
        request.send(quoteNumbers,Types, new IResponseInfoCallback() {
            @Override
            public void callback(Response response) {
                ChartResponse chartResponse = (ChartResponse) response;
                try {
                    assertNotNull(chartResponse.historyItems);
                } catch (AssertionError e) {
                    //                        result.completeExceptionally(e);
                    result.complete(new JSONObject());
                }
                CopyOnWriteArrayList<OHLCItem> list=chartResponse.historyItems;
                JSONObject uploadObj = new JSONObject();
                // TODO fill uploadObj with QuoteResponse value
                try {
                    if (list!=null){
                        for (int k=0;k<list.size();k++) {
                            JSONObject uploadObj_1 = new JSONObject();
                            //存储到JSON
                            uploadObj_1.put("datetime",list.get(k).datetime == null ? "-" : list.get(k).datetime);
                            uploadObj_1.put("closePrice",list.get(k).closePrice == null ? "-" : list.get(k).closePrice);
                            if (list.get(k).tradeVolume==null){
                                uploadObj_1.put("tradeVolume","-");
                            }else {
                                uploadObj_1.put("tradeVolume",Math.round(Float.parseFloat(list.get(k).tradeVolume)));
                            }
                            uploadObj_1.put("averagePrice",list.get(k).averagePrice == null ? "-" : list.get(k).averagePrice);
//                            Log.d("data", String.valueOf(uploadObj_1));
                            uploadObj.put(list.get(k).datetime,uploadObj_1);
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
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
        } catch (Exception e) {
            //                throw new Exception(e);
            throw new TestcaseException(e,rule.getParam());
        }
//        }
    }
}
