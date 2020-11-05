package com.magata.meizusdk;

import android.app.Activity;
import android.content.Intent;

import com.meizu.gamesdk.online.core.MzGameBarPlatform;
import com.unity3d.player.UnityPlayer;

public class MeiZuLifeCycle {
    MzGameBarPlatform mzGameBarPlatform;

    public  void onCreate(Activity activity){
        mzGameBarPlatform = new MzGameBarPlatform(UnityPlayer.currentActivity, MzGameBarPlatform.GRAVITY_RIGHT_BOTTOM);
        mzGameBarPlatform.onActivityCreate();
    }
    //游戏Activity必接 生命周期接口
    public  void onStart(Activity activity){
    }
    //游戏Activity必接生命周期接口
    public  void onResume(Activity activity){
        mzGameBarPlatform.onActivityResume();
    }
    //游戏Activity必接生命周期接口
    public  void onPause(Activity activity){
        mzGameBarPlatform.onActivityPause();
    }
    //游戏Activity必接生命周期接口
    public  void onStop(Activity activity){
    }
    //游戏Activity必接生命周期接口
    public  void onReStart(Activity activity){

    }
    //游戏Activity必接生命周期接口
    public  void onActivityResult (Activity activity,int requestCode, int resultCode, Intent data){

    }
    //游戏Activity必接生命周期接口
    public  void onNewIntent (Activity activity,Intent intent){

    }
    public  void onDestory (Activity activity){
        mzGameBarPlatform.onActivityDestroy();
    }
}
