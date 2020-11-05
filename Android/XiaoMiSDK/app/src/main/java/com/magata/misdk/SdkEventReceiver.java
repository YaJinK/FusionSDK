package com.magata.misdk;

import com.unity3d.player.UnityPlayer;

public class SdkEventReceiver {

    private final String CALLBACK_FUNCTION = "FusionCallback";

    public void onInitSucc() {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onInitSucc", "");
    }

    public void onInitFailed(String data) {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onInitFailed", data);
    }

    public void onLoginSucc(String sid) {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onLoginSucc", sid);
    }

    public void onLoginFailed(String desc) {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onLoginFailed", desc);
    }

    public void onLogoutSucc() {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onLogoutSucc", "");
    }

    public void onLogoutFailed() {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onLogoutFailed", "");
    }

    public void onExitSucc(String desc) {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onExitSucc", desc);
    }

    public void onExitCanceled(String desc) {
        UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onExitCanceled", desc);
    }

    public void onCreateOrderSucc(String orderInfo) {
        try {
            UnityPlayer.UnitySendMessage(CALLBACK_FUNCTION, "onCreateOrderSucc", orderInfo);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void onPayUserExit(String orderInfo) {
        try {
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
}

