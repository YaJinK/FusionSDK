package com.magata.qihoosdk;

import com.qihoo.gamecenter.sdk.matrix.Matrix;

import android.app.Activity;
import android.app.Application;


public class QihooApplication {
    public void onCreate(Application application) {
        // 此处必须先初始化360SDK
        Matrix.initInApplication(application);
    }
}
