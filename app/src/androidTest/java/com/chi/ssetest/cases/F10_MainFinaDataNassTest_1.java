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
    private static final int timeout_ms = 1000000;
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
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("F10_MainFinaDataNassTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final String quoteNumbers1 = rule.getParam().optString("SOURCETYPE");
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
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    JSONObject uploadObj = new JSONObject();
                    try {
                        if (mainFinaDataNasResponse.mMainFinaDataNasList!=null){
                            for (int i=0;i<mainFinaDataNasResponse.mMainFinaDataNasList.size();i++) {
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("REPORTTITLE_",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).REPORTTITLE_);
                                uploadObj_1.put("BasicEPS",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).BasicEPS);
//                                uploadObj_1.put("RESERVEPS_",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).RESERVEPS_);
                                uploadObj_1.put("BVPS_",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).BVPS_);
                                uploadObj_1.put("NETCASHFLOWOPERPS_",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).NETCASHFLOWOPERPS_);
                                uploadObj_1.put("WEIGHTEDROE_",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).WEIGHTEDROE_);
                                uploadObj_1.put("ROA_",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).ROA_);
                                uploadObj_1.put("TotalOperRevenue",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).TotalOperRevenue);
                                uploadObj_1.put("OperProfit",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).OperProfit);
                                uploadObj_1.put("NetProfit",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).NetProfit);
                                uploadObj_1.put("TotalAsset",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).TotalAsset);
                                uploadObj_1.put("TotalLiab",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).TotalLiab);
                                uploadObj_1.put("TotalSHEquity",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).TotalSHEquity);
                                uploadObj_1.put("NetCashFlowOper",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).NetCashFlowOper);
                                uploadObj_1.put("NetCashFlowInv",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).NetCashFlowInv);
                                uploadObj_1.put("NetCashFlowFina",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).NetCashFlowFina);
                                uploadObj_1.put("CashEquiNetIncr",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).CashEquiNetIncr);
                                uploadObj_1.put("EPSBASIC",mainFinaDataNasResponse.mMainFinaDataNasList.get(i).EPSBASIC);
//                                uploadObj_1.put("ENDDATE",item.ENDDATE);
//                                uploadObj_1.put("MOM",item.MOM);
                                uploadObj.put(String.valueOf(i+1),uploadObj_1);
                            }
                        }
                        Log.d("data", String.valueOf(uploadObj));
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
