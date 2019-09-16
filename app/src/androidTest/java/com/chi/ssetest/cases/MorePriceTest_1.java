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
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
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
 *分价1——适合所有市场
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName. MOREPRICETEST_1)
public class MorePriceTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName. MOREPRICETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;

    @BeforeClass

    public static void setup() throws Exception {
        Log.d(" MorePriceTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d(" MorePriceTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final String quoteNumbers1 = rule.getParam().optString("subtype");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
//        for (int i=0;i<quoteNumbers.length;i++){
           MorePriceRequest request = new  MorePriceRequest();
            request.send(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<MorePriceResponse>() {
                @Override
                public void callback(MorePriceResponse morePriceResponse) {
                    assertNotNull(morePriceResponse.strs);
                    List<JSONObject> items=new ArrayList<>();
                    JSONObject uploadObj = new JSONObject();
                    if (morePriceResponse.strs!=null&&morePriceResponse.strs.length>0){
                        for (int i=0;i<morePriceResponse.strs.length;i++){
                            JSONObject uploadObj_1 = new JSONObject();
                            try {
                                uploadObj_1.put("code", quoteNumbers);
                                List<JSONObject> list=new ArrayList<>();
                                if (morePriceResponse.strs[i]!=null&&morePriceResponse.strs[i].length>0){
                                    JSONObject uploadObj_2 = new JSONObject();
                                    uploadObj_2.put("price",morePriceResponse.strs[i][0]);
                                    uploadObj_2.put("volume",morePriceResponse.strs[i][1]);
                                    uploadObj_2.put("buyVolume",morePriceResponse.strs[i][2]);
                                    uploadObj_2.put("sellVolume",morePriceResponse.strs[i][3]);
                                    uploadObj_2.put("unknownVolume",morePriceResponse.strs[i][4]);
                                    uploadObj_2.put("tradeCount",morePriceResponse.strs[i][5]);
                                    uploadObj_2.put("buyCount",morePriceResponse.strs[i][6]);
                                    uploadObj_2.put("sellCount",morePriceResponse.strs[i][7]);
                                    uploadObj_2.put("unknownCount",morePriceResponse.strs[i][8]);
                                    list.add(uploadObj_2);
                                    uploadObj_1.put("list",new JSONArray(list));
                                }else {
                                    uploadObj_1.put("list",morePriceResponse.strs);
                                }
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
                    }
                    //返回JSON结果
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