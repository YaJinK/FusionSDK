package com.magata.vivosdk;

import android.util.Log;
import android.widget.Toast;
import com.unity3d.player.UnityPlayer;
import com.vivo.unionsdk.open.OrderResultInfo;
import com.vivo.unionsdk.open.VivoAccountCallback;
import com.vivo.unionsdk.open.VivoExitCallback;
import com.vivo.unionsdk.open.VivoPayCallback;
import com.vivo.unionsdk.open.VivoPayInfo;
import com.vivo.unionsdk.open.VivoRealNameInfoCallback;
import com.vivo.unionsdk.open.VivoRoleInfo;
import com.vivo.unionsdk.open.VivoUnionSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VivoGameSDK {
    private static final String TAG = "VivoGameSDK";
    private static String vivoUid = "";
    private static String vivoToken = "";
    private static SdkEventReceiver receiver = new SdkEventReceiver();

    //sdk初始化
    public static void initSDK(){
        Log.d(TAG, "initSDK");
        VivoUnionSDK.registerAccountCallback(UnityPlayer.currentActivity, mVivoAccountCallback);
        receiver.onInitSucc();
    }

    private static VivoAccountCallback mVivoAccountCallback = new VivoAccountCallback() {

        @Override
        public void onVivoAccountLogin(String userName, String openId, String authToken) {
            //登录成功
            Log.d(TAG, "name=" + userName + ", openid=" + openId + ", authtoken=" + authToken);
            vivoUid = openId;
            vivoToken = authToken;
//            VivoUnionSDK.queryMissOrderResult(openId);

            JSONObject obj= new JSONObject();

            try {
                obj.put("accessToken",authToken);
            }
            catch(Exception e){}

            receiver.onLoginSucc(obj.toString());
        }

        @Override
        public void onVivoAccountLogout(int requestCode) {
            //注销登录
            Log.d(TAG, "注销登录");
        }

        @Override
        public void onVivoAccountLoginCancel() {
            //登录取消操作
            Toast.makeText(UnityPlayer.currentActivity, "取消登录", Toast.LENGTH_SHORT).show();
            //onShowLoginBt();
        }

    };

    //sdk登陆
    public static void login(){
        VivoUnionSDK.login(UnityPlayer.currentActivity);
    }

    public static void pay(String json) {
        Log.d(TAG, VivoApplication.globalAppId);
        Log.d(TAG, vivoUid);
        try {
            JSONObject jsonData = new JSONObject(json);
            VivoPayInfo vivoPayInfo = new VivoPayInfo.Builder()
                    //基本支付信息
                    .setAppId(VivoApplication.globalAppId)
                    .setCpOrderNo(jsonData.getString("cpOrderNo"))
                    .setExtInfo(jsonData.getString("callbackInfo"))
                    .setNotifyUrl(jsonData.getString("notifyUrl"))
                    .setOrderAmount(jsonData.getString("orderAmount"))
                    .setProductDesc(jsonData.getString("productDesc"))
                    .setProductName(jsonData.getString("productName"))
                    //计算出来的参数验签
                    .setVivoSignature(jsonData.getString("sign"))
                    //接入vivo帐号传uid，未接入传""
                    .setExtUid(vivoUid)
                    .build();
            VivoUnionSDK.payV2(UnityPlayer.currentActivity, vivoPayInfo, new VivoPayCallback() {
                @Override
                public void onVivoPayResult(int var1, OrderResultInfo var2) {
                    Log.d(TAG, "onVivoPayResult: "+var1);
                    Log.d(TAG, "onVivoPayResult: "+var2.toString());

                    if (var1 == 0) {
                        Toast.makeText(UnityPlayer.currentActivity, "支付成功", Toast.LENGTH_SHORT).show();
                        receiver.onCreateOrderSucc("OK");
                    } else {
                        Toast.makeText(UnityPlayer.currentActivity, "支付失败", Toast.LENGTH_SHORT).show();
                        receiver.onPayUserExit("Cancel");
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //提交用户信息
    public static void saveUserInfo(String roleId, String roleLevel, String roleName, String realmId, String realmName) {
        VivoUnionSDK.reportRoleInfo(new VivoRoleInfo(roleId, roleLevel, roleName, realmId, realmName));
        receiver.onSaveUserInfoSucc();
    }

    //退出SDK
    public static void exitSDK(){
        VivoUnionSDK.exit(UnityPlayer.currentActivity, new VivoExitCallback() {

            @Override
            public void onExitConfirm() {
                //确认退出
                receiver.onExitSucc("");
            }

            @Override
            public void onExitCancel() {
                //取消退出
            }
        });
    }

    public static void logout(){
        receiver.onLogoutSucc();
    }

    //实名认证
    public static void getCertificationInfo()
    {
        VivoUnionSDK.getRealNameInfo(UnityPlayer.currentActivity, new VivoRealNameInfoCallback() {
            @Override
            public void onGetRealNameInfoSucc(boolean isRealName, int age) {
                //isRealName是否已实名制，age为年龄信息，请根据这些信息进行防沉迷操作
                JSONObject jsonObject = new JSONObject();
                try {

                    if (isRealName) {
                        jsonObject.put("hasRegistered", 1);
                        if (age < 18) {
                            jsonObject.put("hasAdault", 0);
                        } else {
                            jsonObject.put("hasAdault", 1);
                        }
                        Log.d(TAG, "已经实名认证 Age: " + age);
                    } else {
                        jsonObject.put("hasRegistered", 0);
                        jsonObject.put("hasAdault", 0);
                        Log.d(TAG, "未实名认证");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                receiver.onGetCertificationInfoSucc(jsonObject.toString());
            }

            @Override
            public void onGetRealNameInfoFailed() {
                //获取实名制信息失败，请自行处理是否防沉迷
                receiver.onGetCertificationInfoFailed("Vivo  OnGetRealNameInfoFailed");
            }
        });
    }

    /*
    json: 掉单的getTransNo  json数组
    isReOrder: 普通支付后的通知是false，掉单补单为true
    */
    public static void reportOrderComplete(String json, boolean isReOrder){
        try {
            JSONArray array = new JSONArray(json);
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < array.length(); i++) {
                Log.d(TAG, "reportOrderComplete: " + array.getString(i));
                list.add(array.getString(i));
            }
            VivoUnionSDK.reportOrderComplete(list, isReOrder);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
