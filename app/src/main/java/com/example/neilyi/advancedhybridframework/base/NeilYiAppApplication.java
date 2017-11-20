package com.example.neilyi.advancedhybridframework.base;

import android.app.Application;
import android.content.Context;

/**
 * 系统Application类，设置全局变量以及初始化组件
 *
 * @auther NeilYi  on 2016/11/16.
 */
public class NeilYiAppApplication extends Application {
    private final String TAG = NeilYiAppApplication.class.getSimpleName();

    public static Context myAppContext;
    private static NeilYiAppApplication instance;

    public static NeilYiAppApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        myAppContext = this;
    }
}
