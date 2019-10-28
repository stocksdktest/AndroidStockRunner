package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.CateType;
import com.mitake.core.OHLCItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.ChartType;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.OverLayChartRequest;
import com.mitake.core.request.PointAddType;
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.ChartResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.MorePriceResponse;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
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
 *走势叠加2
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.OVERLAYCHARTTEST_2)
public class OverLayChartTest_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.OVERLAYCHARTTEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("OverLayChartTest_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty",testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("OverLayChartTest_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("baseCode");
        final String quoteNumbers1 = rule.getParam().optString("overLayCode");
        final String quoteNumbers2 = rule.getParam().optString("chartType");
        final String quoteNumbers3 = rule.getParam().optString("pointType");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //PointAddType 0  1   2
        //ChartType
//        for (int i=0;i<quoteNumbers.length;i++){
        QuoteDetailRequest quoteDetailRequest=new QuoteDetailRequest();
        quoteDetailRequest.send(quoteNumbers, new IResponseInfoCallback() {
            @Override
            public void callback(Response response) {
                QuoteResponse quoteResponse=(QuoteResponse) response;
                QuoteItem quoteItem=quoteResponse.quoteItems.get(0);
                OverLayChartRequest request = new OverLayChartRequest();
                request.send(quoteNumbers,quoteNumbers1,quoteNumbers2,Integer.parseInt(quoteNumbers3),new IResponseInfoCallback<ChartResponse>() {
                    @Override
                    public void callback(ChartResponse chartResponse) {
                        try {
                            assertNotNull(chartResponse.historyItems);
                        } catch (AssertionError e) {
                            result.completeExceptionally(e);
                        }
                        CopyOnWriteArrayList<OHLCItem> list=chartResponse.historyItems;
                        CopyOnWriteArrayList<OHLCItem> list2=chartResponse.overLayChartResponse.historyItems;
                        List<JSONObject> items=new ArrayList<>();
                        JSONObject uploadObj = new JSONObject();
                        try {
                            List<JSONObject> dayList=new ArrayList<>();
                            if (quoteNumbers2.equals("ChartTypeOneDay")){
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("day",chartResponse.dayList.get(0));
                                dayList.add(uploadObj_1);
                            }else {
                                for (int k=0;k<chartResponse.dayList.size();k++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("day",chartResponse.dayList.get(k));
                                    dayList.add(uploadObj_1);
                                }
                                uploadObj.put("dayList",new JSONArray(dayList));
                            }
                            List<JSONObject> OHLCItem=new ArrayList<>();
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
                                OHLCItem.add(uploadObj_1);
                            }
                            uploadObj.put("OHLCItem",new JSONArray(OHLCItem));

                            List<JSONObject> overLayChart=new ArrayList<>();
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
                                overLayChart.add(uploadObj_1);
                            }
                            uploadObj.put("overLayChart",new JSONArray(overLayChart));
                        } catch (JSONException e) {
                            result.completeExceptionally(e);
                        }
                        Log.d("data", String.valueOf(uploadObj));
                        result.complete(uploadObj);
                    }
                    @Override
                    public void exception(ErrorInfo errorInfo) {
                        result.completeExceptionally(new Exception(errorInfo.toString()));
                    }
                });
            }
            @Override
            public void exception(ErrorInfo errorInfo) {
                result.completeExceptionally(new Exception(errorInfo.toString()));
            }
        });
        try {
            JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
        } catch (Exception e) {
            throw new Exception(e);
        }
//        }
    }
}
