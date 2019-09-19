package com.chi.ssetest.cases;

import android.content.SyncStatusObserver;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.OHLCItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.parser.FQItem;
import com.mitake.core.request.OHLCRequestV3;
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.response.IResponseCallback;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.OHLCResponse;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;

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

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//历史K线  方法四历史数据(包含当天,仅支持沪深市场)
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.OHLCV3TEST_4)
public class OHLCV3Test_4 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.OHLCV3TEST_4;
    private static SetupConfig.TestcaseConfig testcaseConfig;

    @BeforeClass
    public static void setup() throws Exception {
        Log.d("OHLCV3Test_4", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("OHLCV3Test_4", "requestWork");
        // TODO get custom args from param
        final String CODES = rule.getParam().optString("CODES", "");
        final String BeginDates = rule.getParam().optString("BeginDates", "");
        final String EndDates = rule.getParam().optString("EndDates", "");
        final String Types = rule.getParam().optString("TYPES", "");
        final String FqTypes = rule.getParam().optString("FqTypes", "");
//        OHLChartType
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<CODES.length;i++){
            QuoteDetailRequest quoteDetailRequest=new QuoteDetailRequest();
            quoteDetailRequest.send(CODES, new IResponseInfoCallback() {
                @Override
                public void callback(Response response) {
                    QuoteResponse quoteResponse=(QuoteResponse) response;
                    QuoteItem quoteItem=quoteResponse.quoteItems.get(0);
                    OHLCRequestV3 request = new OHLCRequestV3();
                    String begindate;
                    String enddate;
                    if (BeginDates.equals("null")){
                        begindate=null;
                    }else {
                        begindate=BeginDates;
                    }
                    if (EndDates.equals("null")){
                        enddate=null;
                    }else {
                        enddate=EndDates;
                    }
                    request.send(quoteItem, begindate, enddate, Types, Integer.parseInt(FqTypes), new IResponseCallback() {
                        @Override
                        public void callback(Response response) {
                            OHLCResponse ohlcResponse = (OHLCResponse) response;
                            try {
                                assertNotNull(ohlcResponse.historyItems);
                            } catch (AssertionError e) {
                                result.completeExceptionally(e);
                            }
                            CopyOnWriteArrayList<OHLCItem> list =ohlcResponse.historyItems;
                            CopyOnWriteArrayList<FQItem> list2=ohlcResponse.fq;
                            List<JSONObject> items=new ArrayList<>();
                            List<JSONObject> items_1=new ArrayList<>();
                            JSONObject uploadObj = new JSONObject();
                            for (int k=0;k<list.size();k++){
                                try {
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("code",CODES);
                                    uploadObj_1.put("datetime",list.get(k).datetime);
                                    uploadObj_1.put("openPrice",list.get(k).openPrice);
                                    uploadObj_1.put("highPrice",list.get(k).highPrice);
                                    uploadObj_1.put("lowPrice",list.get(k).lowPrice);
                                    uploadObj_1.put("closePrice",list.get(k).closePrice);
                                    uploadObj_1.put("tradeVolume",list.get(k).tradeVolume);
                                    uploadObj_1.put("averagePrice",list.get(k).averagePrice);//ios需要判断是否存在字段
                                    uploadObj_1.put("reference_price",list.get(k).reference_price);
                                    uploadObj_1.put("transaction_price",list.get(k).transaction_price);
                                    uploadObj_1.put("openInterest",list.get(k).openInterest);//ios需要判断是否存在字段
                                    uploadObj_1.put("fp_volume",list.get(k).fp_volume);
                                    uploadObj_1.put("fp_amount",list.get(k).fp_amount);
                                    items.add(uploadObj_1);
                                } catch (JSONException e) {
                                    result.completeExceptionally(e);
                                }
                            }
                            if (list2!=null){
                                for (int j=0;j<list2.size();j++){
                                    try {
                                        JSONObject uploadObj_2 = new JSONObject();
                                        uploadObj_2.put("code",CODES);
                                        uploadObj_2.put("dateTime",list2.get(j).dateTime);
                                        uploadObj_2.put("increasePrice",list2.get(j).increasePrice);
                                        uploadObj_2.put("allotmentPrice",list2.get(j).allotmentPrice);
                                        uploadObj_2.put("bonusAmount",list2.get(j).bonusAmount);
                                        uploadObj_2.put("bonusProportion",list2.get(j).bonusProportion);
                                        uploadObj_2.put("increaseProportion",list2.get(j).increaseProportion);
                                        uploadObj_2.put("increaseVolume",list2.get(j).increaseVolume);
                                        uploadObj_2.put("allotmentProportion",list2.get(j).allotmentProportion);
                                        items_1.add(uploadObj_2);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                try {
                                    uploadObj.put("items_1",new JSONArray(items_1));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    JSONArray jsonArray_1 = uploadObj.getJSONArray("items_1");
                                    for (int i=0;i<jsonArray_1.length();i++){
                                        JSONObject jsonObject = jsonArray_1.getJSONObject(i);
                                        Log.d("data_2", String.valueOf(jsonObject));
                                        //                            System.out.println(jsonObject.optString("code")+","+jsonObject.optString("datetime"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Log.d("Fqitem","null");
                            }
                            try {
                                uploadObj.put("items",new JSONArray(items));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //解析输出JSON
                            try {
                                JSONArray jsonArray = uploadObj.getJSONArray("items");
                                for (int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Log.d("data_1", String.valueOf(jsonObject));
                                    //                            System.out.println(jsonObject.optString("code")+","+jsonObject.optString("datetime"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            result.complete(uploadObj);
                        }

                        @Override
                        public void exception(int i, String s) {
                            result.completeExceptionally(new Exception(s));
                        }
                    });
//                    request.send(quoteItem,BeginDates[b],EndDates[b],Types[b],Integer.parseInt(FqTypes[b]), new IResponseInfoCallback() {
//                        @Override
//                        public void callback(Response response) {
//                            OHLCResponse ohlcResponse = (OHLCResponse) response;
//                            assertNotNull(ohlcResponse.historyItems);
//                            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
//                            JSONObject uploadObj = new JSONObject();
//                            // TODO fill uploadObj with QuoteResponse value
//                            try {
//                                uploadObj.put("fake_result",CODES);
//                            } catch (JSONException e) {
//                                result.completeExceptionally(e);
//                            }
//                            if (ohlcResponse.historyItems.size()>0){
//                                for (OHLCItem item : ohlcResponse.historyItems) {
//                                    Log.d("StockUnittest", CODES[b]+item.datetime);
//                                }
//                            }else {
//                                System.out.println("null__=+_+=-=-==——+——=——=");
//                            }
//
//                            result.complete(uploadObj);
//                        }
//                        @Override
//                        public void exception(ErrorInfo errorInfo) {
//                            result.completeExceptionally(new Exception(errorInfo.toString()));
//                        }
//                    });
                }
                @Override
                public void exception(ErrorInfo errorInfo) {
                    result.completeExceptionally(new Exception(errorInfo.toString()));
                }
            });
            try {
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}
