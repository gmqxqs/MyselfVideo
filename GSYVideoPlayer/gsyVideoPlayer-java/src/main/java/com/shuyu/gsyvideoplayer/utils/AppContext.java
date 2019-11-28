package com.shuyu.gsyvideoplayer.utils;

import android.content.Context;


public class AppContext {
    public static  AppContext sInstance;
    public static  Context context;
    public static AppContext getInstance() {
        if (sInstance == null) {
            synchronized (AppContext.class) {
                if (sInstance == null) {
                    sInstance = new AppContext();
                }
            }
        }
        return sInstance;
    }
    /**
     * 获取全局上下文*/
    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        AppContext.context = context;
    }
}
