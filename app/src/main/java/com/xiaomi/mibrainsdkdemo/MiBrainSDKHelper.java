package com.xiaomi.mibrainsdkdemo;

import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.xiaomi.account.openauth.XMAuthericationException;
import com.xiaomi.account.openauth.XiaomiOAuthConstants;
import com.xiaomi.account.openauth.XiaomiOAuthFuture;
import com.xiaomi.account.openauth.XiaomiOAuthResults;
import com.xiaomi.account.openauth.XiaomiOAuthorize;
import com.xiaomi.ai.AsrListener;
import com.xiaomi.ai.InstructionListener;
import com.xiaomi.ai.MiAiOauthCallbacks;
import com.xiaomi.ai.NlpListener;
import com.xiaomi.ai.ServiceEventListener;
import com.xiaomi.ai.SpeechEngine;
import com.xiaomi.ai.TtsListener;
import com.xiaomi.ai.mibrain.Mibrainsdk;
import com.xiaomi.ai.utils.DeviceUtils;

import java.io.IOException;

public class MiBrainSDKHelper {
    private static final String TAG = "MiBrainSDKHelper";
    private static SpeechEngine mSpeechEngine;
    private static Context mContext;
    private static Activity mActivity;

    public static synchronized SpeechEngine getEngineInstance(Context context, Activity activity) {
        //仅OAuth类鉴权需要指定activity参数，OAuth鉴权需要弹出登录界面。
        if(mSpeechEngine == null) {
            mContext = context;
            mActivity = activity;
            //mSpeechEngine = initEngine_DeviceOAuth(); //设备OAuth鉴权
            //mSpeechEngine = initEngine_AppOAuth(); //应用OAuth鉴权
            //mSpeechEngine = initEngine_MIOTAuth(); //MIOT鉴权
            mSpeechEngine = initEngine_DeviceTokenAuth(); //设备Token鉴权
            //mSpeechEngine = initEngine_AppAnonymousAuth(); //应用匿名鉴权
            Mibrainsdk.setLogLevel(GlobalConfig.OUTPUT_LOG_LEVEL);//设置Log级别
        }
        return mSpeechEngine;
    }

    //指定ASR，NLP，TTS的Listener
    public static void setASRListener(AsrListener listener) throws Exception {
        if(mSpeechEngine != null) {
            mSpeechEngine.setAsrLisnter(listener);
        } else {
            throw new Exception("need init first");
        }
    }

    public static void setNLPListener(NlpListener listener) throws Exception {
        if(mSpeechEngine != null) {
            mSpeechEngine.setNlpListener(listener);
        } else {
            throw new Exception("need init first");
        }
    }

    public static void setTTSListener(TtsListener listener) throws Exception {
        if(mSpeechEngine != null) {
            mSpeechEngine.setTtsListener(listener);
        } else {
            throw new Exception("need init first");
        }
    }

    //设定Instruction的Listener
    //内测功能，开发者不需要关注
    public static void setInstructionListener(InstructionListener listener) throws Exception {
        if(mSpeechEngine != null) {
            mSpeechEngine.setInstructionListener(listener);
        } else {
            throw new Exception("need init first");
        }
    }

    //设定ServiceEvent的Listener，用于监听服务器返回的消息
    public static void setServiceEventListener(ServiceEventListener listener) {
        mSpeechEngine.setServiceEventListener(listener);
    }

    //以下OAuth鉴权方式依赖OAuth的jar包，可以在
    //https://github.com/xiaomi-passport/oauth-android-sdk/tree/master/sdk
    //中找到

    //设备OAuth鉴权需要实现下面的接口，在SDK第一次开始鉴权会请求OAuthCode
    //此处通过xiaomi-passport的实现来获取OAuthCode，需要登录小米账号
    private static MiAiOauthCallbacks mDeviceOAuthListener;
    //使用设备OAuth鉴权，使用小爱开放平台
    private static SpeechEngine initEngine_DeviceOAuth() {
        Log.w(TAG, "use initEngine_DeviceOAuth");
        mDeviceOAuthListener = new MiAiOauthCallbacks() {
            @Override
            public String getOauthCode() {
                String r = null;
                String deviceId = DeviceUtils.getDeviceId(mContext);
                String state = DeviceUtils.getDefaultState(deviceId);
                final XiaomiOAuthFuture<XiaomiOAuthResults> future = new XiaomiOAuthorize()
                        .setAppId(Long.parseLong(GlobalConfig.MIAI_CLIENT_ID))
                        .setPlatform(XiaomiOAuthConstants.PLATFORM_SHUIDI) //此处一定是XiaomiOAuthConstants.PLATFORM_SHUIDI
                        .setSkipConfirm(true)
                        .setNoMiui(true)
                        .setRedirectUrl(GlobalConfig.MIAI_REDIRECT_URL)
                        .setScope(new int[]{20000})
                        .setDeviceID(deviceId)
                        .setState(state)
                        .startGetOAuthCode(mActivity);
                try {
                    r = future.getResult().getCode();
                } catch (OperationCanceledException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMAuthericationException e) {
                    e.printStackTrace();
                }
                return r;
            }
        };
        SpeechEngine engine = SpeechEngine.createEngine(mContext,
                SpeechEngine.ENGINE_MI_AI,
                GlobalConfig.MIAI_CLIENT_ID,
                GlobalConfig.MIAI_CLIENT_SECRET,
                GlobalConfig.MIAI_REDIRECT_URL,
                false);
        engine.setEnv(GlobalConfig.DEVELOPMENT_ENVIRONMENT); //设置为开发环境
        engine.setMiAIOauthCallbacks(mDeviceOAuthListener); //设置鉴权时回调
        return engine;
    }

