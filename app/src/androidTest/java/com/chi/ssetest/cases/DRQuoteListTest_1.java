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
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("DRQuoteListTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final String quoteNumbers1 = rule.getParam().optString("param");
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
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    List<JSONObject> items =new ArrayList<>();
                    try {
                        for (DRQuoteItem item : drQuoteListResponse.mDRQuoteItems) {
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("code", item.code);
                            uploadObj_1.put("code", item.name);
                            uploadObj_1.put("lastPrice", item.lastPrice);
                            uploadObj_1.put("preClosePrice", item.preClosePrice);
                            uploadObj_1.put("change",item.change);
                            uploadObj_1.put("changeRate", item.changeRate);
                            uploadObj_1.put("subType", item.subType);
                            uploadObj_1.put("dateTime", item.dateTime);
                            uploadObj_1.put("premium", item.premium);
                            uploadObj_1.put("baseCode",item.baseCode);
                            uploadObj_1.put("baseName",item.baseName);
                            uploadObj_1.put("baseLastPrice",item.baseLastPrice);
                            uploadObj_1.put("basePreClosePrice",item.basePreClosePrice);
                            uploadObj_1.put("baseChange",item.baseChange);
                            uploadObj_1.put("baseChangeRate",item.baseChangeRate);
                            uploadObj_1.put("baseSubtype",item.baseSubtype);
                            uploadObj_1.put("baseDateTime",item.baseDateTime);
                            items.add(uploadObj_1);
                        }
                        uploadObj.put("items",new JSONArray(items));
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
            try {
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}