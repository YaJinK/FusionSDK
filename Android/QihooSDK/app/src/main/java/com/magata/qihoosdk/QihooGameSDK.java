package com.magata.qihoosdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qihoo.gamecenter.sdk.protocols.CPCallBackMgr;
import com.unity3d.player.UnityPlayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Log;
import android.widget.Toast;

import com.qihoo.gamecenter.sdk.activity.ContainerActivity;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.matrix.Matrix;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;

import java.util.HashMap;

public class QihooGameSDK {
    private static SdkEventReceiver receiver = new SdkEventReceiver();
    private static String TAG = "Qihoo";

    private static Intent getLoginIntent() {
        Intent intent = new Intent(UnityPlayer.currentActivity, ContainerActivity.class);

        // 界面相关参数，360SDK界面是否以横屏显示。
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,false);

        // 必需参数，使用360SDK的登录模块。
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGIN);

        // 可选参数，是否在自动登录的过程中显示切换账号按钮
        intent.putExtra(ProtocolKeys.IS_SHOW_AUTOLOGIN_SWITCH,false);

        //-- 以下参数仅仅针对自动登录过程的控制
        // 可选参数，自动登录过程中是否不展示任何UI，默认展示。
        intent.putExtra(ProtocolKeys.IS_AUTOLOGIN_NOUI,true);

        return intent;
    }
    /***
     * 生成调用360SDK切换账号接口的Intent
     * 默认无横屏
     * @return Intent
     */
    private static Intent getSwitchAccountIntent() {
        Intent intent = new Intent(UnityPlayer.currentActivity, ContainerActivity.class);
        // 可选参数，是否在自动登录的过程中显示切换账号按钮
        intent.putExtra(ProtocolKeys.IS_SHOW_AUTOLOGIN_SWITCH,false);

        // 必须参数，360SDK界面是否以横屏显示。
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,false);

        // 必需参数，使用360SDK的切换账号模块。
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT);

        return intent;
    }

    private static boolean isCancelLogin(String data) {
        try {
            JSONObject joData = new JSONObject(data);
            int errno = joData.optInt("errno", -1);
            if (-1 == errno) {
//                Toast.makeText(UnityPlayer.currentActivity, data, Toast.LENGTH_LONG).show();
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    private static String parseAccessTokenFromLoginResult(String loginRes) {
        try {
            JSONObject joRes = new JSONObject(loginRes);
            JSONObject joData = joRes.getJSONObject("data");
            return joData.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void initSDK(){
        UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Matrix.setActivity(UnityPlayer.currentActivity,new CPCallBackMgr.MatrixCallBack() {
                    @Override
                    public void execute(Context context, int functionCode, String functionParams) {
                        if (functionCode == ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT) {
                            switchAccount();
                        }else if (functionCode == ProtocolConfigs.FUNC_CODE_INITSUCCESS) {
                            //这里返回成功之后才能调用SDK 其它接口
                            //例如，登录接口必须在该callback回调成功后调用
                            receiver.onInitSucc();
                        }
                    }
                },false);
            }
        });
    }

    public static void exitSDK() {
        Bundle bundle = new Bundle();

        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, false);

        // 可选参数，登录界面的背景图片路径，必须是本地图片路径
        bundle.putString(ProtocolKeys.UI_BACKGROUND_PICTRUE, "");

        // 必需参数，使用360SDK的退出模块。
        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_QUIT);

        Intent intent = new Intent(UnityPlayer.currentActivity, ContainerActivity.class);
        intent.putExtras(bundle);

        Matrix.invokeActivity(UnityPlayer.currentActivity, intent, new IDispatcherCallback() {
            @Override
            public void onFinished(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if(jsonObject.getInt("which") == 2 ){
                        receiver.onExitSucc(data);
                    }else{
                        receiver.onExitCanceled(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //登录
    public static void login(){
        UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
            Intent intent = getLoginIntent();
            IDispatcherCallback callback = new IDispatcherCallback() {
                @Override
                public void onFinished(String data) {
                    // press back
                    if (isCancelLogin(data)) {
                        return;
                    }
                    // 显示一下登录结果
                    boolean mIsInOffline = false;
                    // 解析access_token
                    String mAccessToken = parseAccessTokenFromLoginResult(data);
                    if (!TextUtils.isEmpty(mAccessToken)) {
// 需要去应用的服务器获取用access_token获取一下用户信息
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("accessToken", mAccessToken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        receiver.onLoginSucc(jsonObject.toString());
                    } else {
                        Toast.makeText(UnityPlayer.currentActivity, "登陆失败，请重试!",
                                Toast.LENGTH_LONG).show();
                    }

                }
            };
        Matrix.execute(UnityPlayer.currentActivity,intent,callback);
        }});
    }

    //切换账号
    public static void switchAccount() {
//        receiver.onLogoutSucc();
        Intent intent = getSwitchAccountIntent();
        Matrix.invokeActivity(UnityPlayer.currentActivity, intent,new IDispatcherCallback() {
            @Override
            public void onFinished(String data) {
                // press back
                if (isCancelLogin(data)) {
                    return;
                }
                if(data!=null){
                    // 显示一下登录结果
                }
                // 解析access_token
                String mAccessToken = parseAccessTokenFromLoginResult(data);

                if (!TextUtils.isEmpty(mAccessToken)) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("accessToken", mAccessToken);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    receiver.onLogoutSucc();
                    receiver.onLoginSucc(jsonObject.toString());
                } else {
                    Toast.makeText(UnityPlayer.currentActivity, "登陆失败，请重试!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void pay( String usrinfo) throws JSONException {
        JSONObject payInfo = new JSONObject(usrinfo);
//        if (!checkLoginInfo(usrinfo)) {
//            return;
//        }
//        if(!isAccessTokenValid) {
//            return;
//        }
//        if(!isQTValid) {
//            return;
//        }
        // 支付基础参数
        Intent intent = getPayIntent(payInfo, ProtocolConfigs.FUNC_CODE_PAY);
        //  必需参数，使用360SDK的支付模块:CP可以根据需求选择使用 带有收银台的支付模块 或者 直接调用微信支付模块或者直接调用支付宝支付模块。
        //functionCode 对应三种类型的支付模块：
        //ProtocolConfigs.FUNC_CODE_PAY;// 360聚合支付模块。（首次支付有收银台，基于各类因素推荐用户使用每笔订单支付最便捷的支付方式，常规手游可以此方式接入支付，以最小开发成本快速接入上线）
        //ProtocolConfigs.FUNC_CODE_WEIXIN_PAY;//微信支付模块。（无收银台，直接调用微信发起支付，用户设备中需安装微信客户端）
        //ProtocolConfigs.FUNC_CODE_ALI_PAY;//支付宝支付模块。（无收银台，直接调用支付宝发起支付，用户设备中可不安装支付宝客户端）
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_PAY);
        Matrix.invokeActivity(UnityPlayer.currentActivity, intent, new IDispatcherCallback() {
            @Override
            public void onFinished(String data) {
                if(TextUtils.isEmpty(data)) {
                    return;
                }
                boolean isCallbackParseOk = false;
                String errorMsg = null;
                String text = null;
                JSONObject jsonRes;
                try {
                    jsonRes = new JSONObject(data);
                    // error_code 状态码： 0 支付成功， -1 支付取消， 1 支付失败， -2 支付进行中。
                    // 请务必对case 0、1、-1、-2加入处理语句，如果为空会导致游戏崩溃。若应用有支付服务端，则需以360服务端通知给应用服务端的结果进行道具发放；若应用无服务端，则0、-2需发放道具；-1、1无需发放道具。
                    // error_msg 状态描述
                    int errorCode = jsonRes.optInt("error_code");
                    isCallbackParseOk = true;
                    switch (errorCode) {
                        case 0:
                            //支付成功
                            receiver.onCreateOrderSucc("OK");
                        case 1:
                            //支付失败
                            receiver.onPayUserExit("Failed");
                        case -1:
                            //支付取消
                            receiver.onPayUserExit("Cancel");
                        case -2: {
//                            isAccessTokenValid = true;
                             errorMsg = jsonRes.optString("error_msg");
                            receiver.onPayUserExit("Busy");
                        }
                        break;
                        case 4010201:
//                            isAccessTokenValid = false;
                             errorMsg = jsonRes.optString("error_msg");
                            receiver.onPayUserExit("TokenError");
                            break;
                        case 4009911:
                            //QT失效
//                            isQTValid = false;
                             errorMsg = jsonRes.optString("error_msg");
                            receiver.onPayUserExit("QTError");
                            break;
                        default:
                            receiver.onPayUserExit("NotOK");
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 用于测试数据格式是否异常。
                if (!isCallbackParseOk) {
                }
            }
        });
    }

    protected static Intent getPayIntent(JSONObject payInfo,int functionCode) throws JSONException {
        Bundle bundle = new Bundle();
        JSONObject payDatas = payInfo;
        JSONObject rechargeData = payInfo.getJSONObject("rechargeData");

        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, false);
        // *** 以下非界面相关参数 ***
        // 设置QihooPay中的参数。
        // 必需参数，360帐号id。
        bundle.putString(ProtocolKeys.QIHOO_USER_ID, payDatas.getString("qihooUid"));

        // 必需参数，所购买商品金额，以分为单位，最小金额1分
        bundle.putString(ProtocolKeys.AMOUNT, payDatas.getString("amount"));

        // 必需参数，所购买商品名称，应用指定，建议中文，最大10个中文字。
        bundle.putString(ProtocolKeys.PRODUCT_NAME, rechargeData.getString("propName"));

        // 必需参数，购买商品的商品id，应用指定，最大36中文字。
        bundle.putString(ProtocolKeys.PRODUCT_ID, rechargeData.getString("id"));

        // 必需参数，应用方提供的支付结果通知uri，最大255字符。360服务器将把支付接口回调给该uri，具体协议请查看文档中，支付结果通知接口–应用服务器提供接口。
        bundle.putString(ProtocolKeys.NOTIFY_URI, payDatas.getString("notifyUrl"));

        // 必需参数，游戏或应用名称，最大16中文字。
        bundle.putString(ProtocolKeys.APP_NAME, payDatas.getString("AppName"));

        // 必需参数，应用内的用户名，如游戏角色名。 若应用内绑定360帐号和应用帐号，则可用360用户名，最大16中文字。（充值不分区服，充到统一的用户账户，各区服角色均可使用）。
        bundle.putString(ProtocolKeys.APP_USER_NAME, payDatas.getString("ATaccount"));

        // 必需参数，应用内的用户id。
        // 若应用内绑定360帐号和应用帐号，充值不分区服，充到统一的用户账户，各区服角色均可使用，则可用360用户ID最大32字符。
        bundle.putString(ProtocolKeys.APP_USER_ID, payDatas.getString("uid"));
        //若接入3.3.1下单接口【服务端调用】，则以下两个参数必需传递，由此服务端接口返回值获得；若未接入此服务端接口，则以下两个参数无需传递。

        // 必需参数，应用订单号，应用内必须唯一，最大32字符。。应用方需要生成自己的订单号app_order_id，应用订单号不能重复提交，并且一个应用订单不管是否支付成功，都仅可支付一次，以避免重复支付。若游戏无服务端，此订单号可通过年月日时分秒+设备号等随机数生成即可。
        bundle.putString(ProtocolKeys.APP_ORDER_ID, payDatas.getString("appOrderId"));
        bundle.putString(ProtocolKeys.APP_EXT_1, payDatas.getString("callbackInfo"));
        // 必需参数，使用360SDK的支付模块:CP可以根据需求选择使用 带有收银台的支付模块 或者 直接调用微信支付模块或者直接调用支付宝支付模块。
        //functionCode 对应三种支付模块：
        //ProtocolConfigs.FUNC_CODE_PAY;//表示 带有360收银台的支付模块。
        //ProtocolConfigs.FUNC_CODE_WEIXIN_PAY;//表示 微信支付模块。
        //ProtocolConfigs.FUNC_CODE_ALI_PAY;//表示支付宝支付模块。
        bundle.putInt(ProtocolKeys.FUNCTION_CODE,functionCode);
        Intent intent = new Intent(UnityPlayer.currentActivity, ContainerActivity.class);
        intent.putExtras(bundle);
        intent.putExtra(ProtocolKeys.TOKEN_ID, payDatas.getString("tokenId"));
        intent.putExtra(ProtocolKeys.ORDER_TOKEN, payDatas.getString("orderToken"));
        return intent;
    }

    public static void logout(){
        receiver.onLogoutSucc();
    }

    //提交用户信息
    public static void saveUserInfo(String jsonStr) {
        String type = null;
        String zoneid = null;
        String zonename = null;
        String roleid = null;
        String rolename = null;
        String professionid = null;
        String profession = null;
        String gender = null;
        String professionroleid = null;
        String professionrolename = null;
        String rolelevel = null;
        String power = null;
        String vip = null;
        JSONArray balance = null;
        String partyid = null;
        String partyname = null;
        String partyroleid = null;
        String partyrolename = null;
        JSONArray friendlist = null;
        JSONArray ranking = null;

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            type = jsonObject.getString("type");
            zoneid = jsonObject.getString("zoneid");
            zonename = jsonObject.getString("zonename");
            roleid = jsonObject.getString("roleid");
            rolename = jsonObject.getString("rolename");
            professionid = jsonObject.getString("professionid");
            profession = jsonObject.getString("profession");
            gender = jsonObject.getString("gender");
            professionroleid = jsonObject.getString("professionroleid");
            professionrolename = jsonObject.getString("professionrolename");
            rolelevel = jsonObject.getString("rolelevel");
            power = jsonObject.getString("power");
            vip = jsonObject.getString("vip");
            if (jsonObject.has("balance"))
                balance = jsonObject.getJSONArray("balance");
            partyid = jsonObject.getString("partyid");
            partyname = jsonObject.getString("partyname");
            partyroleid = jsonObject.getString("partyroleid");
            partyrolename = jsonObject.getString("partyrolename");
            if (jsonObject.has("friendlist"))
                friendlist = jsonObject.getJSONArray("friendlist");
            if (jsonObject.has("ranking"))
                ranking = jsonObject.getJSONArray("ranking");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        HashMap<String, String> eventParams=new HashMap<String, String>();

        eventParams.put("type",type);  //（必填）角色状态（enterServer（登录），levelUp（升级），createRole（创建角色），exitServer（退出））
        eventParams.put("zoneid",zoneid);  //（必填）游戏区服ID
        eventParams.put("zonename",zonename);  //（必填）游戏区服名称
        eventParams.put("roleid",roleid);  //（必填）玩家角色ID
        eventParams.put("rolename",rolename);  //（必填）玩家角色名
        if (professionid == null)
            eventParams.put("professionid","0");  //（必填）职业ID
        else
            eventParams.put("professionid",professionid);  //（必填）职业ID
        if (profession == null)
            eventParams.put("profession","无");  //（必填）职业名称
        else
            eventParams.put("profession",profession);  //（必填）职业名称

        if (gender == null)
            eventParams.put("gender","无");  //（必填）性别
        else
            eventParams.put("gender",gender);  //（必填）性别

        if (professionroleid == null)
            eventParams.put("professionroleid","0");  //（选填）职业称号ID
        else
            eventParams.put("professionroleid",professionroleid);  //（选填）职业称号ID

        if (professionrolename == null)
            eventParams.put("professionrolename","无");  //（选填）职业称号
        else
            eventParams.put("professionrolename",professionrolename);  //（选填）职业称号
        eventParams.put("rolelevel",rolelevel);  //（必填）玩家角色等级
        eventParams.put("power",power);  //（必填）战力数值
        eventParams.put("vip",vip);  //（必填）当前用户VIP等级

        if (null == balance)
            eventParams.put("balance", "无");  //（必填）帐号余额
        else
            eventParams.put("balance",balance.toString());  //（必填）帐号余额

        if (null == partyid)
            eventParams.put("partyid","0");  //（必填）所属帮派帮派ID
        else
            eventParams.put("partyid",partyid);  //（必填）所属帮派帮派ID

        if (null == partyname)
            eventParams.put("partyname","无");  //（必填）所属帮派名称
        else
            eventParams.put("partyname",partyname);  //（必填）所属帮派名称

        if (null == partyroleid)
            eventParams.put("partyroleid","0");  //（必填）帮派称号ID
        else
            eventParams.put("partyroleid",partyroleid);  //（必填）帮派称号ID

        if (null == partyrolename)
            eventParams.put("partyrolename","无");  //（必填）帮派称号名称
        else
            eventParams.put("partyrolename",partyrolename);  //（必填）帮派称号名称

        if (null == friendlist)
            eventParams.put("friendlist","无");  //（必填）好友关系
        else
            eventParams.put("friendlist",friendlist.toString());  //（必填）好友关系

        if (null == ranking)
            eventParams.put("ranking","无");  //（选填）排行榜列表

        else
            eventParams.put("ranking",ranking.toString());  //（选填）排行榜列表
        //参数eventParams相关的 key、value键值对 相关具体使用说明，请参考文档。
        //----------------------------模拟数据------------------------------
        Matrix.statEventInfo(UnityPlayer.currentActivity, eventParams);
    }
}
