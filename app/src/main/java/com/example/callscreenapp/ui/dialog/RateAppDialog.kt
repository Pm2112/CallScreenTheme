package com.example.callscreenapp.ui.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import com.example.callscreenapp.AppOwner
import com.example.callscreenapp.Impl.ClickRateAppListener
import com.example.callscreenapp.R
import com.example.callscreenapp.process.EvenFirebase
import com.example.callscreenapp.process.getRateAppMainCreate
import com.example.callscreenapp.process.saveRateApp
import com.example.callscreenapp.ui.activity.ShowImageActivity
import com.example.callscreenapp.utils.Constants.rateApp
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

@SuppressLint("InflateParams")
class RateAppDialog(context: Context, private val activity: Activity, private val clickRateAppListener: ClickRateAppListener) {
    private val dialog: Dialog = Dialog(context)
    private lateinit var reviewManager: ReviewManager
    private lateinit var adLoader: AdLoader

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_rate, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)  // Ngăn người dùng hủy bỏ dialog bằng cách chạm bên ngoài
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val starOne: ImageView = view.findViewById(R.id.star_one)
        val starTwo: ImageView = view.findViewById(R.id.star_two)
        val starThree: ImageView = view.findViewById(R.id.star_three)
        val starFour: ImageView = view.findViewById(R.id.star_four)
        val starFive: ImageView = view.findViewById(R.id.star_five)
        val btnLater: CardView = view.findViewById(R.id.btn_later)
        val btnRate: CardView = view.findViewById(R.id.btn_rate)
        val adContainer: FrameLayout = view.findViewById(R.id.rate_ads)

        val evenFirebase = EvenFirebase(context)

        adLoader = AdLoader.Builder(context, AppOwner.getContext().getString(R.string.native_ad_unit_id))
            .forNativeAd { nativeAd ->
                val adView = LayoutInflater.from(context).inflate(R.layout.layout_dialog_native_ads, null) as NativeAdView
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

        reviewManager = ReviewManagerFactory.create(context)



        val (main, count) = getRateAppMainCreate(context)
        var c = 0
        var m = false
        if (count != null) {
            c = count
        }
        if (main != null) {
            m = main
        }
        saveRateApp(context, m, c + 1)
        Log.d("save_rate", count.toString())

        val activityName = activity.javaClass.simpleName
        evenFirebase.logFirebaseParamEvent("show_rating", "location", activityName)

        var countRate = 0

        starOne.setOnClickListener {
            starOne.isSelected = true
            starTwo.isSelected = false
            starThree.isSelected = false
            starFour.isSelected = false
            starFive.isSelected = false
            countRate = 1
        }

        starTwo.setOnClickListener {
            starOne.isSelected = true
            starTwo.isSelected = true
            starThree.isSelected = false
            starFour.isSelected = false
            starFive.isSelected = false
            countRate = 2
        }

        starThree.setOnClickListener {
            starOne.isSelected = true
            starTwo.isSelected = true
            starThree.isSelected = true
            starFour.isSelected = false
            starFive.isSelected = false
            countRate = 3
        }

        starFour.setOnClickListener {
            starOne.isSelected = true
            starTwo.isSelected = true
            starThree.isSelected = true
            starFour.isSelected = true
            starFive.isSelected = false
            countRate = 4
        }

        starFive.setOnClickListener {
            starOne.isSelected = true
            starTwo.isSelected = true
            starThree.isSelected = true
            starFour.isSelected = true
            starFive.isSelected = true
            countRate = 5
        }

        btnLater.setOnClickListener {
            dismiss()
            clickRateAppListener.onClickLater()
        }

        btnRate.setOnClickListener {
            dismiss()
            if (countRate <= 3) {
                val email = "caprinixdev@gmail.com"
                val subject = "Feedback about the app CallScreenTheme"
                val body = "Hi, I would like to give feedback..."
                val uriText = "mailto:$email?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}"
                val emailUri = Uri.parse(uriText)

                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = emailUri
                }

// Check if there is an app that can handle the intent
                if (emailIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(emailIntent) // Open the email app
                } else {
                    // If no app can handle, show a message or handle the error
                    Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                }
            } else {
                rateApp(activity)
            }
            val (main, count) = getRateAppMainCreate(context)
            var c = 0
            if (count != null) {
                c = count
            }
            saveRateApp(context, main = false, c)
            evenFirebase.logFirebaseParamEvent("success_rate", "location", activityName)
        }
    }

    fun show() {
        dialog.show()
    }

    private fun dismiss() {
        dialog.dismiss()
    }

//    private fun showReviewDialog() {
//        val request = reviewManager.requestReviewFlow()
//        request.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val reviewInfo = task.result
//                val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
//                flow.addOnCompleteListener { _ ->
//                    // Đánh giá đã hoàn tất hoặc bị hủy
//                }
//            } else {
//                // Xử lý lỗi khi yêu cầu đánh giá không thành công
//            }
//        }
//    }

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
