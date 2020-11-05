package com.magata.meizusdk;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.meizu.gamesdk.online.core.MzGameCenterPlatform;
import com.unity3d.player.UnityPlayer;

public class MeiZuApplication {
	private static final String TAG = "MeiZuGameSDK";

	public void onCreate(Application application) {
		Log.e(TAG, "初始化SDK");

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
			String appIdAll = info.metaData.getString("com.magata.meizusdk.appid");
			String[] appIdArr = appIdAll.split("=");
			if (appIdArr.length == 2)
				appId = appIdArr[1];
			else
				Log.e(TAG, "AppId配置错误");

			String appKeyAll = info.metaData.getString("com.magata.meizusdk.appkey");
			String[] appKeyArr = appKeyAll.split("=");
			if (appKeyArr.length == 2)
                appKey = appKeyArr[1];
			else
				Log.e(TAG, "AppKey配置错误");
		}
		MeiZuGameSDK.appId = appId;
        MeiZuGameSDK.appKey = appKey;

        MzGameCenterPlatform.init(UnityPlayer.currentActivity, appId, appKey);
	}

}
