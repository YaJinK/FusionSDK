
using System.IO;
using UnityEditor;
using UnityEngine;
using System.Xml;
using System.Reflection;
using System;

/// <summary>
/// 注意需要先切换渠道，再切换打包方式
/// </summary>
namespace FusionSDK.Editor
{
    public class FusionSDKEditor
    {
        private static string androidManifestLib = Application.dataPath + "/FusionSDK/Configs/Manifest/";
        private static string androidGradleLib = Application.dataPath + "/FusionSDK/Configs/Gradle/";
        private static string androidResLib = Application.dataPath + "/FusionSDK/Configs/Res/";
        private static string androidAssetsLib = Application.dataPath + "/FusionSDK/Configs/AndroidAssets/";
        private static string aarLib = Application.dataPath + "/FusionSDK/ChannelLibs/";
        private static string libPrefix = "Fusion";
        private static string pluginDir = Application.dataPath + "/Plugins/Android/";

        #region 渠道切换
        [MenuItem("FusionSDK/ChannelSwith/UC")]
        public static void SwitchUCChannel()
        {
            CopyPlugins("UC");
            //FillKeyWords("");
        }

        [MenuItem("FusionSDK/ChannelSwith/HuaWei")]
        public static void SwitchHuaWeiChannel()
        {
            CopyPlugins("HuaWei");
        }

        [MenuItem("FusionSDK/ChannelSwith/XiaoMi")]
        public static void SwitchXiaoMiChannel()
        {
            CopyPlugins("Mi");
        }

        [MenuItem("FusionSDK/ChannelSwith/Vivo")]
        public static void SwitchVivoChannel()
        {
            CopyPlugins("Vivo");
        }

        [MenuItem("FusionSDK/ChannelSwith/Oppo")]
        public static void SwitchOppoChannel()
        {
            CopyPlugins("Oppo");
        }

        [MenuItem("FusionSDK/ChannelSwith/MeiZu")]
        public static void SwitchMeiZuChannel()
        {
            CopyPlugins("MeiZu");
        }

        [MenuItem("FusionSDK/ChannelSwith/Qihoo")]
        public static void SwitchQihooChannel()
        {
            CopyPlugins("Qihoo");
        }

        [MenuItem("FusionSDK/ChannelSwith/Tencent")]
        public static void SwitchTencentChannel()
        {
            CopyPlugins("Tencent");
        }
        #endregion

        #region 打包系统切换
        [MenuItem("FusionSDK/BuildSystem/Internal")]
        public static void SwitchBuildInternal()
        {
            CopyJar();

            EditorUserBuildSettings.androidBuildSystem = AndroidBuildSystem.Internal;
            if (File.Exists(pluginDir + "mainTemplate.gradle"))
                File.Delete(pluginDir + "mainTemplate.gradle");
            AssetDatabase.Refresh();
        }

        [MenuItem("FusionSDK/BuildSystem/GradleDefault")]
        public static void SwitchBuildGradleDefault()
        {
            CopyJar();
            CopyGradle("Default");
        }

        [MenuItem("FusionSDK/BuildSystem/GradleWithoutV2")]
        public static void SwitchBuildGradleWithoutV2()
        {
            CopyJar();
            CopyGradle("V1Sign");
        }

        [MenuItem("FusionSDK/BuildSystem/GradleWithDexSplit")]
        public static void SwitchBuildGradleWithDexSplit()
        {
            CopyJar("DexSplit");
            CopyGradle("DexSplit");
        }

        /// <summary>
        /// 复制gradle文件
        /// </summary>
        /// <param name="gradleName"></param>
        public static void CopyGradle(string gradleName)
        {
            EditorUserBuildSettings.androidBuildSystem = AndroidBuildSystem.Gradle;
            if (File.Exists(pluginDir + "mainTemplate.gradle"))
                File.Delete(pluginDir + "mainTemplate.gradle");

            File.Copy(androidGradleLib + gradleName + ".gradle", pluginDir + "mainTemplate.gradle");
            Debug.Log("打包模式切换成功！");
            AssetDatabase.Refresh();
        }

