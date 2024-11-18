package com.xiaomi.flutter_xiaomi_iap;

import io.flutter.plugin.common.MethodCall;

public class ValueGetter {
    public static String getString(String key, MethodCall call) {
        Object value = call.argument(key);
        return value instanceof String ? (String) value : null;
    }

    public static int getInt(String key, MethodCall call) {
        Object value = call.argument(key);
        return value instanceof Integer ? (Integer) value : 0;  // 默认返回 0
    }

    public static boolean getBoolean(String key, MethodCall call) {
        Object value = call.argument(key);
        return value instanceof Boolean ? (Boolean) value : false;  // 默认返回 false
    }
}
