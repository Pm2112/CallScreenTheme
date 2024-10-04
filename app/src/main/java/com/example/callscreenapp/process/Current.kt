package com.example.callscreenapp.process

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Param
import java.util.Calendar

class EvenFirebase(context: Context) {
    private val mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logFirebaseEvent(eventName: String) {
        val bundle = Bundle()
        mFirebaseAnalytics.logEvent(eventName, bundle)
        // Thêm dòng Log để kiểm tra
        Log.d("FirebaseEvent", "Logged event: $eventName")
    }

    fun logFirebaseParamEvent(
        eventName: String,
        parameterName: String,
        paramValue: String
    ) {
        val bundle = Bundle()
        when (parameterName) {
            "location" -> {
                bundle.putString(Param.LOCATION, paramValue)
            }
            "theme_type" -> {
                bundle.putString(Param.CONTENT_TYPE, paramValue)
            }
        }

        mFirebaseAnalytics.logEvent(eventName, bundle)
        Log.d(
            "FirebaseEvent",
            "Logged event: $eventName, param: $paramValue"
        )
    }
}