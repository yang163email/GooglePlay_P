package com.yan.googleplay.util;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by 楠GG on 2017/5/25.
 */

public class UiUtil {
    public static Context sContext;
    public static Handler sHandler;

    public static Context getContext() {
        return sContext;
    }

    public static void init(Application app) {
        sContext = app;
        sHandler = new Handler();
    }

    //发布任务
    public static void postTask(Runnable r) {
        sHandler.post(r);
    }

    public static void removeTask(Runnable r) {
        sHandler.removeCallbacks(r);
    }
    //延期发送
    public static void postDelay(Runnable r, long time) {
        sHandler.postDelayed(r, time);
    }

    public static String[] getStringArray(int resId) {
        return sContext.getResources().getStringArray(resId);
    }
}
