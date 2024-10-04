package com.example.callscreenapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.callscreenapp.BaseActivity
import com.example.callscreenapp.Impl.ClickRateAppListener
import com.example.callscreenapp.Impl.NetworkChangeListener
import com.example.callscreenapp.R
import com.example.callscreenapp.ads.AdmobInterstitialAd
import com.example.callscreenapp.ads.AdmobOpenAd
import com.example.callscreenapp.ads.TYPE_ADS
import com.example.callscreenapp.native_ads.NativeAdsLoader
import com.example.callscreenapp.process.DataManager.Companion.dataCategory
import com.example.callscreenapp.process.DataManager.Companion.isAvatar
import com.example.callscreenapp.process.EvenFirebase
import com.example.callscreenapp.process.getRateAppMainCreate
import com.example.callscreenapp.redux.store.store
import com.example.callscreenapp.ui.dialog.RateAppDialog
import com.example.callscreenapp.ui.dialog.showNetworkCustomDialog

class NotificationActivity : BaseActivity(), NetworkChangeListener, ClickRateAppListener {

    private lateinit var rateAppDialog: RateAppDialog
    private lateinit var nativeAdsLoader: NativeAdsLoader
    private lateinit var firebase: EvenFirebase

    override fun onNetworkUnavailable() {
        runOnUiThread {
            showNetworkCustomDialog(this)
        }
    }

    override fun onClickLater() {

    }


    override fun closeAds(type_ads: TYPE_ADS?) {
        super.closeAds(type_ads)
        if (type_ads == TYPE_ADS.InterstitialAd) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notification)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        rateAppDialog = RateAppDialog(this, this, this)

        nativeAdsLoader = NativeAdsLoader(this)
        nativeAdsLoader.loadNativeAd(R.id.view_ads, R.layout.layout_native_ads, "notification activity")

        val urlItem = store.state.backgroundUrl
        val themeType = extractSegmentFromUrl(urlItem)
        urlItem.let {
            // Sử dụng Glide để tải hình ảnh và đặt làm background
            val imageView = findViewById<ImageView>(R.id.notification_background)
            Glide.with(this)
                .load(it)
//                .centerCrop() // Sử dụng centerCrop để giữ tỷ lệ của hình ảnh
                .into(imageView)
        }

        val avatarImage: ImageView = findViewById(R.id.notification_background_avatar)
        val avatarUrl = store.state.avatarUrl
        if (isAvatar.value == true) {
            Glide.with(this)
                .load(avatarUrl)
//            .centerCrop() // Sử dụng centerCrop để giữ tỷ lệ của hình ảnh
                .into(avatarImage)
        } else {
            Glide.with(this)
                .load("")
//            .centerCrop() // Sử dụng centerCrop để giữ tỷ lệ của hình ảnh
                .into(avatarImage)
        }


        val btnAnswer: ImageView = findViewById(R.id.notification_background_icon_green)
        val iconAnswer = store.state.iconCallShowGreen
        val btnReject: ImageView = findViewById(R.id.notification_background_icon_red)
        val iconReject = store.state.iconCallShowRed

        Glide.with(this)
            .load(iconAnswer)
//            .centerCrop() // Sử dụng centerCrop để giữ tỷ lệ của hình ảnh
            .into(btnAnswer)
        Glide.with(this)
            .load(iconReject)
//            .centerCrop() // Sử dụng centerCrop để giữ tỷ lệ của hình ảnh
            .into(btnReject)

        val btnHome: ImageView = findViewById(R.id.notification_activity_btn)
        btnHome.setOnClickListener() {
            showPopupLoadAds(TYPE_ADS.InterstitialAd)
        }
        val (main, create) = getRateAppMainCreate(this)
        if (main == true) {
            rateAppDialog.show()
        }


        firebase = EvenFirebase(this)
        firebase.logFirebaseParamEvent("create_successfully", "theme_type","$themeType")
    }

    fun extractSegmentFromUrl(url: String): String? {
        // Kiểm tra nếu URL chứa phần đặc biệt "Data_background/All/all"
        return if (url.contains("Data_background/All/all")) {
            // Gọi hàm riêng để xử lý khi URL chứa đoạn "Data_background/All/all"
            handleSpecialCase(url)
        } else {
            // Tiếp tục xử lý bình thường nếu không chứa "Data_background/All/all"
            val start = "https://callthemetest.s3.amazonaws.com/Data_background/"
            val startIndex = url.indexOf(start)

            // Nếu không tìm thấy start, trả về null
            if (startIndex == -1) return null

            // Cắt chuỗi từ sau phần "https://callthemetest.s3.amazonaws.com/Data_Background/"
            val remainingUrl = url.substring(startIndex + start.length)

            // Tìm vị trí dấu "/" đầu tiên sau đoạn đã cắt
            val endIndex = remainingUrl.indexOf("/")

            // Nếu không có "/", trả về null
            if (endIndex == -1) return null

            // Lấy chuỗi từ đầu đến trước dấu "/"
            remainingUrl.substring(0, endIndex)
        }
    }

    // Hàm xử lý khi gặp "Data_background/All/all"
    fun handleSpecialCase(url: String): String? {
        val start = url.substringAfterLast("_")
        val number = start.substringBefore(".webp").toIntOrNull() ?: return null // Chuyển đổi thành Int

        // Lấy tên từ dataCategory ở vị trí number - 1
        return dataCategory.value?.getOrNull(number - 1)?.name ?: "Unknown"
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
    }
}