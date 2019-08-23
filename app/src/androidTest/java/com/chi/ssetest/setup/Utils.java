package com.chi.ssetest.setup;

import android.support.annotation.Nullable;
import android.util.Base64;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.protos.SetupConfig;
import com.mitake.core.util.Permissions;

import org.junit.runner.Description;

public class Utils {
    public static String toPermissionsStr(SetupConfig.SDKPermissions perm) {
        switch (perm) {
            case LEVEL_1: return Permissions.LEVEL_1;
            case LEVEL_2: return Permissions.LEVEL_2;
            case HK10: return Permissions.HK10;
            case HKA1: return Permissions.HKA1;
            case HKD1: return Permissions.HKD1;
            case SHHK1: return Permissions.SHHK1;
            case SHHK5: return Permissions.SHHK5;
            case SZHK1: return Permissions.SZHK1;
            case SZHK5: return Permissions.SZHK5;
            default: return Permissions.LEVEL_1;
        }
    }

    public static byte[] base64Decode(String serialStr) {
        return Base64.decode(serialStr, Base64.NO_WRAP | Base64.URL_SAFE);
    }

    public static String base64Encode(byte []data) {
        return Base64.encodeToString(data, Base64.NO_WRAP | Base64.URL_SAFE);
    }

    public static @Nullable StockTestcaseName getTestcaseNameFromDesc(Description description) {
        StockTestcase annotation = description.getTestClass().getAnnotation(StockTestcase.class);
        if (annotation == null) {
            return null;
        }
        return annotation.value();
    }
}
