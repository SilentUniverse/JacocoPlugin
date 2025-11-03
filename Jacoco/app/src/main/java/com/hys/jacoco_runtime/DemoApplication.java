package com.hys.jacoco_runtime;

import android.app.Application;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 不需要显式调用 Jacoco 逻辑，插件会在编译期注入。
    }
}

