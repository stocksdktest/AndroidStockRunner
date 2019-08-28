package com.chi.ssetest.setup;

import android.content.Context;
import android.util.Log;

import com.chi.ssetest.protos.SetupConfig;
import com.mitake.core.bean.log.ErrorInfo;
import com.mitake.core.config.HttpChangeMode;
import com.mitake.core.config.MitakeConfig;
import com.mitake.core.config.SseSdk;
import com.mitake.core.network.Network;
import com.mitake.core.permission.MarketPermission;
import com.mitake.core.request.RegisterRequest;
import com.mitake.core.request.SearchRequest;
import com.mitake.core.response.IResponseInfoCallback;
import com.mitake.core.response.RegisterResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SDKSetup {
    private static final Map<String, String> permSetupMethods = new HashMap<>();
    static {
        permSetupMethods.put("getLevel",     "setLevel");
        permSetupMethods.put("getSseLevel",  "setSseLevel");
        permSetupMethods.put("getCffLevel",  "setCffLevel");
        permSetupMethods.put("getDceLevel",  "setDceLevel");
        permSetupMethods.put("getCzceLevel", "setCzceLevel");
        permSetupMethods.put("getFeLevel",   "setFeLevel");
        permSetupMethods.put("getGILevel",   "setGILevel");
        permSetupMethods.put("getShfeLevel", "setShfeLevel");
    }

    public static MitakeConfig setup(SetupConfig.SDKConfig cfg, Context context) throws SDKSetupException {
        MitakeConfig config = new MitakeConfig();
        config.setContext(context)
                .setAppkey(cfg.getAppKey())
                .setHttpChangeMode(HttpChangeMode.DEFAULT);
        SseSdk.setConfig(config);

        if (cfg.hasMarketPerm()) {
            SetupConfig.MarketPermission permCfg = cfg.getMarketPerm();
            MarketPermission marketPerm = SseSdk.permission();

            for (Map.Entry<String, String> entry: permSetupMethods.entrySet()) {
                String getterName = entry.getKey();
                String setterName = entry.getValue();
                try {
                    Method getter = SetupConfig.MarketPermission.class.getMethod(getterName);
                    Method setter = MarketPermission.class.getMethod(setterName, String.class);
                    String permStr = (String) getter.invoke(permCfg);
                    if (Utils.verifyPermStr(permStr)) {
                        setter.invoke(marketPerm, permStr);
                        Log.d("SDKSetup", String.format("Invoke MarketPermission.%s = %s", setter.getName(), permStr));
                    } else {
                        Log.d("SDKSetup", "Invalid permission: " + permStr);
                    }
                } catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException e) {
                    Log.e("SDKSetup", "PermSetupMethods error: ", e);
                    throw new SDKSetupException(e);
                }
            }

            for (String permStr : permCfg.getHKPermsList()) {
                if (Utils.verifyPermStr(permStr)) {
                    marketPerm.addHkPermission(permStr);
                }
            }
        }

        final CompletableFuture result = new CompletableFuture<Boolean>();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.send(new IResponseInfoCallback<RegisterResponse>() {
            public void callback(RegisterResponse response) {
                SearchRequest searchRequest = new SearchRequest();
                searchRequest.dowmLoadCodes(null);
                result.complete(Boolean.TRUE);
            }
            public void exception(ErrorInfo errorInfo) {
                result.completeExceptionally(new Exception(errorInfo.toString()));
            }
        });
        try {
            result.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new SDKSetupException(e);
        }

        for (Map.Entry<String, String> entry: cfg.getServerSitesMap().entrySet()) {
            String siteTypeStr = entry.getKey();
            if (Utils.verifyMarketSiteTypeStr(siteTypeStr)) {
                Network.getInstance().server.put(siteTypeStr, new String[]{ entry.getValue() });
            } else {
                Log.d("SDKSetup", "Invalid market site type: " + siteTypeStr);

            }
        }

        return config;
    }

    public static class SDKSetupException extends Exception {
        public SDKSetupException(String msg) {
            super(msg);
        }
        public SDKSetupException(Throwable e) {
            super(e);
        }
    }
}
