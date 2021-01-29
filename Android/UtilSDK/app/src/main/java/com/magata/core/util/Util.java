package com.magata.core.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.magata.core.activity.WebViewActivity;
import com.unity3d.player.UnityPlayer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Util {
    private static String TAG = "Util";

    public static int getRContent(String packageName, String className, String resouceName) {
        try {
            Class<?> resource = Class.forName(packageName + ".R");
            Log.d(TAG, "getRContent: " + packageName);

            Class<?>[] classes = resource.getClasses();
            for (Class<?> c : classes) {
                int i = c.getModifiers();
                String name = c.getName();
                Log.d(TAG, "getRContent: " + name);
                String s = Modifier.toString(i);
                if (s.contains("static") && name.contains(className)) {
                    Field intField = c.getField(resouceName);
                    return intField.getInt(c);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            //e.printStackTrace();
            Log.d(TAG, "getRContent: 没找到" + resouceName);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void showWebViewActivity(String url, String postData) {
        Intent intent = new Intent(UnityPlayer.currentActivity, WebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("postData", postData);
        bundle.putString("url", url);
        intent.putExtra("data", bundle);

        UnityPlayer.currentActivity.startActivityForResult(intent, 1);
    }

    public static void showWebViewActivity(String url) {
        showWebViewActivity(url, "");
    }

    public static void showAlertDialog(String title, String content, DialogInterface.OnClickListener onClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UnityPlayer.currentActivity);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton("知道了",
                onClick);
        builder.setCancelable(false);
        builder.show();
    }
}
