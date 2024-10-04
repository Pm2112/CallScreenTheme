package com.example.callscreenapp.ads;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bluewhaleyt.network.NetworkUtil;
import com.example.callscreenapp.AppOwner;
import com.example.callscreenapp.BaseActivity;
import com.example.callscreenapp.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Arrays;
import java.util.List;

public class AdmobRewardAd extends FullScreenContentCallback {

    public static AdmobRewardAd Instance = new AdmobRewardAd();

    public BaseActivity activity;
    private RewardedAd mRewardedAd;
    private boolean isReward;
    private List<String> list_reward_ad_unit_id;
    private int countTier = 0;

    public AdmobRewardAd() {
        list_reward_ad_unit_id = Arrays.asList(AppOwner.Companion.getContext().getResources().getStringArray(R.array.reward_ad_unit_id));
    }

    public void loadAds() {
        if (!NetworkUtil.isNetworkAvailable(AppOwner.Companion.getContext()))return;
        if(activity == null)return;
//        if(PreferencesManager.checkSUB() != null)return;
        if(mRewardedAd != null)return;
        isReward = false;
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(activity, list_reward_ad_unit_id.get(countTier),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("xxxyyy", loadAdError.toString());
                        mRewardedAd = null;
//                        activity.closeAds(TYPE_ADS.RewardAd);
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        mRewardedAd = ad;
                        mRewardedAd.setFullScreenContentCallback(Instance);
                        Log.d("xxxyyy", "Ad was loaded.");
                    }
                });

        if (countTier >= list_reward_ad_unit_id.size() - 1){
            countTier = 0;
        }else{
            countTier++;
        }
    }

    @Override
    public void onAdClicked() {
        // Called when a click is recorded for an ad.
        Log.d("xxxyyy", "Ad was clicked.");
    }

    @Override
    public void onAdDismissedFullScreenContent() {
        // Called when ad is dismissed.
        // Set the ad reference to null so you don't show the ad a second time.
        Log.d("xxxyyy", "Ad dismissed fullscreen content.");
        mRewardedAd = null;
        if(isReward)activity.haveReward();
        else activity.notHaveReward();
        activity.closeAds(TYPE_ADS.RewardAd);
        loadAds();
    }

    @Override
    public void onAdFailedToShowFullScreenContent(AdError adError) {
        // Called when ad fails to show.
        Log.e("xxxyyy", "Ad failed to show fullscreen content.");
        mRewardedAd = null;
    }

    @Override
    public void onAdImpression() {
        // Called when an impression is recorded for an ad.
        Log.d("xxxyyy", "Ad recorded an impression.");
    }

    @Override
    public void onAdShowedFullScreenContent() {
        // Called when ad is shown.
        Log.d("xxxyyy", "Ad showed fullscreen content.");
    }

    public void ShowAds(){
        if(activity == null)return;
        activity.showAds();
//        if(PreferencesManager.checkSUB() != null )return;
        if (canShowAds()) {
            mRewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    isReward = true;
                }
            });
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
//            activity.closeAds(TYPE_ADS.RewardAd);
//            loadAds();
        }
    }

    public boolean canShowAds(){
        return mRewardedAd != null;
    }
}