    //应用OAuth鉴权需要实现下面的接口，在SDK第一次开始鉴权会请求OAuthCode
    //此处通过xiaomi-passport的实现来获取OAuthCode，需要登录小米账号
    private static MiAiOauthCallbacks mAppOAuthListener;

    //使用应用OAuth鉴权，使用小米开放平台
    private static SpeechEngine initEngine_AppOAuth() {
        Log.w(TAG, "use initEngine_AppOAuth");
        mAppOAuthListener = new MiAiOauthCallbacks() {
            @Override
            public String getOauthCode() {
                String r = null;
                String deviceId = DeviceUtils.getDeviceId(mContext);
                String state = DeviceUtils.getDefaultState(deviceId);
                final XiaomiOAuthFuture<XiaomiOAuthResults> future = new XiaomiOAuthorize()
                        .setAppId(Long.parseLong(GlobalConfig.MIAO_CLIENT_ID))
                        .setPlatform(XiaomiOAuthConstants.PLATFORM_DEV) //此处一定是XiaomiOAuthConstants.PLATFORM_DEV
                        .setSkipConfirm(true)
                        .setNoMiui(true)
                        .setRedirectUrl(GlobalConfig.MIAO_REDIRECT_URL)
                        .setScope(new int[]{20000})
                        .setDeviceID(deviceId)
                        .setState(state)
                        .startGetOAuthCode(mActivity);
                try {
                    r = future.getResult().getCode();
                } catch (OperationCanceledException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMAuthericationException e) {
                    e.printStackTrace();
                }
                return r;
            }
        };
        SpeechEngine engine = SpeechEngine.createEngine(mContext,
                SpeechEngine.ENGINE_MI_AI,
                GlobalConfig.MIAO_CLIENT_ID,
                GlobalConfig.MIAO_CLIENT_SECRET,
                GlobalConfig.MIAO_REDIRECT_URL,
                true);
        engine.setEnv(GlobalConfig.DEVELOPMENT_ENVIRONMENT); //设置为开发环境
        engine.setMiAOOauthCallbacks(mAppOAuthListener); //设置鉴权时回调
        return engine;
    }

    //使用MIOT鉴权，需要集成MIOT SDK，传入对应的参数
    private static SpeechEngine initEngine_MIOTAuth() {
        Log.w(TAG, "use initEngine_MIOTAuth");
        SpeechEngine engine = SpeechEngine.createEngine(mContext,
                SpeechEngine.ENGINE_MI_AI,
                GlobalConfig.MIOT_CLIENT_ID);
        engine.setEnv(GlobalConfig.DEVELOPMENT_ENVIRONMENT); //设置为开发环境
        engine.updateMiotAuth(GlobalConfig.MIOT_TOKEN, GlobalConfig.MIOT_SESSION); //设置MIOT的Token和Session
        return engine;
    }

    //使用设备Token鉴权，需要先设置好设备Token生成的方式
    private static SpeechEngine initEngine_DeviceTokenAuth() {
        Log.w(TAG, "use initEngine_DeviceTokenAuth");
        SpeechEngine engine = SpeechEngine.createEngine(mContext,
                SpeechEngine.ENGINE_MI_AI,
                GlobalConfig.TP_CLIENT_ID,
                true);
        engine.setEnv(GlobalConfig.DEVELOPMENT_ENVIRONMENT); //设置开发环境
        engine.updateTPAuth(GlobalConfig.TP_TOKEN); //设置鉴权用的Token
        return engine;
    }

    //使用应用匿名鉴权
    //需要保证申请的ApiKey中的证书MD5和SHA256与运行时的证书一致。尤其注意是否是以开发者证书签名的。
    private static SpeechEngine initEngine_AppAnonymousAuth() {
        Log.w(TAG, "use initEngine_AppAnonymousAuth");
        SpeechEngine engine = SpeechEngine.createEngine(mContext,
                SpeechEngine.ENGINE_MI_AI,
                GlobalConfig.ANON_CLIENT_ID,
                GlobalConfig.ANON_CLIENT_SECRET,
                GlobalConfig.ANON_API_KEY,
                SpeechEngine.USE_ANONYMOUS);
        engine.setEnv(GlobalConfig.DEVELOPMENT_ENVIRONMENT); //设置为开发环境
        return engine;
    }

    private static synchronized void ReleaseEngine() {
        if(mSpeechEngine != null) {
            mSpeechEngine.release();
            mSpeechEngine = null;
        }
    }
}
