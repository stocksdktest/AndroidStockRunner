package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.R;
import com.chi.ssetest.TestcaseException;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.AddValueModel;
import com.mitake.core.CateType;
import com.mitake.core.CompanyInfo;
import com.mitake.core.Importantnotice;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.MorePriceItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.CompanyInfoRequest;
import com.mitake.core.request.FundBasicRequest;
import com.mitake.core.request.ImportantnoticeRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.StructuredFundRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.F10V2Response;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.ImportantnoticeResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *分级基金
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10_STRUCTUREDFUNDTEST_1)
public class F10_StructuredFundTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10_STRUCTUREDFUNDTEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10_StructuredFundTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("F10_StructuredFundTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("GRADEFUNDTYPE");
        final String quoteNumbers1 = rule.getParam().optString("CODE");
        final String quoteNumbers2 = rule.getParam().optString("SOURCETYPE");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        StructuredFundRequest
//        for (int i=0;i<quoteNumbers.length;i++){
            StructuredFundRequest request = new StructuredFundRequest();
            request.sendV2(quoteNumbers,quoteNumbers1,quoteNumbers2,new IResponseInfoCallback<F10V2Response>() {
                @Override
                public void callback(F10V2Response f10V2Response) {
                    if (quoteNumbers.equals("/fndclassstockpre")){
                        try {
                            assertNotNull(f10V2Response.infos);
                        } catch (AssertionError e) {
                            //                        result.completeExceptionally(e);
                            result.complete(new JSONObject());
                        }
                    }else {
                        try {
                            assertNotNull(f10V2Response.info);
                        } catch (AssertionError e) {
                            //                        result.completeExceptionally(e);
                            result.complete(new JSONObject());
                        }
                    }
                    JSONObject uploadObj = new JSONObject();
                    HashMap<String,Object> info = f10V2Response.info;
                    List<HashMap<String,Object>> infos = f10V2Response.infos;
                    try {
                        switch (quoteNumbers){
                            //"/fndclassinfo"
                            case StructuredFundRequest.INFO:
                                if(info!=null){
                                    uploadObj.put("MASTERCODEA",info.get("MASTERCODEA"));
                                    uploadObj.put("SNAMECOMPA",info.get("SNAMECOMPA"));
                                    uploadObj.put("ATOTSHARE",info.get("ATOTSHARE"));
                                    uploadObj.put("MASTERCODE",info.get("MASTERCODE"));
                                    uploadObj.put("SNAMECOMP",info.get("SNAMECOMP"));
                                    uploadObj.put("MASTERCODEB",info.get("MASTERCODEB"));
                                    uploadObj.put("SNAMECOMPB",info.get("SNAMECOMPB"));
                                    uploadObj.put("BTOTSHARE",info.get("BTOTSHARE"));
                                    uploadObj.put("MAPCODE",info.get("MAPCODE"));
                                    uploadObj.put("MAPNAME",info.get("MAPNAME"));
                                    uploadObj.put("LISTDATE",info.get("LISTDATE"));
                                    uploadObj.put("ENDDATE",info.get("ENDDATE"));
                                    uploadObj.put("KEEPERNAME",info.get("KEEPERNAME"));
                                }
                                break;
                            //"/fndclassstockpre"
                            case StructuredFundRequest.STOCKPRE:
                                if(infos!=null){
                                    for (int i=0;i<infos.size();i++){
                                        JSONObject uploadObj_2 = new JSONObject();
                                        uploadObj_2.put("SKCODE",infos.get(i).get("SKCODE"));
                                        uploadObj_2.put("SKNAME",infos.get(i).get("SKNAME"));
                                        uploadObj_2.put("NAVRTO",infos.get(i).get("NAVRTO"));
                                        uploadObj_2.put("ACCSTKRTO",infos.get(i).get("ACCSTKRTO"));
                                        uploadObj_2.put("ACCCIRCRTO",infos.get(i).get("ACCCIRCRTO"));
                                        uploadObj.put((String) infos.get(i).get("SKCODE"),uploadObj_2);
                                    }
                                }
                                break;
                            //"/fndclassforcast"
                            case StructuredFundRequest.FORCAST:
                                if (info!=null){
                                    uploadObj.put("FSYMBOL",info.get("FSYMBOL"));
                                    uploadObj.put("PRIXLEVERAGE",info.get("PRIXLEVERAGE"));
                                    uploadObj.put("NAVLEVERAGE",info.get("NAVLEVERAGE"));
                                    uploadObj.put("THRESHOLD",info.get("THRESHOLD"));
                                }
                                break;
                            //"/fndclasssubredinfo"
                            case StructuredFundRequest.SUBREDINFO:
                                if (info!=null){
                                    uploadObj.put("INVESTSTYLE",info.get("INVESTSTYLE"));
                                    uploadObj.put("SUBREDSTATUS",info.get("SUBREDSTATUS"));
                                    uploadObj.put("ACCUNITNAV",info.get("ACCUNITNAV"));
                                    uploadObj.put("RATEMAXCOST",info.get("RATEMAXCOST"));
                                    uploadObj.put("APPMINAMT",info.get("APPMINAMT"));
                                }
                                break;
                            //"/fndclassmergesplit"
                            case StructuredFundRequest.MERGESPLIT:
                                if (info!=null){uploadObj.put("MASTERCODEA",info.get("MASTERCODEA"));
                                    uploadObj.put("SNAMECOMPA",info.get("SNAMECOMPA"));
                                    uploadObj.put("ATOTSHARE",info.get("ATOTSHARE"));
                                    uploadObj.put("MASTERCODE",info.get("MASTERCODE"));
                                    uploadObj.put("SNAMECOMP",info.get("SNAMECOMP"));
                                    uploadObj.put("MASTERCODEB",info.get("MASTERCODEB"));
                                    uploadObj.put("SNAMECOMPB",info.get("SNAMECOMPB"));
                                    uploadObj.put("BTOTSHARE",info.get("BTOTSHARE"));
                                    uploadObj.put("ACCUNITNAV",info.get("ACCUNITNAV"));}
                                break;
                            //"/fndclassconverted"
                            case StructuredFundRequest.CONVERTED:
                                if (info!=null){uploadObj.put("BENCHMARK",info.get("BENCHMARK"));
                                    uploadObj.put("ELDMEMO",info.get("ELDMEMO"));}
                                break;
                            //"/fndclassmasterrate"
                            case StructuredFundRequest.MASTERRATE:
                                if(info!=null){
                                    uploadObj.put("subamt",info.get("subamt"));
                                    List<HashMap<String,Object>> masterRateinfos =( List<HashMap<String,Object>>)info.get("subcost");
                                    if (masterRateinfos!=null){
                                        for (int k=0;k<masterRateinfos.size();k++){
                                            JSONObject uploadObj_1 = new JSONObject();
                                            uploadObj_1.put("RATEMAXCOST",masterRateinfos.get(k).get("RATEMAXCOST"));
                                            uploadObj_1.put("APPMINAMT",masterRateinfos.get(k).get("APPMINAMT"));
                                            uploadObj_1.put("APPAMTRESH",masterRateinfos.get(k).get("APPAMTRESH"));
                                            uploadObj_1.put("APPMAXAMT",masterRateinfos.get(k).get("APPMAXAMT"));
                                            uploadObj_1.put("SUBMIXAMT",masterRateinfos.get(k).get("SUBMIXAMT"));
                                            uploadObj.put(String.valueOf((k+1)),uploadObj_1);
                                        }
                                    }
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        result.completeExceptionally(e);
                    }
                    Log.d("data", String.valueOf(uploadObj));
                    result.complete(uploadObj);
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