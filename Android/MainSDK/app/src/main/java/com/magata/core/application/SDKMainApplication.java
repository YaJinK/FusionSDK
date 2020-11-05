package com.magata.core.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.magata.core.util.ApplicationLifeCycleUtil;

public class SDKMainApplication extends Application {

    private ApplicationLifeCycleUtil lifeCycleUtil = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SDKMainApplication", "OnCreate");
        if (lifeCycleUtil == null)
            lifeCycleUtil = new ApplicationLifeCycleUtil(this);
        lifeCycleUtil.handleOnCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d("SDKMainApplication", "attachBaseContext");

        if (lifeCycleUtil == null)
            lifeCycleUtil = new ApplicationLifeCycleUtil(this);
        lifeCycleUtil.handleAttachBaseContext(base);
    }
}
