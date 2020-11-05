/*
 * 在value文件夹中配置键为application_name的字符串，例如
 *     <string name="application_name">com.xxx.LifeCycle</string>
 * 在需要渠道的工程中创建对应的类并实现生命周期方法
 * 需要调用渠道生命周期方法时调用LifeCycleUtil中的handleOnXXX()
 * */

package com.magata.core.util;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ApplicationLifeCycleUtil {
    private String lcClassName = null;
    private Class sdkLifeCycleClass = null;
    private Object sdkLifeCycleObject = null;
    private String TAG = "ApplicationLifeCycleUtil";
    private Application application = null;

    public ApplicationLifeCycleUtil(Application application) {
        this.application = application;
        int classResId = Util.getRContent(application.getPackageName(), "string", "application_name");
        Log.d(TAG, "ApplicationLifeCycleUtil: " + classResId);
        if (classResId != 0)
            lcClassName = application.getString(classResId);

        if (null != lcClassName) {
            try {
                sdkLifeCycleClass = Class.forName(lcClassName);
                sdkLifeCycleObject = sdkLifeCycleClass.newInstance();
            } catch (ClassNotFoundException e) {
                //e.printStackTrace();
                Log.d(TAG, "Constructor: 未找到Sdk生命周期类");
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
                Method onCreateMeth = sdkLifeCycleClass.getMethod("onCreate", Application.class);
                onCreateMeth.invoke(sdkLifeCycleObject, application);
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

    public void handleAttachBaseContext(Context base) {
        if (null != lcClassName) {
            try {
                Method onStartMeth = sdkLifeCycleClass.getMethod("attachBaseContext", Application.class, Context.class);
                onStartMeth.invoke(sdkLifeCycleObject, application, base);
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

}
