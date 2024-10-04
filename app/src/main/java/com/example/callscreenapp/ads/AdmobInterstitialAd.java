package com.example.callscreenapp.ads;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bluewhaleyt.network.NetworkUtil;
import com.example.callscreenapp.AppOwner;
import com.example.callscreenapp.BaseActivity;
import com.example.callscreenapp.R;
import com.example.callscreenapp.process.EvenFirebase;
import com.example.callscreenapp.utils.PreferencesManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.Firebase;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;
import java.util.List;

public class AdmobInterstitialAd extends FullScreenContentCallback {

    public static AdmobInterstitialAd Instance = new AdmobInterstitialAd();

    public BaseActivity activity;
    private InterstitialAd mInterstitialAd;
    private List<String> list_interstitial_ad_unit_id;
    private int countTier = 0;
    FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(activity);
    Bundle bundle = new Bundle();

    
    public AdmobInterstitialAd() {
        list_interstitial_ad_unit_id = Arrays.asList(AppOwner.Companion.getContext().getResources().getStringArray(R.array.interstitial_ad_unit_id));
    }

    public void loadAds() {
        if (!NetworkUtil.isNetworkAvailable(AppOwner.Companion.getContext()))return;
        if(activity == null)return;
        if(PreferencesManager.checkSUB() != null)return;
        if(mInterstitialAd != null)return;
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, list_interstitial_ad_unit_id.get(countTier), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(Instance);

                        bundle.putString("location", activity.getLocalClassName());
                        firebaseAnalytics.logEvent("ad_inter_impress", bundle);

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("xxxyyy", "InterstitialAd " + loadAdError.toString());
                        mInterstitialAd = null;
//                        activity.closeAds(TYPE_ADS.InterstitialAd);
                    }
                });

        if (countTier >= list_interstitial_ad_unit_id.size() - 1){
            countTier = 0;
        }else{
            countTier++;
        }
    }

    @Override
    public void onAdClicked() {
        // Called when a click is recorded for an ad.
        bundle.putString("location", activity.getLocalClassName());
        firebaseAnalytics.logEvent("ad_inter_click", bundle);
        Log.d("xxxyyy", "Ad was clicked.");
    }

    @Override
    public void onAdDismissedFullScreenContent() {
        // Called when ad is dismissed.
        // Set the ad reference to null so you don't show the ad a second time.
        Log.d("xxxyyy", "Ad dismissed fullscreen content.");
        mInterstitialAd = null;
        activity.closeAds(TYPE_ADS.InterstitialAd);
        loadAds();
    }

    @Override
    public void onAdFailedToShowFullScreenContent(AdError adError) {
        // Called when ad fails to show.
        Log.e("xxxyyy", "Ad failed to show fullscreen content.");
        mInterstitialAd = null;
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

    public void countToShowAds(int startAds, int loopAds, String keyCount)
    {
        if(activity == null)return;
        if(PreferencesManager.checkSUB() != null){
            activity.closeAds(TYPE_ADS.InterstitialAd);
            return;
        }

        long countFullAds = PreferencesManager.getCounterAds(keyCount);
        countFullAds += 1;
        PreferencesManager.saveCounterAds(keyCount, countFullAds);
        boolean isShowAds = false;
        if (countFullAds < startAds)
        {
            isShowAds = false;
        }
        else if (countFullAds == startAds)
        {
            isShowAds = true;
        }
        else
        {
            if ((countFullAds - startAds) % loopAds == 0)
            {
                isShowAds = true;
            }
            else
            {
                isShowAds = false;
            }
        }

        if (isShowAds)
        {
            activity.showPopupLoadAds(TYPE_ADS.InterstitialAd);
        }
        else
        {
            activity.closeAds(TYPE_ADS.InterstitialAd);
        }
    }

    public void ShowAds(){
        if(activity == null)return;
        activity.showAds();
        if(PreferencesManager.checkSUB() != null){
            activity.closeAds(TYPE_ADS.InterstitialAd);
            return;
        }

        if (canShowAds()) {
            mInterstitialAd.show(activity);
        } else {
            Log.d("xxxyyy", "The interstitial ad wasn't ready yet.");
//            activity.closeAds(TYPE_ADS.InterstitialAd);
//            loadAds();
        }
    }

    public boolean canShowAds(){
        return mInterstitialAd != null;
    }
}
