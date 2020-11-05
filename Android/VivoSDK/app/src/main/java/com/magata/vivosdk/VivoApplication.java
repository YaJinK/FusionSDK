package com.magata.vivosdk;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.vivo.unionsdk.open.VivoUnionSDK;

public class VivoApplication {
	private static final String TAG = "VivoGameSDK";

	public void onCreate(Application application) {
		Log.e(TAG, "初始化SDK");

		String appId = null;
		ApplicationInfo info = null;
		try {
			info = application.getPackageManager().getApplicationInfo(application.getPackageName(),
					PackageManager.GET_META_DATA);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		if (null != info) {
			String appIdAll = info.metaData.getString("com.magata.vivosdk.appid");
			String[] appIdArr = appIdAll.split("=");
			if (appIdArr.length == 2)
				appId = appIdArr[1];
			else
				Log.e(TAG, "AppId配置错误");
		}
		VivoUnionSDK.initSdk(application, appId, false);
	}

}
