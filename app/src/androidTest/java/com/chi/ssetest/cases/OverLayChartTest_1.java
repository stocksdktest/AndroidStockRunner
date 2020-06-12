package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.OHLCItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.OverLayChartRequest;
import com.mitake.core.response.ChartResponse;
import com.mitake.core.response.IResponseInfoCallback;

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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *走势叠加1
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.OVERLAYCHARTTEST_1)
public class OverLayChartTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.OVERLAYCHARTTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("OverLayChartTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty",testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("OverLayChartTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final String quoteNumbers1 = rule.getParam().optString("superpositionCode");
        final String quoteNumbers2 = rule.getParam().optString("TYPE");
        final String quoteNumbers3 = rule.getParam().optString("pointType");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //PointAddType 0  1   2
        //ChartType
//        for (int i=0;i<quoteNumbers.length;i++){
            OverLayChartRequest request = new OverLayChartRequest();
            request.send(quoteNumbers,quoteNumbers1,quoteNumbers2,Integer.parseInt(quoteNumbers3),new IResponseInfoCallback<ChartResponse>() {
                @Override
                public void callback(ChartResponse chartResponse) {
                    try {
                        assertNotNull(chartResponse.historyItems);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    CopyOnWriteArrayList<OHLCItem> list=chartResponse.historyItems;
                    CopyOnWriteArrayList<OHLCItem> list2=chartResponse.overLayChartResponse.historyItems;
                    JSONObject uploadObj = new JSONObject();
                    JSONObject uploadObj_2 = new JSONObject();
                    JSONObject uploadObj_3 = new JSONObject();
                    try {
                       if(chartResponse.dayList!=null){
                           List<String> dayList=new ArrayList<>();
                           if (quoteNumbers2.equals("ChartTypeOneDay")){
                               dayList.add(chartResponse.dayList.get(0));
                           }else {
                               for (int k=0;k<chartResponse.dayList.size();k++){
                                   dayList.add(chartResponse.dayList.get(k));
                               }
                           }
                           uploadObj.put("dayList",new JSONArray(dayList));
                       }

                        if(list!=null){
                            for (int k=0;k<chartResponse.historyItems.size();k++) {
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("datetime",list.get(k).datetime);
                                uploadObj_1.put("closePrice",list.get(k).closePrice);
                                uploadObj_1.put("tradeVolume",list.get(k).tradeVolume);
                                uploadObj_1.put("averagePrice",list.get(k).averagePrice);
                                uploadObj_1.put("md",list.get(k).getMd());
                                uploadObj_1.put("iopv",list.get(k).iopv);
                                uploadObj_1.put("iopvPre",list.get(k).iopvPre);
                                uploadObj_1.put("openInterest",list.get(k).openInterest);
//                                uploadObj_1.put("volRatio",list.get(k).volRatio);
                                uploadObj_2.put(list.get(k).datetime,uploadObj_1);
                            }
                            uploadObj.put("OHLCItem",uploadObj_2);
                        }

                        if(list2!=null){
                            for (int k=0;k<list2.size();k++){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("datetime",list2.get(k).datetime);
                                uploadObj_1.put("closePrice",list2.get(k).closePrice);
                                uploadObj_1.put("tradeVolume",list2.get(k).tradeVolume);
                                uploadObj_1.put("averagePrice",list2.get(k).averagePrice);
                                uploadObj_1.put("md",list2.get(k).getMd());
                                uploadObj_1.put("iopv",list2.get(k).iopv);
                                uploadObj_1.put("iopvPre",list2.get(k).iopvPre);
                                uploadObj_1.put("openInterest",list2.get(k).openInterest);
//                                uploadObj_1.put("volRatio",list2.get(k).volRatio);
                                uploadObj_3.put(list2.get(k).datetime,uploadObj_1);
                            }
                            uploadObj.put("overLayChart",uploadObj_3);
                        }
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
//                    Log.d("data", String.valueOf(uploadObj));
                    result.complete(uploadObj);
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