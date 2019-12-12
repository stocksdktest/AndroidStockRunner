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
import com.mitake.core.NewShareItem;
import com.mitake.core.NewShareList;
import com.mitake.core.NewsDetailItem;
import com.mitake.core.QuoteItem;
import com.mitake.core.StockNewsDetailItem;
import com.mitake.core.StockShareInfo;
import com.mitake.core.TopLiquidShareHolder;
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
import com.mitake.core.request.NewShareListRequest;
import com.mitake.core.request.NewsRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.StockNewsRequest;
import com.mitake.core.request.StockShareInfoRequest;
import com.mitake.core.request.TopLiquidShareHolderRequest;
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
import com.mitake.core.response.NewShareListResponse;
import com.mitake.core.response.NewsResponse;
import com.mitake.core.response.QuoteResponse;
import com.mitake.core.response.Response;
import com.mitake.core.response.StockNewsResponse;
import com.mitake.core.response.StockShareInfoResponse;
import com.mitake.core.response.TopLiquidShareHolderResponse;

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
 *当天新股列表
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_NEWSHARELISTTEST_1)
public class F10_NewShareListTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_NEWSHARELISTTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_NewShareListTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("F10_NewShareListTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("date");
        final String quoteNumbers1 = rule.getParam().optString("src");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            NewShareListRequest request = new NewShareListRequest();
            request.sendV2(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<NewShareListResponse>() {
                @Override
                public void callback(NewShareListResponse newShareListResponse) {
                    try {
                        assertNotNull(newShareListResponse.infos);
                    } catch (AssertionError e) {
                        result.completeExceptionally(e);
                    }
                    JSONObject uploadObj = new JSONObject();
                    for (NewShareList item : newShareListResponse.infos) {
                        try {
                            uploadObj.put("title",item.getTitle());
                            List<JSONObject> dataList=new ArrayList<>();
                            if(item.getDataList()!=null){
                                for (int i=0;i<item.getDataList().size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("applyCode",item.getDataList().get(i).getApplyCode());
                                    uploadObj_1.put("capplyShare",item.getDataList().get(i).getCapplyShare());
                                    uploadObj_1.put("secuabbr",item.getDataList().get(i).getSecuabbr());
                                    uploadObj_1.put("tradingCode",item.getDataList().get(i).getTradingCode());
                                    uploadObj_1.put("peaIssue",item.getDataList().get(i).getPeaIssue());
                                    uploadObj_1.put("succResultNoticeDate",item.getDataList().get(i).getSuccResultNoticeDate());
                                    uploadObj_1.put("issuePrice",item.getDataList().get(i).getIssuePrice());
                                    uploadObj_1.put("allotrateon",item.getDataList().get(i).getAllotrateon());
                                    uploadObj_1.put("listingDate",item.getDataList().get(i).getListingDate());
                                    uploadObj_1.put("bookStartDateOn",item.getDataList().get(i).getBookStartDateOn());
                                    uploadObj_1.put("issueShare",item.getDataList().get(i).getIssueShare());
                                    uploadObj_1.put("issueShareOn",item.getDataList().get(i).getIssueShareOn());
                                    uploadObj_1.put("capplyPrice",item.getDataList().get(i).getCapplyPrice());
                                    uploadObj_1.put("cissueSharePlan",item.getDataList().get(i).getCissueSharePlan());
                                    uploadObj_1.put("issueShareOnPlan",item.getDataList().get(i).getIssueShareOnPlan());
                                    uploadObj_1.put("capplyPricePlan",item.getDataList().get(i).getCapplyPricePlan());
                                    uploadObj_1.put("capplySharePlan",item.getDataList().get(i).getCapplySharePlan());
                                    uploadObj_1.put("issuePricePlan",item.getDataList().get(i).getIssuePricePlan());
                                    uploadObj_1.put("keyCode",item.getDataList().get(i).getKeyCode());
                                    uploadObj.put(String.valueOf(i+1),uploadObj_1);
                                }

                            }
                            Log.d("data", String.valueOf(uploadObj));
                            result.complete(uploadObj);
                        } catch (JSONException e) {
                            result.completeExceptionally(e);
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
