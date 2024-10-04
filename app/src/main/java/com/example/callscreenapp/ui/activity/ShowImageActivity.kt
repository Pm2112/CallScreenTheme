package com.example.callscreenapp.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.callscreenapp.AppOwner
import com.example.callscreenapp.BaseActivity
import com.example.callscreenapp.Impl.ChangeButtonCallListener
import com.example.callscreenapp.Impl.NetworkChangeListener
import com.example.callscreenapp.Impl.ShowAdsClickListener
import com.example.callscreenapp.R
import com.example.callscreenapp.adapter.ListCallButtonAdapter
import com.example.callscreenapp.ads.AdmobOpenAd
import com.example.callscreenapp.ads.TYPE_ADS
import com.example.callscreenapp.data.ListButtonCall
import com.example.callscreenapp.model.Animation
import com.example.callscreenapp.process.DataManager.Companion.BackgroundUrlShow
import com.example.callscreenapp.process.DataManager.Companion.EffectButton
import com.example.callscreenapp.process.DataManager.Companion.addAnimationToListAnimationSave
import com.example.callscreenapp.process.DataManager.Companion.isAvatar
import com.example.callscreenapp.process.DataManager.Companion.updateEffectButtonFromBackgroundUrl
import com.example.callscreenapp.process.EvenFirebase
import com.example.callscreenapp.process.saveImageFromUri
import com.example.callscreenapp.redux.action.AppAction
import com.example.callscreenapp.redux.store.store
import com.example.callscreenapp.ui.dialog.showNetworkCustomDialog
import com.example.callscreenapp.ui.fragment.select_contact.SelectContactFragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import pl.bclogic.pulsator4droid.library.PulsatorLayout

class ShowImageActivity : BaseActivity(), NetworkChangeListener, ShowAdsClickListener, ChangeButtonCallListener {
    private var storeSubscription: (() -> Unit)? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var adLoader: AdLoader
    private lateinit var firebase: EvenFirebase
    private val animatorSetMap: MutableMap<View, AnimatorSet> = mutableMapOf()
    private val translationAnimatorMap: MutableMap<View, ObjectAnimator> = mutableMapOf()

    enum class EVENT_CLICK() {
        NONE,
        POPUP_BTN_YES,
        IMAGE_ACTIVITY,
        CONTACT_ACTIVITY
    }

    var event_click: EVENT_CLICK = EVENT_CLICK.NONE

    override fun onNetworkUnavailable() {
        runOnUiThread {
            showNetworkCustomDialog(this)
        }
    }

    override fun closeAds(type_ads: TYPE_ADS?) {
        super.closeAds(type_ads)
        if (type_ads == TYPE_ADS.InterstitialAd) {
            if (event_click == EVENT_CLICK.POPUP_BTN_YES) {
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
                finish()
            } else if (event_click == EVENT_CLICK.IMAGE_ACTIVITY) {
                val intent = Intent(this, NotificationActivity::class.java)
                startActivity(intent)
                finish()
            } else if (event_click == EVENT_CLICK.CONTACT_ACTIVITY) {
                val intent = Intent(this, ContactActivity::class.java)
                startActivity(intent)
                finish()

            }
        }
    }

//    fun showImageActivity() : ShowAdsClickListener{
//        showPopupLoadAds(TYPE_ADS.InterstitialAd)
//        return TODO("Provide the return value")
//    }

    override fun checkShowAds(action: String) {
        when (action) {
            "Notification" -> event_click = EVENT_CLICK.IMAGE_ACTIVITY
            "Contact" -> event_click = EVENT_CLICK.CONTACT_ACTIVITY
        }

        showPopupLoadAds(TYPE_ADS.InterstitialAd)
    }

