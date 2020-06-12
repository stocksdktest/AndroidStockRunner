//package com.chi.ssetest.cases;
//
//import android.support.test.runner.AndroidJUnit4;
//import android.util.Log;
//
//import com.chi.ssetest.StockTestcase;
//import com.chi.ssetest.StockTestcaseName;
//import com.chi.ssetest.TestcaseException;
//import com.chi.ssetest.protos.SetupConfig;
//import com.chi.ssetest.setup.RunnerSetup;
//import com.chi.ssetest.setup.TestcaseConfigRule;
//import com.mitake.core.bean.ZTSortingItem;
//import com.mitake.core.bean.log.ErrorInfo;
//import com.mitake.core.request.ZTSortingRequest;
//import com.mitake.core.response.IResponseInfoCallback;
//import com.mitake.core.response.ZTSortingResponse;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.junit.BeforeClass;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.Assert.assertNotNull;
//
///**
// * Example local unit test, which will execute on the development machine (host).
// *涨停统计行情接口
// * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// */
//@RunWith(AndroidJUnit4.class)
//@StockTestcase(StockTestcaseName.ZTSORTINGTEST_1)
//public class ZTSortingTest_1 {
//    private static final StockTestcaseName testcaseName = StockTestcaseName.ZTSORTINGTEST_1;
//    private static SetupConfig.TestcaseConfig testcaseConfig;
//    private static final int timeout_ms = 1000000;
//    @BeforeClass
//    public static void setup() throws Exception {
//        Log.d("ZTSortingTest_1", "Setup");
//        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
//        if (testcaseConfig == null ) {
//            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
//        }
//    }
//
//    @Rule
//    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);
//    @Test(timeout = timeout_ms)
//    public void requestWork() throws Exception {
//        Log.d("ZTSortingTest_1", "requestWork");
//        // TODO get custom args from param
//        final String quoteNumbers = rule.getParam().optString("PARAMS");
//        final String quoteNumbers1 = rule.getParam().optString("TYPE");
//        final CompletableFuture result = new CompletableFuture<JSONObject>();
//        ZTSortingRequest request = new ZTSortingRequest();
//        request.send(quoteNumbers,quoteNumbers1, new IResponseInfoCallback<ZTSortingResponse>() {
//
//            @Override
//            public void callback(ZTSortingResponse response) {
//                try {
//                    assertNotNull(response);
//                } catch (AssertionError e) {
//                    //                        result.completeExceptionally(e);
//                    result.complete(new JSONObject());
//                }
//                JSONObject uploadObj = new JSONObject();
//                try {
//                    List<ZTSortingItem> ztSortingItems = response.ztSortingItems;
//                    for (int i=0;i<ztSortingItems.size();i++){
//                        JSONObject uploadObj_1 = new JSONObject();
//                        uploadObj_1.put("ztdateTime",ztSortingItems.get(i).ztDatetime);
//                        uploadObj_1.put("code",ztSortingItems.get(i).code);
//                        uploadObj_1.put("name",ztSortingItems.get(i).name);
//                        uploadObj_1.put("datetime",ztSortingItems.get(i).datetime);
//                        uploadObj_1.put("market",ztSortingItems.get(i).market);
//                        uploadObj_1.put("subtype",ztSortingItems.get(i).subtype);
//                        uploadObj_1.put("lastPrice",ztSortingItems.get(i).lastPrice);
//                        uploadObj_1.put("preClosePrice",ztSortingItems.get(i).preClosePrice);
//                        uploadObj_1.put("changeRate",ztSortingItems.get(i).changeRate);
////                        uploadObj_1.put("buyVolumes",ztSortingItems.get(i).buyVolumes);
//                        List<String> buyVolumes=new ArrayList<>();
//                        if (ztSortingItems.get(i).buyVolumes!=null&&ztSortingItems.get(i).buyVolumes.size()>0){
//                            for (int j=0;j<ztSortingItems.get(i).buyVolumes.size();j++){
//                                buyVolumes.add(ztSortingItems.get(i).buyVolumes.get(j) == null ? "-" : ztSortingItems.get(i).buyVolumes.get(j));
//                            }
//                            uploadObj_1.put("buyVolumes",new JSONArray(buyVolumes));
//                        }else {
//                            uploadObj_1.put("buyVolumes",ztSortingItems.get(i).buyVolumes == null ? "-" : ztSortingItems.get(i).buyVolumes);
//                        }
//                        uploadObj_1.put("financeFlag",ztSortingItems.get(i).bu);
//                        uploadObj_1.put("securityFlag",ztSortingItems.get(i).su);
//                        uploadObj.put(String.valueOf((i+1)),uploadObj_1);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
////                    Log.d("data", String.valueOf(uploadObj));
//                result.complete(uploadObj);
//            }
//
//            @Override
//            public void exception(ErrorInfo errorInfo) {
//                result.completeExceptionally(new Exception(errorInfo.toString()));
//            }
//        });
//        try {
//            JSONObject resultObj = (JSONObject)result.get(timeout_ms, TimeUnit.MILLISECONDS);
//            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(),resultObj);
//        } catch (Exception e) {
//            //                throw new Exception(e);
//            throw new TestcaseException(e,rule.getParam());
//        }
////        }
//    }
//}
