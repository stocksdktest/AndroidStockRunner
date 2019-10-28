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
import com.mitake.core.bean.TickItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.F10Type;
import com.mitake.core.request.F10V2Request;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.F10V2Response;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.mitake.core.model.F10KeyToChinese.EPSBASIC;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *財汇沪深盘后接口3
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10V2TEST_3)
public class F10V2Test_3 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10V2TEST_3;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10V2Test_3", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
    // F10Type
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("F10V2Test_2", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final String quoteNumbers1 = rule.getParam().optString("src");
        final String quoteNumbers2 = rule.getParam().optString("quarterType");
        final String quoteNumbers3 = rule.getParam().optString("apiType");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        // F10Type
//        for (int i=0;i<quoteNumbers.length;i++){
        String src;
        if (quoteNumbers1.equals("null")){
            src="g";
        }else {
            src=quoteNumbers1;
        }
            F10V2Request request = new F10V2Request();
            request.sendV2(quoteNumbers,src,quoteNumbers2,quoteNumbers3,new IResponseInfoCallback<F10V2Response>() {
                @Override
                public void callback(F10V2Response f10V2Response) {
                    if (quoteNumbers3.equals("/importantindex")){
                        try {
                            assertNotNull(f10V2Response.info);
                        } catch (AssertionError e) {
                            result.completeExceptionally(e);
                        }
                    }else {
                        try {
                            assertNotNull(f10V2Response.infos);
                        } catch (AssertionError e) {
                            result.completeExceptionally(e);
                        }
                    }
                    JSONObject uploadObj = new JSONObject();
                    HashMap<String,Object> info = f10V2Response.info;
                    List<HashMap<String,Object>> infos=f10V2Response.infos;
                    try {
                        switch (quoteNumbers3){
                            //重要指标
                            case F10Type.D_IMPORTANTINDEX:
                                List<JSONObject> IMPORTANTINDEX=new ArrayList<>();
                                JSONObject uploadObj_1 = new JSONObject();
                                uploadObj_1.put("REPORTTITLE",info.get("REPORTTITLE"));
                                uploadObj_1.put("EPSBASIC",info.get("EPSBASIC"));
                                uploadObj_1.put("NAPS",info.get("NAPS"));
                                uploadObj_1.put("NPCUT",info.get("NPCUT"));
                                uploadObj_1.put("TOTALSHARE",info.get("TOTALSHARE"));
                                uploadObj_1.put("CIRCSKAMT",info.get("CIRCSKAMT"));
                                uploadObj_1.put("BIZINCO",info.get("BIZINCO"));
                                uploadObj_1.put("OPERINYOYB",info.get("OPERINYOYB"));
                                uploadObj_1.put("NETPROFITYOYB",info.get("NETPROFITYOYB"));
                                uploadObj_1.put("DISTRIBUTION",info.get("DISTRIBUTION"));
                                uploadObj_1.put("EXRIGHT",info.get("EXRIGHT"));
                                IMPORTANTINDEX.add(uploadObj_1);
                                uploadObj.put("IMPORTANTINDEX",new JSONArray(IMPORTANTINDEX));
                                break;
                            //利润表
                            case F10Type.D_PROINCSTATEMENTNEW:
                                List<JSONObject> PROINCSTATEMENTNEW=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_6 = new JSONObject();
                                    uploadObj_6.put("REPORTTITLE",infos.get(i).get("REPORTTITLE"));
                                    uploadObj_6.put("BIZINCO",infos.get(i).get("BIZINCO"));
                                    uploadObj_6.put("BIZCOST",infos.get(i).get("BIZCOST"));
                                    uploadObj_6.put("MANAEXPE",infos.get(i).get("MANAEXPE"));
                                    uploadObj_6.put("SALESEXPE",infos.get(i).get("SALESEXPE"));
                                    uploadObj_6.put("FINEXPE",infos.get(i).get("FINEXPE"));
                                    uploadObj_6.put("PERPROFIT",infos.get(i).get("PERPROFIT"));
                                    uploadObj_6.put("INVEINCO",infos.get(i).get("INVEINCO"));
                                    uploadObj_6.put("NONOPERINCOMEN",infos.get(i).get("NONOPERINCOMEN"));
                                    uploadObj_6.put("TOTPROFIT",infos.get(i).get("TOTPROFIT"));
                                    uploadObj_6.put("PARENETP",infos.get(i).get("PARENETP"));
                                    PROINCSTATEMENTNEW.add(uploadObj_6);
                                }
                                uploadObj.put("PROINCSTATEMENTNEW",new JSONArray(PROINCSTATEMENTNEW));
                                break;
                            //资产负债表
                            case F10Type.D_PROBALSHEETNEW:
                                List<JSONObject> PROBALSHEETNEW=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_6 = new JSONObject();
                                    uploadObj_6.put("REPORTTITLE",infos.get(i).get("REPORTTITLE"));
                                    uploadObj_6.put("TOTLIABSHAREQUI",infos.get(i).get("TOTLIABSHAREQUI"));
                                    uploadObj_6.put("TOTCURRASSET",infos.get(i).get("TOTCURRASSET"));
                                    uploadObj_6.put("CURFDS",infos.get(i).get("CURFDS"));
                                    uploadObj_6.put("TRADFINASSET",infos.get(i).get("TRADFINASSET"));
                                    uploadObj_6.put("INVE",infos.get(i).get("INVE"));
                                    uploadObj_6.put("ACCORECE",infos.get(i).get("ACCORECE"));
                                    uploadObj_6.put("OTHERRECE",infos.get(i).get("OTHERRECE"));
                                    uploadObj_6.put("FIXEDASSENET",infos.get(i).get("FIXEDASSENET"));
                                    uploadObj_6.put("AVAISELLASSE",infos.get(i).get("AVAISELLASSE"));
                                    uploadObj_6.put("INTAASSET",infos.get(i).get("INTAASSET"));
                                    uploadObj_6.put("SHORTTERMBORR",infos.get(i).get("SHORTTERMBORR"));
                                    uploadObj_6.put("ADVAPAYM",infos.get(i).get("ADVAPAYM"));
                                    uploadObj_6.put("ACCOPAYA",infos.get(i).get("ACCOPAYA"));
                                    uploadObj_6.put("TOTALCURRLIAB",infos.get(i).get("TOTALCURRLIAB"));
                                    uploadObj_6.put("SUNEVENNONCLIAB",infos.get(i).get("SUNEVENNONCLIAB"));
                                    uploadObj_6.put("TOTLIAB",infos.get(i).get("TOTLIAB"));
                                    uploadObj_6.put("PARESHARRIGH",infos.get(i).get("PARESHARRIGH"));
                                    uploadObj_6.put("CAPISURP",infos.get(i).get("CAPISURP"));
                                    uploadObj_6.put("GOODWILL",infos.get(i).get("GOODWILL"));
                                    PROBALSHEETNEW.add(uploadObj_6);
                                }
                                uploadObj.put("PROBALSHEETNEW",new JSONArray(PROBALSHEETNEW));
                                break;
                            //现金流量表
                            case F10Type.D_PROCFSTATEMENTNEW:
                                List<JSONObject> PROCFSTATEMENTNEW=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_6 = new JSONObject();
                                    uploadObj_6.put("REPORTTITLE",infos.get(i).get("REPORTTITLE"));
                                    uploadObj_6.put("BIZCASHINFL",infos.get(i).get("BIZCASHINFL"));
                                    uploadObj_6.put("BIZCASHOUTF",infos.get(i).get("BIZCASHOUTF"));
                                    uploadObj_6.put("MANANETR",infos.get(i).get("MANANETR"));
                                    uploadObj_6.put("INVCASHINFL",infos.get(i).get("INVCASHINFL"));
                                    uploadObj_6.put("INVCASHOUTF",infos.get(i).get("INVCASHOUTF"));
                                    uploadObj_6.put("INVNETCASHFLOW",infos.get(i).get("INVNETCASHFLOW"));
                                    uploadObj_6.put("FINCASHINFL",infos.get(i).get("FINCASHINFL"));
                                    uploadObj_6.put("FINCASHOUTF",infos.get(i).get("FINCASHOUTF"));
                                    uploadObj_6.put("FINNETCFLOW",infos.get(i).get("FINNETCFLOW"));
                                    uploadObj_6.put("CASHNETR",infos.get(i).get("CASHNETR"));
                                    PROCFSTATEMENTNEW.add(uploadObj_6);
                                }
                                uploadObj.put("PROCFSTATEMENTNEW",new JSONArray(PROCFSTATEMENTNEW));
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}