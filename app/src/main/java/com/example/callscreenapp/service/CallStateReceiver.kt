package com.example.callscreenapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telecom.Call
import android.telephony.TelephonyManager
import android.util.Log
import com.example.callscreenapp.Impl.CallServiceListener

class CallStateReceiver(private val listener: CallServiceListener?) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("MyDialerActivity1", "Received call state run")
        val state = intent?.getIntExtra(MyInCallServiceImplementation.EXTRA_CALL_STATE, -1)
        Log.d("MyDialerActivity1", "Received call state: $state")
        when (state) {
            Call.STATE_DISCONNECTED -> {
                Log.d("MyDialerActivity1", "Call disconnected, finishing activity.")
                listener?.onDisconnected()
            }
            Call.STATE_ACTIVE -> {
                Log.d("MyDialerActivity1", "Call active.")
                listener?.onActive()
            }
        }
    }

}