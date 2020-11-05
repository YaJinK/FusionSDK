package com.magata.tencentsdk;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.tencent.tmselfupdatesdk.ITMSelfUpdateListener;
import com.tencent.tmselfupdatesdk.TMSelfUpdateManager;
import com.tencent.tmselfupdatesdk.YYBDownloadListener;
import com.tencent.tmselfupdatesdk.model.TMSelfUpdateUpdateInfo;
import com.tencent.ysdk.api.YSDKApi;
import com.tencent.ysdk.framework.common.eFlag;
import com.tencent.ysdk.framework.common.ePlatform;
import com.tencent.ysdk.module.pay.PayListener;
import com.tencent.ysdk.module.pay.PayRet;
import com.tencent.ysdk.module.user.UserLoginRet;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

public class TencentGameSDK {
    private static SdkEventReceiver receiver = new SdkEventReceiver();
    private static String TAG = "Tencent";
    public static boolean isUseYYB = false;

    public static void initSDK(){

        ApplicationInfo info = null;
        try {
            info = UnityPlayer.currentActivity.getPackageManager().getApplicationInfo(UnityPlayer.currentActivity.getPackageName(),
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != info) {
            TencentGameSDK.isUseYYB = info.metaData.getBoolean("com.magata.tencentsdk.isuseyyb");
        }

        TMSelfUpdateManager.getInstance().init(UnityPlayer.currentActivity.getApplicationContext(), "990483", new ITMSelfUpdateListener() {
            @Override
            public void onDownloadAppStateChanged(int i, int i1, String s) {
            }

            @Override
            public void onDownloadAppProgressChanged(long l, long l1) {
            }

            @Override
            public void onUpdateInfoReceived(TMSelfUpdateUpdateInfo tmSelfUpdateUpdateInfo) {
            }
        }, new YYBDownloadListener() {
            @Override
            public void onDownloadYYBStateChanged(String s, int i, int i1, String s1) {
            }

            @Override
            public void onDownloadYYBProgressChanged(String s, long l, long l1) {
            }

            @Override
            public void onCheckDownloadYYBState(String s, int i, long l, long l1) {
            }
        }, null);
        TMSelfUpdateManager.getInstance().startSelfUpdate(TencentGameSDK.isUseYYB);
        receiver.onInitSucc();
    }

    public static void logout(){
        YSDKApi.logout();
        receiver.onLogoutSucc();
    }

    public static void login(int platform){
        if (1 == platform) {
            YSDKApi.login(ePlatform.QQ);
        }
        else if (2 == platform){
            YSDKApi.login(ePlatform.WX);
        }
    }

    public static void startLoginActivity(){
        Intent loginIntent = new Intent(UnityPlayer.currentActivity, LoginActivity.class);
        UnityPlayer.currentActivity.startActivity(loginIntent);
    }

    public static void login() {
        UserLoginRet userLoginRet = new UserLoginRet();
        YSDKApi.getLoginRecord(userLoginRet);
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
            receiver.onLoginFailed("网络异常，请检查后重试");
            startLoginActivity();
            Toast.makeText(UnityPlayer.currentActivity, "网络异常，请检查后重试", Toast.LENGTH_SHORT).show();
        } else if (eFlag.QQ_LoginFail == userLoginRet.flag || eFlag.WX_LoginFail == userLoginRet.flag) {
            receiver.onLoginFailed("登陆失败");
            startLoginActivity();
            Log.d(TAG, "getLoginInfo: 登陆失败");
        } else if (eFlag.QQ_NotInstall  == userLoginRet.flag) {
            receiver.onLoginFailed("手机未安装手Q，请安装后重试");
            startLoginActivity();
            Toast.makeText(UnityPlayer.currentActivity, "手机未安装手Q，请安装后重试", Toast.LENGTH_SHORT).show();
        } else if (eFlag.WX_NotInstall  == userLoginRet.flag) {
            receiver.onLoginFailed("手机未安装微信，请安装后重试");
            startLoginActivity();
            Toast.makeText(UnityPlayer.currentActivity, "手机未安装微信，请安装后重试", Toast.LENGTH_SHORT).show();
        } else if (eFlag.QQ_UserCancel  == userLoginRet.flag || eFlag.WX_UserCancel  == userLoginRet.flag) {
            receiver.onLoginFailed("用户取消授权，请重试");
            startLoginActivity();
            Toast.makeText(UnityPlayer.currentActivity, "用户取消授权，请重试", Toast.LENGTH_SHORT).show();
        } else if (eFlag.QQ_NotSupportApi  == userLoginRet.flag) {
            receiver.onLoginFailed("手机手Q版本太低，请升级后重试");
            startLoginActivity();
            Toast.makeText(UnityPlayer.currentActivity, "手机手Q版本太低，请升级后重试", Toast.LENGTH_SHORT).show();
        } else if (eFlag.WX_NotSupportApi  == userLoginRet.flag) {
            receiver.onLoginFailed("手机微信版本太低，请升级后重试");
            startLoginActivity();
            Toast.makeText(UnityPlayer.currentActivity, "手机微信版本太低，请升级后重试", Toast.LENGTH_SHORT).show();
        } else if (eFlag.Login_TokenInvalid == userLoginRet.flag) {
            receiver.onLoginFailed(result.toString());
            startLoginActivity();
        } else if (eFlag.Login_NotRegisterRealName == userLoginRet.flag) {
            receiver.onLoginFailed("未进行实名认证，请实名后重试");
            startLoginActivity();
            Toast.makeText(UnityPlayer.currentActivity, "未进行实名认证，请实名后重试", Toast.LENGTH_SHORT).show();
        }
    }

