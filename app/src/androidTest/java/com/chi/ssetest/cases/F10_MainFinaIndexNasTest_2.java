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
import com.mitake.core.MainFinaIndexHas;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.ImportantnoticeRequest;
import com.mitake.core.request.MainFinaDataNasRequest;
import com.mitake.core.request.MainFinaIndexNasRequest;
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
import com.mitake.core.response.MainFinaIndexNasResponse;
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
 *财务指标2--仅用于港股
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_MAINFINAINDEXNASTEST_2)
public class F10_MainFinaIndexNasTest_2 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_MAINFINAINDEXNASTEST_2;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_MainFinaIndexNasTest_2", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("F10_MainFinaIndexNasTest_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("CODE");
        final String quoteNumbers1 = rule.getParam().optString("SRC");
        final String quoteNumbers2 = rule.getParam().optString("PARAMS");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
        String dataSourceType;
        if (quoteNumbers1.equals("null")){
            dataSourceType=null;
        }else {
            dataSourceType=quoteNumbers1;
        }
        String cueryContent;
        if (quoteNumbers2.equals("null")){
            cueryContent=null;
        }else {
            cueryContent=quoteNumbers2;
        }
            MainFinaIndexNasRequest request = new MainFinaIndexNasRequest();
            request.sendV2(quoteNumbers,dataSourceType,cueryContent,new IResponseInfoCallback<MainFinaIndexNasResponse>() {
                @Override
                public void callback(MainFinaIndexNasResponse mainFinaIndexNasResponse) {
                    try {
                        assertNotNull(mainFinaIndexNasResponse.mMainFinaIndexHasList);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    JSONObject uploadObj = new JSONObject();
                    try {
                        if (mainFinaIndexNasResponse.mMainFinaIndexHasList!=null){
                            for (int i=0;i<mainFinaIndexNasResponse.mMainFinaIndexHasList.size();i++) {
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("REPORTTITLE_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).REPORTTITLE_);
                                uploadObj_1.put("BasicEPS",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).BasicEPS);
                                uploadObj_1.put("RESERVEPS_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).RESERVEPS_);
                                uploadObj_1.put("BVPS_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).BVPS_);
                                uploadObj_1.put("TotalOperIncomePS",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).TotalOperIncomePS);
                                uploadObj_1.put("EBITPS_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).EBITPS_);
                                uploadObj_1.put("RetainedEarningPS",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).RetainedEarningPS);
                                uploadObj_1.put("NetCashFlowOperPS",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).NetCashFlowOperPS);
                                uploadObj_1.put("NETCASHFLOWPS_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).NETCASHFLOWPS_);
                                uploadObj_1.put("WEIGHTEDROE_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).WEIGHTEDROE_);
                                uploadObj_1.put("ROA_EBIT_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).ROA_EBIT_);
                                uploadObj_1.put("GROSSPROFITMARGIN_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).GROSSPROFITMARGIN_);
                                uploadObj_1.put("PROFITMARGIN_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).PROFITMARGIN_);
                                uploadObj_1.put("TLToTA_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).TLToTA_);
                                uploadObj_1.put("TAToSHE_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).TAToSHE_);
                                uploadObj_1.put("CurrentRatio",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).CurrentRatio);
                                uploadObj_1.put("QuickRatio",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).QuickRatio);
                                uploadObj_1.put("EBITToIE_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).EBITToIE_);
                                uploadObj_1.put("InventoryTurnover",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).InventoryTurnover);
                                uploadObj_1.put("ACCOUNTRECTURNOVER_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).ACCOUNTRECTURNOVER_);
                                uploadObj_1.put("FixedAssetTurnover",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).FixedAssetTurnover);
                                uploadObj_1.put("TotalAssetTurnover",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).TotalAssetTurnover);
                                uploadObj_1.put("OperRevenueYOY",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).OperRevenueYOY);
                                uploadObj_1.put("OperProfitYOY",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).OperProfitYOY);
                                uploadObj_1.put("NETPROFITPARENTCOMYOY_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).NETPROFITPARENTCOMYOY_);
                                uploadObj_1.put("NetCashFlowOperYOY",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).NetCashFlowOperYOY);
                                uploadObj_1.put("ROEYOY_",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).ROEYOY_);
                                uploadObj_1.put("NetAssetYOY",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).NetAssetYOY);
                                uploadObj_1.put("TotalAssetYOY",mainFinaIndexNasResponse.mMainFinaIndexHasList.get(i).TotalAssetYOY);
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