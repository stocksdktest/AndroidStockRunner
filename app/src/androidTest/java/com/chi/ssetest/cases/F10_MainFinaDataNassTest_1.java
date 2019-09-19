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
import com.mitake.core.Importantnotice;
import com.mitake.core.MainFinaDataNas;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.ImportantnoticeRequest;
import com.mitake.core.request.MainFinaDataNasRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.ImportantnoticeResponse;
import com.mitake.core.response.MainFinaDataNasResponse;
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
 *财务报表1
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_MAINFINADATANASSTEST_1)
public class F10_MainFinaDataNassTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_MAINFINADATANASSTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_MainFinaDataNassTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("F10_MainFinaDataNassTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("stockId");
        final String quoteNumbers1 = rule.getParam().optString("dataSourceType");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
//        for (int i=0;i<quoteNumbers.length;i++){
            MainFinaDataNasRequest request = new MainFinaDataNasRequest();
            request.sendV2(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<MainFinaDataNasResponse>() {
                @Override
                public void callback(MainFinaDataNasResponse mainFinaDataNasResponse) {
                    try {
                        assertNotNull(mainFinaDataNasResponse.mMainFinaDataNasList);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    List<JSONObject> items=new ArrayList<>();
                    if (mainFinaDataNasResponse.mMainFinaDataNasList!=null){
                        for (MainFinaDataNas item : mainFinaDataNasResponse.mMainFinaDataNasList) {
                            JSONObject uploadObj_1 = new JSONObject();
                            try {
                                uploadObj_1.put("REPORTTITLE_",item.REPORTTITLE_);
                                uploadObj_1.put("BasicEPS",item.BasicEPS);
                                uploadObj_1.put("RESERVEPS_",item.RESERVEPS_);
                                uploadObj_1.put("BVPS_",item.BVPS_);
                                uploadObj_1.put("NETCASHFLOWOPERPS_",item.NETCASHFLOWOPERPS_);
                                uploadObj_1.put("WEIGHTEDROE_",item.WEIGHTEDROE_);
                                uploadObj_1.put("ROA_",item.ROA_);
                                uploadObj_1.put("TotalOperRevenue",item.TotalOperRevenue);
                                uploadObj_1.put("OperProfit",item.OperProfit);
                                uploadObj_1.put("NetProfit",item.NetProfit);
                                uploadObj_1.put("TotalAsset",item.TotalAsset);
                                uploadObj_1.put("TotalLiab",item.TotalLiab);
                                uploadObj_1.put("TotalSHEquity",item.TotalSHEquity);
                                uploadObj_1.put("NetCashFlowOper",item.NetCashFlowOper);
                                uploadObj_1.put("NetCashFlowInv",item.NetCashFlowInv);
                                uploadObj_1.put("NetCashFlowFina",item.NetCashFlowFina);
                                uploadObj_1.put("CashEquiNetIncr",item.CashEquiNetIncr);
                                uploadObj_1.put("EPSBASIC",item.EPSBASIC);
                                uploadObj_1.put("ENDDATE",item.ENDDATE);
                                uploadObj_1.put("MOM",item.MOM);
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
                    }else {
                        try {
                            uploadObj.put("items", mainFinaDataNasResponse.mMainFinaDataNasList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

//                    //解析输出JSON
//                    try {
//                        JSONArray jsonArray = uploadObj.getJSONArray("items");
//                        for (int i=0;i<jsonArray.length();i++){
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            Log.d("data", String.valueOf(jsonObject));
////                            System.out.println(jsonObject.optString("code")+","+jsonObject.optString("datetime"));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    Log.d("data", String.valueOf(uploadObj));
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
