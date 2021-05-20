package com.tastyapps.myrecipesmobile.core.util;

import java.math.BigDecimal;

public class NumberUtils {
    public static double round(double value, int decimals) {
        return new BigDecimal(value).setScale(decimals, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static Integer parseIntOrNull(String value) {
        if (isValidInteger(value)) {
            return Integer.valueOf(value);
        } else {
            return null;
        }
    }

    public static Integer parseInt(String value, int defaultValue) {
        if (isValidInteger(value)) {
            return Integer.valueOf(value);
        } else {
            return defaultValue;
        }
    }

    public static boolean isValidInteger(String value) {
        return value.matches("\\d+(?:\\.\\d+)?");
    }
}
