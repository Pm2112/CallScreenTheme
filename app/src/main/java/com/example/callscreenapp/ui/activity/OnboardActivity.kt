package com.example.callscreenapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.callscreenapp.R
import com.example.callscreenapp.adapter.OnboardPagerAdapter
import com.example.callscreenapp.data.ListCategoryAll
import com.example.callscreenapp.process.saveOnboard
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAdOptions
import android.util.Log
import com.example.callscreenapp.BaseActivity
import com.example.callscreenapp.ads.AdmobOpenAd
import com.example.callscreenapp.native_ads.NativeAdsLoader
import com.example.callscreenapp.process.EvenFirebase
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.nativead.MediaView

class OnboardActivity : BaseActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var nextButton: TextView
    private lateinit var dotContainer: LinearLayout
    private lateinit var adLoader: AdLoader
    private lateinit var nativeAdsLoader: NativeAdsLoader
    private lateinit var firebase: EvenFirebase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboard)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebase = EvenFirebase(this)

        viewPager = findViewById(R.id.onboard_view_pager)
        dotContainer = findViewById(R.id.layout_dot_container)
        nextButton = findViewById(R.id.onboard_btn_next)

        val pagerAdapter = OnboardPagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter

        setupDotIndicator(viewPager)

        // Hiển thị dot đầu tiên là active
        selectDot(0)
        firebase.logFirebaseEvent("intro${1}")

        // Bắt sự kiện click vào nút "Next"
        nextButton.setOnClickListener {
            // Nếu vẫn còn fragment tiếp theo trong ViewPager
            if (viewPager.currentItem < pagerAdapter.count - 1) {
                // Di chuyển đến fragment tiếp theo
                viewPager.currentItem += 1
            } else {
                saveOnboard(this@OnboardActivity, false)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        nativeAdsLoader = NativeAdsLoader(this)
        nativeAdsLoader.loadNativeAd(R.id.onboard_ads, R.layout.layout_native_ads, "onboard activity")
    }

    override fun onResume() {
        super.onResume()
//        AdmobOpenAd.Instance.ShowAds()
    }



    private fun setupDotIndicator(viewPager: ViewPager) {
        val pagerAdapter = viewPager.adapter
        val dotCount = pagerAdapter?.count ?: 0

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(10, 0, 10, 0) // Đặt margin trái và phải là 5dp

        // Thêm icon dot vào LinearLayout với margin
        for (i in 0 until dotCount) {
            val dot = ImageView(this)
            dot.setImageResource(R.drawable.icon_dot_gray)
            dot.layoutParams = params // Đặt layout params với margin cho ImageView
            dotContainer.addView(dot)
        }

        // Cập nhật trạng thái của icon dot khi trượt ViewPager
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                selectDot(position)
                firebase.logFirebaseEvent("intro${position + 1}")
                // Kiểm tra nếu là fragment cuối cùng thì thay đổi văn bản của nút "Next"
                if (position == dotCount - 1) {
                    nextButton.text = getString(R.string.onboard_btn_get_started)
                } else {
                    nextButton.text = getString(R.string.onboard_btn_text)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun selectDot(position: Int) {
        val dotCount = dotContainer.childCount
        for (i in 0 until dotCount) {
            val dot = dotContainer.getChildAt(i) as ImageView
            dot.setImageResource(if (i == position) R.drawable.icon_dot_active else R.drawable.icon_dot_gray)
        }
    }
}