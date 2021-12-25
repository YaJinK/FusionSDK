using LitJson;
using System.Collections.Generic;
using UnityEngine;

namespace FusionSDK.Core
{
    public class Fusion
    {
        private static AndroidJavaClass sdkClass = null;

        /// <summary>
        /// SDK初始化
        /// </summary>
        /// <param name="jsonString"></param>
        public static void Init()
        {
#if FUSIONSDK_UC
            sdkClass = new AndroidJavaClass("com.magata.ucsdk.UCGameSdk");
            sdkClass.CallStatic("initSDK");
#elif FUSIONSDK_HUAWEI
            sdkClass = new AndroidJavaClass("com.magata.huaweisdk.HuaWeiGameSDK");
            sdkClass.CallStatic("initSDK");
#elif FUSIONSDK_MI
            sdkClass = new AndroidJavaClass("com.magata.misdk.XiaoMiGameSDK");
            sdkClass.CallStatic("initSDK");
#elif FUSIONSDK_VIVO
            sdkClass = new AndroidJavaClass("com.magata.vivosdk.VivoGameSDK");
            sdkClass.CallStatic("initSDK");
#elif FUSIONSDK_OPPO
            sdkClass = new AndroidJavaClass("com.magata.opposdk.OppoGameSDK");
            sdkClass.CallStatic("initSDK");
#elif FUSIONSDK_MEIZU
            sdkClass = new AndroidJavaClass("com.magata.meizusdk.MeiZuGameSDK");
            sdkClass.CallStatic("initSDK");
#elif FUSIONSDK_QIHOO
            sdkClass = new AndroidJavaClass("com.magata.qihoosdk.QihooGameSDK");
            sdkClass.CallStatic("initSDK");
#elif FUSIONSDK_TENCENT
            sdkClass = new AndroidJavaClass("com.magata.tencentsdk.TencentGameSDK");
            sdkClass.CallStatic("initSDK");
#endif
        }

