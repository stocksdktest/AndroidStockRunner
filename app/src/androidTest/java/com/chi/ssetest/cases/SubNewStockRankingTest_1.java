package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.QuoteItem;
import com.mitake.core.SubNewStockRankingModel;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.SubNewStockRankingRequest;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;
import com.mitake.core.response.SubNewStockRankingResponse;

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
 *次新股
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.SUBNEWSTOCKRANKINGTEST_1)
public class SubNewStockRankingTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.SUBNEWSTOCKRANKINGTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d(" SubNewStockRankingTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("SubNewStockRankingTest_1", "requestWork");
        // TODO get custom args from param
        final String []quoteNumbers = rule.getParam().optString("param").split(";");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        for (int i=0;i<quoteNumbers.length;i++){
            SubNewStockRankingRequest request = new SubNewStockRankingRequest();
            request.send(quoteNumbers[i], new IResponseInfoCallback<SubNewStockRankingResponse>() {
                @Override
                public void callback(SubNewStockRankingResponse subNewStockRankingResponse) {
                    assertNotNull(subNewStockRankingResponse.list);
                    ArrayList<SubNewStockRankingModel> list=subNewStockRankingResponse.list;
                    List<JSONObject> items=new ArrayList<>();
                    JSONObject uploadObj = new JSONObject();
                    for (int i=0;i<list.size();i++) {
                        JSONObject uploadObj_1 = new JSONObject();
                        try {
                            uploadObj_1.put("name",list.get(i).getName());
                            uploadObj_1.put("code",list.get(i).getCode());
                            uploadObj_1.put("subType",list.get(i).getSubType());
                            uploadObj_1.put("originalPrice",list.get(i).getOriginalPrice());
                            uploadObj_1.put("lastestPrice",list.get(i).getLastestPrice());
                            uploadObj_1.put("originalData",list.get(i).getOriginalData());
                            uploadObj_1.put("continuousLimitedDays",list.get(i).getContinuousLimitedDays());
                            uploadObj_1.put("rate",list.get(i).getRate());
                            uploadObj_1.put("allRate",list.get(i).getAllRate());
                            uploadObj_1.put("preClosePrice",list.get(i).getPreClosePrice());
                            items.add(uploadObj_1);
                        } catch (JSONException e) {
                            result.completeExceptionally(e);
                        }
                    }
                    try {
                        //把数组存储到JSON
                        uploadObj.put("items", new JSONArray(items));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //解析输出JSON
                    try {
                        JSONArray jsonArray = uploadObj.getJSONArray("items");
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Log.d("data", String.valueOf(jsonObject));
//                            System.out.println(jsonObject.optString("code")+","+jsonObject.optString("datetime"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        }
    }
}