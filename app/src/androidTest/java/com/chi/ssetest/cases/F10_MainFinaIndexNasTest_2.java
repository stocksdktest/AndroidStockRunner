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
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("F10_MainFinaIndexNasTest_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("stockId");
        final String quoteNumbers1 = rule.getParam().optString("dataSourceType");
        final String quoteNumbers2 = rule.getParam().optString("cueryContent");
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
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    List<JSONObject> items=new ArrayList<>();
                    if (mainFinaIndexNasResponse.mMainFinaIndexHasList!=null){
                        for (MainFinaIndexHas item : mainFinaIndexNasResponse.mMainFinaIndexHasList) {
                            JSONObject uploadObj_1 = new JSONObject();
                            try {
                                uploadObj_1.put("REPORTTITLE_",item.REPORTTITLE_);
                                uploadObj_1.put("BasicEPS",item.BasicEPS);
                                uploadObj_1.put("RESERVEPS_",item.RESERVEPS_);
                                uploadObj_1.put("BVPS_",item.BVPS_);
                                uploadObj_1.put("TotalAssetTurnover",item.TotalAssetTurnover);
                                uploadObj_1.put("EBITPS_",item.EBITPS_);
                                uploadObj_1.put("RetainedEarningPS",item.RetainedEarningPS);
                                uploadObj_1.put("NetCashFlowOperPS",item.NetCashFlowOperPS);
                                uploadObj_1.put("NETCASHFLOWPS_",item.NETCASHFLOWPS_);
                                uploadObj_1.put("WEIGHTEDROE_",item.WEIGHTEDROE_);
                                uploadObj_1.put("ROA_EBIT_",item.ROA_EBIT_);
                                uploadObj_1.put("GROSSPROFITMARGIN_",item.GROSSPROFITMARGIN_);
                                uploadObj_1.put("PROFITMARGIN_",item.PROFITMARGIN_);
                                uploadObj_1.put("TLToTA_",item.TLToTA_);
                                uploadObj_1.put("TAToSHE_",item.TAToSHE_);
                                uploadObj_1.put("CurrentRatio",item.CurrentRatio);
                                uploadObj_1.put("QuickRatio",item.QuickRatio);
                                uploadObj_1.put("EBITPS_",item.EBITPS_);
                                uploadObj_1.put("InventoryTurnover",item.InventoryTurnover);
                                uploadObj_1.put("ACCOUNTRECTURNOVER_",item.ACCOUNTRECTURNOVER_);
                                uploadObj_1.put("FixedAssetTurnover",item.FixedAssetTurnover);
                                uploadObj_1.put("TotalAssetTurnover",item.TotalAssetTurnover);
                                uploadObj_1.put("OperRevenueYOY",item.OperRevenueYOY);
                                uploadObj_1.put("OperProfitYOY",item.OperProfitYOY);
                                uploadObj_1.put("NETPROFITPARENTCOMYOY_",item.NETPROFITPARENTCOMYOY_);
                                uploadObj_1.put("NetCashFlowOperYOY",item.NetCashFlowOperYOY);
                                uploadObj_1.put("ROEYOY_",item.ROEYOY_);
                                uploadObj_1.put("NetAssetYOY",item.NetAssetYOY);
                                uploadObj_1.put("TotalAssetYOY",item.TotalAssetYOY);
                                Log.d("data", String.valueOf(uploadObj_1));
                                result.complete(uploadObj_1);
                            } catch (JSONException e) {
                                result.completeExceptionally(e);
                            }
                        }
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