package com.magata.qihoosdk;


import android.app.Activity;
import android.content.Intent;

import com.qihoo.gamecenter.sdk.matrix.Matrix;

public class ActivityLifeCycle {
    public  void onCreate(Activity activity){
    }
    //游戏Activity必接 生命周期接口
    public  void onStart(Activity activity){
        Matrix.onStart(activity);
    }
    //游戏Activity必接生命周期接口
    public  void onResume(Activity activity){
        Matrix.onResume(activity);
    }
    //游戏Activity必接生命周期接口
    public  void onPause(Activity activity){
        Matrix.onPause(activity);
    }
    //游戏Activity必接生命周期接口
    public  void onStop(Activity activity){
        Matrix.onStop(activity);
    }
    //游戏Activity必接生命周期接口
    public  void onReStart(Activity activity){
        Matrix.onRestart(activity);
    }
    //游戏Activity必接生命周期接口
    public  void onActivityResult (Activity activity,int requestCode, int resultCode, Intent data){
        Matrix.onActivityResult(activity,requestCode,resultCode,data);
    }
    //游戏Activity必接生命周期接口
    public  void onNewIntent (Activity activity,Intent intent){
        Matrix.onNewIntent(activity,intent);
    }
    public  void onDestory (Activity activity){
        Matrix.destroy(activity);
    }
}
