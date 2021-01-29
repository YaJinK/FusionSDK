package com.magata.misdk;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.OnInitProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiAppInfo;

import java.util.List;


public class MiApplication {
    private static String TAG = "XiaoMiGameSDK";

    public void onCreate(Application application) {
        Log.i(TAG, "onCreate");

        String appId = null;
        String appKey = null;
        ApplicationInfo info = null;
        try {
            info = application.getPackageManager().getApplicationInfo(application.getPackageName(),
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != info) {
            String appIdAll = info.metaData.getString("com.magata.misdk.appid");
            String appKeyAll = info.metaData.getString("com.magata.misdk.appkey");
            String[] appIdArr = appIdAll.split("=");
            if (appIdArr.length == 2)
                appId = appIdArr[1];
            else
                Log.e(TAG, "AppId配置错误");

            String[] appKeyArr = appKeyAll.split("=");
            if (appKeyArr.length == 2)
                appKey = appKeyArr[1];
            else
                Log.e(TAG, "AppKey配置错误");

        }
        Log.e(TAG, appId);
        Log.e(TAG, appKey);

        MiAppInfo appInfo = new MiAppInfo();
        appInfo.setAppId(appId);
        appInfo.setAppKey(appKey);
        MiCommplatform.Init(application, appInfo, new OnInitProcessListener() {
            @Override
            public void finishInitProcess(List<String> list, int i) {
                Log.i(TAG, "InitSucc");
            }

            @Override
            public void onMiSplashEnd() {

            }
        });
    }

    public void attachBaseContext(Application application, Context base) {
        Log.i(TAG, "attachBaseContext");
    }
}