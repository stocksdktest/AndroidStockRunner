package com.chi.ssetest.setup;

import android.support.annotation.Nullable;
import android.util.Base64;

import com.chi.ssetest.StockTestcase;
import com.chi.ssetest.StockTestcaseName;
import com.chi.ssetest.protos.SetupConfig;
import com.mitake.core.util.MarketSiteType;
import com.mitake.core.util.Permissions;

import org.junit.runner.Description;

import java.lang.reflect.Field;

public class Utils {
    public static boolean verifyPermStr(@Nullable String permStr) {
        return verifyClassFields(permStr, Permissions.class);
    }

    public static boolean verifyMarketSiteTypeStr(@Nullable String siteTypeStr) {
        return verifyClassFields(siteTypeStr, MarketSiteType.class);
    }

    public static boolean verifyClassFields(@Nullable String fieldValue, Class<?> cls) {
        if (fieldValue == null || fieldValue.isEmpty()) {
            return false;
        }
        Field[] fields = cls.getFields();
        try {
            for (Field field : fields) {
                if (field.getType() != String.class) {
                    continue;
                }
                String value = (String) field.get(null);
                if (value.equals(fieldValue)) {
                    return true;
                }
            }
            return false;
        } catch (IllegalAccessException e) {
            return false;
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
