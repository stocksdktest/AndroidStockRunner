package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.CateType;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.DRQuoteItem;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.keys.quote.DRSortType;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.DRQuoteListRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.DRQuoteListResponse;
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
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *CDR,GDR列表
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.DRQUOTELISTTEST_1)
public class DRQuoteListTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.DRQUOTELISTTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("DRQuoteListTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("DRQuoteListTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("DRCODE");
        final String quoteNumbers1 = rule.getParam().optString("PARAMS");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //DRSortType
//        for (int i=0;i<quoteNumbers.length;i++){
           DRQuoteListRequest request = new DRQuoteListRequest();
            request.send(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<DRQuoteListResponse>() {
                @Override
                public void callback(DRQuoteListResponse drQuoteListResponse) {
                    try {
                        assertNotNull(drQuoteListResponse.mDRQuoteItems);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    JSONObject uploadObj = new JSONObject();
                    try {
                        if(drQuoteListResponse.mDRQuoteItems!=null){
                            for (int i=0;i<drQuoteListResponse.mDRQuoteItems.size();i++) {
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("code",drQuoteListResponse.mDRQuoteItems.get(i).code);
                                uploadObj_1.put("name", drQuoteListResponse.mDRQuoteItems.get(i).name);
                                uploadObj_1.put("lastPrice", drQuoteListResponse.mDRQuoteItems.get(i).lastPrice);
                                uploadObj_1.put("preClosePrice", drQuoteListResponse.mDRQuoteItems.get(i).preClosePrice);
                                uploadObj_1.put("change",drQuoteListResponse.mDRQuoteItems.get(i).change);
                                uploadObj_1.put("changeRate", drQuoteListResponse.mDRQuoteItems.get(i).changeRate);
                                uploadObj_1.put("subType", drQuoteListResponse.mDRQuoteItems.get(i).subType);
                                uploadObj_1.put("dateTime", drQuoteListResponse.mDRQuoteItems.get(i).dateTime);
                                uploadObj_1.put("premium", drQuoteListResponse.mDRQuoteItems.get(i).premium);
                                uploadObj_1.put("baseCode",drQuoteListResponse.mDRQuoteItems.get(i).baseCode);
                                uploadObj_1.put("baseName",drQuoteListResponse.mDRQuoteItems.get(i).baseName);
                                uploadObj_1.put("baseLastPrice",drQuoteListResponse.mDRQuoteItems.get(i).baseLastPrice);
                                uploadObj_1.put("basePreClosePrice",drQuoteListResponse.mDRQuoteItems.get(i).basePreClosePrice);
                                uploadObj_1.put("baseChange",drQuoteListResponse.mDRQuoteItems.get(i).baseChange);
                                uploadObj_1.put("baseChangeRate",drQuoteListResponse.mDRQuoteItems.get(i).baseChangeRate);
                                uploadObj_1.put("baseSubtype",drQuoteListResponse.mDRQuoteItems.get(i).baseSubtype);
                                uploadObj_1.put("baseDateTime",drQuoteListResponse.mDRQuoteItems.get(i).baseDateTime);
//                            Log.d("data", String.valueOf(uploadObj_1));
                                uploadObj.put(String.valueOf(i+1),uploadObj_1);
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