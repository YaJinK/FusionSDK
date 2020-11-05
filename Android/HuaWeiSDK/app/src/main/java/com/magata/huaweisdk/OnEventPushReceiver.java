package com.magata.huaweisdk;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.support.api.push.PushReceiver;

public class OnEventPushReceiver extends PushReceiver {
    @Override
    public void onEvent(Context var1, Event var2, Bundle var3) {
        //开发者自行实现Token保存逻辑。
        Log.d("HuaWei", "onEvent:");
    }
}
