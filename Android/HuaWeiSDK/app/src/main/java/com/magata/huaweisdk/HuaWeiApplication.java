package com.magata.huaweisdk;

import android.app.Application;
import android.util.Log;

import com.huawei.android.hms.agent.HMSAgent;

public class HuaWeiApplication {

    public void onCreate(Application application) {
        Log.d("HuaweiApp", "onCreate");
        HMSAgent.init(application);
    }
}
