package com.magata.tencentsdk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.tencent.tmselfupdatesdk.TMSelfUpdateManager;
import com.tencent.ysdk.api.YSDKApi;
import com.tencent.ysdk.framework.common.eFlag;
import com.tencent.ysdk.module.bugly.BuglyListener;
import com.tencent.ysdk.module.user.UserListener;
import com.tencent.ysdk.module.user.UserLoginRet;
import com.tencent.ysdk.module.user.UserRelationRet;
import com.tencent.ysdk.module.user.WakeupRet;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityLifeCycle {
    private String TAG = "TencentSDK";
    private SdkEventReceiver receiver = new SdkEventReceiver();

    public void onCreate(Activity activity){
        YSDKApi.onCreate(activity);
        YSDKApi.setUserListener(new UserListener() {
            @Override
            public void OnLoginNotify(UserLoginRet userLoginRet) {
                JSONObject result = new JSONObject();
                try {
                    result.put("openKey", userLoginRet.getAccessToken());
                    result.put("openId", userLoginRet.open_id);
                    result.put("platform", userLoginRet.platform);
                    result.put("PayToken", userLoginRet.getPayToken());
                    result.put("pfkey", userLoginRet.pf_key);
                    result.put("userType", userLoginRet.platform);
                    result.put("pf", userLoginRet.pf);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (eFlag.Succ == userLoginRet.flag){
                    receiver.onLoginSucc(result.toString());
                } else if (eFlag.QQ_NetworkErr == userLoginRet.flag) {
                    receiver.onLoginFailed("Fail");
                    Toast.makeText(UnityPlayer.currentActivity, "网络异常，请检查后重试", Toast.LENGTH_SHORT).show();
                } else if (eFlag.QQ_LoginFail == userLoginRet.flag || eFlag.WX_LoginFail == userLoginRet.flag) {
                    receiver.onLoginFailed("Fail");
                    Log.d(TAG, "getLoginInfo: 登陆失败");
                } else if (eFlag.QQ_NotInstall  == userLoginRet.flag) {
                    receiver.onLoginFailed("Fail");
                    Toast.makeText(UnityPlayer.currentActivity, "手机未安装手Q，请安装后重试", Toast.LENGTH_SHORT).show();
                } else if (eFlag.WX_NotInstall  == userLoginRet.flag) {
                    receiver.onLoginFailed("Fail");
                    Toast.makeText(UnityPlayer.currentActivity, "手机未安装微信，请安装后重试", Toast.LENGTH_SHORT).show();
                } else if (eFlag.QQ_UserCancel  == userLoginRet.flag || eFlag.WX_UserCancel  == userLoginRet.flag) {
                    receiver.onLoginFailed("Fail");
                    Toast.makeText(UnityPlayer.currentActivity, "用户取消授权，请重试", Toast.LENGTH_SHORT).show();
                } else if (eFlag.QQ_NotSupportApi  == userLoginRet.flag) {
                    receiver.onLoginFailed("Fail");
                    Toast.makeText(UnityPlayer.currentActivity, "手机手Q版本太低，请升级后重试", Toast.LENGTH_SHORT).show();
                } else if (eFlag.WX_NotSupportApi  == userLoginRet.flag) {
                    receiver.onLoginFailed("Fail");
                    Toast.makeText(UnityPlayer.currentActivity, "手机微信版本太低，请升级后重试", Toast.LENGTH_SHORT).show();
                } else if (eFlag.Login_TokenInvalid == userLoginRet.flag) {
                    receiver.onLoginFailed(result.toString());
                } else if (eFlag.Login_NotRegisterRealName == userLoginRet.flag) {
                    receiver.onLoginFailed("Fail");
                    Toast.makeText(UnityPlayer.currentActivity, "未进行实名认证，请实名后重试", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, userLoginRet.toString());
            }

            @Override
            public void OnWakeupNotify(WakeupRet wakeupRet) {
                Log.d(TAG, wakeupRet.msg);
                JSONObject result = new JSONObject();
                try {
                    result.put("platform", wakeupRet.platform);
                    result.put("open_id", wakeupRet.open_id);
                    result.put("media_tag_name", wakeupRet.media_tag_name);
                    result.put("message_ext", wakeupRet.message_ext);
                    result.put("country", wakeupRet.country);
                    result.put("lang", wakeupRet.lang);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "OnWakeupNotify: " + result.toString());
            }

            @Override
            public void OnRelationNotify(UserRelationRet userRelationRet) {
                Log.d(TAG, userRelationRet.msg);
            }
        });
        YSDKApi.setBuglyListener(new BuglyListener() {
            @Override
            public String OnCrashExtMessageNotify() {
                Log.d(TAG, "OnCrashExtMessageNotify: ");
                return null;
            }

            @Override
            public byte[] OnCrashExtDataNotify() {
                Log.d(TAG, "OnCrashExtDataNotify: ");
                return new byte[0];
            }
        });
    }
    public void onStart(Activity activity){
    }
    public void onResume(Activity activity){
        YSDKApi.onResume(activity);
        if (TencentGameSDK.isUseYYB)
            TMSelfUpdateManager.getInstance().onActivityResume();
    }
    public void onPause(Activity activity){
        YSDKApi.onPause(activity);
    }
    public void onStop(Activity activity){
        YSDKApi.onStop(activity);
    }
    public void onReStart(Activity activity){
        YSDKApi.onRestart(activity);
    }
    public void onActivityResult (Activity activity,int requestCode, int resultCode, Intent data){
        YSDKApi.onActivityResult(requestCode, resultCode,data);
    }
    public void onNewIntent (Activity activity,Intent intent){
        YSDKApi.handleIntent(intent);
    }
    public void onDestory (Activity activity){
        YSDKApi.onDestroy(activity);
        if (TencentGameSDK.isUseYYB)
            TMSelfUpdateManager.getInstance().destroy();
    }
}
