package com.magata.opposdk;

import android.util.Log;
import android.widget.Toast;

import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.ApiCallback;
import com.nearme.game.sdk.callback.GameExitCallback;
import com.nearme.game.sdk.common.model.ApiResult;
import com.nearme.game.sdk.common.model.biz.PayInfo;
import com.nearme.game.sdk.common.model.biz.ReportUserGameInfoParam;
import com.nearme.game.sdk.common.model.biz.ReqUserInfoParam;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class OppoGameSDK {
    private static final String TAG = "OppoGameSDK";
    private static SdkEventReceiver receiver = new SdkEventReceiver();

    //sdk初始化
    public static void initSDK(){
        Log.e(TAG, "===initSDK===");
        receiver.onInitSucc();
    }

    public static void getUserInfo(String token, String ssoid){
        ReqUserInfoParam userInfoParam = new ReqUserInfoParam(token, ssoid);
        GameCenterSDK.getInstance().doGetUserInfo(userInfoParam, new ApiCallback() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, s);
                receiver.onGetUserInfoSucc(s);
            }

            @Override
            public void onFailure(String s, int i) {
                Log.d(TAG, "onFailure:"+s);
                Log.d(TAG, "onFailure:"+i);
            }
        });
    }

    //sdk登陆
    public static void login(){
        GameCenterSDK.getInstance().doLogin(UnityPlayer.currentActivity, new ApiCallback() {
            @Override
            public void onSuccess(String s) {
                doGetTokenAndSsoid();
            }

            @Override
            public void onFailure(String s, int i) {
                Toast.makeText(UnityPlayer.currentActivity, "登陆失败，请重试!", Toast.LENGTH_SHORT).show();
                receiver.onLoginFailed("");
            }
        });
    }

    public static void doGetTokenAndSsoid()
    {
        GameCenterSDK.getInstance().doGetTokenAndSsoid(new ApiCallback() {

            @Override
            public void onSuccess(String resultMsg)
            {
                receiver.onLoginSucc(resultMsg);
            }

            @Override
            public void onFailure(String arg0, int arg1)
            {
                Log.e(TAG, "===doGetTokenAndSsoid==onFailure===" + arg1);
                receiver.onLoginFailed("");
            }
        });
    }

    public static void pay(String orderId, int amount, String notifyUrl, String attach, String productName, String productDesc) {
        final PayInfo payInfo = new PayInfo(orderId, attach, amount);
        payInfo.setProductName(productName);
        payInfo.setProductDesc(productDesc);
        payInfo.setCallbackUrl(notifyUrl);
        payInfo.setAttach(attach);

        GameCenterSDK.getInstance().doPay(UnityPlayer.currentActivity, payInfo, new ApiCallback() {

            @Override
            public void onSuccess(String arg0)
            {
                Toast.makeText(UnityPlayer.currentActivity, "支付成功", Toast.LENGTH_SHORT).show();
                receiver.onCreateOrderSucc("OK");
            }

            @Override
            public void onFailure(String arg0, int arg1)
            {
                Log.e(TAG, "onFailure: " + "code:" + arg1);
                Toast.makeText(UnityPlayer.currentActivity, "支付失败", Toast.LENGTH_SHORT).show();
                receiver.onPayUserExit("Cancel");
            }
        });
    }

    //提交用户信息
    public static void saveUserInfo(String roleId, String roleName, int roleLevel, String realmId, String realmName, String chapter, long combatValue, long pointValue) {
        HashMap<String,Number> map = null;
        if (combatValue >= 0 || pointValue >=0) {
            map = new HashMap<>();
            if (combatValue >= 0)
                map.put("combatValue",combatValue);
            if (pointValue >= 0)
                map.put("pointValue",pointValue);
        }
        ReportUserGameInfoParam param = new ReportUserGameInfoParam(roleId, roleName, roleLevel, realmId,realmName,chapter,map);
        GameCenterSDK.getInstance().doReportUserGameInfoData(param, new ApiCallback() {
            @Override
            public void onSuccess(String arg0)
            {
                receiver.onSaveUserInfoSucc();
            }

            @Override
            public void onFailure(String arg0, int arg1)
            {
                if (arg0 == null)
                    arg0 = "";
                String info = "ErrorCode:" + arg1 + " " + arg0;
                receiver.onSaveUserInfoFailed(info);

            }
        });
    }

    public static void logout(){
        receiver.onLogoutSucc();
    }

    //退出SDK
    public static void exitSDK(){
        GameCenterSDK.getInstance().onExit(UnityPlayer.currentActivity, new GameExitCallback() {

            @Override
            public void exitGame()
            {
                receiver.onExitSucc("");
            }
        });
    }

    //实名认证
    public static void getCertificationInfo()
    {
        GameCenterSDK.getInstance().doGetVerifiedInfo(new ApiCallback() {

            @Override
            public void onSuccess(String resultMsg) {
                try{
                    int age = Integer.parseInt(resultMsg);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("IsVerify", true);
                    jsonObject.put("IsAdult", age >= 18);
                    receiver.onGetCertificationInfoSucc(jsonObject.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String resultMsg, int resultCode) {
                if (resultCode == ApiResult.RESULT_CODE_VERIFIED_FAILED_AND_RESUME_GAME){
                    //实名认证失败，但可以继续玩游戏
                    Log.e(TAG, "onFailure: 实名认证失败，但可以继续玩游戏");
                }else if (resultCode == ApiResult.RESULT_CODE_VERIFIED_FAILED_AND_STOP_GAME) {
                    //实名认证失败，不允许继续玩游戏，CP需要自己处理退出
                    Log.e(TAG, "onFailure: 实名认证失败，不允许继续玩游戏");
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("code", resultCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                receiver.onGetCertificationInfoFailed(jsonObject.toString());
            }
        });
    }
}
