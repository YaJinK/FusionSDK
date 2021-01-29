package com.magata.vivosdk;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.vivo.unionsdk.open.MissOrderEventHandler;
import com.vivo.unionsdk.open.OrderResultInfo;
import com.vivo.unionsdk.open.VivoUnionSDK;

import org.json.JSONArray;
import java.util.List;

public class VivoApplication {
	private static final String TAG = "VivoGameSDK";
	private static SdkEventReceiver receiver = new SdkEventReceiver();
	public static String globalAppId = "";

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
			if (appIdArr.length == 2) {
				appId = appIdArr[1];
				globalAppId = appId;
			}
			else
				Log.e(TAG, "AppId配置错误");
		}
		VivoUnionSDK.initSdk(application, appId, false);
		/**
		 * 掉单注册接口  需要接入掉单补单处理的一定要加
		 * !!!! 一定要加，否则无法通过上架审核 !!!
		 * 作用：商品补发回调
		 * 场景：支付完成后，游戏未正常发放商品，或发放后未成功通知到vivo侧，在异常订单查询后自动触发
		 */
//		VivoUnionSDK.registerMissOrderEventHandler(application, new MissOrderEventHandler() {
//			@Override
//			public void process(List orderResultInfos) {
//				/**
//				 * 注意这里是查到未核销的订单
//				 * 需要调用自己的逻辑完成道具核销后再调用我们的订单完成接口
//				 * 切记！！！一定要走自己逻辑发送完道具后再调用完成接口！！！切记！切记！
//				 */
//				Log.i(TAG, "registerOrderResultEventHandler: orderResultInfos = " + orderResultInfos);
//
//				List<OrderResultInfo> infoList = orderResultInfos;
//
//				if (infoList == null || infoList.isEmpty()) {
//					return;
//				}
//
//				JSONArray list = new JSONArray();
//				for (int i = 0; i < infoList.size(); i++) {
//					/**
//					 * ！！！游戏根据订单号检查、补发商品！！！
//					 * 自行完成补发逻辑  一定要完成道具补发后才能调用完成接口 此处一定要注意！！！
//					 * 如果不处理直接调用完成则掉单无法解决
//					 * 此处只是用log演示处理  真正的逻辑需要游戏自己处理补发道具后再调用完成
//					 * 注意！！！注意！！！
//					 */
//					Log.i(TAG, "process: " + infoList.get(i).getCpOrderNumber());
//					list.put(infoList.get(i).getTransNo());
//				}
//
//				Log.i(TAG, "HandleOrderList: " + list.toString());
//				receiver.onQueryMissOrderResultSucc(list.toString());
//			}
//		});
	}

}