        #endregion
        #region 清除sdk
        [MenuItem("FusionSDK/ClearSDK")]
        public static void ClearSDK()
        {
            DOClearSDK();
        }
        #endregion
        /// <summary>
        /// 调用后打包方式会切回Internal
        /// </summary>
        /// <param name="channelKey"></param>
        private static void CopyPlugins(string channelKey)
        {

            MergeManifest(channelKey);

            SwitchBuildInternal();
            CopyAar(channelKey);
            CopyRes(channelKey);
            CopyAssets(channelKey);
            string define = PlayerSettings.GetScriptingDefineSymbolsForGroup(BuildTargetGroup.Android);

            int channelDefineBegin = define.IndexOf("FUSIONSDK_");
            if (channelDefineBegin != -1)
            {
                string tempStr = define.Substring(channelDefineBegin);
                int channelDefineEnd = tempStr.IndexOf(";");
                string channelDefine = "";
                if (channelDefineEnd == -1)
                    channelDefine = tempStr;
                else
                    channelDefine = tempStr.Substring(0, channelDefineEnd + 1);
                define = define.Replace(channelDefine, "");
            }

            string symbols = "FUSIONSDK_" + channelKey.ToUpper();
            if (!define.Contains(symbols))
            {
                define += ";" + symbols;
            }
            PlayerSettings.SetScriptingDefineSymbolsForGroup(BuildTargetGroup.Android, define);
            Debug.Log(channelKey + "渠道切换成功！");
            AssetDatabase.Refresh();
        }

        /// <summary>
        /// 融合manifest文件
        /// </summary>
        /// <param name="channelKey"></param>
        private static void MergeManifest(string channelKey)
        {
            XmlDocument mainManifest = new XmlDocument();
            mainManifest.Load(androidManifestLib + "AndroidManifest.xml");
            XmlNode mainApplicationNode = mainManifest.SelectSingleNode("/manifest/application");

            XmlDocument channelManifest = new XmlDocument();
            channelManifest.Load(androidManifestLib + channelKey + " /AndroidManifest.xml");
            XmlNode channelApplicationNode = channelManifest.SelectSingleNode("/manifest/application");
            XmlNodeList children = channelApplicationNode.ChildNodes;
            for (int index = 0; index < children.Count; index++)
            {
                XmlNode tempNode = mainManifest.ImportNode(children[index], true);
                mainApplicationNode.AppendChild(tempNode);
            }

            mainManifest.Save(pluginDir + "AndroidManifest.xml");
        }

        /// <summary>
        /// 复制jar文件
        /// </summary>
        /// <param name="prefix"></param>
        private static void CopyJar(string prefix = "")
        {
            if (!Directory.Exists(pluginDir))
                Directory.CreateDirectory(pluginDir);

            DirectoryInfo pluginDirInfo = new DirectoryInfo(pluginDir);
            FileInfo[] pluginDirFileInfos = pluginDirInfo.GetFiles();
            foreach (var i in pluginDirFileInfos)
            {
                if (i.Name.Length > libPrefix.Length)
                {
                    string currentPrefix = i.Name.Substring(0, 6);
                    string surfix = i.Name.Substring(i.Name.Length - 3, 3);
                    if (libPrefix.Equals(currentPrefix) && surfix.Equals("jar"))
                        File.Delete(i.FullName);
                }
            }

            if (!File.Exists(pluginDir + libPrefix + prefix + "-SDK.jar"))
                File.Copy(aarLib + libPrefix + prefix + "-SDK.jarlib", pluginDir + libPrefix + prefix + "-SDK.jar");
        }

