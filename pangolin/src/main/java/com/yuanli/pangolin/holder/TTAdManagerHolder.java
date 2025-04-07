package com.yuanli.pangolin.holder;


import static com.yuanli.pangolin.constants.AdConstants.TAG;

import android.annotation.SuppressLint;
import android.content.Context;


import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTCustomController;
import com.yuanli.base.log.Logger;
import com.yuanli.pangolin.constants.AdConstants;
import com.yuanli.pangolin.constants.PangolinAdBean;
import com.yuanli.pangolin.utils.InitUtils;

public class TTAdManagerHolder {
    private static boolean sInit;
    private static boolean isFail;
    private static InitListener initListener;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static TTAdManager get() {
        if (!sInit) {
            throw new RuntimeException("TTAdSdk is not init, please check.");
        }
        return TTAdSdk.getAdManager();
    }

    public static void init(Context context, PangolinAdBean pangolinAdBean) {
        TTAdManagerHolder.context = context;
        InitUtils.init(pangolinAdBean);
        doInit(context);
    }


    public interface InitListener{
        void onSuccess();
        void onError(String msg);
    }

    public static void setInitListener(InitListener initListener){
        TTAdManagerHolder.initListener = initListener;
        if (sInit){
            initListener.onSuccess();
        }else if (isFail){
            doInit(context);
        }
    }

    public static void clear(){
        initListener = null;
    }


    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(Context context) {
        if (!sInit) {
            TTAdSdk.init(context, buildConfig());
            TTAdSdk.start(new TTAdSdk.Callback() {
                @Override
                public void success() {
                    Logger.i(TAG, "success: " + TTAdSdk.isInitSuccess());
                    isFail = false;
                    sInit = true;
                    if (initListener != null){
                        initListener.onSuccess();
                    }
                }

                @Override
                public void fail(int code, String msg) {
                    Logger.i(TAG, "fail:  code = " + code + " msg = " + msg);
                    isFail = true;
                    if (initListener != null){
                        initListener.onError(msg);
                    }
                }
            });
        }
    }

    private static TTAdConfig buildConfig() {
        return new TTAdConfig.Builder()
                .appId(InitUtils.getBean().getAppId())
                .appName(InitUtils.getBean().getAppName())
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .debug(false) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_5G,TTAdConstant.NETWORK_STATE_4G) //允许直接下载的网络状态集合
                .supportMultiProcess(true)//是否支持多进程
                .customController(new TTCustomController() {
                    @Override
                    public boolean isCanUseLocation() {
                        return false;
                    }

                    @Override
                    public boolean alist() {
                        return false;
                    }

                    @Override
                    public boolean isCanUsePhoneState() {
                        return false;
                    }

                    @Override
                    public boolean isCanUseWifiState() {
                        return false;
                    }

                    @Override
                    public boolean isCanUseWriteExternal() {
                        return false;
                    }

                    @Override
                    public boolean isCanUseAndroidId() {
                        return false;
                    }

                    @Override
                    public boolean isCanUsePermissionRecordAudio() {
                        return false;
                    }
                })
                .build();
    }
}
