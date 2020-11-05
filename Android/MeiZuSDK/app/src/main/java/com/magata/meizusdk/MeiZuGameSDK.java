package com.magata.meizusdk;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.meizu.gamesdk.model.callback.MzAuthInfoListener;
import com.meizu.gamesdk.model.callback.MzAuthenticateListener;
import com.meizu.gamesdk.model.callback.MzExitListener;
import com.meizu.gamesdk.model.callback.MzLoginListener;
import com.meizu.gamesdk.model.callback.MzPayListener;
import com.meizu.gamesdk.model.model.GameRoleInfo;
import com.meizu.gamesdk.model.model.LoginResultCode;
import com.meizu.gamesdk.model.model.MzAccountInfo;
import com.meizu.gamesdk.model.model.MzAuthInfo;
import com.meizu.gamesdk.model.model.MzAuthenticationCode;
import com.meizu.gamesdk.model.model.MzBuyInfo;
import com.meizu.gamesdk.model.model.PayResultCode;
import com.meizu.gamesdk.online.common.exception.ParamsException;
import com.meizu.gamesdk.online.core.MzGameCenterPlatform;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

public class MeiZuGameSDK {
    private static SdkEventReceiver receiver = new SdkEventReceiver();
    private static String TAG = "MeiZuGameSDK";
    public static String appId = "";
    public static String appKey = "";
    public static MzAccountInfo accountInfo = null;
    public static void initSDK(){
        Log.d(TAG, appId);
        Log.d(TAG, appKey);
        receiver.onInitSucc();
    }

    public static void exitSDK() {
        MzGameCenterPlatform.exitSDK(UnityPlayer.currentActivity, new MzExitListener() {
            public void callback(int code, String msg) {
                if (code == MzExitListener.CODE_SDK_EXIT) {
//TODO 在这里处理退出逻辑
                    receiver.onExitSucc(msg);
                } else if (code == MzExitListener.CODE_SDK_CONTINUE) {
//TODO 继续游戏
                    receiver.onExitCanceled(msg);
                }
            }
        });
    }

