package com.example.tinyrouter.core;

import android.util.Log;

public class RouterLog {

    public static void LOG_I(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }
}
