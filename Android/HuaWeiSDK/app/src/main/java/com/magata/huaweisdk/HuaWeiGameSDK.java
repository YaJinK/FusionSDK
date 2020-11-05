package com.magata.huaweisdk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.common.handler.CheckUpdateHandler;
import com.huawei.android.hms.agent.common.handler.ConnectHandler;
import com.huawei.android.hms.agent.game.handler.GetCertificationInfoHandler;
import com.huawei.android.hms.agent.game.handler.GetCertificationIntentHandler;
import com.huawei.android.hms.agent.game.handler.LoginHandler;
import com.huawei.android.hms.agent.game.handler.SaveInfoHandler;
import com.huawei.android.hms.agent.pay.handler.PayHandler;
import com.huawei.android.hms.agent.push.handler.GetTokenHandler;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.support.api.entity.core.CommonCode;
import com.huawei.hms.support.api.entity.game.GamePlayerInfo;
import com.huawei.hms.support.api.entity.game.GameStatusCodes;
import com.huawei.hms.support.api.entity.game.GameUserData;
import com.huawei.hms.support.api.entity.pay.PayReq;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.game.CertificateIntentResult;
import com.huawei.hms.support.api.game.PlayerCertificationInfo;
import com.huawei.hms.support.api.pay.PayResultInfo;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

public class HuaWeiGameSDK {

    private static SdkEventReceiver receiver = new SdkEventReceiver();
    private static String TAG = "HuaWeiGameSDK";

    public static SdkEventReceiver GetReceiver() {
        if (null == receiver)
            receiver = new SdkEventReceiver();
        return receiver;
    }

    public static void GetPushToken() {
        HMSAgent.Push.getToken(new GetTokenHandler() {
            @Override
            public void onResult(int rst) {
                Log.d(TAG, "GetTokenHandler rst:" + rst);
            }
        });
    }

    public static void initSDK(){
        HMSAgent.connect(UnityPlayer.currentActivity, new ConnectHandler() {
            @Override
            public void onConnect(int rst) {
                Log.d(TAG, "HMS connect end:" + rst);
                Log.d(TAG, "onConnect: isNetwork " + isNetworkConnected());

                if (!isNetworkConnected()) {
                    receiver.onInitFailed("当前网络异常，请稍后重试");
                    return;
                }

                if (HMSAgent.AgentResultCode.HMSAGENT_SUCCESS == rst) {
                    HMSAgent.checkUpdate(UnityPlayer.currentActivity, new CheckUpdateHandler() {
                        @Override
                        public void onResult(int rst) {
                            Log.d("HuaWei", "check app update rst:" + rst);
                        }
                    });
                    if (!isHMSVersionGreater303(UnityPlayer.currentActivity)){
                        Toast.makeText(UnityPlayer.currentActivity, "请您到“华为帐号-个人信息-实名认证”页面进行实名认证，如果您已认证，请忽略此消息。", Toast.LENGTH_LONG).show();
                    }
                    receiver.onInitSucc();
                } else if (ConnectionResult.CANCELED == rst) {
                    initSDK();
                } else if (ConnectionResult.SERVICE_MISSING == rst) {
                    initSDK();
                } else if (ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED == rst) {
                    receiver.onInitFailed("设备上安装的华为移动服务需要升级");
                } else if (ConnectionResult.SERVICE_DISABLED == rst) {
                    receiver.onInitFailed("华为移动服务被禁用，请到设备系统设置中启用。");
                } else if (ConnectionResult.BINDFAIL_RESOLUTION_REQUIRED == rst) {
                    receiver.onInitFailed("初始化失败");
                } else if (ConnectionResult.INTERNAL_ERROR == rst) {
                    initSDK();
                } else if (ConnectionResult.SERVICE_INVALID == rst) {
                    receiver.onInitFailed("设备上安装的华为移动服务应用有伪造嫌疑，请确认华为移动服务应用来源是否正确。");
                } else if (ConnectionResult.TIMEOUT == rst) {
                    receiver.onInitFailed("连接超时，请检查网络后重试。");
                } else if (ConnectionResult.SERVICE_UNSUPPORTED == rst) {
                    receiver.onInitFailed("HMS支持的android最低版本为Android4.0.3。");
                } else if (HMSAgent.AgentResultCode.APICLIENT_TIMEOUT == rst) {
                    receiver.onInitFailed("华为移动服务连接超时，请检查网络后重试。");
                } else if (6006 == rst) {
                    receiver.onInitFailed("接口鉴权：授权过期");
                } else if (ConnectionResult.INTERNAL_ERROR == rst) {
                    receiver.onInitFailed("发生内部错误");
                } else if (907135006 == rst) {
                    receiver.onInitFailed("AIDL连接session无效");
                } else if (907135005 == rst) {
                    receiver.onInitFailed("当前区域不支持此业务");
                } else if (907135700 == rst) {
                    receiver.onInitFailed("查询应用scope失败，请检查手机网络是否可以正常访问互联网。");
                }
                else {
                    receiver.onInitFailed("发生未知错误：" + rst);
                }
            }
        });

    }

