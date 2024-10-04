package com.example.callscreenapp.ads;

import android.util.Log;

import com.example.callscreenapp.AppOwner;
import com.example.callscreenapp.BaseActivity;
import com.example.callscreenapp.R;
import com.example.callscreenapp.utils.PreferencesManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AdmobOpenAd extends FullScreenContentCallback {

    public static AdmobOpenAd Instance = new AdmobOpenAd();

    public BaseActivity activity;
    private AppOpenAd mAppOpenAd;

    private List<String> list_open_ad_unit_id;
    private int countTier = 0;

    public AdmobOpenAd() {
        list_open_ad_unit_id = Arrays.asList(AppOwner.Companion.getContext().getResources().getStringArray(R.array.open_ad_unit_id));
    }

    public void loadAds() {
        //if (!NetworkUtil.isNetworkAvailable(App.getContext()))return;
        if(activity == null)return;
        if(PreferencesManager.checkSUB() != null)return;
        if(mAppOpenAd != null)return;
        AdRequest adRequest = new AdRequest.Builder().build();

        AppOpenAd.load(
                activity, list_open_ad_unit_id.get(countTier), adRequest,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
                        // Called when an app open ad has loaded.
                        Log.d("xxxyyy", "Ad was loaded.");
                        mAppOpenAd = ad;
                        mAppOpenAd.setFullScreenContentCallback(Instance);
                        PreferencesManager.saveTimeShowAppOpen(new Date().getTime());
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        // Called when an app open ad has failed to load.
                        Log.d("xxxyyy", loadAdError.getMessage());
//                        mAppOpenAd = null;
                        activity.closeAds(TYPE_ADS.OpenAd);
                    }
                });

        if (countTier >= list_open_ad_unit_id.size() - 1){
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
        // Dung 4 tieng command mAppOpenAd = null; va loadAds();
        mAppOpenAd = null;
        activity.closeAds(TYPE_ADS.OpenAd);
        loadAds();
    }

    @Override
    public void onAdFailedToShowFullScreenContent(AdError adError) {
        // Called when ad fails to show.
        Log.e("xxxyyy", "Ad failed to show fullscreen content.");
        // Dung 4 tieng command mAppOpenAd = null;
        mAppOpenAd = null;
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
            activity.closeAds(TYPE_ADS.OpenAd);
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
            ShowAds();
        }
        else
        {
            activity.closeAds(TYPE_ADS.OpenAd);
        }
    }

    public void ShowAds(){
        if(activity == null || AppOwner.Companion.isSkipOpenAd())return;
        if(PreferencesManager.checkSUB() != null)return;
        activity.showAds();
        if (isAdAvailable()) {
            mAppOpenAd.show(activity);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
            activity.closeAds(TYPE_ADS.OpenAd);
            loadAds();
        }
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = new Date().getTime() - PreferencesManager.getTimeShowAppOpen();
        long numMilliSecondsPerHour = 60 * 60 * 1000;
        Log.d("xxxyyy", "wasLoadTimeLessThanNHoursAgo : " + dateDifference + ", " + (numMilliSecondsPerHour * numHours));
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /** Check if ad exists and can be shown. */
    public boolean isAdAvailable() {
        return mAppOpenAd != null; // && wasLoadTimeLessThanNHoursAgo(4);
    }
}
