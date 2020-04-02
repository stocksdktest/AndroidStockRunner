package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.SearchResultItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.SearchRequestV2;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.SearchResponse;

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

/**新版股名在线搜索接口 2
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.SEARCHV2TEST_2)
public class SearchV2Test_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.SEARCHV2TEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("   SearchV2Test_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("   SearchV2Test_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("KEYWORD");
        final String quoteNumbers1 = rule.getParam().optString("MARKET");
        final String quoteNumbers2 = rule.getParam().optString("CATEGORIES");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
        SearchRequestV2 request = new  SearchRequestV2 ();
        request.send(quoteNumbers,quoteNumbers1, Integer.parseInt(quoteNumbers2),new IResponseInfoCallback<SearchResponse>() {
            @Override
            public void callback(SearchResponse searchResponse) {
                try {
                    assertNotNull(searchResponse.results);
                } catch (AssertionError e) {
                    //                        result.completeExceptionally(e);
                    result.complete(new JSONObject());
                }
                ArrayList<SearchResultItem> list=searchResponse.results;
                try {
                    JSONObject uploadObj = new JSONObject();
                    if (list!=null){
                        for (int i=0;i<list.size();i++){
                            JSONObject uploadObj_1 = new JSONObject();
                            uploadObj_1.put("stockID",list.get(i).stockID);
                            uploadObj_1.put("name",list.get(i).name);
                            uploadObj_1.put("market",list.get(i).market);
                            uploadObj_1.put("pinyin",list.get(i).pinyin);
                            uploadObj_1.put("subtype",list.get(i).subtype);
                            uploadObj_1.put("stockType",list.get(i).stockType);
//                            uploadObj_1.put("hkType",list.get(i).hkType);
                            uploadObj_1.put("st",list.get(i).st);
//                        Log.d("data", String.valueOf(uploadObj_1));
                            uploadObj.put(String.valueOf(i+1),uploadObj_1);
                        }
                    }
//                    Log.d("data", String.valueOf(uploadObj));
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
