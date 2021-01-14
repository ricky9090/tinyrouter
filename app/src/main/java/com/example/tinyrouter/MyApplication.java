package com.example.tinyrouter;

import android.app.Application;

import com.example.tinyrouter.core.TinyRouter;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TinyRouter.init(this);
    }
}
