package com.magata.huaweisdk;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.agconnect.config.LazyInputStream;
import com.huawei.hms.api.HuaweiMobileServicesUtil;

import java.io.IOException;
import java.io.InputStream;

public class HuaWeiApplication {

    public void onCreate(Application application) {
        Log.d("HuaweiApp", "onCreate");
        HuaweiMobileServicesUtil.setApplication(application);
    }

    public void attachBaseContext(Application application, Context context) {
        Log.d("HuaweiApp", "onCreate");
        AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(context);
        config.overlayWith(new LazyInputStream(context) {
            public InputStream get(Context context) {
                try {
                    return context.getAssets().open("agconnect-services.json");
                } catch (IOException e) {
                    return null;
                }
            }
        });
    }
}
