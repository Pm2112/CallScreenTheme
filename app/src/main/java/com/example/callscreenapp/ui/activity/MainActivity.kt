package com.example.callscreenapp.ui.activity


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.callscreenapp.AppOwner
import com.example.callscreenapp.BaseActivity
import com.example.callscreenapp.Impl.CategoryAdsClickListener
import com.example.callscreenapp.Impl.DataFetchedListener
import com.example.callscreenapp.Impl.NetworkChangeListener
import com.example.callscreenapp.R
import com.example.callscreenapp.ads.AdmobBannerAd
import com.example.callscreenapp.ads.AdmobInterstitialAd
import com.example.callscreenapp.ads.TYPE_ADS
import com.example.callscreenapp.data.AvatarSize
import com.example.callscreenapp.data.ButtonAllSize
import com.example.callscreenapp.data.ButtonCallSize
import com.example.callscreenapp.data.CategoryAllSize
import com.example.callscreenapp.data.CategoryAnimalSize
import com.example.callscreenapp.data.CategoryAnimeSize
import com.example.callscreenapp.data.CategoryCastleSize
import com.example.callscreenapp.data.CategoryFantasySize
import com.example.callscreenapp.data.CategoryGameSize
import com.example.callscreenapp.data.CategoryGifSize
import com.example.callscreenapp.data.CategoryLoveSize
import com.example.callscreenapp.data.CategoryNatureSize
import com.example.callscreenapp.data.CategorySeaSize
import com.example.callscreenapp.data.CategoryTechSize
import com.example.callscreenapp.data.CreateListAvatar
import com.example.callscreenapp.data.CreateListButtonAll
import com.example.callscreenapp.data.CreateListButtonCall
import com.example.callscreenapp.data.CreateListCategoryAll
import com.example.callscreenapp.data.CreateListCategoryAnimal
import com.example.callscreenapp.data.CreateListCategoryAnime
import com.example.callscreenapp.data.CreateListCategoryCastle
import com.example.callscreenapp.data.CreateListCategoryFantasy
import com.example.callscreenapp.data.CreateListCategoryGame
import com.example.callscreenapp.data.CreateListCategoryGif
import com.example.callscreenapp.data.CreateListCategoryLove
import com.example.callscreenapp.data.CreateListCategoryNature
import com.example.callscreenapp.data.CreateListCategorySea
import com.example.callscreenapp.data.CreateListCategoryTech
import com.example.callscreenapp.data.ListAvatar
import com.example.callscreenapp.data.ListCategoryAll
import com.example.callscreenapp.model.Animation
import com.example.callscreenapp.model.ContactData
import com.example.callscreenapp.model.DataCate
import com.example.callscreenapp.model.DataTheme
import com.example.callscreenapp.model.ListAvatar
import com.example.callscreenapp.model.PhoneCallListImage
import com.example.callscreenapp.process.DataManager.Companion.ListAnimationSave
import com.example.callscreenapp.process.DataManager.Companion.dataCategory
import com.example.callscreenapp.process.EvenFirebase
import com.example.callscreenapp.process.JsonManager
import com.example.callscreenapp.process.getRateAppMainCreate
import com.example.callscreenapp.process.jsonToList
import com.example.callscreenapp.process.saveRateApp
import com.example.callscreenapp.service.NetworkChangeReceiver
import com.example.callscreenapp.service.NetworkUtil
import com.example.callscreenapp.ui.dialog.InternetDialog
import com.example.callscreenapp.ui.fragment.call_themes.CallThemesFragment
import com.example.callscreenapp.ui.fragment.customize.CustomizeFragment
import com.example.callscreenapp.ui.fragment.my_themes.MyThemesFragment
import com.example.callscreenapp.utils.Constants.rateApp
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class MainActivity : BaseActivity(), NetworkChangeListener, CategoryAdsClickListener,
    DataFetchedListener {

    // Lưu trữ các fragments để tái sử dụng
    private lateinit var callThemesFragment: CallThemesFragment
    private val customizeFragment by lazy { CustomizeFragment() }
    private val myThemesFragment by lazy { MyThemesFragment() }
    private lateinit var adLoader: AdLoader
    private lateinit var firebase: EvenFirebase

    private lateinit var bannerAds: RelativeLayout
    private var countClickMenu: Int = 0
    private var countClickCategory: Int = 0
    private var currentSelectedButton: Int = R.id.btn_call_themes

    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    private var checkNetWork: Boolean = true
    private var checkShowRateApp: Boolean = false

    override fun closeAds(type_ads: TYPE_ADS?) {
        super.closeAds(type_ads)
        if (type_ads == TYPE_ADS.InterstitialAd) {

        } else if (type_ads == TYPE_ADS.RewardAd) {

        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval = 5000L // 5 seconds

    private val checkInternetRunnable = object : Runnable {
        override fun run() {
            val isConnected = NetworkUtil.isNetworkAvailable(this@MainActivity)
            Log.d("InternetCheck", "Internet connection: $isConnected")
            if (isConnected) {
                Log.d("zzzxxx", "InternetCheck true")
                if (isInternetAvailable()) {
                    if (!checkNetWork) {
                        Log.d("zzzxxx", "checkNetWork: $checkNetWork run")
                        val url = "https://callthemetest.s3.amazonaws.com/data.json"
                        fetchJsonData(url, this@MainActivity)
                        val listImage: List<PhoneCallListImage> = ListCategoryAll
                        if (listImage.isNotEmpty()) {
                            val fragment =
                                supportFragmentManager.findFragmentByTag("CallThemesFragment") as? CallThemesFragment
                            Log.d("zzzxxx", "fragment: $fragment")
                            val customizeFragment =
                                supportFragmentManager.findFragmentByTag("CustomizeFragment") as? CustomizeFragment

                            fragment?.setListImage(fragment.requireView(), listImage)

                            val listAvatar: MutableList<ListAvatar> = ListAvatar
                            customizeFragment?.setAvatar(
                                customizeFragment.requireView(),
                                listAvatar
                            )
                            customizeFragment?.setButtonView(customizeFragment.requireView())
                            customizeFragment?.setListImage(customizeFragment.requireView())

                            checkNetWork = true
                        }

                    }
//                    Log.d("zzzxxx", "CategoryLoveSize: $CategoryLoveSize")
                } else {
                    Log.d("zzzxxx", "checkNetWork: $checkNetWork")
                    checkNetWork = false
                }
            } else {
                Log.d("zzzxxx", "InternetCheck false")
            }
            handler.postDelayed(this, checkInterval)
        }
    }

    override fun haveReward() {
        super.haveReward()
    }

    override fun onStart() {
        super.onStart()
        Log.d("onStartActivity", "MainActivity")
        registerReceiver(
            networkChangeReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        handler.post(checkInternetRunnable)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(networkChangeReceiver)
    }

    override fun onNetworkUnavailable() {
        runOnUiThread {
//            showNetworkCustomDialog(this)
            if (!isInternetAvailable()) {
                val internetDialog = InternetDialog()
                internetDialog.showNetworkCustomDialog(this)
            }
        }
    }

    fun isInternetAvailable(): Boolean {
        return try {
            val executor = Executors.newSingleThreadExecutor()
            val future = executor.submit<Boolean> {
                try {
                    val url = URL("https://www.google.com")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.connectTimeout = 2000 // 2 giây
                    connection.connect()
                    connection.responseCode == 200
                } catch (e: IOException) {
                    false
                }
            }
            future.get(2, TimeUnit.SECONDS)
        } catch (e: Exception) {
            false
        }
    }

    override fun onCategoryCountClicked() {
        countClickCategory++
        if (countClickCategory % 3 == 0) {
            showPopupLoadAds(TYPE_ADS.InterstitialAd)
        }
    }

    private val showRateAppRunnable = Runnable {
        val sharedPref = getSharedPreferences("save_show_rate", Context.MODE_PRIVATE)
        val isShowRate = sharedPref.getBoolean("save_show_rate", false)
        if (!isShowRate) {
            showRateAppDialog("save_show_rate")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        callThemesFragment = CallThemesFragment.newInstance(this)
        firebase = EvenFirebase(this)



        val sharedPref = getSharedPreferences("save_show_rate_onboard", Context.MODE_PRIVATE)
        val isShowRate = sharedPref.getBoolean("save_show_rate_onboard", false)
        if (!isShowRate) {
            showRateAppDialog("save_show_rate_onboard")
        }

        // Gắn CallThemesFragment vào MainActivity
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CallThemesFragment.newInstance(this))
                .commitNow()
        }

        networkChangeReceiver = NetworkChangeReceiver(this)

        bannerAds = findViewById(R.id.banner_view)

        val jsonManager = JsonManager()
        jsonManager.createJsonFileIfNotExist(this, "mytheme.json")
        jsonManager.createJsonFileIfNotExist(this, "contact.json")


        initializeFragments()
        setupButtons()

        bannerAds.postDelayed(Runnable {
            AdmobBannerAd.Instance.loadBanner(bannerAds)
        }, 200)




        handler.postDelayed(showRateAppRunnable, 45000)

        getCategory()

    }

    fun getCategory() {
        Log.d("zzzxxx", "fetchJsonData: fetchJsonData run")
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("zzzxxx", "fetchJsonData: CoroutineScope run")
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://callthemetest.s3.amazonaws.com/cate.json")
                .build()

            Log.d("zzzxxx", "fetchJsonData: client $client")
            Log.d(
                "zzzxxx",
                "fetchJsonData: request body ${request.body}, request url ${request.url}, request header ${request.headers}"
            )

            try {
                client.newCall(request).execute().use { response ->
                    Log.d("zzzxxx", "fetchJsonData: response $response")
                    if (response.isSuccessful) {
                        Log.d("zzzxxx", "fetchJsonData: Request success")
                        val body = response.body?.string()
                        body?.let {
                            Log.d("xxxzzz", "fetchJsonData: ${it[0]}")
                            val gson = Gson()
                            val itemType = object : TypeToken<List<DataCate>>() {}.type
                            dataCategory.postValue(gson.fromJson(it, itemType))

                            Log.d("dataCategory", "$dataCategory")
                        }
                    } else {
                        Log.e("zzzxxx", "fetchJsonData: Request failed with code ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Log.e("zzzxxx", "fetchJsonData: Exception occurred", e)
            }
        }
    }


    @SuppressLint("CommitTransaction")
    private fun initializeFragments() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, callThemesFragment, "CallThemesFragment")
            add(R.id.fragment_container, customizeFragment, "CustomizeFragment")
            add(R.id.fragment_container, myThemesFragment, "MyThemesFragment")
            // Chỉ hiển thị CallThemesFragment khi bắt đầu
            hide(customizeFragment)
            hide(myThemesFragment)
            commit()
        }
    }

    private fun setupButtons() {

        val btnCallThemes: ConstraintLayout = findViewById(R.id.btn_call_themes)
        val btnCustomize: ConstraintLayout = findViewById(R.id.btn_customize)
        val btnMyThemes: ConstraintLayout = findViewById(R.id.btn_my_themes)
        btnCallThemes.isSelected = true

        btnCallThemes.setOnClickListener {
            if (currentSelectedButton != R.id.btn_call_themes) {
                showFragment(callThemesFragment)
                btnCallThemes.isSelected = true
                btnCustomize.isSelected = false
                btnMyThemes.isSelected = false
                currentSelectedButton = R.id.btn_call_themes
                showInterstitialAd("countClickMenu")
            }
        }
        btnCustomize.setOnClickListener {
            if (currentSelectedButton != R.id.btn_customize) {
                showFragment(customizeFragment)
                btnCallThemes.isSelected = false
                btnCustomize.isSelected = true
                btnMyThemes.isSelected = false
                currentSelectedButton = R.id.btn_customize
                showInterstitialAd("countClickMenu")
            }
        }
        btnMyThemes.setOnClickListener {
            if (currentSelectedButton != R.id.btn_my_themes) {
                showFragment(myThemesFragment)
                btnCallThemes.isSelected = false
                btnCustomize.isSelected = false
                btnMyThemes.isSelected = true
                currentSelectedButton = R.id.btn_my_themes
                showInterstitialAd("countClickMenu")
            }
        }
    }

    fun showInterstitialAd(key_id: String) {
        AdmobInterstitialAd.Instance.countToShowAds(0, 3, key_id)
    }

    @SuppressLint("CommitTransaction")
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            // Ẩn tất cả các fragments
            supportFragmentManager.fragments.forEach {
                if (it != fragment) hide(it)
            }
            // Hiển thị fragment yêu cầu
            show(fragment)
            commit()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment != callThemesFragment) {
            showFragment(callThemesFragment)
            val btnCallThemes: ConstraintLayout = findViewById(R.id.btn_call_themes)
            val btnCustomize: ConstraintLayout = findViewById(R.id.btn_customize)
            val btnMyThemes: ConstraintLayout = findViewById(R.id.btn_my_themes)
            btnCallThemes.isSelected = true
            btnCustomize.isSelected = false
            btnMyThemes.isSelected = false
        } else {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop checking internet connectivity
        handler.removeCallbacks(checkInternetRunnable)
        handler.removeCallbacks(showRateAppRunnable)
    }

    private fun fetchJsonData(url: String, dataFetchedListener: DataFetchedListener) {
        Log.d("zzzxxx", "fetchJsonData: fetchJsonData run")
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("zzzxxx", "fetchJsonData: CoroutineScope run")
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            Log.d("zzzxxx", "fetchJsonData: client $client")
            Log.d(
                "zzzxxx",
                "fetchJsonData: request body ${request.body}, request url ${request.url}, request header ${request.headers}"
            )

            try {
                client.newCall(request).execute().use { response ->
                    Log.d("zzzxxx", "fetchJsonData: response $response")
                    if (response.isSuccessful) {
                        Log.d("zzzxxx", "fetchJsonData: Request success")
                        val body = response.body?.string()
                        body?.let {
                            Log.d("xxxzzz", "fetchJsonData: ${it[0]}")
                            val gson = Gson()
                            val itemType = object : TypeToken<List<DataTheme>>() {}.type
                            val itemList: List<DataTheme> = gson.fromJson(it, itemType)

                            CategoryAnimeSize = itemList[0].number
                            CategoryLoveSize = itemList[1].number
                            CategoryAnimalSize = itemList[2].number
                            CategoryNatureSize = itemList[3].number
                            CategoryGameSize = itemList[4].number
                            CategoryCastleSize = itemList[5].number
                            CategoryFantasySize = itemList[6].number
                            CategoryTechSize = itemList[7].number
                            CategorySeaSize = itemList[8].number
                            ButtonCallSize = itemList[9].number
                            AvatarSize = itemList[10].number
                            CategoryAllSize = itemList[11].number
                            CategoryGifSize = itemList[12].number
                            ButtonAllSize = itemList[13].number

                            withContext(Dispatchers.Main) {
                                dataFetchedListener.onDataFetched()
                            }
                        }
                    } else {
                        Log.e(
                            "zzzxxx",
                            "fetchJsonData: Request failed with code ${response.code}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("zzzxxx", "fetchJsonData: Exception occurred", e)
            }
        }
    }

    override fun onDataFetched() {
        Log.d("zzzxxx", "Create data")

        CreateListCategoryAll()
        CreateListCategoryAnime()
        CreateListCategoryLove()
        CreateListCategoryAnimal()
        CreateListCategoryNature()
        CreateListCategoryGame()
        CreateListCategoryCastle()
        CreateListCategoryFantasy()
        CreateListCategoryTech()
        CreateListCategorySea()
        CreateListCategoryGif()
        CreateListButtonCall()
        CreateListButtonAll()
        CreateListAvatar()
    }

    private fun showRateAppDialog(name: String) {
        // Tạo builder cho dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_rate, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val starOne: ImageView = dialogView.findViewById(R.id.star_one)
        val starTwo: ImageView = dialogView.findViewById(R.id.star_two)
        val starThree: ImageView = dialogView.findViewById(R.id.star_three)
        val starFour: ImageView = dialogView.findViewById(R.id.star_four)
        val starFive: ImageView = dialogView.findViewById(R.id.star_five)
        val btnLater: CardView = dialogView.findViewById(R.id.btn_later)
        val btnRate: CardView = dialogView.findViewById(R.id.btn_rate)
        val adContainer: FrameLayout = dialogView.findViewById(R.id.rate_ads)


        val sharedPref = getSharedPreferences(name, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putBoolean(name, true)
        editor.apply()

        val evenFirebase = EvenFirebase(this)
        val activityName = this@MainActivity.javaClass.simpleName
        evenFirebase.logFirebaseParamEvent(
            "show_rating", "location",
            activityName
        )

        loadNativeAd(adContainer, "MainActivity RateApp")
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
            alertDialog.dismiss()
        }

        btnRate.setOnClickListener {
            alertDialog.dismiss()
            if (countRate <= 3) {
                val email = "caprinixdev@gmail.com"
                val subject = "Feedback about the app CallScreenTheme"
                val body = "Hi, I would like to give feedback..."
                val uriText =
                    "mailto:$email?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}"
                val emailUri = Uri.parse(uriText)

                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = emailUri
                }

// Check if there is an app that can handle the intent
                if (emailIntent.resolveActivity(packageManager) != null) {
                    startActivity(emailIntent) // Open the email app
                } else {
                    // If no app can handle, show a message or handle the error
                    Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
                }
            } else {
                rateApp(this)
            }
            val (main, count) = getRateAppMainCreate(this)
            var c = 0
            if (count != null) {
                c = count
            }
            saveRateApp(this, main = false, c)
            evenFirebase.logFirebaseParamEvent(
                "success_rate", "location",
                activityName
            )
        }

        alertDialog.show() // Hiển thị dialog
    }

    private fun loadNativeAd(adContainer: FrameLayout, location: String) {
        adLoader =
            AdLoader.Builder(this, AppOwner.getContext().getString(R.string.native_ad_unit_id))
                .forNativeAd { nativeAd ->
                    val adView =
                        layoutInflater.inflate(
                            R.layout.layout_dialog_native_ads,
                            null
                        ) as NativeAdView
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
                        firebase.logFirebaseParamEvent(
                            "ad_native_impress", "location",
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


        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.bodyView as TextView).text = nativeAd.body
        (adView.mediaView as MediaView).mediaContent = nativeAd.mediaContent

        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        (adView.callToActionView as TextView).text = nativeAd.callToAction

        adView.setNativeAd(nativeAd)
    }

}