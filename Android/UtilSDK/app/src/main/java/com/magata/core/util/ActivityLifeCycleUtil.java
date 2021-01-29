/*
* 在value文件夹中配置键为sdk_lifecycle_name的字符串，例如
*     <string name="sdk_lifecycle_name">com.xxx.LifeCycle</string>
* 在需要渠道的工程中创建对应的类并实现生命周期方法
* 需要调用渠道生命周期方法时调用LifeCycleUtil中的handleOnXXX()
 * */

package com.magata.core.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActivityLifeCycleUtil {
    private String lcClassName = null;
    private Class sdkLifeCycleClass = null;
    private Object sdkLifeCycleObject = null;
    private String TAG = "LifeCycleUtil";
    private Activity activity = null;

    public ActivityLifeCycleUtil(Activity activity) {
        this.activity = activity;
        int classResId = Util.getRContent(activity.getPackageName(), "string", "sdk_lifecycle_name");
        Log.d(TAG, "LifeCycleUtil: " + classResId);
        if (classResId != 0)
            lcClassName = activity.getString(classResId);

        if (null != lcClassName) {
            try {
                sdkLifeCycleClass = Class.forName(lcClassName);
                sdkLifeCycleObject = sdkLifeCycleClass.newInstance();
            } catch (ClassNotFoundException e) {
                //e.printStackTrace();
                Log.d(TAG, "onCreate: 未找到Sdk生命周期类");
                sdkLifeCycleClass = null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "未配置Sdk生命周期类");
        }
    }

    public void handleOnCreate() {
        if (null != lcClassName) {
            try {
                Method onCreateMeth = sdkLifeCycleClass.getMethod("onCreate", Activity.class);
                onCreateMeth.invoke(sdkLifeCycleObject, activity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "onCreate: Sdk生命周期类未实现onCreate方法");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "未配置Sdk生命周期类");
        }
    }

    public void handleOnStart() {
        if (null != lcClassName) {
            try {
                Method onStartMeth = sdkLifeCycleClass.getMethod("onStart", Activity.class);
                onStartMeth.invoke(sdkLifeCycleObject, activity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "onStart: Sdk生命周期类未实现onStart方法");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "未配置Sdk生命周期类");
        }
    }

    public void handleOnDestroy() {
        if (null != lcClassName) {
            try {
                Method onDestroyMeth = sdkLifeCycleClass.getMethod("onDestroy", Activity.class);
                onDestroyMeth.invoke(sdkLifeCycleObject, activity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "onDestroy: Sdk生命周期类未实现onDestroy方法");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "未配置Sdk生命周期类");
        }
    }

    public void handleOnResume() {
        if (null != lcClassName) {
            try {
                Method onResumeMeth = sdkLifeCycleClass.getMethod("onResume", Activity.class);
                onResumeMeth.invoke(sdkLifeCycleObject, activity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "onResume: Sdk生命周期类未实现onResume方法");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "未配置Sdk生命周期类");
        }
    }

    public void handleOnPause() {
        if (null != lcClassName) {
            try {
                Method onPauseMeth = sdkLifeCycleClass.getMethod("onPause", Activity.class);
                onPauseMeth.invoke(sdkLifeCycleObject, activity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "onPause: Sdk生命周期类未实现onPause方法");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "未配置Sdk生命周期类");
        }
    }

    public void handleOnStop() {
        if (null != lcClassName) {
            try {
                Method onStopMeth = sdkLifeCycleClass.getMethod("onStop", Activity.class);
                onStopMeth.invoke(sdkLifeCycleObject, activity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "onStop: Sdk生命周期类未实现onStop方法");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "未配置Sdk生命周期类");
        }
    }

    public void handleOnRestart() {
        if (null != lcClassName) {
            try {
                Method onRestartMeth = sdkLifeCycleClass.getMethod("onRestart", Activity.class);
                onRestartMeth.invoke(sdkLifeCycleObject, activity);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "onRestart: Sdk生命周期类未实现onRestart方法");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "未配置Sdk生命周期类");
        }
    }

    public void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != lcClassName) {
            try {
                Method onActivityResultMeth = sdkLifeCycleClass.getMethod("onActivityResult", Activity.class, int.class, int.class, Intent.class);
                onActivityResultMeth.invoke(sdkLifeCycleObject, activity, requestCode, resultCode, data);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "onActivityResult: Sdk生命周期类未实现onActivityResult方法");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "未配置Sdk生命周期类");
        }
    }

    public void handleOnNewIntent(Intent intent) {
        if (null != lcClassName) {
            try {
                Method onNewIntentMeth = sdkLifeCycleClass.getMethod("onNewIntent", Activity.class, Intent.class);
                onNewIntentMeth.invoke(sdkLifeCycleObject, activity, intent);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Log.d(TAG, "onNewIntent: Sdk生命周期类未实现onNewIntent方法");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "未配置Sdk生命周期类");
        }
    }

}