        /// <summary>
        /// 复制aar文件
        /// </summary>
        /// <param name="channelKey"></param>
        private static void CopyAar(string channelKey)
        {
            if (!Directory.Exists(pluginDir))
                Directory.CreateDirectory(pluginDir);

            DirectoryInfo info = new DirectoryInfo(pluginDir);
            FileInfo[] fileInfos = info.GetFiles();
            foreach (var i in fileInfos)
            {
                if (i.Name.Length > libPrefix.Length)
                {
                    string currentPrefix = i.Name.Substring(0, 6);
                    string surfix = i.Name.Substring(i.Name.Length - 3, 3);
                    if (libPrefix.Equals(currentPrefix) && surfix.Equals("aar"))
                        File.Delete(i.FullName);
                }
            }
            if (!File.Exists(pluginDir + libPrefix + channelKey + "-SDK.aar"))
                File.Copy(aarLib + libPrefix + channelKey + "-SDK.aarlib", pluginDir + libPrefix + channelKey + "-SDK.aar");
        }

        /// <summary>
        /// 复制res文件夹
        /// </summary>
        /// <param name="channelKey"></param>
        private static void CopyRes(string channelKey)
        {
            if (!Directory.Exists(pluginDir))
                Directory.CreateDirectory(pluginDir);

            if (Directory.Exists(pluginDir + "res"))
                Directory.Delete(pluginDir + "res", true);

            if (!Directory.Exists(androidResLib + channelKey))
                return;

            CopyDir(androidResLib + channelKey, pluginDir);
        }

        /// <summary>
        /// 复制assets文件夹
        /// </summary>
        /// <param name="channelKey"></param>
        private static void CopyAssets(string channelKey)
        {
            if (!Directory.Exists(pluginDir))
                Directory.CreateDirectory(pluginDir);

            if (Directory.Exists(pluginDir + "assets"))
                Directory.Delete(pluginDir + "assets", true);

            if (!Directory.Exists(androidAssetsLib + channelKey))
                return;

            CopyDir(androidAssetsLib + channelKey, pluginDir);
        }

        /// <summary>
        /// 复制文件夹下的所有文件
        /// </summary>
        /// <param name="path"></param>
        /// <param name="desPath"></param>
        private static void CopyDir(string path, string desPath)
        {
            DirectoryInfo info = new DirectoryInfo(path);
            FileInfo[] fileInfos = info.GetFiles();
            DirectoryInfo[] dirInfos = info.GetDirectories();

            foreach (var i in fileInfos)
            {
                if (!".meta".Equals(i.Name.Substring(i.Name.Length - 1 - 4, 5)))
                    File.Copy(i.FullName, desPath + i.Name);

            }

            foreach (var i in dirInfos)
            {
                if (!Directory.Exists(desPath + i.Name + "/"))
                    Directory.CreateDirectory(desPath + i.Name + "/");
                CopyDir(i.FullName, desPath + i.Name + "/");
            }
        }

        /// <summary>
        /// 清除渠道sdk相关aar
        /// 打包方式恢复默认Internal
        /// </summary>
        private static void DOClearSDK()
        {
            SwitchBuildInternal();
            DirectoryInfo info = new DirectoryInfo(pluginDir);
            FileInfo[] fileInfos = info.GetFiles();
            foreach (var i in fileInfos)
            {
                if (i.Name.Length > libPrefix.Length)
                {
                    string currentPrefix = i.Name.Substring(0, 6);
                    string surfix = i.Name.Substring(i.Name.Length - 3, 3);
                    if (libPrefix.Equals(currentPrefix) && surfix.Equals("aar"))
                        File.Delete(i.FullName);
                }
            }

            if (Directory.Exists(pluginDir + "res"))
                Directory.Delete(pluginDir + "res", true);

            if (Directory.Exists(pluginDir + "assets"))
                Directory.Delete(pluginDir + "assets", true);

            if (File.Exists(pluginDir + "AndroidManifest.xml"))
                File.Delete(pluginDir + "AndroidManifest.xml");

            File.Copy(androidManifestLib + "AndroidManifest.xml", pluginDir + "AndroidManifest.xml");
            AssetDatabase.Refresh();
            Debug.Log("渠道文件清除成功！");
        }

        public static void FillKeyWords(string text)
        {
            //Type type = Type.GetType("FusionChannelInfo");
            //foreach (var i in type.GetFields())
            //{
            //    text = text.Replace()

            //    Debug.LogError(i.Name);
            //}
        }
    }
}


