package com.magata.huaweisdk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.huawei.android.hms.agent.HMSAgent;

public class HuaWeiLifeCycle{
    private static String TAG = "HuaWeiGameSDK";

    public void onResume(Activity activity) {
        HMSAgent.Game.showFloatWindow(activity);
    }

    public void onPause(Activity activity) {
        HMSAgent.Game.hideFloatWindow(activity);
    }

    public void onActivityResult (Activity activity,int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult: " + requestCode);
        Log.d(TAG, "onActivityResult: " + resultCode);
    }

}
