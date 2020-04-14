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
    private static final int timeout_ms = 1000000;
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
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("F10_NewShareListTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("DATE");
        final String quoteNumbers1 = rule.getParam().optString("SRC");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        for (int i=0;i<quoteNumbers.length;i++){
            NewShareListRequest request = new NewShareListRequest();
            request.sendV2(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<NewShareListResponse>() {
                @Override
                public void callback(NewShareListResponse newShareListResponse) {
                    try {
                        assertNotNull(newShareListResponse.infos);
                    } catch (AssertionError e) {
                        //                        result.completeExceptionally(e);
                        result.complete(new JSONObject());
                    }
                    JSONObject uploadObj = new JSONObject();
                    try {
                        if (newShareListResponse.infos!=null){

                            for (int k=0;k<newShareListResponse.infos.size();k++) {
                                JSONObject uploadObj_2 = new JSONObject();
                                uploadObj_2.put("title",newShareListResponse.infos.get(k).getTitle());
                                JSONObject uploadObj_3 = new JSONObject();
                                if(newShareListResponse.infos.get(k).getDataList()!=null){
                                    for (int i=0;i<newShareListResponse.infos.get(k).getDataList().size();i++){
                                        JSONObject uploadObj_1 = new JSONObject();
                                        uploadObj_1.put("applyCode",newShareListResponse.infos.get(k).getDataList().get(i).getApplyCode());
                                        uploadObj_1.put("capplyShare",newShareListResponse.infos.get(k).getDataList().get(i).getCapplyShare());
                                        uploadObj_1.put("secuabbr",newShareListResponse.infos.get(k).getDataList().get(i).getSecuabbr());
                                        uploadObj_1.put("tradingCode",newShareListResponse.infos.get(k).getDataList().get(i).getTradingCode());
                                        uploadObj_1.put("peaIssue",newShareListResponse.infos.get(k).getDataList().get(i).getPeaIssue());
                                        uploadObj_1.put("succResultNoticeDate",newShareListResponse.infos.get(k).getDataList().get(i).getSuccResultNoticeDate());
                                        uploadObj_1.put("issuePrice",newShareListResponse.infos.get(k).getDataList().get(i).getIssuePrice());
                                        uploadObj_1.put("allotrateon",newShareListResponse.infos.get(k).getDataList().get(i).getAllotrateon());
                                        uploadObj_1.put("listingDate",newShareListResponse.infos.get(k).getDataList().get(i).getListingDate());
                                        uploadObj_1.put("bookStartDateOn",newShareListResponse.infos.get(k).getDataList().get(i).getBookStartDateOn());
                                        uploadObj_1.put("issueShare",newShareListResponse.infos.get(k).getDataList().get(i).getIssueShare());
                                        uploadObj_1.put("issueShareOn",newShareListResponse.infos.get(k).getDataList().get(i).getIssueShareOn());
                                        uploadObj_1.put("capplyPrice",newShareListResponse.infos.get(k).getDataList().get(i).getCapplyPrice());
                                        uploadObj_1.put("cissueSharePlan",newShareListResponse.infos.get(k).getDataList().get(i).getCissueSharePlan());
                                        uploadObj_1.put("issueShareOnPlan",newShareListResponse.infos.get(k).getDataList().get(i).getIssueShareOnPlan());
                                        uploadObj_1.put("capplyPricePlan",newShareListResponse.infos.get(k).getDataList().get(i).getCapplyPricePlan());
                                        uploadObj_1.put("capplySharePlan",newShareListResponse.infos.get(k).getDataList().get(i).getCapplySharePlan());
                                        uploadObj_1.put("issuePricePlan",newShareListResponse.infos.get(k).getDataList().get(i).getIssuePricePlan());
                                        uploadObj_1.put("keyCode",newShareListResponse.infos.get(k).getDataList().get(i).getKeyCode());
                                        uploadObj_3.put(newShareListResponse.infos.get(k).getDataList().get(i).getApplyCode(),uploadObj_1);
                                    }
                                    uploadObj_2.put("dataList",uploadObj_3);
                                }
                                if (newShareListResponse.infos.get(k).getTitle().equals("即将发行")){
                                    uploadObj.put("jjfxlist",uploadObj_2);
                                }else if (newShareListResponse.infos.get(k).getTitle().equals("申购")){
                                    uploadObj.put("sglist",uploadObj_2);
                                }else if (newShareListResponse.infos.get(k).getTitle().equals("中签")){
                                    uploadObj.put("zqlist",uploadObj_2);
                                }else if (newShareListResponse.infos.get(k).getTitle().equals("上市")){
                                    uploadObj.put("sslist",uploadObj_2);
                                }else if (newShareListResponse.infos.get(k).getTitle().equals("未上市")){
                                    uploadObj.put("wsslist",uploadObj_2);
                                }
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
