package com.magata.opposdk;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.nearme.game.sdk.GameCenterSDK;

public class OppoApplication {
	private static final String TAG = "OppoGameSDK";

	public void onCreate(Application application) {
		String appSecret = null;
		ApplicationInfo info = null;
		try {
			info = application.getPackageManager().getApplicationInfo(application.getPackageName(),
					PackageManager.GET_META_DATA);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		if (null != info) {
			String appSecretAll = info.metaData.getString("com.magata.opposdk.appsecret");
			String[] appSecretArr = appSecretAll.split("=");
			if (appSecretArr.length == 2)
				appSecret = appSecretArr[1];
			else
				Log.e(TAG, "AppSecret配置错误");
		}
		Log.e(TAG, "初始化SDK");
		GameCenterSDK.init(appSecret, application);
	}
}
