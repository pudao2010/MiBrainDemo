package com.xiaomi.mibrainsdkdemo;

import com.xiaomi.ai.SpeechEngine;
import com.xiaomi.ai.mibrain.Mibrainsdk;

public class GlobalConfig {
    //开发环境，分为STAGING，PREVIEW，PRODUCTION
    //STAGING为小米内部使用，仅内网用户可以访问[ENV_STAGING]
    //PREVIEW为常规开发环境，供测试联调使用，与PRODUCTION隔离[SpeechEngine.ENV_PREVIEW]
    //PRODUCTION为正式环境，当产品发布时，需要切换到PRODUCTION环境[SpeechEngine.ENV_PRODUCTION]
    public static int DEVELOPMENT_ENVIRONMENT = SpeechEngine.ENV_PREVIEW;

    //日志等级
    //DEBUG为调试时使用，发布时需要关闭[Mibrainsdk.MIBRAIN_DEBUG_LEVEL_DEBUG]
    //WARNING为一般模式[Mibrainsdk.MIBRAIN_DEBUG_LEVEL_WARNING]
    //ERROR为最少输出模式[Mibrainsdk.MIBRAIN_DEBUG_LEVEL_ERROR]
    public static int OUTPUT_LOG_LEVEL = Mibrainsdk.MIBRAIN_DEBUG_LEVEL_DEBUG;

    //设备OAuth鉴权，使用小爱开放平台，需要如下信息
    public static String MIAI_CLIENT_ID = "";
    public static String MIAI_CLIENT_SECRET = "";
    public static String MIAI_REDIRECT_URL = "";

    //应用OAuth鉴权，使用小米开放平台，需要如下信息
    public static String MIAO_CLIENT_ID = "";
    public static String MIAO_CLIENT_SECRET = "";
    public static String MIAO_REDIRECT_URL = "";

    //MIOT鉴权，需要如下信息
    public static String MIOT_CLIENT_ID = "";
    public static String MIOT_TOKEN = "";
    public static String MIOT_SESSION = "";

    //设备Token鉴权，需要如下信息
    public static String TP_CLIENT_ID = "";
    public static String TP_TOKEN = "";

    //应用匿名鉴权，需要如下信息
    public static String ANON_CLIENT_ID = "";
    public static String ANON_CLIENT_SECRET = "";
    public static String ANON_API_KEY = "";
}
