using UnityEngine;
using FusionSDK.Core;
using LitJson;

public class SDKTest : MonoBehaviour
{

    public void Init()
    {
        Fusion.Init();
    }

    public void Login()
    {
        Fusion.Login();
    }

    public void Logout()
    {
        Fusion.Logout();
    }

    public void SubmitRoleData()
    {
        string jsonStr = "";
        JsonData jsonData = new JsonData();
        jsonData["zoneId"] = "1";
        jsonData["zoneName"] = "大侠客栈";
        jsonData["roleId"] = "1-1";
        jsonData["roleName"] = "李逍遥";
        jsonData["roleLevel"] = 1;
        jsonData["roleCreateTime"] = "5497223";
        jsonData["sociatyId"] = "10";
        jsonData["sociaty"] = "FT";
        jsonData["vip"] = "11";
        jsonData["type"] = "enterServer";
        jsonStr = JsonMapper.ToJson(jsonData);
        Fusion.SubmitRoleData(jsonStr);
    }

    public void Pay()
    {
        //string jsonStr = "";
        //Fusion.Pay(jsonStr);
    }

    public void GetCertificationInfo()
    {
        Fusion.GetCertificationInfo();
    }

    public void CheckMissingOrder()
    {
        Fusion.CheckMissingOrder();
    }

    public void Exit()
    {
        Fusion.Exit();
    }
}
