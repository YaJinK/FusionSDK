//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tencent.tauth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.mob.tools.MobUIShell;
import com.mob.tools.utils.Hashon;
import com.mob.tools.utils.ResHelper;
import com.tencent.connect.common.AssistActivity;
import com.tencent.connect.common.UIListenerManager;
import com.tencent.open.utils.h;
import com.tencent.open.utils.k;
import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

public class AuthActivity extends Activity {
    public static final String ACTION_KEY = "action";
    private static int a = 0;
    public static final String ACTION_SHARE_PRIZE = "sharePrize";
    public String TAG = "openSDK_LOGAuthActivity";
    private static String uriScheme;
    private static PlatformActionListener listener;
    public static void setUriScheme(String scheme) {
        uriScheme = scheme;
    }

    public static void setPlatformActionListener(PlatformActionListener actionListener) {
        listener = actionListener;
    }


    public AuthActivity() {
    }

    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        if (null == this.getIntent()) {
            Log.d(TAG, "-->onCreate, getIntent() return null");
            this.finish();
        } else {
            Uri var2 = null;

            try {
                var2 = this.getIntent().getData();
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            Log.d(TAG, "-->onCreate, uri: " + var2);
            this.a(var2);
        }
    }

    private void a(Uri var1) {
        Log.d(TAG, "-->handleActionUri--start");
        if (null != var1 && null != var1.toString() && !var1.toString().equals("")) {
            String var2 = var1.toString();
            String var3 = var2.substring(var2.indexOf("#") + 1);
            Bundle var4 = k.a(var3);
            if (null == var4) {
                Log.d(TAG, "-->handleActionUri, bundle is null");
                this.finish();
            } else {
                String var5 = var4.getString("action");
                Log.d(TAG, "-->handleActionUri, action: " + var5);
                if (null == var5) {
                    this.finish();
                } else {
                    Intent var6;
                    if (!var5.equals("shareToQQ") && !var5.equals("shareToQzone") && !var5.equals("sendToMyComputer") && !var5.equals("shareToTroopBar")) {
                        if (var5.equals("addToQQFavorites")) {
                            var6 = this.getIntent();
                            var6.putExtras(var4);
                            var6.putExtra("key_action", "action_share");
                            IUiListener var7 = UIListenerManager.getInstance().getListnerWithAction(var5);
                            if (var7 != null) {
                                UIListenerManager.getInstance().handleDataToListener(var6, (IUiListener)var7);
                            }

                            this.finish();
                        } else if (var5.equals("sharePrize")) {
                            var6 = this.getPackageManager().getLaunchIntentForPackage(this.getPackageName());
                            String var12 = var4.getString("response");
                            JSONObject var8 = null;
                            String var9 = "";

                            try {
                                var8 = k.d(var12);
                                var9 = var8.getString("activityid");
                            } catch (Exception var11) {
                                Log.d(TAG, "sharePrize parseJson has exception.", var11);
                            }

                            if (!TextUtils.isEmpty(var9)) {
                                var6.putExtra("sharePrize", true);
                                Bundle var10 = new Bundle();
                                var10.putString("activityid", var9);
                                var6.putExtras(var10);
                            }

                            this.startActivity(var6);
                            this.finish();
                        } else {
                            this.finish();
                        }
                    } else {
//                        if (var5.equals("shareToQzone") && h.a(this, "com.tencent.mobileqq") != null && h.c(this, "5.2.0") < 0) {
//                            ++a;
//                            if (a == 2) {
//                                a = 0;
//                                this.finish();
//                                return;
//                            }
//                        }
//
//                        Log.d(TAG, "-->handleActionUri, most share action, start assistactivity");
//                        var6 = new Intent(this, AssistActivity.class);
//                        var6.putExtras(var4);
//                        var6.setFlags(603979776);
//                        this.startActivity(var6);
//                        this.finish();
                        Intent intent = this.getIntent();
                        Bundle bundle = ResHelper.urlToBundle(intent.getDataString());
                        String result = String.valueOf(bundle.get("result"));
                        Log.d(TAG, "a: " + result);
                        if ("shareToQQ".equals(var5) || "shareToQzone".equals(var5)) {
                            String resStr;
                            if ("complete".equals(result)) {
                                resStr = String.valueOf(bundle.get("response"));
                                UnityPlayer.UnitySendMessage("FusionCallback", "OnShareComplete", resStr);
                            } else if ("error".equals(result)) {
                                resStr = String.valueOf(bundle.get("response"));
                                UnityPlayer.UnitySendMessage("FusionCallback", "OnShareError", resStr);
                            } else {
                                UnityPlayer.UnitySendMessage("FusionCallback", "OnShareCancel", "");
                            }
                        }

                        if (Build.VERSION.SDK_INT <= 22) {
                            Intent var10 = new Intent("android.intent.action.VIEW");
                            var10.setClass(this, MobUIShell.class);
                            var10.setFlags(335544320);
                            this.startActivity(var10);
                        }
                        this.finish();
                    }
                }

            }
        } else {
            Log.d(TAG, "-->handleActionUri, uri invalid");
            this.finish();
        }
    }
}
