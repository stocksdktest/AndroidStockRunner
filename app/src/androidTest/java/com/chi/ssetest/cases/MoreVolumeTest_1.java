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
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.request.AddValueRequest;
import com.mitake.core.request.BankuaisortingRequest;
import com.mitake.core.request.CategoryType;
import com.mitake.core.request.CatequoteRequest;
import com.mitake.core.request.MorePriceRequest;
import com.mitake.core.request.MoreVolumeRequest;
import com.mitake.core.request.QuoteRequest;
import com.mitake.core.request.offer.OfferQuoteSort;
import com.mitake.core.response.AddValueResponse;
import com.mitake.core.response.BankuaiRankingResponse;
import com.mitake.core.response.Bankuaisorting;
import com.mitake.core.response.BankuaisortingResponse;
import com.mitake.core.response.CatequoteResponse;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.MorePriceResponse;
import com.mitake.core.response.MoreVolumeResponse;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *分价1——适合所有市场
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@StockTestcase(StockTestcaseName. MOREVOLUMETEST_1)
public class MoreVolumeTest_1 {
    private static final StockTestcaseName testcaseName = StockTestcaseName. MOREVOLUMETEST_1;
    private static SetupConfig.TestcaseConfig testcaseConfig;

    @BeforeClass

    public static void setup() throws Exception {
        Log.d(" MoreVolumeTest_1", "Setup");
        testcaseConfig = RunnerSetup.getInstance().getTestcaseConfig(testcaseName);
        if (testcaseConfig == null ) {
            throw new Exception(String.format("Testcase(%s) setup failed, config is empty", testcaseName));
        }
    }

    @Rule
    public TestcaseConfigRule rule = new TestcaseConfigRule(testcaseConfig);

    @Test(timeout = 5000)
    public void requestWork() throws Exception {
        Log.d(" MoreVolumeTest_1", "requestWork");
        // TODO get custom args from param
        final String quoteNumbers = rule.getParam().optString("code");
        final String quoteNumbers1 = rule.getParam().optString("subtype");
        final CompletableFuture result = new CompletableFuture<JSONObject>();
        //CategoryType
//        for (int i=0;i<quoteNumbers.length;i++){
        MoreVolumeRequest request = new MoreVolumeRequest();
        request.send(quoteNumbers,quoteNumbers1,new IResponseInfoCallback<MoreVolumeResponse>() {

            @Override
            public void callback(MoreVolumeResponse moreVolumeResponse) {
                try {
                    assertNotNull(moreVolumeResponse.values);
                } catch (AssertionError e) {
                    result.completeExceptionally(e);
                }
                try {
                    JSONObject uploadObj = new JSONObject();
                    List<String> volumes=new ArrayList<>();
                    if (moreVolumeResponse.values[0]!=null){
                        for (int i=0;i<moreVolumeResponse.values[0].length;i++){
                            volumes.add(moreVolumeResponse.values[0][i]);
                        }
                        uploadObj.put("volumes",new JSONArray(volumes));
                    }
                    List<String> buyVolumes=new ArrayList<>();
                    if (moreVolumeResponse.values[1]!=null){
                        for (int i=0;i<moreVolumeResponse.values[1].length;i++){
                            volumes.add(moreVolumeResponse.values[1][i]);
                        }
                        uploadObj.put("buyVolumes",new JSONArray(volumes));
                    }
                    List<String> sellVolumes=new ArrayList<>();
                    if (moreVolumeResponse.values[2]!=null){
                        for (int i=0;i<moreVolumeResponse.values[2].length;i++){
                            volumes.add(moreVolumeResponse.values[2][i]);
                        }
                        uploadObj.put("sellVolumes",new JSONArray(volumes));
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
            JSONObject resultObj = (JSONObject)result.get(5000, TimeUnit.MILLISECONDS);
            RunnerSetup.getInstance().getCollector().onTestResult(testcaseName, rule.getParam(), resultObj);
        } catch (Exception e) {
            throw new Exception(e);
        }
//        }
    }
}