        /// <summary>
        /// 用户登录
        /// </summary>
        public static void Login()
        {
            if (null == sdkClass)
                return;
#if FUSIONSDK_UC
            sdkClass.CallStatic("login");
#elif FUSIONSDK_HUAWEI
            sdkClass.CallStatic("login");
#elif FUSIONSDK_MI
            sdkClass.CallStatic("login");
#elif FUSIONSDK_VIVO
            sdkClass.CallStatic("login");
#elif FUSIONSDK_OPPO
            sdkClass.CallStatic("login");
#elif FUSIONSDK_MEIZU
            sdkClass.CallStatic("login");
#elif FUSIONSDK_QIHOO
            sdkClass.CallStatic("login");
#elif FUSIONSDK_TENCENT
            sdkClass.CallStatic("login");
#endif
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="type">1 QQ 2 微信</param>
        public static void LoginByType(int type)
        {
#if FUSIONSDK_TENCENT
            sdkClass.CallStatic("login", type);
#else
            Debug.LogWarning("该渠道不区分登录方式，调用普通登录方法。");
            sdkClass.CallStatic("login");
#endif
        }
        /// <summary>
        /// 自动登录方法
        /// </summary>
        public static void AutoLogin()
        {
#if FUSIONSDK_TENCENT
            sdkClass.CallStatic("getLoginInfo");
#else
            Debug.LogWarning("该渠道无自动登录，调用普通登录方法。");
            sdkClass.CallStatic("login");
#endif
        }

        /// <summary>
        /// 用户登出
        /// </summary>
        public static void Logout()
        {
            if (null == sdkClass)
                return;
#if FUSIONSDK_UC
            sdkClass.CallStatic("logout");
#elif FUSIONSDK_HUAWEI
            sdkClass.CallStatic("logout");
#elif FUSIONSDK_MI
            sdkClass.CallStatic("logout");
#elif FUSIONSDK_VIVO
            sdkClass.CallStatic("logout");
#elif FUSIONSDK_OPPO
            sdkClass.CallStatic("logout");
#elif FUSIONSDK_MEIZU
            sdkClass.CallStatic("logout");
#elif FUSIONSDK_QIHOO
            sdkClass.CallStatic("logout");
#elif FUSIONSDK_TENCENT
            sdkClass.CallStatic("logout");
#endif
        }

        /// <summary>
        /// 上传角色信息
        /// 调用时机：玩家登陆、创建角色、升级、改名、退出
        /// 
        /// 必填：
        /// 1.zoneId:区服ID
        /// 2.zoneName:区服名称
        /// 3.roleId:角色id
        /// 4.roleName:角色名称
        /// 5.roleLevel:角色等级
        /// 6.roleCreateTime:角色创建时间
        /// 7.sociatyId:公会id
        /// 8.sociaty:公会名称
        /// 9.vip:vip等级
        /// 10.type:时机类型，enterServer（登录），levelUp（升级），createRole（创建角色），exitServer（退出）
        /// 可选：
        /// 11、chapter：关卡章节
        /// 12、power：战力
        /// 13、point：积分
        /// 14、professionId：职业id
        /// 15、professionName：职业名称
        /// 16、gender：性别
        /// 17、professionRoleId：职业称号id
        /// 18、professionRoleName：职业称号名称
        /// 19、balance：库存余额
        /// 20、partyRoleId：公会职位id
        /// 21、friendList：好友列表
        /// 22、ranking：排行榜
        /// </summary>
        /// <param name="jsonString"></param>
        public static void SubmitRoleData(string jsonString)
        {
            if (null == sdkClass)
                return;
            JsonData jsonData = JsonMapper.ToObject(jsonString);

            string typeG = jsonData["type"].ToString(); // enterServer（登录），levelUp（升级），createRole（创建角色），exitServer（退出）
            string zoneIdG = jsonData["zoneId"].ToString();
            string zoneNameG = jsonData["zoneName"].ToString();
            string roleIdG = jsonData["roleId"].ToString();
            string roleNameG = jsonData["roleName"].ToString();
            string levelG = jsonData["roleLevel"].ToString();
            string roleCTimeG = jsonData["roleCreateTime"].ToString();
            string sociatyIdG = jsonData["sociatyId"].ToString();
            string sociatyG = jsonData["sociaty"].ToString();
            string vipG = jsonData["vip"].ToString();


            string currentChapterG = "0";
            try
            {
                if (jsonData.ContainsKey("chapter"))
                    currentChapterG = jsonData["chapter"].ToString(); // 关卡章节           

            }
            catch (KeyNotFoundException e)
            {
                Debug.Log("Key not found: chapter");
            }

            string powerG = "0";
            try
            {
                if (jsonData.ContainsKey("power"))
                    powerG = jsonData["power"].ToString(); // 战力
            }
            catch (KeyNotFoundException e)
            {
                Debug.Log("Key not found: power");
            }

            string pointG = "0";
            try
            {
                if (jsonData.ContainsKey("point"))
                    pointG = jsonData["point"].ToString();  // 积分
            }
            catch (KeyNotFoundException e)
            {
                Debug.Log("Key not found: point");
            }

            string professionIdG = null;
            try
            {
                if (jsonData.ContainsKey("professionId"))
                    professionIdG = jsonData["professionId"].ToString();  // 职业id
            }
            catch (KeyNotFoundException e)
            {
                Debug.Log("Key not found: professionId");
            }

            string professionNameG = null;
            try
            {
                if (jsonData.ContainsKey("professionName"))
                    professionNameG = jsonData["professionName"].ToString();  // 职业名称
            }
            catch (KeyNotFoundException e)
            {
                Debug.Log("Key not found: professionName");
            }

            string genderG = null;
            try
            {
                if (jsonData.ContainsKey("gender"))
                    genderG = jsonData["gender"].ToString();  // 性别
            }
            catch (KeyNotFoundException e)
            {
                Debug.Log("Key not found: gender");
            }
            string professionRoleIdG = null;
            try
            {
                if (jsonData.ContainsKey("professionRoleId"))
                    professionRoleIdG = jsonData["professionRoleId"].ToString();  // 职业称号id
            }
            catch (KeyNotFoundException e)
            {
                Debug.Log("Key not found: professionRoleId");
            }
            string professionRoleNameG = null;
            try
            {
                if (jsonData.ContainsKey("professionRoleName"))
                    professionRoleNameG = jsonData["professionRoleName"].ToString();  // 职业称号名称
            }
            catch (KeyNotFoundException e)
            {
                Debug.Log("Key not found: professionRoleName");
            }
            JsonData balanceG = null;
            try
            {
                if (jsonData.ContainsKey("balance"))
                    balanceG = jsonData["balance"];  // [{“balanceid”:”1”,”balancename":"\u91d1\u5e01","balancenum":"600"},{"balanceid":"1","balancename":"\u91d1\u5e01","balancenum":"600"}]
            }
            catch (KeyNotFoundException e)
            {
                Debug.Log("Key not found: balance");
            }
            string sociatyRoleIdG = null;
            try
            {
                if (jsonData.ContainsKey("partyRoleId"))
                    sociatyRoleIdG = jsonData["partyRoleId"].ToString(); // 公会职位id
            }
            catch (KeyNotFoundException e)
            {
                Debug.Log("Key not found: partyRoleId");
            }
            string sociatyRoleNameG = null;
            try
            {
                if (jsonData.ContainsKey("partyRoleName"))
                    sociatyRoleNameG = jsonData["partyRoleName"].ToString(); // 公会职位名称
            }
            catch (KeyNotFoundException e)
            {
                Debug.Log("Key not found: partyRoleName");
            }
            JsonData friendListG = null;
            try
            {
                if (jsonData.ContainsKey("friendList"))
                    friendListG = jsonData["friendList"]; // [{“roleid”:”1”,"intimacy":"0","nexusid":"600","nexusname":"情侣"},{"roleid":"2","intimacy":"0","nexusid":"200","nexusname":"仇人"}]
            }
            catch (KeyNotFoundException e)
            {
                Debug.Log("Key not found: friendList");
            }
            JsonData rankingG = null;
            try
            {
                if (jsonData.ContainsKey("ranking"))
                    rankingG = jsonData["ranking"];  // [{“listid”:”1”,"listname":"\u91d1\u5e01","num":"600","coin":"XX","cost":"XX"},{"listid":"1","listname":"\u91d1\u5e01","num":"600","coin":"XX","cost":"XX"}]
            }
            catch (KeyNotFoundException e)
            {
                Debug.Log("Key not found: ranking");
            }
#if FUSIONSDK_UC
            string zoneId = zoneIdG;
            string zoneName = zoneNameG;
            string roleId = roleIdG;
            string roleName = roleNameG;
            long roleLevel = int.Parse(levelG);
            long roleCTime = int.Parse(roleCTimeG);
            sdkClass.CallStatic("submitRoleData", zoneId, zoneName, roleId, roleName, roleLevel, roleCTime);
#elif FUSIONSDK_HUAWEI
            string server = zoneNameG;
            string level = levelG;
            string name = roleNameG;
            string sociaty = sociatyG;
            sdkClass.CallStatic("saveUserInfo", server, level, name, sociaty);
#elif FUSIONSDK_MI
            string roleId = roleIdG;
            string name = roleNameG;
            string level = levelG;
            string serverId = zoneIdG;
            string serverName = zoneNameG;
            string zoneId = zoneIdG;
            string zoneName = zoneNameG;
            sdkClass.CallStatic("saveUserInfo", roleId, name, level, serverId, serverName, zoneId, zoneName);
#elif FUSIONSDK_VIVO
            string roleId = roleIdG;
            string name = roleNameG;
            string level = levelG;
            string serverId = zoneIdG;
            string serverName = zoneNameG;
            sdkClass.CallStatic("saveUserInfo", roleId, level, name, serverId, serverName);
#elif FUSIONSDK_OPPO
            string roleId = roleIdG;
            string roleName = roleNameG;
            int roleLevel = int.Parse(levelG);
            string serverId = zoneIdG;
            string serverName = zoneNameG;
            string chapter = currentChapterG;
            long combatValue = int.Parse(powerG);
            long pointValue = int.Parse(pointG);
            sdkClass.CallStatic("saveUserInfo", roleId, roleName, roleLevel, serverId, serverName, chapter, combatValue, pointValue);
#elif FUSIONSDK_MEIZU
            string roleId = roleIdG;
            string name = roleNameG;
            int level = int.Parse(levelG);
            string serverId = zoneIdG;
            sdkClass.CallStatic("saveUserInfo", roleId, name, level, serverId);
#elif FUSIONSDK_QIHOO
            JsonData qihooUserJsonData = new JsonData();
            qihooUserJsonData["type"] = typeG;
            qihooUserJsonData["zoneid"] = zoneIdG;
            qihooUserJsonData["zonename"] = zoneNameG;
            qihooUserJsonData["roleid"] = roleIdG;
            qihooUserJsonData["rolename"] = roleNameG;
            qihooUserJsonData["professionid"] = professionIdG;
            qihooUserJsonData["profession"] = professionNameG;
            qihooUserJsonData["gender"] = genderG;
            qihooUserJsonData["professionroleid"] = professionRoleIdG;
            qihooUserJsonData["professionrolename"] = professionRoleNameG;
            qihooUserJsonData["rolelevel"] = levelG;
            qihooUserJsonData["power"] = powerG;
            qihooUserJsonData["vip"] = powerG;
            if (null != balanceG)
                qihooUserJsonData["balance"] = balanceG;
            qihooUserJsonData["partyid"] = sociatyIdG;
            qihooUserJsonData["partyname"] = sociatyG;
            qihooUserJsonData["partyroleid"] = sociatyRoleIdG;
            qihooUserJsonData["partyrolename"] = sociatyRoleNameG;
            if (null != friendListG)
                qihooUserJsonData["friendlist"] = friendListG;
            if (null != rankingG)
                qihooUserJsonData["ranking"] = rankingG;
            string qihooUserJsonStr = JsonMapper.ToJson(qihooUserJsonData);
            sdkClass.CallStatic("saveUserInfo", qihooUserJsonStr);
#elif FUSIONSDK_TENCENT
            Debug.Log("YSDK无上传角色接口");
#endif
        }

        /// <summary>
        /// 用户支付
        /// </summary>
        /// <param name="jsonString"></param>
        public static void Pay(string jsonString)
        {
            if (null == sdkClass)
                return;
#if FUSIONSDK_UC
            JsonData jsonData = JsonMapper.ToObject(jsonString);
            string accountId = jsonData["accountId"].ToString();
            string cpOrderId = jsonData["cpOrderId"].ToString();
            string amount = jsonData["amount"].ToString();
            string callbackInfo = jsonData["callbackInfo"].ToString();
            string notifyUrl = jsonData["notifyUrl"].ToString();
            string signType = jsonData["signType"].ToString();
            string sign = jsonData["sign"].ToString();
            sdkClass.CallStatic("pay", accountId, cpOrderId, amount, callbackInfo, notifyUrl, signType, sign);
#elif FUSIONSDK_HUAWEI
            JsonData jsonData = JsonMapper.ToObject(jsonString);
            string productId = jsonData["productId"].ToString();
            int productType = (int)jsonData["productType"];
            string callbackInfo = jsonData["callbackInfo"].ToString();
            sdkClass.CallStatic("pay", productId, productType, callbackInfo);
#elif FUSIONSDK_MI
            JsonData jsonData = JsonMapper.ToObject(jsonString);
            string cpOrderId = jsonData["cpOrderId"].ToString();
            string productCode = jsonData["productCode"].ToString();
            string cpUserInfo = jsonData["callbackInfo"].ToString();
            int num = (int)jsonData["num"];
            string balance = jsonData["diamond"].ToString();
            string vip = jsonData["vip"].ToString();
            string level = jsonData["level"].ToString();
            string sociaty = jsonData["sociaty"].ToString();
            string name = jsonData["ATaccount"].ToString();
            string uid = jsonData["uid"].ToString();
            string server = jsonData["serverId"].ToString();
            sdkClass.CallStatic("pay", cpOrderId, productCode, num, cpUserInfo, balance, vip, level, sociaty, name, uid, server);
#elif FUSIONSDK_VIVO
            sdkClass.CallStatic("pay", jsonString);
#elif FUSIONSDK_OPPO
            JsonData jsonData = JsonMapper.ToObject(jsonString);
            string orderId = jsonData["order"].ToString();
            int amount = int.Parse(jsonData["amount"].ToString());
            string notifyUrl = jsonData["callbackUrl"].ToString();
            string attach = jsonData["callbackInfo"].ToString();
            string productName = jsonData["productName"].ToString();
            string productDesc = jsonData["productDesc"].ToString();
            sdkClass.CallStatic("pay", orderId, amount, notifyUrl, attach, productName, productDesc);
#elif FUSIONSDK_MEIZU
            sdkClass.CallStatic("pay", jsonString);
#elif FUSIONSDK_QIHOO
            sdkClass.CallStatic("pay", jsonString);
#elif FUSIONSDK_TENCENT
            JsonData jsonData = JsonMapper.ToObject(jsonString);
            string zoneId = jsonData["zoneId"].ToString();
            string saveValue = jsonData["saveValue"].ToString();
            bool isCanChange = (bool)jsonData["isCanChange"];
            string ysdkExt = jsonData["ysdkExt"].ToString();
            byte[] iconByte = null;
            //string iconPath = jsonData["iconPath"].ToString();
            //if (!"null".Equals(iconPath))
            //    iconByte = AppFacade.Instance.ResMgr.LoadSprite(iconPath).texture.EncodeToPNG();
            sdkClass.CallStatic("pay", zoneId, saveValue, isCanChange, iconByte, ysdkExt);
#endif
        }

        /// <summary>
        /// 退出sdk
        /// 游戏退出时调用
        /// </summary>
        public static void Exit()
        {
            if (null == sdkClass)
                return;
#if FUSIONSDK_UC
            sdkClass.CallStatic("exitSDK");
#elif FUSIONSDK_HUAWEI
            sdkClass.CallStatic("exitSDK");
#elif FUSIONSDK_MI
            sdkClass.CallStatic("exitSDK");
#elif FUSIONSDK_VIVO
            sdkClass.CallStatic("exitSDK");
#elif FUSIONSDK_OPPO
            sdkClass.CallStatic("exitSDK");
#elif FUSIONSDK_MEIZU
            sdkClass.CallStatic("exitSDK");
#elif FUSIONSDK_QIHOO
            sdkClass.CallStatic("exitSDK");
#elif FUSIONSDK_TENCENT
            sdkClass.CallStatic("exitSDK");
#endif
        }
        public static void GetCertificationInfo()
        {
            if (null == sdkClass)
                return;
#if FUSIONSDK_UC
            Debug.LogWarning("该渠道客户端无防沉迷接口。");
            FusionCallback.Instance.onGetCertificationInfoFailed("该渠道客户端无防沉迷接口。");
#elif FUSIONSDK_HUAWEI
            sdkClass.CallStatic("getCertificationInfo");
#elif FUSIONSDK_MI
            Debug.LogWarning("该渠道客户端无防沉迷接口。");
            FusionCallback.Instance.onGetCertificationInfoFailed("该渠道客户端无防沉迷接口。");
#elif FUSIONSDK_VIVO
            sdkClass.CallStatic("getCertificationInfo");
#elif FUSIONSDK_OPPO
            sdkClass.CallStatic("getCertificationInfo");
#elif FUSIONSDK_MEIZU
            sdkClass.CallStatic("getCertificationInfo");
#elif FUSIONSDK_QIHOO
            Debug.LogWarning("该渠道客户端无防沉迷接口。"); // 有服务器接口
            FusionCallback.Instance.onGetCertificationInfoFailed("该渠道客户端无防沉迷接口。");
#elif FUSIONSDK_TENCENT
            Debug.LogWarning("该渠道客户端无防沉迷接口。");
            FusionCallback.Instance.onGetCertificationInfoFailed("该渠道客户端无防沉迷接口。");
#endif
        }

        /// <summary>
        /// 支付完成后上报完成
        /// </summary>
        /// <param name="json">掉单的getTransNo  json数组</param>
        /// <param name="isReOrder">普通支付后的通知是false，掉单补单为true</param>
        public static void ReportOrderComplete(string json, bool isReOrder)
        {
            if (null == sdkClass)
                return;
#if FUSIONSDK_UC
            Debug.LogWarning("该渠道无上报订单完成接口。");
#elif FUSIONSDK_HUAWEI
            Debug.LogWarning("该渠道无上报订单完成接口。");
#elif FUSIONSDK_MI
            Debug.LogWarning("该渠道无上报订单完成接口。");
#elif FUSIONSDK_VIVO
            sdkClass.CallStatic("reportOrderComplete", json, isReOrder);
#elif FUSIONSDK_OPPO
            Debug.LogWarning("该渠道无上报订单完成接口。");
#elif FUSIONSDK_MEIZU
            Debug.LogWarning("该渠道无上报订单完成接口。");
#elif FUSIONSDK_QIHOO
            Debug.LogWarning("该渠道无上报订单完成接口。");
#elif FUSIONSDK_TENCENT
            Debug.LogWarning("该渠道无上报订单完成接口。");
#endif
        }

        /// <summary>
        /// 检查掉单
        /// </summary>
        public static void CheckMissingOrder()
        {
            if (null == sdkClass)
                return;
#if FUSIONSDK_UC
            Debug.LogWarning("该渠道无需检查掉单。");
#elif FUSIONSDK_HUAWEI
            // 0 消耗型 1 非消耗 2 订阅
            sdkClass.CallStatic("checkMissingOrder", 0);
#elif FUSIONSDK_MI
            Debug.LogWarning("该渠道无需检查掉单。");
#elif FUSIONSDK_VIVO
            Debug.LogWarning("该渠道无需检查掉单。");
#elif FUSIONSDK_OPPO
            Debug.LogWarning("该渠道无需检查掉单。");
#elif FUSIONSDK_MEIZU
            Debug.LogWarning("该渠道无需检查掉单。");
#elif FUSIONSDK_QIHOO
            Debug.LogWarning("该渠道无需检查掉单。");
#elif FUSIONSDK_TENCENT
            Debug.LogWarning("该渠道无需检查掉单。");
#endif
        }
    }
}

