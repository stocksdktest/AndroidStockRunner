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

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *財汇沪深盘后接口1
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.F10V2TEST_1)
public class F10V2Test_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.F10V2TEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static final int timeout_ms = 1000000;
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("F10V2Test_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }
  // F10Type
    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
    @Test(timeout = timeout_ms)
    public void requestWork() throws Exception {
        Log.d("F10V2Test_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final String quoteNumbers1 = rule.getParam().optString("src");
        final String quoteNumbers2 = rule.getParam().optString("apiType");
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
            request.send(quoteNumbers,src,quoteNumbers2,new IResponseInfoCallback<F10V2Response>() {
                @Override
                public void callback(F10V2Response f10V2Response) {
                    if (quoteNumbers2.equals("/proindicdata")||quoteNumbers2.equals("/exptperformance")||quoteNumbers2.equals("/newsinteractive")||quoteNumbers2.equals("/shareinfo")
                    ||quoteNumbers2.equals("/companyinfo")||quoteNumbers2.equals("/exptskstatn")||quoteNumbers2.equals("/exptskinvrating")||quoteNumbers2.equals("/importantindex")){
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
                        switch (quoteNumbers2){
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
                                //主营构成
                            case F10Type.D_BUSINESSINFO:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("CLASSNAME",infos.get(i).get("CLASSNAME"));
                                    uploadObj_1.put("TCOREBIZINCOME",infos.get(i).get("TCOREBIZINCOME"));
                                    uploadObj_1.put("TYPESTYLE",infos.get(i).get("TYPESTYLE"));
                                    uploadObj_1.put("PUBLISHDATE",infos.get(i).get("PUBLISHDATE"));
                                    uploadObj.put((String) infos.get(i).get("PUBLISHDATE"),uploadObj_1);
                                }
                                break;
                                //龙虎榜-买入前5营业部
                            case F10Type.D_CHARTS5BUYS:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("BIZSUNITNAME",infos.get(i).get("BIZSUNITNAME"));
                                    uploadObj_1.put("BUYAMT",infos.get(i).get("BUYAMT"));
                                    uploadObj_1.put("SALEAMT",infos.get(i).get("SALEAMT"));
                                    uploadObj_1.put("TRADEDATE",infos.get(i).get("TRADEDATE"));
                                    uploadObj_1.put("CHGDESC",infos.get(i).get("CHGDESC"));
                                    uploadObj.put((String) infos.get(i).get("TRADEDATE"),uploadObj_1);
                                }
                                break;
                                //龙虎榜-卖出前5营业部
                            case F10Type.D_CHARTS5SELLS:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("BIZSUNITNAME",infos.get(i).get("BIZSUNITNAME"));
                                    uploadObj_1.put("BUYAMT",infos.get(i).get("BUYAMT"));
                                    uploadObj_1.put("SALEAMT",infos.get(i).get("SALEAMT"));
                                    uploadObj_1.put("TRADEDATE",infos.get(i).get("TRADEDATE"));
                                    uploadObj_1.put("CHGDESC",infos.get(i).get("CHGDESC"));
                                    uploadObj.put((String) infos.get(i).get("TRADEDATE"),uploadObj_1);
                                }
                                break;
                                //机构观点-机构评级
                            case F10Type.D_EXPTSKINVRATING:
                                uploadObj.put("STDRATING",info.get("STDRATING"));
                                List<HashMap<String,Object>> items= (List<HashMap<String,Object>>) info.get("list");
                                if (items!=null){
                                    for (int i=0;i<items.size();i++){
                                        JSONObject uploadObj_1 = new JSONObject();
                                        uploadObj_1.put("COMPNAME",items.get(i).get("COMPNAME"));
                                        uploadObj_1.put("RATING",items.get(i).get("RATING"));
                                        uploadObj_1.put("RADJUSTDIR",items.get(i).get("RADJUSTDIR"));
                                        uploadObj_1.put("DATE",items.get(i).get("DATE"));
                                        uploadObj_1.put("PRICECAP",items.get(i).get("PRICECAP"));
                                        uploadObj.put((String) items.get(i).get("DATE"),uploadObj_1);
                                    }
                                }
                                break;
                                //机构观点-一致预测
                            case F10Type.D_EXPTSKSTATN:
                                uploadObj.put("TMBIZINCOME",info.get("TMBIZINCOME"));
                                uploadObj.put("NMBIZINCOME",info.get("NMBIZINCOME"));
                                uploadObj.put("YANMBIZINCOME",info.get("YANMBIZINCOME"));
                                uploadObj.put("MAINBIZINCOMEYOY1",info.get("MAINBIZINCOMEYOY1"));
                                uploadObj.put("MAINBIZINCOMEYOY2",info.get("MAINBIZINCOMEYOY2"));
                                uploadObj.put("MAINBIZINCOMEYOY3",info.get("MAINBIZINCOMEYOY3"));
                                uploadObj.put("TNETPROFIT",info.get("TNETPROFIT"));
                                uploadObj.put("NNETPROFIT",info.get("NNETPROFIT"));
                                uploadObj.put("YANNETPROFIT",info.get("YANNETPROFIT"));
                                uploadObj.put("NETPROFITYOY1",info.get("NETPROFITYOY1"));
                                uploadObj.put("NETPROFITYOY2",info.get("NETPROFITYOY2"));
                                uploadObj.put("NETPROFITYOY3",info.get("NETPROFITYOY3"));
                                uploadObj.put("TEPS",info.get("TEPS"));
                                uploadObj.put("NEPS",info.get("NEPS"));
                                uploadObj.put("YANEPS",info.get("YANEPS"));
                                uploadObj.put("TENDDATE",info.get("TENDDATE"));
                                uploadObj.put("NENDDATE",info.get("NENDDATE"));
                                uploadObj.put("YANENDDATE",info.get("YANENDDATE"));
                                break;
                                //公司简介
                            case F10Type.D_COMPANYINFO:
                                uploadObj.put("LISTDATE",info.get("LISTDATE"));
                                uploadObj.put("LISTOPRICE",info.get("LISTOPRICE"));
                                uploadObj.put("FCLEVEL2NAME",info.get("FCLEVEL2NAME"));
                                uploadObj.put("COMPNAME",info.get("COMPNAME"));
                                uploadObj.put("CHAIRMAN",info.get("CHAIRMAN"));
                                uploadObj.put("BSECRETARY",info.get("BSECRETARY"));
                                uploadObj.put("BSECRETARYMAIL",info.get("BSECRETARYMAIL"));
                                uploadObj.put("REGADDR",info.get("REGADDR"));
                                uploadObj.put("ISSPRICE",info.get("ISSPRICE"));
                                uploadObj.put("SWLEVEL2NAME",info.get("SWLEVEL2NAME"));
                                break;
                                //分红扩股
                            case F10Type.D_SHAREBONUS:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("DATETYPENAME",infos.get(i).get("DATETYPENAME"));
                                    uploadObj_1.put("PRETAXCASHMAXDVCNY",infos.get(i).get("PRETAXCASHMAXDVCNY"));
                                    uploadObj_1.put("EQURECORDDATE",infos.get(i).get("EQURECORDDATE"));
                                    uploadObj_1.put("XDRDATE",infos.get(i).get("XDRDATE"));
                                    uploadObj.put((String) infos.get(i).get("DATETYPENAME"),uploadObj_1);
                                }
                                break;
                                //公司高管
                            case F10Type.D_COMPANYMANAGER:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("CNAME",infos.get(i).get("CNAME"));
                                    uploadObj_1.put("DUTY",infos.get(i).get("DUTY"));
                                    uploadObj_1.put("HOLDAFAMT",infos.get(i).get("HOLDAFAMT"));
                                    uploadObj_1.put("REMBEFTAX",infos.get(i).get("REMBEFTAX"));
                                    uploadObj_1.put("BEGINEND",infos.get(i).get("BEGINEND"));
                                    uploadObj_1.put("MEMO",infos.get(i).get("MEMO"));
                                    uploadObj.put((String) infos.get(i).get("CNAME"),uploadObj_1);
                                }
                                break;
                                //十大流通股东
                            case F10Type.D_OTSHOLDER10:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("ENDDATE",infos.get(i).get("ENDDATE"));
                                    uploadObj_1.put("SHHOLDERNAME",infos.get(i).get("SHHOLDERNAME"));
                                    uploadObj_1.put("HOLDERAMT",infos.get(i).get("HOLDERAMT"));
                                    uploadObj_1.put("PCTOFFLOTSHARES",infos.get(i).get("PCTOFFLOTSHARES"));
                                    uploadObj_1.put("HOLDERSUMCHG",infos.get(i).get("HOLDERSUMCHG"));
                                    uploadObj_1.put("SHHOLDERCODE",infos.get(i).get("SHHOLDERCODE"));
                                    uploadObj.put((String) infos.get(i).get("ENDDATE"),uploadObj_1);
                                }
                                break;//十大股东
                            case F10Type.D_SHAREHOLDER10:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("ENDDATE",infos.get(i).get("ENDDATE"));
                                    uploadObj_1.put("SHHOLDERNAME",infos.get(i).get("SHHOLDERNAME"));
                                    uploadObj_1.put("HOLDERAMT",infos.get(i).get("HOLDERAMT"));
                                    uploadObj_1.put("HOLDERRTO",infos.get(i).get("HOLDERRTO"));
                                    uploadObj_1.put("CURCHG",infos.get(i).get("CURCHG"));
                                    uploadObj_1.put("SHHOLDERCODE",infos.get(i).get("SHHOLDERCODE"));
                                    uploadObj.put((String) infos.get(i).get("ENDDATE"),uploadObj_1);
                                }
                                break;
                                //股本信息
                            case F10Type.D_SHAREINFO:
                                uploadObj.put("TOTALSHARE",info.get("TOTALSHARE"));
                                uploadObj.put("CIRCSKAMT",info.get("CIRCSKAMT"));
                                uploadObj.put("RELANAME",info.get("RELANAME"));
                                uploadObj.put("SHHOLDERNAME",info.get("SHHOLDERNAME"));
                                uploadObj.put("HOLDERRTO",info.get("HOLDERRTO"));
                                uploadObj.put("TOTALSHRTO",info.get("TOTALSHRTO"));
                                uploadObj.put("TOTALSHAMT",info.get("TOTALSHAMT"));
                                break;
                                //股东户数
                            case F10Type.D_SHAREHOLDERNUM:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("ENDDATE",infos.get(i).get("ENDDATE"));
                                    uploadObj_1.put("TOTALSHAMT",infos.get(i).get("TOTALSHAMT"));
                                    uploadObj_1.put("KAVGSH",infos.get(i).get("KAVGSH"));
                                    uploadObj_1.put("MOM",infos.get(i).get("MOM"));
                                    uploadObj.put((String) infos.get(i).get("ENDDATE"),uploadObj_1);
                                }
                                break;
                                //主要指标
                            case F10Type.D_PROFINMAININDEX:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("REPORTTITLE",infos.get(i).get("REPORTTITLE"));
                                    uploadObj_1.put("EPSBASIC",infos.get(i).get("EPSBASIC"));
                                    uploadObj_1.put("EPSDILUTED",infos.get(i).get("EPSDILUTED"));
                                    uploadObj_1.put("NAPS",infos.get(i).get("NAPS"));
                                    uploadObj_1.put("UPPS",infos.get(i).get("UPPS"));
                                    uploadObj_1.put("CRPS",infos.get(i).get("CRPS"));
                                    uploadObj_1.put("SGPMARGIN",infos.get(i).get("SGPMARGIN"));
                                    uploadObj_1.put("OPPRORT",infos.get(i).get("OPPRORT"));
                                    uploadObj_1.put("SNPMARGIN",infos.get(i).get("SNPMARGIN"));
                                    uploadObj_1.put("ROEWEIGHTED",infos.get(i).get("ROEWEIGHTED"));
                                    uploadObj_1.put("ROEDILUTED",infos.get(i).get("ROEDILUTED"));
                                    uploadObj_1.put("CURRENTRT",infos.get(i).get("CURRENTRT"));
                                    uploadObj_1.put("QUICKRT",infos.get(i).get("QUICKRT"));
                                    uploadObj_1.put("OPNCFPS",infos.get(i).get("OPNCFPS"));
                                    uploadObj.put((String) infos.get(i).get("REPORTTITLE"),uploadObj_1);
                                }
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
                                //股东深度挖掘数据
                            case F10Type.IINVHOLDCHG:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("REPORTDATE",infos.get(i).get("REPORTDATE"));
                                    uploadObj_1.put("TRADING",infos.get(i).get("TRADING"));
                                    uploadObj_1.put("SESNAME",infos.get(i).get("SESNAME"));
                                    uploadObj_1.put("SETYPENAME",infos.get(i).get("SETYPENAME"));
                                    uploadObj_1.put("HOLDQTY",infos.get(i).get("HOLDQTY"));
                                    uploadObj_1.put("HOLDAMT",infos.get(i).get("HOLDAMT"));
                                    uploadObj_1.put("HOLDQTYSUMCHG",infos.get(i).get("HOLDQTYSUMCHG"));
                                    uploadObj.put((String) infos.get(i).get("REPORTDATE"),uploadObj_1);
                                }
                                break;
                                //沪深股api----大事提醒---按时间
                            case F10Type.IMPORT_NOTICE_DATA:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("REPTITLE",infos.get(i).get("REPTITLE"));
                                    uploadObj_1.put("TRADEDATE",infos.get(i).get("TRADEDATE"));
                                    uploadObj_1.put("TEXT",infos.get(i).get("TEXT"));