    public static void pay(String zoneId, String saveValue, boolean isCanChange, byte[] appResData, String ysdkExt) {
        YSDKApi.recharge(zoneId, saveValue, isCanChange, appResData, ysdkExt, new PayListener() {
            @Override
            public void OnPayNotify(PayRet ret) {
                Log.d(TAG, ret.toString());
                if(PayRet.RET_SUCC == ret.ret){
                    //支付流程成功
                    switch (ret.payState){
                        //支付成功
                        case PayRet.PAYSTATE_PAYSUCC:
                            UserLoginRet userLoginRet = new UserLoginRet();
                            YSDKApi.getLoginRecord(userLoginRet);
                            JSONObject result = new JSONObject();
                            try {
                                result.put("openKey", userLoginRet.getPayToken());
                                result.put("openId", userLoginRet.open_id);
                                result.put("userType", userLoginRet.platform);
                                result.put("pf", userLoginRet.pf);
                                result.put("pfkey", userLoginRet.pf_key);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            receiver.onCreateOrderSucc(result.toString());
                            break;
                        //取消支付
                        case PayRet.PAYSTATE_PAYCANCEL:
                            receiver.onPayUserExit("Cancel");
                            break;
                        //支付结果未知
                        case PayRet.PAYSTATE_PAYUNKOWN:
                            receiver.onPayUserExit("Unknow");
                            break;
                        //支付失败
                        case PayRet.PAYSTATE_PAYERROR:
                            receiver.onPayUserExit("Error");
                            break;
                        default:
                            receiver.onPayUserExit("");
                            break;
                    }
                }else{
                    switch (ret.flag){
                        case eFlag.Login_TokenInvalid:
                            receiver.onPayUserExit("TokenInvalid");
                        case eFlag.Pay_User_Cancle:
                            //用户取消支付
                            receiver.onPayUserExit("Cancel");
                            break;
                        case eFlag.Error:
                            receiver.onPayUserExit("Error");
                        default:
                            receiver.onPayUserExit("");
                            break;
                    }
                }
            }
        });
    }

    public static void exitSDK(){
        receiver.onExitSucc("Succ");
    }
}
