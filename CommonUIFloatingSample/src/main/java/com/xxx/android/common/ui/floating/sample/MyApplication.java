package com.xxx.android.common.ui.floating.sample;

import android.app.Application;

import com.xxx.android.foundation.Foundations;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Foundations.install(this);
    }
}
