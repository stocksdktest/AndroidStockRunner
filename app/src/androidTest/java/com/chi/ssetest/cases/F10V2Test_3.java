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
                                uploadObj.put("REPORTTITLE",info.get("REPORTTITLE"));
                                uploadObj.put("EPSBASIC",info.get("EPSBASIC"));
                                uploadObj.put("NAPS",info.get("NAPS"));
                                uploadObj.put("NPCUT",info.get("NPCUT"));
                                uploadObj.put("TOTALSHARE",info.get("TOTALSHARE"));
                                uploadObj.put("CIRCSKAMT",info.get("CIRCSKAMT"));
                                uploadObj.put("BIZINCO",info.get("BIZINCO"));
                                uploadObj.put("OPERINYOYB",info.get("OPERINYOYB"));
                                uploadObj.put("NETPROFITYOYB",info.get("NETPROFITYOYB"));
                                uploadObj.put("DISTRIBUTION",info.get("DISTRIBUTION"));
                                uploadObj.put("EXRIGHT",info.get("EXRIGHT"));
                                break;
                            //利润表
                            case F10Type.D_PROINCSTATEMENTNEW:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("REPORTTITLE",infos.get(i).get("REPORTTITLE"));
                                    uploadObj_1.put("BIZINCO",infos.get(i).get("BIZINCO"));
                                    uploadObj_1.put("BIZCOST",infos.get(i).get("BIZCOST"));
                                    uploadObj_1.put("MANAEXPE",infos.get(i).get("MANAEXPE"));
                                    uploadObj_1.put("SALESEXPE",infos.get(i).get("SALESEXPE"));
                                    uploadObj_1.put("FINEXPE",infos.get(i).get("FINEXPE"));
                                    uploadObj_1.put("PERPROFIT",infos.get(i).get("PERPROFIT"));
                                    uploadObj_1.put("INVEINCO",infos.get(i).get("INVEINCO"));
                                    uploadObj_1.put("NONOPERINCOMEN",infos.get(i).get("NONOPERINCOMEN"));
                                    uploadObj_1.put("TOTPROFIT",infos.get(i).get("TOTPROFIT"));
                                    uploadObj_1.put("PARENETP",infos.get(i).get("PARENETP"));
                                    uploadObj.put((String) infos.get(i).get("REPORTTITLE"),uploadObj_1);
                                }
                                break;
                            //资产负债表
                            case F10Type.D_PROBALSHEETNEW:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("REPORTTITLE",infos.get(i).get("REPORTTITLE"));
                                    uploadObj_1.put("TOTLIABSHAREQUI",infos.get(i).get("TOTLIABSHAREQUI"));
                                    uploadObj_1.put("TOTCURRASSET",infos.get(i).get("TOTCURRASSET"));
                                    uploadObj_1.put("CURFDS",infos.get(i).get("CURFDS"));
                                    uploadObj_1.put("TRADFINASSET",infos.get(i).get("TRADFINASSET"));
                                    uploadObj_1.put("INVE",infos.get(i).get("INVE"));
                                    uploadObj_1.put("ACCORECE",infos.get(i).get("ACCORECE"));
                                    uploadObj_1.put("OTHERRECE",infos.get(i).get("OTHERRECE"));
                                    uploadObj_1.put("FIXEDASSENET",infos.get(i).get("FIXEDASSENET"));
                                    uploadObj_1.put("AVAISELLASSE",infos.get(i).get("AVAISELLASSE"));
                                    uploadObj_1.put("INTAASSET",infos.get(i).get("INTAASSET"));
                                    uploadObj_1.put("SHORTTERMBORR",infos.get(i).get("SHORTTERMBORR"));
                                    uploadObj_1.put("ADVAPAYM",infos.get(i).get("ADVAPAYM"));
                                    uploadObj_1.put("ACCOPAYA",infos.get(i).get("ACCOPAYA"));
                                    uploadObj_1.put("TOTALCURRLIAB",infos.get(i).get("TOTALCURRLIAB"));
                                    uploadObj_1.put("SUNEVENNONCLIAB",infos.get(i).get("SUNEVENNONCLIAB"));
                                    uploadObj_1.put("TOTLIAB",infos.get(i).get("TOTLIAB"));
                                    uploadObj_1.put("PARESHARRIGH",infos.get(i).get("PARESHARRIGH"));
                                    uploadObj_1.put("CAPISURP",infos.get(i).get("CAPISURP"));
                                    uploadObj_1.put("GOODWILL",infos.get(i).get("GOODWILL"));
                                    uploadObj.put((String) infos.get(i).get("REPORTTITLE"),uploadObj_1);
                                }
                                break;
                            //现金流量表
                            case F10Type.D_PROCFSTATEMENTNEW:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("REPORTTITLE",infos.get(i).get("REPORTTITLE"));
                                    uploadObj_1.put("BIZCASHINFL",infos.get(i).get("BIZCASHINFL"));
                                    uploadObj_1.put("BIZCASHOUTF",infos.get(i).get("BIZCASHOUTF"));
                                    uploadObj_1.put("MANANETR",infos.get(i).get("MANANETR"));
                                    uploadObj_1.put("INVCASHINFL",infos.get(i).get("INVCASHINFL"));
                                    uploadObj_1.put("INVCASHOUTF",infos.get(i).get("INVCASHOUTF"));
                                    uploadObj_1.put("INVNETCASHFLOW",infos.get(i).get("INVNETCASHFLOW"));
                                    uploadObj_1.put("FINCASHINFL",infos.get(i).get("FINCASHINFL"));
                                    uploadObj_1.put("FINCASHOUTF",infos.get(i).get("FINCASHOUTF"));
                                    uploadObj_1.put("FINNETCFLOW",infos.get(i).get("FINNETCFLOW"));
                                    uploadObj_1.put("CASHNETR",infos.get(i).get("CASHNETR"));
                                    uploadObj.put((String) infos.get(i).get("REPORTTITLE"),uploadObj_1);
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
                JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}