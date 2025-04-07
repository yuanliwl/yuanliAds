package com.yuanli.pangolin.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.yuanli.base.log.Logger;
import com.yuanli.pangolin.holder.TTAdManagerHolder;
import com.yuanli.pangolin.widget.DislikeDialog;

import java.util.List;

public class BannerAdUtils {
    private Context mContext;
    private TTAdNative mTTAdNative;
    private TTNativeExpressAd mTTAd;
    private RelativeLayout mExpressContainer;
    private TextView welcomeTv;

    private long startTime = 0;
    private boolean mHasShowDownloadActive = false;
    private static final String TAG = "BannerAdUtils";

    public BannerAdUtils(Context context){
        this.mContext = context;
        initAdConfig(context);
    }

    public void setmExpressContainer(RelativeLayout mExpressContainer,TextView welcomeTv) {
        this.mExpressContainer = mExpressContainer;
        this.welcomeTv = welcomeTv;
    }

    public void setmExpressContainer(RelativeLayout mExpressContainer){
        this.mExpressContainer = mExpressContainer;
    }

    private void initAdConfig(Context context) {
        mTTAdNative = TTAdManagerHolder.get().createAdNative(context);
    }

    /**
     * 加载首页广告
     * */
    public void loadHomeBannerAd(){
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(InitUtils.getBean().getBannerId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(3) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(750,421) //期望个性化模板广告view的size,单位dp
                .setImageAcceptedSize(640,320 )//这个参数设置即可，不影响个性化模板广告的size
                .build();

        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int i, String s) {
                mExpressContainer.removeAllViews();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0){
                    return;
                }
                mTTAd = ads.get(0);
                mTTAd.setSlideIntervalTime(3000);
                bindAdListener(mTTAd);
                startTime = System.currentTimeMillis();
                mTTAd.render();
            }
        });
    }

    /**
     * 加载插片广告
     * */
    public void loadPicBannerAd(){
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(InitUtils.getBean().getBannerId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(640,100) //期望个性化模板广告view的size,单位dp
                //.setImageAcceptedSize(640,320 )//这个参数设置即可，不影响个性化模板广告的size
                .build();

        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int i, String s) {
                mExpressContainer.removeAllViews();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0){
                    return;
                }
                mTTAd = ads.get(0);
                //mTTAd.setSlideIntervalTime(3000); 不调用则不进行轮播
                bindAdListener(mTTAd);
                startTime = System.currentTimeMillis();
                mTTAd.render();
            }
        });
    }

    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                Logger.i(TAG, "onAdClicked: 广告被点击");
            }

            @Override
            public void onAdShow(View view, int type) {
                Logger.i(TAG, "onAdShow: 广告展示");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Logger.i("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
                Logger.i(TAG, "onRenderFail: " + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Logger.i("ExpressView", "render suc:" + (System.currentTimeMillis() - startTime));
                Logger.i(TAG, "onRenderSuccess: 渲染成功");
                //返回view的宽高 单位 dp
                mExpressContainer.setVisibility(View.VISIBLE);
                if (welcomeTv != null) {
                    welcomeTv.setVisibility(View.GONE);
                }
                mExpressContainer.removeAllViews();
                mExpressContainer.addView(view);
            }
        });
        //dislike设置
        bindDislike(ad, true);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                //Logger.i(TAG, "onIdle: 点击开始下载");
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    Logger.i(TAG, "onDownloadActive: 下载中，点击暂停");
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                Logger.i(TAG, "onDownloadPaused: 下载暂停，点击继续");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                Logger.i(TAG, "onDownloadFailed: 下载失败，点击重新下载");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                Logger.i(TAG, "onInstalled: 安装完成，点击图片打开");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                Logger.i(TAG, "onDownloadFinished: 点击安装");
            }
        });
    }

    /**
     * 设置广告的不喜欢不再推荐。
     * 注意：强烈建议设置该逻辑，如果不设置dislike处理逻辑，则模板广告中的 dislike区域不响应dislike事件。
     * @param ad
     * @param customStyle 是否自定义样式，true:样式自定义
     */
    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        if (customStyle) {
            //使用自定义样式
            List<FilterWord> words = ad.getDislikeInfo().getFilterWords();
            if (words == null || words.isEmpty()) {
                return;
            }

            final DislikeDialog dislikeDialog = new DislikeDialog(mContext, words);
            dislikeDialog.setOnDislikeItemClick(filterWord -> {
                //屏蔽广告
                mExpressContainer.removeAllViews();
                mExpressContainer.setVisibility(View.GONE);
                if(welcomeTv != null){
                    welcomeTv.setVisibility(View.VISIBLE);
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //使用默认模板中默认dislike弹出样式
        ad.setDislikeCallback((Activity) mContext, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {
            }

            @Override
            public void onSelected(int i, String s, boolean b) {
                //用户选择不喜欢原因后，移除广告展示
                mExpressContainer.removeAllViews();
                mExpressContainer.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {
                Logger.i(TAG, "onCancel: 点击取消");
            }
        });
    }

    public void release(){
        if (mTTAd != null) {
            //调用destroy()方法释放
            mTTAd.destroy();
        }
    }
}


