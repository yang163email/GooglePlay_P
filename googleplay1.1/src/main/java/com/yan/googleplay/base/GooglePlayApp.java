package com.yan.googleplay.base;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;
import com.yan.googleplay.util.UiUtil;

/**
 * Created by 楠GG on 2017/5/25.
 */

public class GooglePlayApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //程序入口，初始化操作(友盟统计)
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this, "foiaeji1o90fjijdfi", "jifjaidnoj8892"));

        //初始化组件
        UiUtil.init(this);
    }
}
