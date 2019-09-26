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
    @Test(timeout = 5000)
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
                                List<JSONObject> BUSINESSINFO=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_2 = new JSONObject();
                                    uploadObj_2.put("CLASSNAME",infos.get(i).get("CLASSNAME"));
                                    uploadObj_2.put("TCOREBIZINCOME",infos.get(i).get("TCOREBIZINCOME"));
                                    uploadObj_2.put("TYPESTYLE",infos.get(i).get("TYPESTYLE"));
                                    uploadObj_2.put("PUBLISHDATE",infos.get(i).get("PUBLISHDATE"));
                                    BUSINESSINFO.add(uploadObj_2);
                                }
                                uploadObj.put("list",new JSONArray(BUSINESSINFO));
                                break;
                                //龙虎榜-买入前5营业部
                            case F10Type.D_CHARTS5BUYS:
                                List<JSONObject> CHARTS5BUYS=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_2 = new JSONObject();
                                    uploadObj_2.put("BIZSUNITNAME",infos.get(i).get("BIZSUNITNAME"));
                                    uploadObj_2.put("BUYAMT",infos.get(i).get("BUYAMT"));
                                    uploadObj_2.put("SALEAMT",infos.get(i).get("SALEAMT"));
                                    uploadObj_2.put("TRADEDATE",infos.get(i).get("TRADEDATE"));
                                    uploadObj_2.put("CHGDESC",infos.get(i).get("CHGDESC"));
                                    CHARTS5BUYS.add(uploadObj_2);
                                }
                                uploadObj.put("list",new JSONArray(CHARTS5BUYS));
                                break;
                                //龙虎榜-卖出前5营业部
                            case F10Type.D_CHARTS5SELLS:
                                List<JSONObject> CHARTS5SELLS=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_2 = new JSONObject();
                                    uploadObj_2.put("BIZSUNITNAME",infos.get(i).get("BIZSUNITNAME"));
                                    uploadObj_2.put("BUYAMT",infos.get(i).get("BUYAMT"));
                                    uploadObj_2.put("SALEAMT",infos.get(i).get("SALEAMT"));
                                    uploadObj_2.put("TRADEDATE",infos.get(i).get("TRADEDATE"));
                                    uploadObj_2.put("CHGDESC",infos.get(i).get("CHGDESC"));
                                    CHARTS5SELLS.add(uploadObj_2);
                                }
                                uploadObj.put("list",new JSONArray(CHARTS5SELLS));
                                break;
                                //机构观点-机构评级
                            case F10Type.D_EXPTSKINVRATING:
                                uploadObj.put("STDRATING",info.get("STDRATING"));
                                List<HashMap<String,Object>> items= (List<HashMap<String,Object>>) info.get("list");
                                if (items!=null){
                                    List<JSONObject> list=new ArrayList<>();
                                    for (int i=0;i<items.size();i++){
                                        JSONObject uploadObj_3 = new JSONObject();
                                        uploadObj_3.put("COMPNAME",items.get(i).get("COMPNAME"));
                                        uploadObj_3.put("RATING",items.get(i).get("RATING"));
                                        uploadObj_3.put("RADJUSTDIR",items.get(i).get("RADJUSTDIR"));
                                        uploadObj_3.put("DATE",items.get(i).get("DATE"));
                                        uploadObj_3.put("PRICECAP",items.get(i).get("PRICECAP"));
                                        list.add(uploadObj_3);
                                    }
                                    uploadObj.put("list",new JSONArray(list));
                                }else {
                                    uploadObj.put("list",items);
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
                                List<JSONObject> SHAREBONUS=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_5 = new JSONObject();
                                    uploadObj_5.put("DATETYPENAME",infos.get(i).get("DATETYPENAME"));
                                    uploadObj_5.put("PRETAXCASHMAXDVCNY",infos.get(i).get("PRETAXCASHMAXDVCNY"));
                                    uploadObj_5.put("EQURECORDDATE",infos.get(i).get("EQURECORDDATE"));
                                    uploadObj_5.put("XDRDATE",infos.get(i).get("XDRDATE"));
                                    SHAREBONUS.add(uploadObj_5);
                                }
                                uploadObj.put("list",new JSONArray(SHAREBONUS));
                                break;
                                //公司高管
                            case F10Type.D_COMPANYMANAGER:
                                List<JSONObject> COMPANYMANAGER=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_5 = new JSONObject();
                                    uploadObj_5.put("CNAME",infos.get(i).get("CNAME"));
                                    uploadObj_5.put("DUTY",infos.get(i).get("DUTY"));
                                    uploadObj_5.put("HOLDAFAMT",infos.get(i).get("HOLDAFAMT"));
                                    uploadObj_5.put("REMBEFTAX",infos.get(i).get("REMBEFTAX"));
                                    uploadObj_5.put("BEGINEND",infos.get(i).get("BEGINEND"));
                                    uploadObj_5.put("MEMO",infos.get(i).get("MEMO"));
                                    COMPANYMANAGER.add(uploadObj_5);
                                }
                                uploadObj.put("list",new JSONArray(COMPANYMANAGER));
                                break;
                                //十大流通股东
                            case F10Type.D_OTSHOLDER10:
                                List<JSONObject> OTSHOLDER10=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_5 = new JSONObject();
                                    uploadObj_5.put("ENDDATE",infos.get(i).get("ENDDATE"));
                                    uploadObj_5.put("SHHOLDERNAME",infos.get(i).get("SHHOLDERNAME"));
                                    uploadObj_5.put("HOLDERAMT",infos.get(i).get("HOLDERAMT"));
                                    uploadObj_5.put("PCTOFFLOTSHARES",infos.get(i).get("PCTOFFLOTSHARES"));
                                    uploadObj_5.put("HOLDERSUMCHG",infos.get(i).get("HOLDERSUMCHG"));
                                    uploadObj_5.put("SHHOLDERCODE",infos.get(i).get("SHHOLDERCODE"));
                                    OTSHOLDER10.add(uploadObj_5);
                                }
                                uploadObj.put("list",new JSONArray(OTSHOLDER10));
                                break;//十大股东
                            case F10Type.D_SHAREHOLDER10:
                                List<JSONObject> SHAREHOLDER10=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_5 = new JSONObject();
                                    uploadObj_5.put("ENDDATE",infos.get(i).get("ENDDATE"));
                                    uploadObj_5.put("SHHOLDERNAME",infos.get(i).get("SHHOLDERNAME"));
                                    uploadObj_5.put("HOLDERAMT",infos.get(i).get("HOLDERAMT"));
                                    uploadObj_5.put("HOLDERRTO",infos.get(i).get("HOLDERRTO"));
                                    uploadObj_5.put("CURCHG",infos.get(i).get("CURCHG"));
                                    uploadObj_5.put("SHHOLDERCODE",infos.get(i).get("SHHOLDERCODE"));
                                    SHAREHOLDER10.add(uploadObj_5);
                                }
                                uploadObj.put("list",new JSONArray(SHAREHOLDER10));
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
                                List<JSONObject> SHAREHOLDERNUM=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_6 = new JSONObject();
                                    uploadObj_6.put("ENDDATE",infos.get(i).get("ENDDATE"));
                                    uploadObj_6.put("TOTALSHAMT",infos.get(i).get("TOTALSHAMT"));
                                    uploadObj_6.put("KAVGSH",infos.get(i).get("KAVGSH"));
                                    uploadObj_6.put("MOM",infos.get(i).get("MOM"));
                                    SHAREHOLDERNUM.add(uploadObj_6);
                                }
                                uploadObj.put("list",new JSONArray(SHAREHOLDERNUM));
                                break;
                                //主要指标
                            case F10Type.D_PROFINMAININDEX:
                                List<JSONObject> PROFINMAININDEX=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_6 = new JSONObject();
                                    uploadObj_6.put("REPORTTITLE",infos.get(i).get("REPORTTITLE"));
                                    uploadObj_6.put("EPSBASIC",infos.get(i).get("EPSBASIC"));
                                    uploadObj_6.put("EPSDILUTED",infos.get(i).get("EPSDILUTED"));
                                    uploadObj_6.put("NAPS",infos.get(i).get("NAPS"));
                                    uploadObj_6.put("UPPS",infos.get(i).get("UPPS"));
                                    uploadObj_6.put("CRPS",infos.get(i).get("CRPS"));
                                    uploadObj_6.put("SGPMARGIN",infos.get(i).get("SGPMARGIN"));
                                    uploadObj_6.put("OPPRORT",infos.get(i).get("OPPRORT"));
                                    uploadObj_6.put("SNPMARGIN",infos.get(i).get("SNPMARGIN"));
                                    uploadObj_6.put("ROEWEIGHTED",infos.get(i).get("ROEWEIGHTED"));
                                    uploadObj_6.put("ROEDILUTED",infos.get(i).get("ROEDILUTED"));
                                    uploadObj_6.put("CURRENTRT",infos.get(i).get("CURRENTRT"));
                                    uploadObj_6.put("QUICKRT",infos.get(i).get("QUICKRT"));
                                    uploadObj_6.put("OPNCFPS",infos.get(i).get("OPNCFPS"));
                                    PROFINMAININDEX.add(uploadObj_6);
                                }
                                uploadObj.put("list",new JSONArray(PROFINMAININDEX));
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
                                uploadObj.put("list",new JSONArray(PROINCSTATEMENTNEW));
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
                                uploadObj.put("list",new JSONArray(PROBALSHEETNEW));
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
                                uploadObj.put("list",new JSONArray(PROCFSTATEMENTNEW));
                                break;
                                //股东深度挖掘数据
                            case F10Type.IINVHOLDCHG:
                                List<JSONObject> IINVHOLDCHG=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_6 = new JSONObject();
                                    uploadObj_6.put("REPORTDATE",infos.get(i).get("REPORTDATE"));
                                    uploadObj_6.put("TRADING",infos.get(i).get("TRADING"));
                                    uploadObj_6.put("SESNAME",infos.get(i).get("SESNAME"));
                                    uploadObj_6.put("SETYPENAME",infos.get(i).get("SETYPENAME"));
                                    uploadObj_6.put("HOLDQTY",infos.get(i).get("HOLDQTY"));
                                    uploadObj_6.put("HOLDAMT",infos.get(i).get("HOLDAMT"));
                                    uploadObj_6.put("HOLDQTYSUMCHG",infos.get(i).get("HOLDQTYSUMCHG"));
                                    IINVHOLDCHG.add(uploadObj_6);
                                }
                                uploadObj.put("list",new JSONArray(IINVHOLDCHG));
                                break;
                                //沪深股api----大事提醒---按时间
                            case F10Type.IMPORT_NOTICE_DATA:
                                List<JSONObject> IMPORT_NOTICE_DATA=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_6 = new JSONObject();
                                    uploadObj_6.put("REPTITLE",infos.get(i).get("REPTITLE"));
                                    uploadObj_6.put("TRADEDATE",infos.get(i).get("TRADEDATE"));
                                    uploadObj_6.put("TEXT",infos.get(i).get("TEXT"));