    @SuppressLint("CommitTransaction", "CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_image)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.show_image_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backgroundUrl = store.state.backgroundUrl
        val iconAnswer = store.state.iconCallShowGreen
        val iconReject = store.state.iconCallShowRed
        var avatarUrl = store.state.avatarUrl

        BackgroundUrlShow.value = backgroundUrl

        val imageView = findViewById<ImageView>(R.id.show_image_view_activity)
        Glide.with(this)
            .load(backgroundUrl)
//            .centerCrop() // Sử dụng centerCrop để giữ tỷ lệ của hình ảnh
            .into(imageView)

        val editAvatar: ImageView = findViewById(R.id.show_image_activity_btn_edit)


        firebase = EvenFirebase(this)

        val sharedPref = getSharedPreferences("save_show_avatar", Context.MODE_PRIVATE)
        val iAvatar = sharedPref.getBoolean("save_show_avatar", true)
        isAvatar.value = iAvatar

        val imageAvatar: ImageView = findViewById(R.id.show_image_activity_avatar)
        isAvatar.observe(this, Observer {
            Log.d("isAvatar", "$it")
            if (it == true) {
                imageAvatar.visibility = VISIBLE
                editAvatar.visibility = VISIBLE
            } else {
                imageAvatar.visibility = GONE
                editAvatar.visibility = GONE
            }
        })
        if (avatarUrl != "") {
            Glide.with(this)
                .load(avatarUrl)
                .into(imageAvatar)
        } else {
            editAvatar.visibility = GONE
            Glide.with(this)
                .load(avatarUrl)
                .into(imageAvatar)
        }

//        val hideAvatar: ImageView = findViewById(R.id.show_image_activity_btn_hide_avatar)
//        hideAvatar.setOnClickListener() {
//            store.dispatch(AppAction.SetAvatarUrl(""))
//            changeAvatar(imageAvatar, editAvatar)
//        }

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    val imagePath = this.saveImageFromUri(it)
                    Glide.with(this)
                        .load(imagePath)
                        .into(imageAvatar)

                    avatarUrl = imagePath.toString()
                }
            }

        editAvatar.setOnClickListener() {
            pickImageLauncher.launch("image/*")
        }


        val iconCallShowGreen: ImageView = findViewById(R.id.show_image_activity_icon_green)
        val iconCallShowRed: ImageView = findViewById(R.id.show_image_activity_icon_red)
        Glide.with(this)
            .load(iconAnswer)
            .into(iconCallShowGreen)
        Glide.with(this)
            .load(iconReject)
            .into(iconCallShowRed)



        // Bắt đầu hiệu ứng scale liên tục cho cả hai ImageView


        // Bắt đầu hiệu ứng xung động cho các ImageView
        val pulsatorGreen = findViewById<PulsatorLayout>(R.id.pulsator_green)
        val pulsatorRed = findViewById<PulsatorLayout>(R.id.pulsator_red)

        // Khởi động hiệu ứng




        BackgroundUrlShow.observe(this, Observer {
            updateEffectButtonFromBackgroundUrl()
        })

        EffectButton.observe(this, Observer {
            when (it) {
                "effect" -> {
                    pulsatorGreen.start()
                    pulsatorRed.start()
                    startContinuousScaleAnimation(iconCallShowGreen)
                    startContinuousScaleAnimation(iconCallShowRed)
                    stopTranslationAnimation(iconCallShowGreen, iconCallShowRed)
                    val animation = Animation(
                        BackgroundUrlShow.value.toString(),
                        EffectButton.value.toString()
                    )
                    addAnimationToListAnimationSave(this, animation)
                }
                "motion" -> {
                    pulsatorGreen.stop()
                    pulsatorRed.stop()
                    stopScaleAnimation(iconCallShowGreen, iconCallShowRed)
                    startTranslationAnimation(iconCallShowGreen, iconCallShowRed)
                    val animation = Animation(
                        BackgroundUrlShow.value.toString(),
                        EffectButton.value.toString()
                    )
                    addAnimationToListAnimationSave(this, animation)
                }
                "default" -> {
                    stopScaleAnimation(iconCallShowGreen, iconCallShowRed)
                    pulsatorGreen.stop()
                    pulsatorRed.stop()
                    stopTranslationAnimation(iconCallShowGreen, iconCallShowRed)
                    val animation = Animation(
                        BackgroundUrlShow.value.toString(),
                        EffectButton.value.toString()
                    )
                    addAnimationToListAnimationSave(this, animation)
                }
                "" -> {}
            }
        })

        val btnBack: ImageView = findViewById(R.id.show_image_activity_btn_back)
        btnBack.setOnClickListener() {
            showCustomDialog()
        }

