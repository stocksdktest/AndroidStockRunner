package com.chi.ssetest.cases;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.protos.SetupConfig;
import com.chi.ssetest.setup.RunnerSetup;
import com.chi.ssetest.setup.TestcaseConfigRule;
import com.mitake.core.QuoteItem;
import com.mitake.core.bean.PlateIndexItem;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.QuoteDetailRequest;
import com.mitake.core.request.plate.PlateIndexQuoteRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.PlateIndexResponse;
import com.mitake.core.response.QuoteResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//行情快照 方法一
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName.PLATEINDEXQUOTETEST_1)
public class PlateIndexQuoteTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName.PLATEINDEXQUOTETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;
    private static JSONObject uploadObj = new JSONObject();
    private static List<JSONObject> items=new ArrayList<>();
    @BeforeClass
    public static void setup() throws Exception {
        Log.d("PlateIndexQuoteTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d("PlateIndexQuoteTest_1", "requestWork");
        // TODO get custom args from param

        final String quoteNumbers = rule.getParam().optString("CODES", "");
        final CompletableFuture result = new CompletableFuture<JSONObject>();

//        for (int i=0;i<quoteNumbers.length;i++){
            PlateIndexQuoteRequest request = new PlateIndexQuoteRequest();
            request.send(quoteNumbers, new IResponseInfoCallback<PlateIndexResponse>() {
                @Override
                public void callback(PlateIndexResponse plateIndexResponse) {
                    List<PlateIndexItem> list=plateIndexResponse.indexItems;
                    assertNotNull(plateIndexResponse.indexItems);
                    if (list!=null){
                        for (int k=0;k<list.size();k++){
                            JSONObject uploadObj_1 = new JSONObject();
                            try {
                                uploadObj_1.put("blockID", list.get(k).blockID);
                                uploadObj_1.put("dateTime", list.get(k).dateTime);
                                uploadObj_1.put("blockIndex", list.get(k).blockIndex);
                                uploadObj_1.put("blockChg", list.get(k).blockChg);
                                uploadObj_1.put("averageChg", list.get(k).averageChg);
                                uploadObj_1.put("turnoverRate", list.get(k).turnoverRate);
                                List<JSONObject> ratioUpDown=new ArrayList<>();
                                if (ratioUpDown!=null&&ratioUpDown.size()>0){
                                    JSONObject uploadObj_2 = new JSONObject();
                                    uploadObj_2.put("up",list.get(k).ratioUpDown[0]);
                                    uploadObj_2.put("down",list.get(k).ratioUpDown[1]);
                                    uploadObj_2.put("same",list.get(k).ratioUpDown[2]);
                                    ratioUpDown.add(uploadObj_2);
                                    uploadObj_1.put("ratioUpDown",new JSONArray(ratioUpDown));
                                }else {
                                    uploadObj_1.put("ratioUpDown",list.get(k).ratioUpDown);
                                }
                                //涨跌标识 + 涨跌幅
                                uploadObj_1.put("indexChg", list.get(k).upDownFlag+list.get(k).indexChg);
                                uploadObj_1.put("indexChg5", list.get(k).indexChg5);
                                uploadObj_1.put("indexChg10", list.get(k).indexChg10);
                                uploadObj_1.put("largeMoneyNetInflow", list.get(k).largeMoneyNetInflow);
                                uploadObj_1.put("bigMoneyNetInflow", list.get(k).bigMoneyNetInflow);
                                uploadObj_1.put("midMoneyNetInflow", list.get(k).midMoneyNetInflow);
                                uploadObj_1.put("smallMoneyNetInflow", list.get(k).smallMoneyNetInflow);
                                uploadObj_1.put("mainforceMoneyInflow", list.get(k).mainforceMoneyInflow);
                                uploadObj_1.put("mainforceMoneyOutflow", list.get(k).mainforceMoneyOutflow);
                                uploadObj_1.put("mainforceMoneyNetInflow5", list.get(k).mainforceMoneyNetInflow5);
                                uploadObj_1.put("mainforceMoneyNetInflow10", list.get(k).mainforceMoneyNetInflow10);
                                uploadObj_1.put("mainforceMoneyNetInflow", list.get(k).mainforceMoneyNetInflow);
                                uploadObj_1.put("largeVolumeB", list.get(k).largeVolumeB);
                                uploadObj_1.put("largeVolumeS", list.get(k).largeVolumeS);
                                uploadObj_1.put("largeMoneyB", list.get(k).largeMoneyB);
                                uploadObj_1.put("largeMoneyS", list.get(k).largeMoneyS);
                                uploadObj_1.put("bigVolumeB", list.get(k).bigVolumeB);
                                uploadObj_1.put("bigVolumeS", list.get(k).bigVolumeS);
                                uploadObj_1.put("bigMoneyB", list.get(k).bigMoneyB);
                                uploadObj_1.put("bigMoneyS", list.get(k).bigMoneyS);
                                uploadObj_1.put("midVolumeB", list.get(k).midVolumeB);
                                uploadObj_1.put("midVolumeS", list.get(k).midVolumeS);
                                uploadObj_1.put("midMoneyB", list.get(k).midMoneyB);
                                uploadObj_1.put("midMoneyS", list.get(k).midMoneyS);
                                uploadObj_1.put("smallVolumeB", list.get(k).smallVolumeB);
                                uploadObj_1.put("smallVolumeS", list.get(k).smallVolumeS);
                                uploadObj_1.put("smallMoneyB", list.get(k).smallMoneyB);
                                uploadObj_1.put("smallMoneyS", list.get(k).smallMoneyS);
                                uploadObj_1.put("totalTrdMoney", list.get(k).totalTrdMoney);
                                uploadObj_1.put("blockFAMC", list.get(k).blockFAMC);
                                uploadObj_1.put("totalMarketValue", list.get(k).totalMarketValue);
                                uploadObj_1.put("mainforceMoneyNetInflow20", list.get(k).mainforceMoneyNetInflow20);
                                uploadObj_1.put("ratioMainforceMoneyNetInflow5", list.get(k).ratioMainforceMoneyNetInflow5);
                                uploadObj_1.put("ratioMainforceMoneyNetInflow10", list.get(k).ratioMainforceMoneyNetInflow10);
                                uploadObj_1.put("ratioMainforceMoneyNetInflow20", list.get(k).ratioMainforceMoneyNetInflow20);
                                uploadObj_1.put("totalTrdVolume", list.get(k).totalTrdVolume);
                                uploadObj_1.put("openBlockIndex", list.get(k).openBlockIndex);
                                uploadObj_1.put("highBlockIndex", list.get(k).highBlockIndex);
                                uploadObj_1.put("lowBlockIndex", list.get(k).lowBlockIndex);
//                                uploadObj_1.put("closeBlockIndex", list.get(k).closeBlockIndex);//ios可能没有需要ios验证
                                uploadObj_1.put("committee", list.get(k).committee);
                                uploadObj_1.put("deviation", list.get(k).deviation);
                                uploadObj_1.put("buyNum", list.get(k).buyNum);
                                uploadObj_1.put("sellNum", list.get(k).sellNum);
                                uploadObj_1.put("ttm", list.get(k).ttm);
                                uploadObj_1.put("lyr", list.get(k).lyr);
                                uploadObj_1.put("marketRate", list.get(k).marketRate);
                                uploadObj_1.put("blockName", list.get(k).blockName);
                                uploadObj_1.put("preCloseBlockIndex", list.get(k).preCloseBlockIndex);
                                uploadObj_1.put("upsDowns", list.get(k).upsDowns);
                                uploadObj_1.put("amplitude", list.get(k).amplitude);
                                items.add(uploadObj_1);
                            } catch (JSONException e) {
                                result.completeExceptionally(e);
                            }
                        }
                        try {
                            //把数组存储到JSON
                            uploadObj.put("items", new JSONArray(items));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //解析输出JSON
                        try {
                            JSONArray jsonArray = uploadObj.getJSONArray("items");
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Log.d("data", String.valueOf(jsonObject));
//                            System.out.println(jsonObject.optString("code")+","+jsonObject.optString("datetime"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        result.complete(uploadObj);
                    }
                }

                @Override
                public void exception(ErrorInfo errorInfo) {
                    result.completeExceptionally(new Exception(errorInfo.toString()));
                }
            });
//        }
        try {
            JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName,rule.getParam(), resultObj);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