   static private MzLoginListener onLoginResultCb = new MzLoginListener() {
        @Override
        public void onLoginResult(int code, MzAccountInfo accountInfo, String errorMsg) {
// TODO 登录结果回调。注意，该回调跑在应用主线程，不能在这里做耗时操作
            switch(code){
                case LoginResultCode.LOGIN_SUCCESS:
                    MeiZuGameSDK.accountInfo = accountInfo;
                    MzGameCenterPlatform.getMzAuthInfo(UnityPlayer.currentActivity, MeiZuGameSDK.appId, accountInfo, new MzAuthInfoListener() {
                        @Override
                        public void onSuccess(int code, MzAuthInfo authinfo) {
                            if (code == MzAuthenticationCode.ALREADY_AUTHENTICATED) {
                                Log.d(TAG, "已经实名认证");
                            } else if (code == MzAuthenticationCode.NO_AUTHENTICATION) {
                                MzGameCenterPlatform.authenticateID(UnityPlayer.currentActivity, new MzAuthenticateListener() {
                                    @Override
                                    public void onAuthenticateIDResult(int code, String msg) {
                                        if (MzAuthenticateListener.CODE_AUTHENTICATED_ID_SUCCESS == code) {
//todo 实名认证成功
                                            Log.d(TAG, "实名认证成功");
                                        } else if (MzAuthenticateListener.CODE_AUTHENTICATED_ID_CANCEL == code)
                                        {
                                            if (null != msg)
                                                Log.d(TAG, msg);
                                        } else {
//todo 实名认证失败
                                            if (null != msg)
                                                Log.d(TAG, "实名认证失败: " + msg);
                                        } }
                                });
                            } }
                        @Override
                        public void onFailed(int code, String errorMsg) {
                            if (null != errorMsg)
                                Log.d(TAG, "code: " + code + " error msg: " + errorMsg);
                        }
                    });

// TODO 登录成功，拿到uid 和 session到自己的服务器去校验session合法性
                    String mzUid = accountInfo.getUid();
                    String sessionId = accountInfo.getSession();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("mzUid", mzUid);
                        jsonObject.put("sessionId", sessionId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    receiver.onLoginSucc(jsonObject.toString());
                    break;
                case LoginResultCode.LOGIN_ERROR_CANCEL:
// TODO 用户取消登陆操作
                    break;
                default:
// TODO 登陆失败，包含错误码和错误消息。
// TODO 注意，错误消息(errorMsg)需要由游戏展示给用户，提示失败原因
                    Toast.makeText(UnityPlayer.currentActivity, "登陆失败，请重试!"+ errorMsg,
                            Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public static void login(){
        UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MzGameCenterPlatform.login(UnityPlayer.currentActivity, onLoginResultCb);
            }
        });
    }

    public static void logout(){
        MzGameCenterPlatform.logout(UnityPlayer.currentActivity,new MzLoginListener(){
            @Override
            public void onLoginResult(int code, MzAccountInfo mzAccountInfo, String msg) {
//TODO 在这里处理登出逻辑
                receiver.onLogoutSucc();
            }
        });
    }


    private  static Bundle buildButBundle(String appId,String appKey,String appSecret,String payInfo) throws JSONException {
        JSONObject payObj = new JSONObject(payInfo);

        String orderId = payObj.getString("cp_order_id"); // cp_order_id (不能为空)
        String sign = payObj.getString("sign"); // sign (不能为空)
        String signType = payObj.getString("sign_type"); // sign_type　(不能为空)
        int buyCount = payObj.getInt("buy_amount"); // buy_amount
        String cpUserInfo = payObj.getString("callbackInfo"); // user_info
        String amount = payObj.getString("total_price"); // total_price
        String productId = payObj.getString("product_id"); // product_id
        String productSubject = payObj.getString("product_subject"); // product_subject
        String productBody = payObj.getString("product_body"); // product_body
        String productUnit = payObj.getString("product_unit"); // product_unit
        String appid = payObj.getString("app_id"); // app_id　(不能为空)
        String uid = payObj.getString("flymeUid"); // uid　(不能为空)flyme账号用户ID
        String perPrice = payObj.getString("product_per_price"); // product_per_price
        long createTime = payObj.getLong("create_time"); // create_time
        int payType = payObj.getInt("pay_type");

        Bundle buyBundle = new MzBuyInfo().setBuyCount(buyCount).setCpUserInfo(cpUserInfo)
                .setOrderAmount(amount).setOrderId(orderId).setPerPrice(perPrice)
                .setProductBody(productBody).setProductId(productId)
                .setProductSubject(productSubject).setProductUnit(productUnit)
                .setSign(sign).setSignType(signType).setCreateTime(createTime)
                .setAppid(appid).setUserUid(uid).setPayType(payType).toBundle();

        return buyBundle;
    }

    public static void pay(String payInfo) throws JSONException {
        JSONObject payObj = new JSONObject(payInfo);
        String appSecret = payObj.getString("appSecret");
        Bundle  bundle =  buildButBundle(appId,appKey,appSecret,payInfo);
        MzGameCenterPlatform.payOnline(UnityPlayer.currentActivity, bundle, new MzPayListener() {
            @Override
            public void onPayResult(int code, Bundle info, String errorMsg) {
// TODO 支付结果回调，该回调跑在应用主线程。注意，该回调跑在应用主线程，不能在这里做耗时操作
                switch(code){
                    case PayResultCode.PAY_SUCCESS:
// TODO 如果成功，接下去需要到自己的服务器查询订单结果
//                        MzBuyInfo payInfo = MzBuyInfo.fromBundle(arg1);
////                        displayMsg("支付成功 : " + payInfo.getOrderId());
                        receiver.onCreateOrderSucc("OK");
                        break;
                    case PayResultCode.PAY_ERROR_CANCEL:
// TODO 用户主动取消支付操作，不需要提示用户失败
                        receiver.onPayUserExit("Cancel");
                        break;
                    default:
// TODO 支付失败，包含错误码和错误消息。
// TODO 注意，错误消息(errorMsg)需要由游戏展示给用户，提示失败原因
                        Toast.makeText(UnityPlayer.currentActivity, "支付失败 : " + errorMsg,
                                Toast.LENGTH_LONG).show();
                        receiver.onPayUserExit("Failed");
                        break;
                }
            }
        });
    }

    //实名认证
    public static void getCertificationInfo()
    {
        if (accountInfo == null)
            return;
        MzGameCenterPlatform.getMzAuthInfo(UnityPlayer.currentActivity, MeiZuGameSDK.appId, accountInfo, new MzAuthInfoListener() {
            @Override
            public void onSuccess(int code, MzAuthInfo authinfo) {
                JSONObject jsonObject = new JSONObject();
                try {

                if (code == MzAuthenticationCode.ALREADY_AUTHENTICATED) {
                    Log.d(TAG, "已经实名认证");
                    jsonObject.put("hasAdault", 1);
                } else if (code == MzAuthenticationCode.NO_AUTHENTICATION) {
                    jsonObject.put("hasAdault", 0);
                    Log.d(TAG, "未实名认证");
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                receiver.onGetCertificationInfoSucc(jsonObject.toString());
            }

            @Override
            public void onFailed(int code, String errorMsg) {
                if (null != errorMsg)
                {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("Msg", "code: " + code + " error msg: " + errorMsg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    receiver.onGetCertificationInfoFailed(jsonObject.toString());
                }
            }
        });

    }

    //提交用户信息
    public static void saveUserInfo(String roleId, String roleName, int roleLevel, String roleZone) {
        GameRoleInfo gameRoleInfo = new GameRoleInfo()
                .setRoleId(roleId) //设置游戏角色 ID, 必填项
                .setRoleName(roleName) //设置游戏角色名称, 必填项
                .setRoleZone(roleZone) //设置角色区服-若无区服则提交 0，选填
                .setRoleLevel(roleLevel); //设置游戏角色等级，选填
        try {
            MzGameCenterPlatform.submitRoleInfo(UnityPlayer.currentActivity, gameRoleInfo.toBundle());
            receiver.onSaveUserInfoSucc();
        } catch (ParamsException e) {
            //参数异常
            e.printStackTrace();
        }
    }
}