//                                    uploadObj_1.put("ID",infos.get(i).get("ID"));
//                                    uploadObj_1.put("ISPDF",infos.get(i).get("ISPDF"));
                                    uploadObj.put((String) infos.get(i).get("TRADEDATE"),uploadObj_1);                                }
                                break;
                                //沪深股api----大事提醒---按标题
                            case F10Type.IMPORT_NOTICE_TITLE:
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_1 = new JSONObject();
                                    uploadObj_1.put("REPTITLE",infos.get(i).get("REPTITLE"));
                                    uploadObj_1.put("TRADEDATE",infos.get(i).get("TRADEDATE"));
                                    uploadObj_1.put("TEXT",infos.get(i).get("TEXT"));
//                                    uploadObj_1.put("ID",infos.get(i).get("ID"));
//                                    uploadObj_1.put("ISPDF",infos.get(i).get("ISPDF"));
                                    uploadObj.put((String) infos.get(i).get("TRADEDATE"),uploadObj_1);
                                }
                                break;
                                //董秘问答
                            case F10Type.NEWS_INTEARACTIVE:
                                List<HashMap<String,Object>> items1= (List<HashMap<String,Object>>) info.get("List");
                                if (items1!=null){
                                    for (int i=0;i<items1.size();i++){
                                        JSONObject uploadObj_1 = new JSONObject();
                                        uploadObj_1.put("TRADING",items1.get(i).get("TRADING"));
                                        uploadObj_1.put("SESNAME",items1.get(i).get("SESNAME"));
                                        uploadObj_1.put("PROBLEM",items1.get(i).get("PROBLEM"));
                                        uploadObj_1.put("QUESTIONTIME",items1.get(i).get("QUESTIONTIME"));
                                        uploadObj_1.put("REPLY",items1.get(i).get("REPLY"));
                                        uploadObj_1.put("ANSWERTIME",items1.get(i).get("ANSWERTIME"));
                                        uploadObj_1.put("NEWSSOURCE",items1.get(i).get("NEWSSOURCE"));
                                        uploadObj_1.put("INTERACTIVEID",items1.get(i).get("INTERACTIVEID"));
                                        uploadObj.put((String) items1.get(i).get("TRADING"),uploadObj_1);
                                    }
                                }
                                break;
                                //大事提醒-业绩预告
                            case F10Type.EXPT_PERFORMANCE:
                                List<HashMap<String,Object>> items2= (List<HashMap<String,Object>>) info.get("List");
                                if (items2!=null){
                                    for (int i=0;i<items2.size();i++){
                                        JSONObject uploadObj_1 = new JSONObject();
                                        uploadObj_1.put("SESNAME",items2.get(i).get("SESNAME"));
                                        uploadObj_1.put("PUBLISHDATE",items2.get(i).get("PUBLISHDATE"));
                                        uploadObj_1.put("RETAMAXPROFITSMK",items2.get(i).get("RETAMAXPROFITSMK"));
                                        uploadObj_1.put("RETAMAXPROFITSINC",items2.get(i).get("RETAMAXPROFITSINC"));
                                        uploadObj.put((String) items2.get(i).get("PUBLISHDATE"),uploadObj_1);
                                    }
                                }
                                break;
                                //沪深股api----大事提醒—业绩公告
                            case F10Type.PROINDIC_DATA:
                                List<HashMap<String,Object>> items3= (List<HashMap<String,Object>>) info.get("List");
                                if (items3!=null){
                                    for (int i=0;i<items3.size();i++){
                                        JSONObject uploadObj_1 = new JSONObject();
                                        uploadObj_1.put("SESNAME",items3.get(i).get("SESNAME"));
                                        uploadObj_1.put("PUBLISHDATE",items3.get(i).get("PUBLISHDATE"));
                                        uploadObj_1.put("EPSBASIC",items3.get(i).get("EPSBASIC"));
                                        uploadObj_1.put("TAGRT",items3.get(i).get("TAGRT"));
                                        uploadObj_1.put("NPGRT",items3.get(i).get("NPGRT"));
                                        uploadObj.put((String) items3.get(i).get("PUBLISHDATE"),uploadObj_1);
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
                RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
            } catch (Exception e) {
                throw new Exception(e);
            }
//        }
    }
}