package com.example.callscreenapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast
import com.example.callscreenapp.Impl.NetworkChangeListener

@Suppress("DEPRECATION")
class NetworkChangeReceiver(private val listener: NetworkChangeListener?) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!isNetworkAvailable(context)) {
            listener?.onNetworkUnavailable()
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}