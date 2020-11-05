using LitJson;
using UnityEngine;
namespace FusionSDK.Core
{ 
    /// <summary>
    /// 用来接收Android端的回调
    /// </summary>
    public class FusionCallback : MonoBehaviour {
        void Awake()
        {
            DontDestroyOnLoad(gameObject);
            Instance = this;
        }

        public static FusionCallback Instance { get; private set; }

        public delegate void CallBackFunction();
        public delegate void CallBackFunctionString(string msg);

        public CallBackFunction onInitSuccHandle;
        public CallBackFunctionString onInitFailedHandle;
        public CallBackFunctionString onLoginSuccHandle;
        public CallBackFunctionString onLoginFailedHandle;
        public CallBackFunction onLogoutSuccHandle;
        public CallBackFunctionString onLogoutFailedHandle;
        public CallBackFunctionString onCreateOrderSuccHandle;
        public CallBackFunctionString onPayUserExitHandle;
        public CallBackFunctionString onGetUserInfoHandle;
        public CallBackFunction onExitSDKSuccHandle;
        public CallBackFunctionString onGetCertificationInfoSuccHandle;
        public CallBackFunctionString onGetCertificationInfoFailedHandle;

        /// <summary>
        /// 进入游戏调用登录
        /// </summary>
        public void onInitSucc()
        {
            log("初始化成功");
            if (null != onInitSuccHandle)
            {
                onInitSuccHandle();
            }
        }

        /// <summary>
        /// 初始化失败游戏
        /// </summary>
        /// <param name="msg">Message.</param>
        public void onInitFailed(string msg)
        {
            log("初始化失败：" + msg);
            if (null != onInitFailedHandle)
            {
                onInitFailedHandle(msg);
            }
        }

        /// <summary>
        /// 登录成功
        /// </summary>
        /// <param name="sid">Sid.</param>
        public void onLoginSucc(string sid)
        {
            log("账号登录成功 sid:" + sid);

            if (null != onLoginSuccHandle)
            {
                onLoginSuccHandle(sid);
            }
        }

        /// <summary>
        /// 登录界面退出，返回到游戏画面
        /// </summary>
        /// <param name="msg">Message.</param>
        public void onLoginFailed(string msg)
        {
            log("账号登录失败：" + msg);

            if (null != onLoginFailedHandle)
            {
                onLoginFailedHandle(msg);
            }
        }

        /// <summary>
        /// 当前登录用户已退出，应将游戏切换到未登录的状态。
        /// </summary>
        public void onLogoutSucc()
        {
            log("账号退出成功");

            if (null != onLogoutSuccHandle)
            {
                onLogoutSuccHandle();
            }
        }

        /// <summary>
        /// 登录失败
        /// </summary>
        /// <param name="msg">Message.</param>
        public void onLogoutFailed(string msg)
        {
            log("账号退出失败：" + msg);

            if (null != onLogoutFailedHandle)
            {
                onLogoutFailedHandle(msg);
            }
        }

        /// <summary>
        /// 退出游戏成功
        /// </summary>
        public void onExitSucc()
        {
            log("退出游戏");
#if FUSIONSDK_HUAWEI || FUSIONSDK_TENCENT
            if (null != onExitSDKSuccHandle)
                onExitSDKSuccHandle();
            else
                Application.Quit();
#else
            Application.Quit();
#endif
        }

        /// <summary>
        /// 用户取消退出游戏
        /// </summary>
        /// <param name="msg">Message.</param>
        public void onExitCanceled(string msg)
        {
            log("退出游戏失败：" + msg);
        }

        /// <summary>
        /// 创建订单成功
        /// </summary>
        /// <param name="orderInfo">Order info.</param>
        public void onCreateOrderSucc(string orderInfo)
        {
            log(orderInfo);

            if (null != onCreateOrderSuccHandle)
            {
                onCreateOrderSuccHandle(orderInfo);
            }
        }

        /// <summary>
        /// 用户取消订单支付
        /// </summary>
        /// <param name="orderInfo">Order info.</param>
        public void onPayUserExit(string orderInfo)
        {
            log(orderInfo);

            if (null != onPayUserExitHandle)
            {
                onPayUserExitHandle(orderInfo);
            }
        }

        /// <summary>
        /// 指令执行成功
        /// </summary>
        /// <param name="msg">Message.</param>
        public void onExecuteSucc(string msg)
        {
            log("指令执行成功：" + msg);
        }

        /// <summary>
        /// 指令执行失败
        /// </summary>
        /// <param name="msg">Message.</param>
        public void onExecuteFailed(string msg)
        {
            log("指令执行失败：" + msg);
        }

        /// <summary>
        /// 打开指定页面回调
        /// </summary>
        /// <param name="msg">Message.</param>
        public void onShowPageResult(string msg)
        {
            JsonData json = JsonMapper.ToObject(msg);
            string business = (string)json["business"];
            string result = (string)json["result"];
            log("打开指定页面回调：" + result);
        }

        /// <summary>
        /// 上传角色信息成功
        /// </summary>
        /// <param name="msg"></param>
        public void onSaveUserInfoSucc()
        {
            log("上传角色信息成功！");
        }

        /// <summary>
        /// 上传角色信息失败
        /// </summary>
        /// <param name="msg"></param>
        public void onSaveUserInfoSucc(string msg)
        {
            log("上传角色信息失败：" + msg);
        }

        /// <summary>
        /// 获取防沉迷信息成功
        /// </summary>
        /// <param name="info"></param>
        public void onGetCertificationInfoSucc(string info)
        {
            log("防沉迷信息：" + info);
            if (onGetCertificationInfoSuccHandle != null)
            {
                onGetCertificationInfoSuccHandle(info);
            }
        }

        /// <summary>
        /// 获取防沉迷信息失败
        /// </summary>
        /// <param name="info"></param>
        public void onGetCertificationInfoFailed(string msg)
        {
            log("获取防沉迷失败：" + msg);
            if (onGetCertificationInfoFailedHandle != null)
            {
                onGetCertificationInfoFailedHandle(msg);
            }
        }

        public void onGetUserInfoSucc(string msg)
        {
            if (null != onGetUserInfoHandle)
            {
                onGetUserInfoHandle(msg);
            }
        }

        private void log(string msg)
        {
            Debug.Log(msg);
        }


        ///
        /// QQ渠道与ShareSDK兼容处理  如未接入ShareSDK  可不处理
        ///
        public CallBackFunctionString onShareCompleteHandle;
        public CallBackFunctionString onShareErrorHandle;
        public CallBackFunction onShareCancelHandle;

        public void OnShareComplete(string msg)
        {
            if (null != onShareCompleteHandle)
            {
                onShareCompleteHandle(msg);
            }
        }
        public void OnShareError(string msg)
        {
            if (null != onShareErrorHandle)
            {
                onShareErrorHandle(msg);
            }
        }
        public void OnShareCancel()
        {
            if (null != onShareCancelHandle)
            {
                onShareCancelHandle();
            }
        }

        /// 兼容处理结束

    }
}