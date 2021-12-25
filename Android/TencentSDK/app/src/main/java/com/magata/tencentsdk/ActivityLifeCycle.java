package com.magata.tencentsdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.magata.core.util.Util;
import com.tencent.ysdk.api.YSDKApi;
import com.tencent.ysdk.framework.common.eFlag;
import com.tencent.ysdk.framework.common.ePlatform;
import com.tencent.ysdk.module.AntiAddiction.listener.AntiAddictListener;
import com.tencent.ysdk.module.AntiAddiction.model.AntiAddictRet;
import com.tencent.ysdk.module.bugly.BuglyListener;
import com.tencent.ysdk.module.user.PersonInfo;
import com.tencent.ysdk.module.user.UserListener;
import com.tencent.ysdk.module.user.UserLoginRet;
import com.tencent.ysdk.module.user.UserRelationListener;
import com.tencent.ysdk.module.user.UserRelationRet;
import com.tencent.ysdk.module.user.WakeupRet;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityLifeCycle {
    private String TAG = "TencentSDK";
    private SdkEventReceiver receiver = new SdkEventReceiver();
    public static boolean mAntiAddictExecuteState = false;

    private static int mAntiMode = 0;

    public void onCreate(Activity activity){
        YSDKApi.setUserListener(new UserListener() {
            @Override
            public void OnLoginNotify(final UserLoginRet userLoginRet) {
                final JSONObject result = new JSONObject();
                try {
                    result.put("openKey", userLoginRet.getAccessToken());
                    result.put("openId", userLoginRet.open_id);
                    result.put("platform", userLoginRet.platform);
                    result.put("ysdkEnv", TencentGameSDK.ysdkEnv);
                    result.put("PayToken", userLoginRet.getPayToken());
                    result.put("pfkey", userLoginRet.pf_key);
                    result.put("userType", userLoginRet.platform);
                    result.put("pf", userLoginRet.pf);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (eFlag.Succ == userLoginRet.flag){
                    YSDKApi.queryUserInfo(ePlatform.getEnum(userLoginRet.platform), new UserRelationListener() {
                        @Override
                        public void OnRelationNotify(UserRelationRet ret) {
                            Log.d(TAG, "OnRelationNotify: " + ret.toString());
                            for (int i=0; i < ret.persons.size(); i++){
                                PersonInfo pi = (PersonInfo)ret.persons.get(i);
                                if (userLoginRet.open_id == pi.openId) {
                                    try {
                                        result.put("nickName", pi.nickName);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                            receiver.onLoginSucc(result.toString());
                        }
                    });

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

        YSDKApi.setAntiAddictListener(new AntiAddictListener() {
            @Override
            public void onLoginLimitNotify(AntiAddictRet ret) {
                Log.d(TAG, "onLoginLimitNotify: " + ret.ret);
                if (AntiAddictRet.RET_SUCC == ret.ret) {
                    // 防沉迷指令
                    switch (ret.ruleFamily) {
                        case AntiAddictRet.RULE_WORK_TIP:
                        case AntiAddictRet.RULE_WORK_NO_PLAY:
                        case AntiAddictRet.RULE_HOLIDAY_TIP:
                        case AntiAddictRet.RULE_HOLIDAY_NO_PLAY:
                        case AntiAddictRet.RULE_NIGHT_NO_PLAY:
                        case AntiAddictRet.RULE_GUEST:
                        default:
                            excuteAnti(ret);
                            break;
                    }

                }
            }

            @Override
            public void onTimeLimitNotify(AntiAddictRet ret) {
                Log.d(TAG, "onTimeLimitNotify: " + ret.ret);
                if (AntiAddictRet.RET_SUCC == ret.ret) {
                    // 防沉迷指令
                    switch (ret.ruleFamily) {
                        case AntiAddictRet.RULE_WORK_TIP:
                        case AntiAddictRet.RULE_WORK_NO_PLAY:
                        case AntiAddictRet.RULE_HOLIDAY_TIP:
                        case AntiAddictRet.RULE_HOLIDAY_NO_PLAY:
                        case AntiAddictRet.RULE_NIGHT_NO_PLAY:
                        case AntiAddictRet.RULE_GUEST:
                        default:
                            excuteAnti(ret);
                            break;
                    }
                }
            }
        });
        ApplicationInfo info = null;
        try {
            info = UnityPlayer.currentActivity.getPackageManager().getApplicationInfo(UnityPlayer.currentActivity.getPackageName(),
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != info) {
            TencentGameSDK.ysdkEnv = info.metaData.getInt("com.magata.tencentsdk.ysdkEnv");
        }
        YSDKApi.setAntiAddictLogEnable(TencentGameSDK.ysdkEnv == 0);
    }
    public void onStart(Activity activity){
    }
    public void onResume(Activity activity){
    }
    public void onPause(Activity activity){
    }
    public void onStop(Activity activity){
    }
    public void onReStart(Activity activity){
    }
    public void onActivityResult (Activity activity,int requestCode, int resultCode, Intent data){
        YSDKApi.onActivityResult(requestCode, resultCode,data);
        if (requestCode == 1 && resultCode == 1001) {
            Log.d(TAG, "onActivityResult: " + "AntiWebviewClose");
            mAntiAddictExecuteState = false;
        }
    }
    public void onNewIntent (Activity activity,Intent intent){
    }
    public void onDestory (Activity activity){
        YSDKApi. setAntiAddictGameEnd();
    }

    private void excuteAnti(AntiAddictRet ret) {
        final int modal = ret.modal;
        mAntiMode = ret.modal;
        Log.d(TAG, "excuteAnti: " + modal);

        switch (ret.type) {
            case AntiAddictRet.TYPE_TIPS:
                if (!mAntiAddictExecuteState) {
                    mAntiAddictExecuteState = true;
                    Util.showAlertDialog(ret.title, ret.content, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            if (modal == 1) {
                                // 根据modal字段来判断是否需要强制用户下线
                                // 强制用户下线
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                            mAntiAddictExecuteState = false;
                        }
                    });
                    // 已执行指令
                    YSDKApi.reportAntiAddictExecute(ret, System.currentTimeMillis());
                }
                break;
            case AntiAddictRet.TYPE_LOGOUT:
                if (!mAntiAddictExecuteState) {
                    Util.showAlertDialog(ret.title, ret.content, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            if (modal == 1) {
                                // 根据modal字段来判断是否需要强制用户下线
                                // 强制用户下线
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                            mAntiAddictExecuteState = false;
                        }
                    });
                    // 已执行指令
                    YSDKApi.reportAntiAddictExecute(ret, System.currentTimeMillis());
                }
                break;
            case AntiAddictRet.TYPE_OPEN_URL:
                if (!mAntiAddictExecuteState) {
                    mAntiAddictExecuteState = true;
                    Util.showWebViewActivity(ret.url);
                    if (modal == 1) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                    YSDKApi.reportAntiAddictExecute(ret, System.currentTimeMillis());
                }
                break;
        }
    }
}
