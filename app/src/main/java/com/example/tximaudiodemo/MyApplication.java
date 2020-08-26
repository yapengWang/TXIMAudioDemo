package com.example.tximaudiodemo;

import android.app.Application;
import android.content.Context;

import com.example.tximaudiodemo.audiocall.TXContents;
import com.tencent.imsdk.v2.V2TIMManager;

public class MyApplication extends Application {
    static Context context;
    private static MyApplication mInstance;

    public static Context getContext() {
        return context;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mInstance = this;

    }

}
