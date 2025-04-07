package com.yuanli.pangolin.utils;


import static com.yuanli.pangolin.constants.AdConstants.TAG;

import android.app.Activity;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdLoadType;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.yuanli.base.AdStateListener;
import com.yuanli.pangolin.constants.AdConstants;
import com.yuanli.pangolin.holder.TTAdManagerHolder;

public class NewInsertAdUtils {
    private Activity mContext;
    private TTAdNative mTTAdNative;
    private AdStateListener mAdStateListener;

    public void loadInsertAd(Activity context,AdStateListener adStateListener) {
        mContext = context;
        mAdStateListener = adStateListener;
        AdSlot adSlot = getAdSlot();
        mTTAdNative = TTAdManagerHolder.get().createAdNative(context);
        mTTAdNative.loadFullScreenVideoAd(adSlot,nativeExpressAdListener);
    }

    public AdSlot getAdSlot() {
        return new AdSlot.Builder()
                .setCodeId(InitUtils.getBean().getNewInsertId()) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(300, 450) //期望模板广告view的size,单位dp
                .setOrientation(TTAdConstant.VERTICAL)
                .setAdLoadType(TTAdLoadType.PRELOAD)
                .build();
    }

    private TTAdNative.FullScreenVideoAdListener nativeExpressAdListener = new TTAdNative.FullScreenVideoAdListener() {
        @Override
        public void onError(int i, String s) {
            Log.e(TAG,"onError:"+s);
            if(mAdStateListener != null){
                mAdStateListener.onError(s);
            }
        }

        @Override
        public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ttFullScreenVideoAd) {
            ttFullScreenVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
                @Override
                public void onAdShow() {
                    if(mAdStateListener != null){
                        mAdStateListener.success();
                    }
                }

                @Override
                public void onAdVideoBarClick() { }

                @Override
                public void onAdClose() {
                    if (mAdStateListener != null){
                        mAdStateListener.onClose();
                    }
                }

                @Override
                public void onVideoComplete() {
                    if (mAdStateListener != null){
                        mAdStateListener.onClose();
                    }
                }

                @Override
                public void onSkippedVideo() {
                    if (mAdStateListener != null){
                        mAdStateListener.onClose();
                    }
                }
            });
        }

        @Override
        public void onFullScreenVideoCached() { }


        @Override
        public void onFullScreenVideoCached(TTFullScreenVideoAd ttFullScreenVideoAd) {
            Log.e(TAG,"onFullScreenVideoCached:");
            ttFullScreenVideoAd.showFullScreenVideoAd(mContext, TTAdConstant.RitScenes.GAME_GIFT_BONUS, null);
        }

    };

    public void release(){
        mContext = null;
        mAdStateListener = null;
        if (mTTAdNative != null) {
            mTTAdNative = null;
        }
    }
}