//        val selectContactFragment = SelectContactFragment.newInstance(showImageActivity())
        val selectContactFragment = SelectContactFragment.newInstance(this@ShowImageActivity)
        supportFragmentManager.beginTransaction()
            .add(R.id.show_image_activity, selectContactFragment)
            .hide(selectContactFragment)
            .commit()
        val btnSubmit: ImageView = findViewById(R.id.show_image_activity_btn_submit)
        btnSubmit.setOnClickListener() {
            store.dispatch(AppAction.SetBackgroundUrl(backgroundUrl))
            store.dispatch(AppAction.SetIconCallShowId(iconAnswer, iconReject))
            store.dispatch(AppAction.SetAvatarUrl(avatarUrl))

            supportFragmentManager.beginTransaction()
                .show(selectContactFragment)
                .commit()
        }

        val listCallIcon: RecyclerView = findViewById(R.id.show_image_activity_list_call_icon)
        listCallIcon.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(listCallIcon)

        // Tạo danh sách mẫu
        val nameTopic = ListButtonCall
        listCallIcon.adapter = ListCallButtonAdapter(nameTopic, this@ShowImageActivity)

    }

    private fun startTranslationAnimation(vararg views: View) {
        views.forEach { view ->
            // Tạo ObjectAnimator để di chuyển lên xuống
            val translationAnimator = ObjectAnimator.ofFloat(view, "translationY", 30f, 0f).apply {
                duration = 500
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }

            // Bắt đầu hiệu ứng và lưu vào map
            translationAnimator.start()
            translationAnimatorMap[view] = translationAnimator
        }
    }

    // Hàm dừng hiệu ứng di chuyển cho các View
    private fun stopTranslationAnimation(vararg views: View) {
        views.forEach { view ->
            translationAnimatorMap[view]?.let { animator ->
                if (animator.isRunning) {
                    animator.cancel() // Dừng hiệu ứng
                }
                translationAnimatorMap.remove(view) // Xóa animator khỏi map sau khi dừng
            }
        }
    }


    private fun startContinuousScaleAnimation(vararg views: View) {
        views.forEach { view ->
            // Tạo hiệu ứng scaleX từ 1.0 xuống 0.8 và ngược lại
            val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.8f, 1.0f).apply {
                repeatCount = ObjectAnimator.INFINITE // Thiết lập để lặp lại vô tận
                repeatMode = ObjectAnimator.REVERSE // Đảo ngược khi hoàn thành mỗi chu kỳ
                interpolator = AccelerateDecelerateInterpolator() // Làm mượt hơn với interpolator
            }

            // Tạo hiệu ứng scaleY từ 1.0 xuống 0.8 và ngược lại
            val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.8f, 1.0f).apply {
                repeatCount = ObjectAnimator.INFINITE // Thiết lập để lặp lại vô tận
                repeatMode = ObjectAnimator.REVERSE // Đảo ngược khi hoàn thành mỗi chu kỳ
                interpolator = AccelerateDecelerateInterpolator() // Làm mượt hơn với interpolator
            }

            // Kết hợp 2 hiệu ứng scaleX và scaleY
            val animatorSet = AnimatorSet().apply {
                playTogether(scaleX, scaleY)
                duration = 800 // Tăng thời gian thực hiện mỗi chu kỳ hiệu ứng để mượt hơn
                start()
            }

            // Lưu AnimatorSet vào map
            animatorSetMap[view] = animatorSet
        }
    }

    private fun stopScaleAnimation(vararg views: View) {
        views.forEach { view ->
            animatorSetMap[view]?.let { animatorSet ->
                if (animatorSet.isRunning) {
                    animatorSet.end() // Dừng hoàn toàn hiệu ứng đang chạy
                    animatorSet.cancel() // Hủy bỏ animator để không tái sử dụng lại
                }
                animatorSetMap.remove(view) // Xóa AnimatorSet khỏi map sau khi dừng
            }
        }
    }

    fun changeAvatar(imageAvatar: ImageView, editAvatar: ImageView) {
        val avatarUrl = store.state.avatarUrl
        if (avatarUrl != "") {
            Glide.with(this)
                .load(avatarUrl)
                .into(imageAvatar)
        } else {
            editAvatar.visibility = GONE
            Glide.with(this)
                .load(avatarUrl)
                .into(imageAvatar)
        }
    }

    override fun changeButtonCall(answerUrl: String, dejectUrl: String) {
        val iconCallShowGreen: ImageView = findViewById(R.id.show_image_activity_icon_green)
        val iconCallShowRed: ImageView = findViewById(R.id.show_image_activity_icon_red)
        Glide.with(this)
            .load(answerUrl)
            .into(iconCallShowGreen)
        Glide.with(this)
            .load(dejectUrl)
            .into(iconCallShowRed)
    }

    private fun showCustomDialog() {
        // Tạo builder cho dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<TextView>(R.id.dialog_title).text = "BACK"
        // Thiết lập các sự kiện click cho các nút trong dialog
        dialogView.findViewById<CardView>(R.id.btn_yes).setOnClickListener {
            // Thực hiện hành động khi chọn "Có"
            alertDialog.dismiss() // Đóng dialog
            event_click = EVENT_CLICK.POPUP_BTN_YES
            showPopupLoadAds(TYPE_ADS.InterstitialAd)
        }

        dialogView.findViewById<CardView>(R.id.btn_no).setOnClickListener {
            alertDialog.dismiss()
        }

        // Tải Native Ad vào dialog
        loadNativeAd(dialogView.findViewById(R.id.dialog_ads), "show image activity")

        alertDialog.show() // Hiển thị dialog
    }

    private fun loadNativeAd(adContainer: FrameLayout, location:String) {
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
                    firebase.logFirebaseParamEvent("ad_native_impress",  "location",
                        location
                    )
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    firebase.logFirebaseParamEvent("ad_native_click",  "location", location)
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        
        showCustomDialog()
    }
}
