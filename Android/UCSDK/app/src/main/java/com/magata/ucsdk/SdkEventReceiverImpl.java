package com.magata.ucsdk;

import android.util.Log;

import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import cn.gundam.sdk.shell.even.SDKEventKey;
import cn.gundam.sdk.shell.even.SDKEventReceiver;
import cn.gundam.sdk.shell.even.Subscribe;
import cn.gundam.sdk.shell.open.OrderInfo;

/**
 * Created by junhong.kjh@alibaba.com on 2016/12/20.
 */

public class SdkEventReceiverImpl extends SDKEventReceiver {
    private static final String TAG = SdkEventReceiverImpl.class.getSimpleName();

    private final String CALLBACK_FUNCTION = "FusionCallback";

    @Subscribe(event = SDKEventKey.ON_INIT_SUCC)
    private void onInitSucc() {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onInitSucc", "");
    }

    @Subscribe(event = SDKEventKey.ON_INIT_FAILED)
    private void onInitFailed(String data) {
        if (data != null) {
            Log.d(TAG, data);
            UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onInitFailed", data);
        }
    }

    @Subscribe(event = SDKEventKey.ON_LOGIN_SUCC)
    private void onLoginSucc(String sid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sid", sid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onLoginSucc: " + jsonObject.toString());
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onLoginSucc", jsonObject.toString());
    }
    
    @Subscribe(event = SDKEventKey.ON_LOGIN_FAILED)
    private void onLoginFailed(String desc) {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onLoginFailed", desc);
    }

    @Subscribe(event = SDKEventKey.ON_LOGOUT_SUCC)
    private void onLogoutSucc() {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onLogoutSucc", "");
    }

    @Subscribe(event = SDKEventKey.ON_LOGOUT_FAILED)
    private void onLogoutFailed() {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onLogoutFailed", "");
    }

    @Subscribe(event = SDKEventKey.ON_EXIT_SUCC)
    private void onExitSucc(String desc) {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onExitSucc", desc);
    }

    @Subscribe(event = SDKEventKey.ON_EXIT_CANCELED)
    private void onExitCanceled(String desc) {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onExitCanceled", desc);
    }

    @Subscribe(event = SDKEventKey.ON_CREATE_ORDER_SUCC)
    private void onCreateOrderSucc(OrderInfo orderInfo) {
        try {
            UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onCreateOrderSucc", createOrderString(orderInfo));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Subscribe(event = SDKEventKey.ON_PAY_USER_EXIT)
    private void onPayUserExit(OrderInfo orderInfo) {
        try {
            UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onPayUserExit", createOrderString(orderInfo));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Subscribe(event = SDKEventKey.ON_EXECUTE_SUCC)
    private void onExecuteSucc(String msg) {
        Log.d(TAG, "onExecuteSucc > " + msg);
        try {
            UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onExecuteSucc", msg);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Subscribe(event = SDKEventKey.ON_EXECUTE_FAILED)
    private void onExecuteFailed(String msg) {
        Log.d(TAG, "onExecuteFailed > " + msg);
        try {
            UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onExecuteFailed", msg);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Subscribe(event = 110001)
    private void onShowPageResult(String business, String result) {
        Log.d(TAG, "onShowPageResult > " + result);
        try {
            UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onShowPageResult", createShowPageResult(business, result));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private String createOrderString(OrderInfo orderInfo) {
        try {
            JSONObject json = new JSONObject();
            json.put("orderId", orderInfo.getOrderId());
            json.put("orderAmount", orderInfo.getOrderAmount());
            json.put("payWay", orderInfo.getPayWay());
            json.put("payWayName", orderInfo.getPayWayName());
            return json.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    private String createShowPageResult(String business, String result) {
        try {
            JSONObject json = new JSONObject();
            json.put("business", business);
            json.put("result", result);
            return json.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    public void onSaveUserInfoSucc() {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onSaveUserInfoSucc", "");
    }

    public void onSaveUserInfoFailed(String info) {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onSaveUserInfoFailed", info);
    }
}

