package com.magata.huaweisdk;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.agconnect.config.LazyInputStream;
import com.huawei.hms.api.HuaweiMobileServicesUtil;

import java.io.IOException;
import java.io.InputStream;

public class HuaWeiApplication {
    private static String TAG = "HuaWeiGameSDK";
    public static boolean isGetPlayerId = false;
    public void onCreate(Application application) {
        Log.d("HuaweiApp", "onCreate");

        Log.i(TAG, "onCreate");

        ApplicationInfo info = null;
        try {
            info = application.getPackageManager().getApplicationInfo(application.getPackageName(),
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != info) {
            isGetPlayerId = info.metaData.getBoolean("com.magata.huaweisdk.isgetplayerid");

        }
        Log.e(TAG, "" + isGetPlayerId);

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
