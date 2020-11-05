package com.magata.huaweisdk;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.support.api.push.PushReceiver;

public class OnTokenPushReceiver extends PushReceiver {
    private static String TAG = "HuaWeiGameSDK";

    @Override
    public void onToken(Context context, String token, Bundle extras) {
        //开发者自行实现Token保存逻辑。
        Log.d(TAG, "onToken:" + token);
        HuaWeiGameSDK.GetReceiver().onPushTokenGet(token);
    }
}