//                                    uploadObj_6.put("ID",infos.get(i).get("ID"));
//                                    uploadObj_6.put("ISPDF",infos.get(i).get("ISPDF"));
                                    IMPORT_NOTICE_DATA.add(uploadObj_6);
                                }
                                uploadObj.put("list",new JSONArray(IMPORT_NOTICE_DATA));
                                break;
                                //沪深股api----大事提醒---按标题
                            case F10Type.IMPORT_NOTICE_TITLE:
                                List<JSONObject> IMPORT_NOTICE_TITLE=new ArrayList<>();
                                for (int i=0;i<infos.size();i++){
                                    JSONObject uploadObj_6 = new JSONObject();
                                    uploadObj_6.put("REPTITLE",infos.get(i).get("REPTITLE"));
                                    uploadObj_6.put("TRADEDATE",infos.get(i).get("TRADEDATE"));
                                    uploadObj_6.put("TEXT",infos.get(i).get("TEXT"));
//                                    uploadObj_6.put("ID",infos.get(i).get("ID"));
//                                    uploadObj_6.put("ISPDF",infos.get(i).get("ISPDF"));
                                    IMPORT_NOTICE_TITLE.add(uploadObj_6);
                                }
                                uploadObj.put("list",new JSONArray(IMPORT_NOTICE_TITLE));
                                break;
                                //董秘问答
                            case F10Type.NEWS_INTEARACTIVE:
                                List<HashMap<String,Object>> items1= (List<HashMap<String,Object>>) info.get("List");
                                if (items1!=null){
                                    List<JSONObject> list=new ArrayList<>();
                                    for (int i=0;i<items1.size();i++){
                                        JSONObject uploadObj_7 = new JSONObject();
                                        uploadObj_7.put("TRADING",items1.get(i).get("TRADING"));
                                        uploadObj_7.put("SESNAME",items1.get(i).get("SESNAME"));
                                        uploadObj_7.put("PROBLEM",items1.get(i).get("PROBLEM"));
                                        uploadObj_7.put("QUESTIONTIME",items1.get(i).get("QUESTIONTIME"));
                                        uploadObj_7.put("REPLY",items1.get(i).get("REPLY"));
                                        uploadObj_7.put("ANSWERTIME",items1.get(i).get("ANSWERTIME"));
                                        uploadObj_7.put("NEWSSOURCE",items1.get(i).get("NEWSSOURCE"));
                                        uploadObj_7.put("INTERACTIVEID",items1.get(i).get("INTERACTIVEID"));
                                        list.add(uploadObj_7);
                                    }
                                    uploadObj.put("list",new JSONArray(list));
                                }else {
                                    uploadObj.put("list",items1);
                                }
                                break;
                                //大事提醒-业绩预告
                            case F10Type.EXPT_PERFORMANCE:
                                List<HashMap<String,Object>> items2= (List<HashMap<String,Object>>) info.get("List");
                                if (items2!=null){
                                    List<JSONObject> list=new ArrayList<>();
                                    for (int i=0;i<items2.size();i++){
                                        JSONObject uploadObj_7 = new JSONObject();
                                        uploadObj_7.put("SESNAME",items2.get(i).get("SESNAME"));
                                        uploadObj_7.put("PUBLISHDATE",items2.get(i).get("PUBLISHDATE"));
                                        uploadObj_7.put("RETAMAXPROFITSMK",items2.get(i).get("RETAMAXPROFITSMK"));
                                        uploadObj_7.put("RETAMAXPROFITSINC",items2.get(i).get("RETAMAXPROFITSINC"));
                                        list.add(uploadObj_7);
                                    }
                                    uploadObj.put("list",new JSONArray(list));
                                }else {
                                    uploadObj.put("list",items2);
                                }
                                break;
                                //沪深股api----大事提醒—业绩公告
                            case F10Type.PROINDIC_DATA:
                                List<HashMap<String,Object>> items3= (List<HashMap<String,Object>>) info.get("List");
                                if (items3!=null){
                                    List<JSONObject> list=new ArrayList<>();
                                    for (int i=0;i<items3.size();i++){
                                        JSONObject uploadObj_7 = new JSONObject();
                                        uploadObj_7.put("SESNAME",items3.get(i).get("SESNAME"));
                                        uploadObj_7.put("PUBLISHDATE",items3.get(i).get("PUBLISHDATE"));
                                        uploadObj_7.put("EPSBASIC",items3.get(i).get("EPSBASIC"));
                                        uploadObj_7.put("TAGRT",items3.get(i).get("TAGRT"));
                                        uploadObj_7.put("NPGRT",items3.get(i).get("NPGRT"));
                                        list.add(uploadObj_7);
                                    }
                                    uploadObj.put("list",new JSONArray(list));
                                }else {
                                    uploadObj.put("list",items3);
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