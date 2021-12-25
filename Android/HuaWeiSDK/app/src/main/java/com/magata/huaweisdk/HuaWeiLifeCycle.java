package com.magata.huaweisdk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.entity.OrderStatusCode;
import com.huawei.hms.iap.entity.PurchaseResultInfo;

import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.PlayersClient;
import com.huawei.hms.jos.games.buoy.BuoyClient;
import com.huawei.hms.jos.games.player.Player;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

public class HuaWeiLifeCycle{
    private static String TAG = "HuaWeiGameSDK";

    public void onResume(Activity activity) {
        BuoyClient buoyClient = Games.getBuoyClient(UnityPlayer.currentActivity);
        buoyClient.showFloatWindow();
    }

    public void onPause(Activity activity) {
        BuoyClient buoyClient = Games.getBuoyClient(UnityPlayer.currentActivity);
        buoyClient.hideFloatWindow();
    }

    public void onActivityResult (Activity activity,int requestCode, int resultCode, Intent data){
        //授权登录结果处理，从AuthHuaweiId中获取Authorization Code
        if (requestCode == 8888) {
            //调用getPlayersClient方法初始化
            PlayersClient client = Games.getPlayersClient(UnityPlayer.currentActivity);
            //获取玩家信息
            Task<Player> task = client.getGamePlayer(HuaWeiApplication.isGetPlayerId);
            task.addOnSuccessListener(new OnSuccessListener<Player>() {
                @Override
                public void onSuccess(Player player) {
                    String accessToken = player.getAccessToken();
                    String displayName = player.getDisplayName();
                    String unionId = player.getUnionId();
                    String openId = player.getOpenId();

                    try {
                        JSONObject json = new JSONObject();
                        json.put("accessToken", accessToken);
                        if(HuaWeiApplication.isGetPlayerId == true){
                            String playerId = player.getPlayerId();
                            String ts = player.getSignTs();
                            String playerSSign = player.getPlayerSign();
                            String playerLevel =  Integer.toString(player.getLevel());
                            String openIdSign = player.getOpenIdSign();

                            json.put("playerId", playerId);
                            json.put("ts", ts);
                            json.put("playerSSign", playerSSign);
                            json.put("playerLevel", playerLevel);
                            json.put("openIdSign", openIdSign);
                            json.put("openId", openId);

                        }
                        HuaWeiGameSDK.GetReceiver().onLoginSucc(json.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    if (e instanceof ApiException) {
                        String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                        Log.d(TAG, result);

                    }
                }
            });
        }else if (requestCode == 6666) {
            if (data == null) {
                Log.e("onActivityResult", "data is null");
                return;
            }
            // 调用parsePurchaseResultInfoFromIntent方法解析支付结果数据
            PurchaseResultInfo purchaseResultInfo = Iap.getIapClient(UnityPlayer.currentActivity).parsePurchaseResultInfoFromIntent(data);
            switch(purchaseResultInfo.getReturnCode()) {
                case OrderStatusCode.ORDER_STATE_CANCEL:
                    // 用户取消
                    break;
                case OrderStatusCode.ORDER_STATE_FAILED:
                case OrderStatusCode.ORDER_PRODUCT_OWNED:
                    // 检查是否存在未发货商品
                    HuaWeiGameSDK.checkMissingOrder(0);
                    break;
                case OrderStatusCode.ORDER_STATE_SUCCESS:
                    // 支付成功
                    String inAppPurchaseData = purchaseResultInfo.getInAppPurchaseData();
                    String inAppPurchaseDataSignature = purchaseResultInfo.getInAppDataSignature();
                    // 使用您应用的IAP公钥验证签名
                    // 若验签成功，则进行发货
                    // 若用户购买商品为消耗型商品，您需要在发货成功后调用consumeOwnedPurchase接口进行消耗
                    try {
                        JSONObject json = new JSONObject();
                        json.put("inAppPurchaseData", inAppPurchaseData);
                        json.put("inAppPurchaseDataSignature", inAppPurchaseDataSignature);
                        HuaWeiGameSDK.GetReceiver().onCreateOrderSucc(json.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
