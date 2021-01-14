package com.example.tinyrouter.core;

public class RouterUtils {

    public static boolean isEmptyStr(String target) {
        return target == null || target.trim().length() == 0;
    }
}
