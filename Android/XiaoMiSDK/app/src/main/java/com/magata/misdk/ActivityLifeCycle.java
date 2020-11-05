package com.magata.misdk;

import android.app.Activity;
import android.util.Log;

import com.xiaomi.gamecenter.sdk.MiCommplatform;

public class ActivityLifeCycle {
    public void onCreate(Activity activity){
        Log.d("XiaomiLC", "onCreate: ");
        MiCommplatform.getInstance().onMainActivityCreate(activity);
    }

    public void onDestory (Activity activity){
        Log.d("XiaomiLC", "onDestory: ");
        MiCommplatform.getInstance().onMainActivityDestory(activity);
    }
}
