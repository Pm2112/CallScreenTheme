package com.example.callscreenapp.ui.fragment.select_effect

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.example.callscreenapp.AppOwner
import com.example.callscreenapp.R
import com.example.callscreenapp.process.DataManager.Companion.EffectButton
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SelectEffectFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = SelectEffectFragment()
    }

    private val viewModel: SelectEffectViewModel by viewModels()
    private lateinit var adLoader: AdLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_select_effect, container, false)

        val btnEffect: ConstraintLayout = view.findViewById(R.id.btn_effect)
        val btnMotion: ConstraintLayout = view.findViewById(R.id.btn_motion)
        val btnDefault: ConstraintLayout = view.findViewById(R.id.btn_default)
        val icEffect: ImageView = view.findViewById(R.id.icon_effect_check)
        val icMotion: ImageView = view.findViewById(R.id.icon_motion_check)
        val icDefault: ImageView = view.findViewById(R.id.icon_default_check)
        val adContainer: FrameLayout = view.findViewById(R.id.native_ads)

        adLoader = AdLoader.Builder(requireActivity(), AppOwner.getContext().getString(R.string.native_ad_unit_id))
            .forNativeAd { nativeAd ->
                val adView =
                    layoutInflater.inflate(R.layout.layout_native_ads, null) as NativeAdView
                populateNativeAdView(nativeAd, adView)
                adContainer.removeAllViews()
                adContainer.addView(adView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdLoader", "Failed to load ad: ${adError.message}")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())

        EffectButton.observe(this, Observer {
            when (it) {
                "effect" -> {
                    icEffect.visibility = VISIBLE
                    icMotion.visibility = GONE
                    icDefault.visibility = GONE
                }
                "motion" -> {
                    icEffect.visibility = GONE
                    icMotion.visibility = VISIBLE
                    icDefault.visibility = GONE
                }
                "default" -> {
                    icEffect.visibility = GONE
                    icMotion.visibility = GONE
                    icDefault.visibility = VISIBLE
                }
                "" -> {
                    icEffect.visibility = GONE
                    icMotion.visibility = GONE
                    icDefault.visibility = VISIBLE
                }
            }
        })

        btnEffect.setOnClickListener {
            EffectButton.value = "effect"
            icEffect.visibility = VISIBLE
            icMotion.visibility = GONE
            icDefault.visibility = GONE
        }
        btnMotion.setOnClickListener {
            EffectButton.value = "motion"
            icEffect.visibility = GONE
            icMotion.visibility = VISIBLE
            icDefault.visibility = GONE
        }
        btnDefault.setOnClickListener {
            EffectButton.value = "default"
            icEffect.visibility = GONE
            icMotion.visibility = GONE
            icDefault.visibility = VISIBLE
        }

        return view
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