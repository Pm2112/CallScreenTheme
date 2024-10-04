package com.example.callscreenapp.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.callscreenapp.AppOwner
import com.example.callscreenapp.BaseActivity
import com.example.callscreenapp.Impl.ClickRateAppListener
import com.example.callscreenapp.Impl.NetworkChangeListener
import com.example.callscreenapp.PrivacyPolicyActivity
import com.example.callscreenapp.R
import com.example.callscreenapp.TermsOfUseActivity
import com.example.callscreenapp.ads.AdmobOpenAd
import com.example.callscreenapp.process.EvenFirebase
import com.example.callscreenapp.ui.dialog.RateAppDialog
import com.example.callscreenapp.ui.dialog.showNetworkCustomDialog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class SettingActivity : BaseActivity(), NetworkChangeListener, ClickRateAppListener {
    private lateinit var rateAppDialog: RateAppDialog
    private lateinit var firebase: EvenFirebase
    private lateinit var adLoader: AdLoader
    override fun onNetworkUnavailable() {
        runOnUiThread {
            showNetworkCustomDialog(this)
        }
    }

    override fun onClickLater() {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebase = EvenFirebase(this)
        firebase.logFirebaseEvent("Settings")

        rateAppDialog = RateAppDialog(this, this, this)

        val iconBack: ImageView = findViewById(R.id.setting_activity_icon_back)

        iconBack.setOnClickListener() {
            finish()
        }

        val btnRateApp: CardView = findViewById(R.id.rate_app)
        val btnPrivacyPolicy: CardView = findViewById(R.id.privacy_policy)
        val btnTermsOfUse: CardView = findViewById(R.id.terms_of_use)
        val btnMoreApps: CardView = findViewById(R.id.more_apps)

        val adContainer: FrameLayout = findViewById(R.id.setting_ads)
        loadNativeAd(adContainer, "setting activity")

        btnRateApp.setOnClickListener() {
            rateAppDialog.show()
        }

        btnPrivacyPolicy.setOnClickListener() {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        btnTermsOfUse.setOnClickListener() {
            val intent = Intent(this, TermsOfUseActivity::class.java)
            startActivity(intent)
        }

        btnMoreApps.setOnClickListener(){
            showMoreApp()
        }
    }

    private fun showMoreApp() {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.more_app)))
        startActivity(browserIntent)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        
        finish()
    }

    private fun loadNativeAd(adContainer: FrameLayout, location: String) {
        adLoader = AdLoader.Builder(this, AppOwner.getContext().getString(R.string.native_ad_unit_id))
            .forNativeAd { nativeAd ->
                val adView =
                    layoutInflater.inflate(R.layout.layout_dialog_native_ads, null) as NativeAdView
                populateNativeAdView(nativeAd, adView)
                adContainer.removeAllViews()
                adContainer.addView(adView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdLoader", "Failed to load ad: ${adError.message}")
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    firebase.logFirebaseParamEvent("ad_native_impress", "location", location)
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

        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.bodyView as TextView).text = nativeAd.body
        (adView.mediaView as MediaView).mediaContent = nativeAd.mediaContent

        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        (adView.callToActionView as TextView).text = nativeAd.callToAction

        adView.setNativeAd(nativeAd)
    }
}