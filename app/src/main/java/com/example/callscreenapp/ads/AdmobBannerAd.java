package com.example.callscreenapp.ads;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bluewhaleyt.network.NetworkUtil;
import com.example.callscreenapp.R;
import com.example.callscreenapp.utils.PreferencesManager;
import com.example.callscreenapp.AppOwner;
import com.example.callscreenapp.BaseActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

public class AdmobBannerAd extends AdListener {
    public static AdmobBannerAd Instance = new AdmobBannerAd();

    public BaseActivity activity;
    private AdView mBannerAd;
    private ProgressBar mProgressBar;

    public AdmobBannerAd(){

    }
    private AdSize getAdSize(RelativeLayout adContainerView) {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = this.activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this.activity, adWidth);
    }

    public void loadBanner(RelativeLayout adContainerView) {
        destroyView();

        if (!NetworkUtil.isNetworkAvailable(AppOwner.Companion.getContext())) {
            return;
        }

        if(activity == null)return;

        if(PreferencesManager.checkSUB() != null){
            adContainerView.setVisibility(View.GONE);
            return;
        }

        if(mBannerAd != null)return;
        // Create a new ad view.
        mBannerAd = new AdView(this.activity);
        mBannerAd.setAdSize(getAdSize(adContainerView));
        mBannerAd.setAdUnitId(activity.getString(R.string.banner_ad_unit_id));
        mBannerAd.setAdListener(this);

        mProgressBar = new ProgressBar(this.activity);
        mProgressBar.setIndeterminate(true);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30);
        layoutParams.setMargins(
                (int) ((adContainerView.getWidth() - 30) / 2.0f),
                (int) ((adContainerView.getHeight() - 30) / 2.0f), 0, 0);
        mProgressBar.setLayoutParams(layoutParams);

        // Replace ad container with new ad view.
        adContainerView.removeAllViews();
        adContainerView.addView(mBannerAd);
        adContainerView.addView(mProgressBar);

        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        mBannerAd.loadAd(adRequest);
        mBannerAd.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAdLoaded() {
        super.onAdLoaded();
        mProgressBar.setVisibility(View.GONE);
        mBannerAd.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
        super.onAdFailedToLoad(loadAdError);
        Log.d("xxxyyy", "bannerView " + loadAdError.getMessage());
    }

    public void destroyView(){
        if(activity == null)return;
        if(mBannerAd != null) {
            mBannerAd.destroy();
            mBannerAd = null;
        }
        if(mProgressBar != null) {
            mProgressBar.destroyDrawingCache();
            mProgressBar = null;
        }
    }
}
