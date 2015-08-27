package com.hak.wymi.utility;

import java.util.HashMap;
import java.util.Map;

public final class AppConfig {
    public static final Integer BASE_TIME = 1438963378;
    private static Map<String, String> values;

    private AppConfig() {
    }

    public static String get(String key) {
        if (values == null) {
            values = new HashMap<>();
            values.put("IP", "10.0.0.3");
        }
        return values.get(key);
    }
}
