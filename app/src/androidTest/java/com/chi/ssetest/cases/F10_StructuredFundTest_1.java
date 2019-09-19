package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.R;
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

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("F10_StructuredFundTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("requestType");
        final String quoteNumbers1 = rule.getParam().optString("stockId");
        final String quoteNumbers2 = rule.getParam().optString("src");
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
                            result.completeExceptionally(e);
                        }
                    }else {
                        try {
                            assertNotNull(f10V2Response.info);
                        } catch (AssertionError e) {
                            result.completeExceptionally(e);
                        }
                    }
                    JSONObject uploadObj = new JSONObject();
                    HashMap<String,Object> info = f10V2Response.info;
                    List<HashMap<String,Object>> infos = f10V2Response.infos;
                    try {
                        switch (quoteNumbers){
                            //"/fndclassinfo"
                            case StructuredFundRequest.INFO:
                                List<JSONObject> fndclassinfo=new ArrayList<>();
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("MASTERCODEA",info.get("MASTERCODEA"));
                                uploadObj_1.put("SNAMECOMPA",info.get("SNAMECOMPA"));
                                uploadObj_1.put("ATOTSHARE",info.get("ATOTSHARE"));
                                uploadObj_1.put("MASTERCODE",info.get("MASTERCODE"));
                                uploadObj_1.put("SNAMECOMP",info.get("SNAMECOMP"));
                                uploadObj_1.put("MASTERCODEB",info.get("MASTERCODEB"));
                                uploadObj_1.put("SNAMECOMPB",info.get("SNAMECOMPB"));
                                uploadObj_1.put("BTOTSHARE",info.get("BTOTSHARE"));
                                uploadObj_1.put("MAPCODE",info.get("MAPCODE"));
                                uploadObj_1.put("MAPNAME",info.get("MAPNAME"));
                                uploadObj_1.put("LISTDATE",info.get("LISTDATE"));
                                uploadObj_1.put("ENDDATE",info.get("ENDDATE"));
                                uploadObj_1.put("KEEPERNAME",info.get("KEEPERNAME"));
                                fndclassinfo.add(uploadObj_1);
                                uploadObj.put("fndclassinfo",new JSONArray(fndclassinfo));
                                break;
                            //"/fndclassstockpre"
                            case StructuredFundRequest.STOCKPRE:
                                List<JSONObject> fndclassstockpre=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_2 = new JSONObject();
                                    uploadObj_2.put("SKCODE",infos.get(i).get("SKCODE"));
                                    uploadObj_2.put("SKNAME",infos.get(i).get("SKNAME"));
                                    uploadObj_2.put("NAVRTO",infos.get(i).get("NAVRTO"));
                                    uploadObj_2.put("ACCSTKRTO",infos.get(i).get("ACCSTKRTO"));
                                    uploadObj_2.put("ACCCIRCRTO",infos.get(i).get("ACCCIRCRTO"));
                                    fndclassstockpre.add(uploadObj_2);
                                }
                                uploadObj.put("fndclassstockpre",new JSONArray(fndclassstockpre));
                                break;
                            //"/fndclassforcast"
                            case StructuredFundRequest.FORCAST:
                                List<JSONObject> fndclassforcast=new ArrayList<>();
                                JSONObject uploadObj_3 = new JSONObject();
                                uploadObj_3.put("FSYMBOL",info.get("FSYMBOL"));
                                uploadObj_3.put("PRIXLEVERAGE",info.get("PRIXLEVERAGE"));
                                uploadObj_3.put("NAVLEVERAGE",info.get("NAVLEVERAGE"));
                                uploadObj_3.put("THRESHOLD",info.get("THRESHOLD"));
                                fndclassforcast.add(uploadObj_3);
                                uploadObj.put("fndclassforcast",new JSONArray(fndclassforcast));
                                break;
                            //"/fndclasssubredinfo"
                            case StructuredFundRequest.SUBREDINFO:
                                List<JSONObject> fndclasssubredinfo=new ArrayList<>();
                                JSONObject uploadObj_4 = new JSONObject();
                                uploadObj_4.put("INVESTSTYLE",info.get("INVESTSTYLE"));
                                uploadObj_4.put("SUBREDSTATUS",info.get("SUBREDSTATUS"));
                                uploadObj_4.put("ACCUNITNAV",info.get("ACCUNITNAV"));
                                uploadObj_4.put("RATEMAXCOST",info.get("RATEMAXCOST"));
                                uploadObj_4.put("APPMINAMT",info.get("APPMINAMT"));
                                fndclasssubredinfo.add(uploadObj_4);
                                uploadObj.put("fndclasssubredinfo",new JSONArray(fndclasssubredinfo));
                                break;
                            //"/fndclassmergesplit"
                            case StructuredFundRequest.MERGESPLIT:
                                List<JSONObject> fndclassmergesplit=new ArrayList<>();
                                JSONObject uploadObj_5 = new JSONObject();
                                uploadObj_5.put("MASTERCODEA",info.get("MASTERCODEA"));
                                uploadObj_5.put("SNAMECOMPA",info.get("SNAMECOMPA"));
                                uploadObj_5.put("ATOTSHARE",info.get("ATOTSHARE"));
                                uploadObj_5.put("MASTERCODE",info.get("MASTERCODE"));
                                uploadObj_5.put("SNAMECOMP",info.get("SNAMECOMP"));
                                uploadObj_5.put("MASTERCODEB",info.get("MASTERCODEB"));
                                uploadObj_5.put("SNAMECOMPB",info.get("SNAMECOMPB"));
                                uploadObj_5.put("BTOTSHARE",info.get("BTOTSHARE"));
                                uploadObj_5.put("ACCUNITNAV",info.get("ACCUNITNAV"));
                                fndclassmergesplit.add(uploadObj_5);
                                uploadObj.put("fndclassmergesplit",new JSONArray(fndclassmergesplit));
                                break;
                            //"/fndclassconverted"
                            case StructuredFundRequest.CONVERTED:
                                List<JSONObject> fndclassconverted=new ArrayList<>();
                                JSONObject uploadObj_6 = new JSONObject();
                                uploadObj_6.put("BENCHMARK",info.get("BENCHMARK"));
                                uploadObj_6.put("ELDMEMO",info.get("ELDMEMO"));
                                fndclassconverted.add(uploadObj_6);
                                uploadObj.put("fndclassconverted",new JSONArray(fndclassconverted));
                                break;
                            //"/fndclassmasterrate"
                            case StructuredFundRequest.MASTERRATE:
                                List<JSONObject> fndclassmasterrate=new ArrayList<>();
                                JSONObject uploadObj_7 = new JSONObject();
                                uploadObj_7.put("subamt",info.get("subamt"));
                                List<HashMap<String,Object>> masterRateinfos =( List<HashMap<String,Object>>)info.get("subcost");
                                List<JSONObject> subcost=new ArrayList<>();
                                if (masterRateinfos!=null){
                                    for (int k=0;k<masterRateinfos.size();k++){
                                        JSONObject uploadObj_8 = new JSONObject();
                                        uploadObj_8.put("RATEMAXCOST",masterRateinfos.get(k).get("RATEMAXCOST"));
                                        uploadObj_8.put("APPMINAMT",masterRateinfos.get(k).get("APPMINAMT"));
                                        uploadObj_8.put("APPAMTRESH",masterRateinfos.get(k).get("APPAMTRESH"));
                                        uploadObj_8.put("APPMAXAMT",masterRateinfos.get(k).get("APPMAXAMT"));
                                        uploadObj_8.put("SUBMIXAMT",masterRateinfos.get(k).get("SUBMIXAMT"));
                                        subcost.add(uploadObj_8);
                                    }
                                    uploadObj_7.put("subcost",new JSONArray(subcost));
                                }else {
                                    uploadObj_7.put("subcost",info.get("subcost"));
                                }
                                fndclassmasterrate.add(uploadObj_7);
                                uploadObj.put("fndclassmasterrate",new JSONArray(fndclassmasterrate));
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
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}