package com.magata.vivosdk;

import android.util.Log;
import android.widget.Toast;
import com.unity3d.player.UnityPlayer;
import com.vivo.unionsdk.open.VivoAccountCallback;
import com.vivo.unionsdk.open.VivoExitCallback;
import com.vivo.unionsdk.open.VivoPayCallback;
import com.vivo.unionsdk.open.VivoPayInfo;
import com.vivo.unionsdk.open.VivoRealNameInfoCallback;
import com.vivo.unionsdk.open.VivoRoleInfo;
import com.vivo.unionsdk.open.VivoUnionSDK;

import org.json.JSONException;
import org.json.JSONObject;

public class VivoGameSDK {
    private static final String TAG = "VivoGameSDK";
    private static String vivoUid = "";
    private static String vivoToken = "";
    private static SdkEventReceiver receiver = new SdkEventReceiver();
    public static boolean loginState = false;

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
            loginState = true;
            vivoUid = openId;
            vivoToken = authToken;
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
        if (loginState == false)
        {
            VivoUnionSDK.login(UnityPlayer.currentActivity);
        }
        else
        {
            JSONObject obj= new JSONObject();
            try {
                obj.put("accessToken",vivoToken);
            }
            catch(Exception e){}
            receiver.onLoginSucc(obj.toString());
        }
    }

    public static void pay(String productName, String productDes, String productPrice, String vivoSignature, String appId, String transNo) {
        VivoPayInfo.Builder builder = new VivoPayInfo.Builder();
        builder.setProductName(productName);
        builder.setProductDes(productDes);
        builder.setProductPrice(productPrice);
        builder.setVivoSignature(vivoSignature);
        builder.setAppId(appId);
        builder.setTransNo(transNo);
        builder.setUid(vivoUid);

        VivoPayInfo vivoPayInfo = builder.build();

        VivoUnionSDK.pay(UnityPlayer.currentActivity, vivoPayInfo, new VivoPayCallback() {
            @Override
            public void onVivoPayResult(String arg0, boolean arg1, String arg2) {
                if (arg1) {
                    Toast.makeText(UnityPlayer.currentActivity, "支付成功", Toast.LENGTH_SHORT).show();
                    receiver.onCreateOrderSucc("OK");
                } else {
                    Toast.makeText(UnityPlayer.currentActivity, "支付失败", Toast.LENGTH_SHORT).show();
                    receiver.onPayUserExit("Cancel");
                }
            }
        });
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
}
