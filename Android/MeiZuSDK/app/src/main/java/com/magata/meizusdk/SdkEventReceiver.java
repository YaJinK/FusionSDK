package com.magata.meizusdk;

import com.unity3d.player.UnityPlayer;

public class SdkEventReceiver {

    private final String CALLBACK_FUNCTION = "FusionCallback";

    public void onInitSucc() {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onInitSucc", "");
    }

    public void onInitFailed(String data) {
        if(data == null)
            data = "";
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onInitFailed", data);
    }

    public void onLoginSucc(String sid) {
        if(sid == null)
            sid = "";
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onLoginSucc", sid);
    }

    public void onLoginFailed(String desc) {
        if(desc == null)
            desc = "";
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onLoginFailed", desc);
    }

    public void onLogoutSucc() {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onLogoutSucc", "");
    }

    public void onLogoutFailed() {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onLogoutFailed", "");
    }

    public void onExitSucc(String desc) {
        if(desc == null)
            desc = "";
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onExitSucc", desc);
    }

    public void onExitCanceled(String desc) {
        if(desc == null)
            desc = "";
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onExitCanceled", desc);
    }

    public void onCreateOrderSucc(String orderInfo) {
        try {
            if(orderInfo == null)
                orderInfo = "";
            UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onCreateOrderSucc", orderInfo);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void onPayUserExit(String orderInfo) {
        try {
            if(orderInfo == null)
                orderInfo = "";
            UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onPayUserExit", orderInfo);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void onSaveUserInfoSucc() {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onSaveUserInfoSucc", "");
    }

    public void onSaveUserInfoFailed(String info) {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onSaveUserInfoFailed", info);
    }

    public void onGetCertificationInfoSucc(String info) {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onGetCertificationInfoSucc", info);
    }

    public void onGetCertificationInfoFailed(String msg) {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onGetCertificationInfoFailed", msg);
    }
}

