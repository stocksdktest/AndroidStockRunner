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
import com.mitake.core.SearchResultItem;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.keys.FuturesQuoteBaseField;
import com.mitake.core.keys.FuturesQuoteField;
import com.mitake.core.keys.quote.AddValueCustomField;
import com.mitake.core.keys.quote.QuoteCustomField;
import com.mitake.core.keys.quote.SortType;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CateSortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.SearchRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CateSortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.MorePriceResponse;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;
import com.mitake.core.response.SearchResponse;

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

/**股票查询5
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.SEARCHTEST_5)
public class SearchTest_5 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.SEARCHTEST_5;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("   SearchTest_5", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    // SortType
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("   SearchTest_5", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("keyword");
        final String quoteNumbers1 = rule.getParam().optString("searchCode");
        final String quoteNumbers2 = rule.getParam().optString("searchSize");
        final String quoteNumbers3 = rule.getParam().optString("querySts");
//        CateType
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
        String[] querySts;
        String[] searchCode;
        if (quoteNumbers1.equals("null")){
            searchCode=null;
        }else {
            searchCode=new String[]{quoteNumbers1};
        }
        if (quoteNumbers3.equals("null")){
            querySts=null;
        }else {
            querySts=new String[]{quoteNumbers3};
        }
            SearchRequest request = new  SearchRequest();
            request.sendV2(quoteNumbers,searchCode,Integer.parseInt(quoteNumbers2),querySts,new IResponseInfoCallback<SearchResponse>() {
                //CateType
                @Override
                public void callback(SearchResponse searchResponse) {
                    try {
                        assertNotNull(searchResponse.results);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    ArrayList<SearchResultItem> list=searchResponse.results;
                    try {
                        for (int i=0;i<list.size();i++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("stockID",list.get(i).stockID);
                            uploadObj_1.put("name",list.get(i).name);
                            uploadObj_1.put("market",list.get(i).market);
                            uploadObj_1.put("pinyin",list.get(i).pinyin);
                            uploadObj_1.put("subtype",list.get(i).subtype);
                            uploadObj_1.put("stockType",list.get(i).stockType);
                            uploadObj_1.put("hkType",list.get(i).hkType);
                            uploadObj_1.put("st",list.get(i).st);
                            Log.d("data", String.valueOf(uploadObj_1));
                            result.complete(uploadObj_1);
                        }
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
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}
