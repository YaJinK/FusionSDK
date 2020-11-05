package com.magata.core.activity;

import android.content.Intent;
import android.os.Bundle;

import com.magata.core.util.ActivityLifeCycleUtil;
import com.unity3d.player.UnityPlayerActivity;

public class SDKMainActivity extends UnityPlayerActivity {

    private ActivityLifeCycleUtil lifeCycleUtil = null;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifeCycleUtil = new ActivityLifeCycleUtil(this);
        if (null != lifeCycleUtil)
            lifeCycleUtil.handleOnCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != lifeCycleUtil)
            lifeCycleUtil.handleOnDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != lifeCycleUtil)
            lifeCycleUtil.handleOnPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != lifeCycleUtil)
            lifeCycleUtil.handleOnResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != lifeCycleUtil)
            lifeCycleUtil.handleOnStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (null != lifeCycleUtil)
            lifeCycleUtil.handleOnRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != lifeCycleUtil)
            lifeCycleUtil.handleOnStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != lifeCycleUtil)
            lifeCycleUtil.handleOnNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != lifeCycleUtil)
            lifeCycleUtil.handleOnActivityResult(requestCode, resultCode, data);
    }
}
