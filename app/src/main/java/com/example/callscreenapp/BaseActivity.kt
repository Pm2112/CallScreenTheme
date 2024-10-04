package com.example.callscreenapp

import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.bluewhaleyt.network.NetworkUtil
import com.example.callscreenapp.Impl.ClickRateAppListener
import com.example.callscreenapp.ads.AdmobBannerAd
import com.example.callscreenapp.ads.AdmobInterstitialAd
import com.example.callscreenapp.ads.AdmobOpenAd
import com.example.callscreenapp.ads.AdmobRewardAd
import com.example.callscreenapp.ads.REWARD_POS
import com.example.callscreenapp.ads.TYPE_ADS
import com.example.callscreenapp.databinding.PopupLoadAdsBinding
import com.example.callscreenapp.google_iab.BillingClientLifecycle
import com.example.callscreenapp.process.getRateAppMainCreate
import com.example.callscreenapp.ui.dialog.RateAppDialog
import com.example.callscreenapp.utils.Constants
import com.example.callscreenapp.utils.PRODUCT_ID
import com.example.callscreenapp.utils.PreferencesManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


abstract class BaseActivity : AppCompatActivity() {
    private var mLaunchSubForResult = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()) {
        AppOwner.getContext().isShowSub = false
        isFromBuySub = true
        checkSubToUpdateUI()
        if (subUpdateUI != null) subUpdateUI!!.checkSubUpdateUI()
    }

    interface SubUpdateUI {
        fun checkSubUpdateUI()
    }

    var subUpdateUI: SubUpdateUI? = null
    protected var isFromBuySub = false
    protected var isSplash = false
    protected var countTimeShowAds = 0

    protected lateinit var billingClientLifecycle: BillingClientLifecycle

    open fun checkSubToUpdateUI() {
        if (PreferencesManager.checkSUB() != null && isFromBuySub) {
            isFromBuySub = false
            if (Constants.productIdBuy == PRODUCT_ID.SUB_WEEKLY.stringValue) {
            } else if (Constants.productIdBuy == PRODUCT_ID.SUB_YEARLY.stringValue) {
            }
        }
    }

    var rewardPos = REWARD_POS.Home_ShopCoins_ID1
    var dialogLoadAds: Dialog? = null
    var countDownTimerLoadAds: CountDownTimer? = null
    var _isLoadAds: Boolean = false
    var isFetch: Boolean = false

    open fun closeAds(type_ads: TYPE_ADS?) {
        runOnUiThread {
            AppOwner.getContext().isShowPopup = false
            delEventDialogLoadAds()
        }
    }

    private fun delEventDialogLoadAds() {
        if (!isFinishing && !isDestroyed && dialogLoadAds != null && dialogLoadAds?.isShowing == true) {
            dialogLoadAds?.dismiss()
            dialogLoadAds = null
        }
        if (countDownTimerLoadAds != null) {
            countDownTimerLoadAds?.cancel()
            countDownTimerLoadAds = null
        }
    }

    fun showAds() {
        runOnUiThread { delEventDialogLoadAds() }
    }

    open fun haveReward() {
        runOnUiThread {}
    }

    open fun notHaveReward() {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        supportActionBar?.hide()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        billingClientLifecycle = AppOwner.getContext().billingClientLifecycle

        // Register purchases when they change.
        billingClientLifecycle.productFetch.observe(this) { purchases ->
            if(!isFetch) return@observe
            if (purchases.isNotEmpty()) {
                PreferencesManager.purchaseAndRestoreSuccess()
            } else {
                PreferencesManager.purchaseFailed()
            }

            checkSubSuccessfully(_isLoadAds)
        }
//        CrashDebugger.init(this)

    }

    protected fun showPopupNetworkError() {
        if (NetworkUtil.isNetworkAvailable(this)) return
        AlertDialog.Builder(this)
            .setTitle("Network error")
            .setMessage("The connection to the network is impossible. Please check the status of your connection or try again in a few minutes.")
            .setCancelable(false)
            .setPositiveButton(
                "OK"
            ) { dialog, which -> clickPopupNetworkErrorButtonOK() }
            .create()
            .show()
    }

    protected open fun clickPopupNetworkErrorButtonOK() {}
    fun loadSub(isLoadAds: Boolean) {
        if (NetworkUtil.isNetworkAvailable(AppOwner.getContext())) {
//             Run heavy request requiring strong network connection
            if (PreferencesManager.checkLifetime() != null) {
                nextScreen()
                return
            }
//            if (PreferencesManager.checkSUB() == null) {
//                PreferencesManager.purchaseFailed()
//                nextScreen()
//                return
//            }
//            PreferencesManager.purchaseFailed()

            Log.d("xxxyyy", "loadSub : " + isLoadAds)
            checkSub(isLoadAds)
        } else {
            nextScreen()
        }
    }


    fun checkSub(isLoadAds: Boolean){
        _isLoadAds = isLoadAds
        isFetch = true

        readyToCheckSub()
    }

    fun readyToCheckSub() {
        MainScope().launch {
            if (billingClientLifecycle.isReadyBillingConnector()){
                billingClientLifecycle.fetchSubPurchasedProducts()
            }else{
                delay(1000)
//                billingClientLifecycle.connectBillingConnector()
                readyToCheckSub()
            }
        }
    }


    open fun checkSubSuccessfully(isLoadAds: Boolean) {
        checkSubToUpdateUI()
        if (isLoadAds) nextScreen()
        _isLoadAds = false
    }

    protected fun isActivityRunning(activityClass: Class<*>): Boolean {
        val activityManager =
            baseContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = activityManager.getRunningTasks(Int.MAX_VALUE)

        for (task in tasks) {
            if (activityClass.canonicalName.equals(
                    task.baseActivity!!.className,
                    ignoreCase = true
                )
            ) return true
        }

        return false
    }

    open fun nextScreen() {
        if (PreferencesManager.checkSUB() == null) {
//            AdmobOpenAd.Instance.loadAds();
        } else {

        }
    }

    override fun onStart() {
        super.onStart()
        AdmobBannerAd.Instance.activity = this
        AdmobInterstitialAd.Instance.activity = this
        AdmobOpenAd.Instance.activity = this
        AdmobRewardAd.Instance.activity = this
    }

    override fun onResume() {
        super.onResume()
        if (!isSplash || !AppOwner.getContext().isShowSub) checkSub(false)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    
    fun setStatusBarAndNavigationBar(colorStatus: Int, colorNavigationBar: Int) {
        this.window.statusBarColor = ContextCompat.getColor(this, colorStatus)
        this.window.navigationBarColor =
            ContextCompat.getColor(this, colorNavigationBar)
    }

    fun showPopupLoadAds(type_ads: TYPE_ADS) {
        AppOwner.getContext().isShowPopup = true
        dialogLoadAds = Dialog(this)
        dialogLoadAds?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoadAds?.setCancelable(false)
        dialogLoadAds?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding = PopupLoadAdsBinding.inflate(dialogLoadAds!!.layoutInflater)
        dialogLoadAds?.setContentView(binding.getRoot())

//        binding.animationView.spin();

        //*** Admob ***
        if (type_ads == TYPE_ADS.RewardAd) AdmobRewardAd.Instance.loadAds() else if (type_ads == TYPE_ADS.InterstitialAd) AdmobInterstitialAd.Instance.loadAds()
        countTimeShowAds = 0
        countDownTimerLoadAds = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countTimeShowAds++
                if (countTimeShowAds < 3) return
                //*** Admob ***
                if (type_ads == TYPE_ADS.RewardAd && AdmobRewardAd.Instance.canShowAds()) {
                    AdmobRewardAd.Instance.ShowAds()
                } else if (type_ads == TYPE_ADS.InterstitialAd && AdmobInterstitialAd.Instance.canShowAds()) {
                    AdmobInterstitialAd.Instance.ShowAds()
                }
            }

            override fun onFinish() {
                if (!isFinishing && !isDestroyed && dialogLoadAds != null && dialogLoadAds!!.isShowing) {
                    dialogLoadAds?.dismiss()
                }

                //*** Admob ***
                if (type_ads == TYPE_ADS.RewardAd) {
                    if (AdmobRewardAd.Instance.canShowAds()) AdmobRewardAd.Instance.ShowAds() else closeAds(
                        type_ads
                    )
                } else if (type_ads == TYPE_ADS.InterstitialAd) {
                    if (AdmobInterstitialAd.Instance.canShowAds()) AdmobInterstitialAd.Instance.ShowAds() else closeAds(
                        type_ads
                    )
                }
            }
        }.start()
        dialogLoadAds?.show()
    }

    fun showScreenSub(fromActivity : Activity) {
        val intent = Intent(fromActivity, SubscriptionActivity::class.java)
        AppOwner.getContext().isShowSub = true
        mLaunchSubForResult.launch(intent)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
//        if (hasFocus) {
//            fitWindowFullScreen()
//        } else {
//
//        }
    }

    private fun fitWindowFullScreen() {
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}