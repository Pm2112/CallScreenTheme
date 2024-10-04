package com.example.callscreenapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.callscreenapp.ads.AdmobOpenAd
import com.example.callscreenapp.firebase.FbFirestore
import com.example.callscreenapp.google_iab.BillingClientLifecycle
import com.example.callscreenapp.ui.activity.MyDialerActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.firebase.FirebaseApp

class AppOwner : Application(), LifecycleObserver {
    var isShowSub = false
    var isShowPopup = false

    private var currentActivity: Activity? = null

    val billingClientLifecycle: BillingClientLifecycle
        get() = BillingClientLifecycle.getInstance(this)

    override fun onCreate() {
        super.onCreate()
        Log.d("xxxyyy", "application create")
        context = applicationContext
        FirebaseApp.initializeApp(this)
        MobileAds.initialize(getContext()) { initializationStatus: InitializationStatus? ->

        }

        // Theo dõi trạng thái activity
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {
                currentActivity = activity
            }

            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {
                if (currentActivity == activity) {
                    currentActivity = null
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                if (currentActivity == activity) {
                    currentActivity = null
                }
            }
        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppBackgrounded() {
        Log.d("xxxyyy", "ON_PAUSE App in background")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.d("xxxyyy", "ON_RESUME App in foreground")
        if (isShowSub) return
        if (isShowPopup) return
        //AdmobOpenAd.Instance.countToShowAds(2, 3, "COUNT_APP_OPEN_ADS");
        if (currentActivity !is MyDialerActivity) {
            AdmobOpenAd.Instance.ShowAds()
        }
    }



    override fun onTerminate() {
        super.onTerminate()
        FbFirestore.Instance.stopListener()
        billingClientLifecycle.destroyBillingConnector()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null

        // pick image
        var isSkipOpenAd: Boolean = false
        fun getContext(): AppOwner {
            return context as AppOwner
        }
    }
}
