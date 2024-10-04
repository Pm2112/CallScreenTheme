package com.example.callscreenapp.native_ads

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.callscreenapp.AppOwner
import com.example.callscreenapp.R
import com.example.callscreenapp.process.EvenFirebase
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdAssetNames
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.nativead.NativeCustomFormatAd

class NativeAdsLoader(private val context: Context) {

    private lateinit var adLoader: AdLoader
    private lateinit var firebase: EvenFirebase

    fun loadNativeAd(adContainerId: Int, layoutId: Int, location: String) {
        firebase = EvenFirebase(context)
        adLoader = AdLoader.Builder(context, AppOwner.getContext().getString(R.string.native_ad_unit_id))
            .forNativeAd { nativeAd ->
                val adView = LayoutInflater.from(context).inflate(layoutId, null) as NativeAdView
                populateNativeAdView(nativeAd, adView)
                val adContainer = (context as AppCompatActivity).findViewById<FrameLayout>(adContainerId)
                adContainer.removeAllViews()
                adContainer.addView(adView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdLoader", "Failed to load ad: ${adError.message}")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    firebase.logFirebaseParamEvent("ad_native_impress", "location",
                        location
                    )
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    firebase.logFirebaseParamEvent("ad_native_click", "location", location)
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)

        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.bodyView as TextView).text = nativeAd.body
        (adView.mediaView as MediaView).mediaContent = nativeAd.mediaContent

        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        (adView.callToActionView as TextView).text = nativeAd.callToAction


        adView.setNativeAd(nativeAd)
    }

//    fun loadNativeAd(adContainerId: Int, layoutId: Int) {
//        adLoader =
//            AdLoader.Builder(context, AppOwner.getContext().getString(R.string.native_ad_unit_id))
//                .forCustomFormatAd("10063170",
//                    { ad ->
//                        val adView = LayoutInflater.from(context).inflate(layoutId, null) as NativeAdView
//                        populateNativeAdView(ad, adView)
//                    },
//                    { ad, s ->
//                        // Handle the click action
//                    })
//                .withAdListener(object : AdListener() {
//                    override fun onAdFailedToLoad(adError: LoadAdError) {
//                        Log.e("AdLoader", "Failed to load ad: ${adError.message}")
//                    }
//                })
//                .withNativeAdOptions(NativeAdOptions.Builder().build())
//                .build()
//
//        adLoader.loadAd(AdRequest.Builder().build())
//    }
//
//    private fun populateNativeAdView(nativeAd: NativeCustomFormatAd, adView: NativeAdView) {
//        adView.headlineView = adView.findViewById(R.id.ad_headline)
//        adView.bodyView = adView.findViewById(R.id.ad_body)
//        adView.mediaView = adView.findViewById(R.id.ad_media)
//
//        (adView.headlineView as TextView).text = nativeAd.getText(NativeAdAssetNames.ASSET_HEADLINE)
//        (adView.bodyView as TextView).text = nativeAd.getText(NativeAdAssetNames.ASSET_BODY)
//        (adView.mediaView as MediaView).mediaContent = nativeAd.mediaContent
//
////        adView.setNativeAd(nativeAd)
//    }


}