    private static boolean isNetworkConnected(){
        ConnectivityManager mConnectivityManager = (ConnectivityManager) UnityPlayer.currentActivity
                         .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }else {
            return false;
        }
    }

    public static void showFloatWindow() {
        HMSAgent.Game.showFloatWindow(UnityPlayer.currentActivity);
    }

    public static void hideFloatWindow() {
        HMSAgent.Game.hideFloatWindow(UnityPlayer.currentActivity);
    }

    public static void saveUserInfo(String server, String level, String name, String sociaty){
        GamePlayerInfo gpi = new GamePlayerInfo();
        gpi.area = server;
        gpi.rank = "level " + level;
        gpi.role = name;
        gpi.sociaty = sociaty;
        HMSAgent.Game.savePlayerInfo(gpi, new SaveInfoHandler(){
            @Override
            public void onResult(int retCode) {
                Log.d(TAG, "game savePlayerInfo: onResult=" + retCode);
                if (retCode == GameStatusCodes.GAME_STATE_SUCCESS)
                    receiver.onSaveUserInfoSucc();
                else
                    receiver.onSaveUserInfoFailed("FailedCode:" + retCode);
            }
        });
    }

    public static void login(){
        HMSAgent.Game.login(new LoginHandler() {
            @Override
            public void onChange() {
                Log.d(TAG, "onChange: ");
                receiver.onLogoutSucc();
            }

            @Override
            public void onResult(int rst, GameUserData result) {
                Log.d(TAG, "onResult: " + rst);

                if (HMSAgent.AgentResultCode.HMSAGENT_SUCCESS == rst) {
                    HMSAgent.Game.showFloatWindow(UnityPlayer.currentActivity);
                    if (1 == result.getIsAuth()) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("playerId", result.getPlayerId());
                            jsonObject.put("ts", result.getTs());
                            jsonObject.put("playerLevel", result.getPlayerLevel());
                            jsonObject.put("playerSSign", result.getGameAuthSign());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, jsonObject.toString());
                        if (isHMSVersionGreater303(UnityPlayer.currentActivity)) {
                            HMSAgent.Game.getCertificationInfo(new GetCertificationInfoHandler() {
                                @Override
                                public void onResult(int resultCode, PlayerCertificationInfo certificationInfo) {
                                    if (resultCode == CommonCode.OK && certificationInfo != null) {
                                        int statusCode = certificationInfo.getStatus().getStatusCode();
                                        if (statusCode == GameStatusCodes.GAME_STATE_SUCCESS) {
                                            // TODO: 通过 certificationInfo.hasAdault()获取实名认证结果，储存结果已进行后续处理
                                            Log.d(TAG, "is CertificateAdult:" + certificationInfo.hasAdault());
                                            if (certificationInfo.hasAdault() == -1) {
                                                getCertificationIntent();
                                            }
                                        } else if (statusCode == GameStatusCodes.GAME_STATE_NO_SUPPORT) {
                                            Toast.makeText(UnityPlayer.currentActivity, "防沉迷信息验证失败，请升级'华为移动服务'。", Toast.LENGTH_LONG).show();
                                        } else {
                                            Log.d(TAG, "getPlayerCertificationInfo result:" + statusCode);
                                        }
                                    } else {
                                        Log.d(TAG, "getPlayerCertificationIntent resultCode:" + resultCode);
                                    }
                                }
                            });
                        }
                        receiver.onLoginSucc(jsonObject.toString());
                    }
                } else if (GameStatusCodes.GAME_STATE_ERROR == rst) {
                    Toast.makeText(UnityPlayer.currentActivity, "登录操作异常，请重试。", Toast.LENGTH_SHORT).show();
                } else if (GameStatusCodes.GAME_STATE_NETWORK_ERROR == rst) {
                    Toast.makeText(UnityPlayer.currentActivity, "请检查网络状态。", Toast.LENGTH_SHORT).show();
                } else if (GameStatusCodes.GAME_STATE_USER_CANCEL_LOGIN == rst) {
                    Toast.makeText(UnityPlayer.currentActivity, "用户取消登陆。", Toast.LENGTH_SHORT).show();
                } else if (GameStatusCodes.GAME_STATE_USER_CANCEL == rst) {
                    Toast.makeText(UnityPlayer.currentActivity, "用户取消实名认证。", Toast.LENGTH_SHORT).show();
                } else if (GameStatusCodes.GAME_STATE_DISAGREE_PROTOCOL == rst) {
                    Toast.makeText(UnityPlayer.currentActivity, "用户未同意协议。", Toast.LENGTH_SHORT).show();
                } else if (GameStatusCodes.GAME_STATE_CALL_REPEAT == rst) {
                    Toast.makeText(UnityPlayer.currentActivity, "正在登陆中，请稍后...", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1);
    }

    public static void logout(){
        receiver.onLogoutSucc();
    }

//serviceCatalog 为程序类型 游戏为X6  sdkChannel游戏为3
    public static void pay(String productName, String productDesc, String appId, String requestId, String amount, String merchantId, String serviceCatalog, String merchantName, int sdkChannel, String url, String sign, String extReserved) {
        PayReq payReq = new PayReq();

        payReq.productName = productName;
        payReq.productDesc = productDesc;
        payReq.applicationID = appId;
        payReq.requestId = requestId;
        payReq.amount = amount;
        payReq.merchantId = merchantId;
        payReq.serviceCatalog = serviceCatalog;
        payReq.merchantName = merchantName;
        payReq.sdkChannel = sdkChannel;
        payReq.url = url;
        payReq.sign = sign;
        payReq.extReserved = extReserved;

        HMSAgent.Pay.pay(payReq, new PayHandler() {
            @Override
            public void onResult(int retCode, PayResultInfo payInfo) {
                Log.d(TAG, "onResult: " + retCode);

                if (retCode == HMSAgent.AgentResultCode.HMSAGENT_SUCCESS && payInfo != null) {
                    receiver.onCreateOrderSucc("OK");
                } else {
                    String result = "NotOK";
                    if (retCode == HMSAgent.AgentResultCode.ON_ACTIVITY_RESULT_ERROR) {
                        Toast.makeText(UnityPlayer.currentActivity, "请前往应用管理开启“华为移动服务”“华为应用市场”的“后台弹出界面”权限！", Toast.LENGTH_LONG).show();
                    } else if (retCode == PayStatusCodes.PAY_STATE_CANCEL) {
                        Toast.makeText(UnityPlayer.currentActivity, "用户取消支付。", Toast.LENGTH_SHORT).show();
                    } else if (retCode == PayStatusCodes.PAY_STATE_NET_ERROR){
                        Toast.makeText(UnityPlayer.currentActivity, "网络连接异常。", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(UnityPlayer.currentActivity, "支付失败！错误code：" + retCode, Toast.LENGTH_SHORT).show();
                    }
                    receiver.onPayUserExit(result);
                }
            }
        });
    }

    public static void exitSDK(){
        receiver.onExitSucc("Exit");
    }

    public static void getCertificationIntent() {
        HMSAgent.Game.getCertificationIntent(new GetCertificationIntentHandler() {
            @Override
            public void onResult(int resultCode, CertificateIntentResult result) {
                if(resultCode == CommonCode.OK && result != null) {
                    int statusCode = result.getStatus().getStatusCode();
                    if(statusCode == GameStatusCodes.GAME_STATE_SUCCESS) {
                        Log.d(TAG, "GetCtfIntent success, Start intent");
                        /**
                         * 拉起实名认证页面
                         */
                        Intent intent = result.getCertificationIntent();
                        UnityPlayer.currentActivity.startActivityForResult(intent,1000);
                    }
                    else {
                        Log.d(TAG, "getPlayerCertificationIntent onResult:" + statusCode);
                    }
                }
                else
                {
                    Log.d(TAG, "getPlayerCertificationIntent resultCode:" + resultCode);
                }
            }
        });
    }

    public static void getCertificationInfo() {
        HMSAgent.Game.getCertificationInfo(new GetCertificationInfoHandler() {
            @Override
            public void onResult(int resultCode, PlayerCertificationInfo certificationInfo) {
                if (resultCode == CommonCode.OK && certificationInfo != null) {
                    int statusCode = certificationInfo.getStatus().getStatusCode();
                    if (statusCode == GameStatusCodes.GAME_STATE_SUCCESS) {
                        // TODO: 通过 certificationInfo.hasAdault()获取实名认证结果，储存结果已进行后续处理
                        Log.d(TAG, "is CertificateAdult:" + certificationInfo.hasAdault());
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("hasAdault", certificationInfo.hasAdault());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        receiver.onGetCertificationInfoSucc(jsonObject.toString());
                    } else if (statusCode == GameStatusCodes.GAME_STATE_NO_SUPPORT) {
                        Toast.makeText(UnityPlayer.currentActivity, "防沉迷信息验证失败，请升级'华为移动服务'。", Toast.LENGTH_LONG).show();
                        receiver.onGetCertificationInfoFailed("防沉迷信息验证失败，请升级'华为移动服务'。");
                    } else {
                        Log.d(TAG, "getPlayerCertificationInfo result:" + statusCode);
                        receiver.onGetCertificationInfoFailed("getPlayerCertificationInfo result:" + statusCode);
                    }
                } else {
                    Log.d(TAG, "getPlayerCertificationIntent resultCode:" + resultCode);
                    receiver.onGetCertificationInfoFailed("getPlayerCertificationIntent resultCode:" + resultCode);

                }
            }
        });
    }

    public static boolean isHMSVersionGreater303(Context context) {
        PackageManager manager = context.getPackageManager();
        boolean codeStr = false;
        try {
            PackageInfo info = manager.getPackageInfo("com.huawei.hwid", 0);
            if (info.versionCode >= 30003300)
                codeStr = true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return codeStr;
    }
}
