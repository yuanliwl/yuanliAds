package com.yuanli.ksads.utils;


import android.app.Activity;

import androidx.annotation.Nullable;

import com.kwad.sdk.api.KsLoadManager;
import com.kwad.sdk.api.KsRewardVideoAd;
import com.kwad.sdk.api.KsScene;
import com.yuanli.base.Ad;
import com.yuanli.base.AdStateListener;
import com.yuanli.base.log.Logger;
import com.yuanli.ksads.KsManagerHolder;
import com.yuanli.ksads.constants.KsConstants;

import java.util.List;

public class KsRewardAdUtils implements Ad {
    private Activity activity;
    private AdStateListener adStateListener;
    private boolean isLoadSuccess = false;
    private KsRewardVideoAd mRewardVideoAd;
    public KsRewardAdUtils(Activity activity){
        this.activity = activity;
    }

    public void loadAd(final AdStateListener adStateListener){
        isLoadSuccess = false;
        KsRewardAdUtils.this.adStateListener = adStateListener;
        KsManagerHolder.setInitListener(new KsManagerHolder.InitListener() {
            @Override
            public void onSuccess() {
                adStateListener.loading();
                KsScene scene = new KsScene.Builder(InitUtils.getBean().getRewardId()).build(); // 此为测试posId，请联系快手平台申请正式posId
                KsManagerHolder.get().loadRewardVideoAd(scene, new KsLoadManager.RewardVideoAdListener() {
                    @Override
                    public void onError(int code, String msg) {
                        Logger.i(KsConstants.TAG, "onError: " + msg);
                        adStateListener.onError("onError: " + msg);
                        adStateListener.stopLoading();
                    }

                    @Override
                    public void onRewardVideoResult(@Nullable List<KsRewardVideoAd> adList) {
                        Logger.i(KsConstants.TAG, "onRewardVideoResult: ");
                    }

                    @Override
                    public void onRewardVideoAdLoad(@Nullable List<KsRewardVideoAd> adList) {
                        Logger.i(KsConstants.TAG, "onRewardVideoAdLoad: ");
                        if (adList != null && adList.size() > 0) {
                            mRewardVideoAd = adList.get(0);
                            adStateListener.stopLoading();
                            adStateListener.real(KsRewardAdUtils.this);
                        }
                    }
                });
            }

            @Override
            public void onError(String msg) {
                adStateListener.stopLoading();
                adStateListener.onError("onError :" + msg);
                adStateListener.toast(msg);
            }
        });
    }

    private final KsRewardVideoAd.RewardAdInteractionListener rewardAdInteractionListener = new KsRewardVideoAd.RewardAdInteractionListener() {
        @Override
        public void onAdClicked() {
            Logger.i(KsConstants.TAG, "onAdClicked: ");
        }

        @Override
        public void onPageDismiss() {
            Logger.i(KsConstants.TAG, "onPageDismiss: ");
            if (isLoadSuccess && adStateListener != null) {
                adStateListener.successClose();
            }
        }

        @Override
        public void onVideoPlayError(int code, int extra) {
            Logger.i(KsConstants.TAG, "onVideoPlayError: ");
            adStateListener.stopLoading();
            adStateListener.onError("onVideoPlayError" + "广告播放错误");
        }

        @Override
        public void onVideoPlayEnd() {
            Logger.i(KsConstants.TAG, "onVideoPlayEnd: ");
        }

        @Override
        public void onVideoSkipToEnd(long l) {
            Logger.i(KsConstants.TAG, "onVideoSkipToEnd: ");
        }

        @Override
        public void onVideoPlayStart() {
            Logger.i(KsConstants.TAG, "onVideoPlayStart: ");
        }

        @Override
        public void onRewardVerify() {
            Logger.i(KsConstants.TAG, "onRewardVerify: ");
            isLoadSuccess = true;
            if (adStateListener != null) {
                adStateListener.success();
            }
        }

        @Override
        public void onRewardStepVerify(int taskType, int currentTaskStatus) {
            Logger.i(KsConstants.TAG, "onRewardVerify: ");
        }

        @Override
        public void onExtraRewardVerify(int i) {
            Logger.i(KsConstants.TAG, "onExtraRewardVerify: ");

        }
    };



    @Override
    public void showAd() {
        if (mRewardVideoAd != null && mRewardVideoAd.isAdEnable()) {
            mRewardVideoAd.setRewardAdInteractionListener(rewardAdInteractionListener);
            mRewardVideoAd.showRewardVideoAd(activity, null);
        } else {
            Logger.i("TAG", "showAd: " + "暂无可用激励视频广告，请等待缓存加载或者重新刷新");
            adStateListener.stopLoading();
            adStateListener.onError("showAd: " + "暂无可用激励视频广告，请等待缓存加载或者重新刷新");
        }
    }

    @Override
    public void onDestroy() {
        KsManagerHolder.clear();
        mRewardVideoAd = null;
        adStateListener = null;
        activity = null;
    }
}
