package com.magata.misdk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
import com.xiaomi.gamecenter.sdk.GameInfoField;
import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.OnExitListner;
import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.OnPayProcessListener;
import com.xiaomi.gamecenter.sdk.RoleData;
import com.xiaomi.gamecenter.sdk.entry.MiAccountInfo;
import com.xiaomi.gamecenter.sdk.entry.MiBuyInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class XiaoMiGameSDK {

    private static SdkEventReceiver receiver = new SdkEventReceiver();
    private static String TAG = "XiaoMiGameSDK";

    public static void initSDK() {
        MiCommplatform.getInstance().onUserAgreed(UnityPlayer.currentActivity);
        receiver.onInitSucc();
    }

    public static void exitSDK() {
        MiCommplatform.getInstance().miAppExit(UnityPlayer.currentActivity, new OnExitListner() {
            @Override
            public void onExit(int i) {
                Log.d(TAG, "onExit: " + i);
                if (i == MiErrorCode.MI_XIAOMI_EXIT) {
                    //执行退出的一些操作
                    receiver.onExitSucc(Integer.toString(i));
                }

            }
        });
    }

    public static void login() {
        MiCommplatform.getInstance().miLogin(UnityPlayer.currentActivity, new OnLoginProcessListener() {
            @Override
            public void finishLoginProcess(int code, MiAccountInfo arg1) {
                Log.d(TAG, "finishLoginProcess: " + code);
                switch (code) {
                    case MiErrorCode.MI_XIAOMI_GAMECENTER_SUCCESS:
                        // 登陆成功
                        //获取用户的登陆后的 UID(即用户唯一标识)
                        String uid = arg1.getUid();
                        //获取用户的登陆的 Session(请参考 3.3用户session验证接口)
                        String session = arg1.getSessionId();//若没有登录返回 null
                        //请开发者完成将uid和session提交给开发者自己服务器进行session验证
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("miuid", uid);
                            jsonObject.put("session", session);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        receiver.onLoginSucc(jsonObject.toString());
                        break;
                    case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_LOGIN_FAIL:
                        // 登陆失败
                        Toast.makeText(UnityPlayer.currentActivity, "登陆失败，请重试!", Toast.LENGTH_SHORT).show();
                        break;
                    case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_CANCEL:
                        // 取消登录
                        break;
                    case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_ACTION_EXECUTED:
                        // 登录操作正在进行中
                        Toast.makeText(UnityPlayer.currentActivity, "正在登陆，请稍后...", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        // 登录失败
                        Toast.makeText(UnityPlayer.currentActivity, "登陆失败，请重试!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    public static void logout(){
        receiver.onLogoutSucc();
    }

    public static void pay(String cpOrderId, String productCode, int num, String cpUserInfo, String balance, String vip, String level, String sociaty, String name, String uid, String server) {
        MiBuyInfo miBuyInfo = new MiBuyInfo();
        miBuyInfo.setCpOrderId(cpOrderId);//订单号唯一（不为空）
        miBuyInfo.setCpUserInfo(cpUserInfo); //此参数在用户支付成功后会透传给CP的服务器

        miBuyInfo.setProductCode(productCode);//商品代码，开发者申请获得（不为空）
        miBuyInfo.setCount(num);//购买数量(商品数量最大9999，最小1)（不为空）



//用户信息，网游必须设置、单机游戏或应用可选
        Bundle mBundle = new Bundle();
        mBundle.putString(GameInfoField.GAME_USER_BALANCE, balance);   //用户余额
        mBundle.putString(GameInfoField.GAME_USER_GAMER_VIP, vip);  //vip等级
        mBundle.putString(GameInfoField.GAME_USER_LV, level);           //角色等级
        mBundle.putString(GameInfoField.GAME_USER_PARTY_NAME, sociaty);  //工会，帮派
        mBundle.putString(GameInfoField.GAME_USER_ROLE_NAME, name); //角色名称
        mBundle.putString(GameInfoField.GAME_USER_ROLEID, uid);    //角色id
        mBundle.putString(GameInfoField.GAME_USER_SERVER_NAME, server);  //所在服务器
        miBuyInfo.setExtraInfo(mBundle); //设置用户信息

        MiCommplatform.getInstance().miUniPay(UnityPlayer.currentActivity, miBuyInfo,
                new OnPayProcessListener() {
                    @Override
                    public void finishPayProcess(int code) {
                        Log.d(TAG, "finishPayProcess: " + code);
                        switch (code) {
                            case MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS://购买成功
                                receiver.onCreateOrderSucc("OK");
                                break;
                            case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_CANCEL://取消购买
                                receiver.onPayUserExit("Cancel");
                                Toast.makeText(UnityPlayer.currentActivity, "用户取消", Toast.LENGTH_SHORT).show();
                                break;
                            case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_FAILURE://购买失败
                                receiver.onPayUserExit("Failed");
                                break;
                            case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED://操作正在进行中
                                receiver.onPayUserExit("Busy");
                                break;
                            default://购买失败
                                receiver.onPayUserExit("NotOK");
                                break;
                        }
                    }
                });
    }

    public static void payByBalance(String cpOrderId, String cpUserInfo, int amount, String balance, String vip, String level, String sociaty, String name, String uid, String server) {
        MiBuyInfo miBuyInfo = new MiBuyInfo();
        miBuyInfo.setCpOrderId(cpOrderId);//订单号唯一（不为空）
        miBuyInfo.setCpUserInfo(cpUserInfo); //此参数在用户支付成功后会透传给CP的服务器
        miBuyInfo.setAmount(amount); //必须是大于1的整数，10代表10米币，即10元人民币（不为空）
//用户信息，网游必须设置、单机游戏或应用可选
        Bundle mBundle = new Bundle();
        mBundle.putString(GameInfoField.GAME_USER_BALANCE, balance);   //用户余额
        mBundle.putString(GameInfoField.GAME_USER_GAMER_VIP, vip);  //vip等级
        mBundle.putString(GameInfoField.GAME_USER_LV, level);           //角色等级
        mBundle.putString(GameInfoField.GAME_USER_PARTY_NAME, sociaty);  //工会，帮派
        mBundle.putString(GameInfoField.GAME_USER_ROLE_NAME, name); //角色名称
        mBundle.putString(GameInfoField.GAME_USER_ROLEID, uid);    //角色id
        mBundle.putString(GameInfoField.GAME_USER_SERVER_NAME, server);  //所在服务器
        miBuyInfo.setExtraInfo(mBundle); //设置用户信息

        MiCommplatform.getInstance().miUniPay(UnityPlayer.currentActivity, miBuyInfo,
                new OnPayProcessListener() {
                    @Override
                    public void finishPayProcess(int code) {
                        Log.d(TAG, "finishPayProcess: " + code);
                        switch (code) {
                            case MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS://购买成功
                                receiver.onCreateOrderSucc("OK");
                                break;
                            case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_CANCEL://取消购买
                                receiver.onPayUserExit("Cancel");
                                Toast.makeText(UnityPlayer.currentActivity, "用户取消", Toast.LENGTH_SHORT).show();
                                break;
                            case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_FAILURE://购买失败
                                receiver.onPayUserExit("Failed");
                                break;
                            case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED://操作正在进行中
                                receiver.onPayUserExit("Busy");
                                break;
                            default://购买失败
                                receiver.onPayUserExit("NotOK");
                                break;
                        }
                    }
                });
    }

    public static void saveUserInfo(String uid, String level, String name, String sid, String sName, String zoneId, String zoneName) {
        RoleData data = new RoleData();
        data.setLevel(level);
        data.setRoleId(uid);
        data.setRoleName(name);
        data.setServerId(sid);
        data.setServerName(sName);
        data.setZoneId(zoneId);
        data.setZoneName(zoneName);
        MiCommplatform.getInstance().submitRoleData(UnityPlayer.currentActivity, data);
        receiver.onSaveUserInfoSucc();
    }
}
