package com.example.callscreenapp.service

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.telecom.Call
import android.telecom.InCallService
import android.util.Log
import androidx.core.net.toUri
import com.example.callscreenapp.process.getFlashSetting
import com.example.callscreenapp.process.getRingtone
import com.example.callscreenapp.process.getVibrateSetting
import com.example.callscreenapp.redux.action.AppAction
import com.example.callscreenapp.redux.store.store
import com.example.callscreenapp.ui.activity.MyDialerActivity

@Suppress("DEPRECATION")
class MyInCallServiceImplementation : InCallService() {
    companion object {
        const val ACTION_CALL_STATE_CHANGED = "com.example.callscreenapp.ACTION_CALL_STATE_CHANGED"
        const val EXTRA_CALL_STATE = "extra_call_state"
        const val EXTRA_CALL_RINGING = "extra_call_ringing"
        var currentCall: Call? = null
    }

    private var ringtone: Ringtone? = null
    private lateinit var vibrator: Vibrator
    private var isFlashOn = false
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private val handler = Handler(Looper.getMainLooper())

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        currentCall = call

        if (call.state == Call.STATE_RINGING) {
            handleIncomingCall(call)
        } else {
            handleOutgoingCall(call)
        }

        sendCallStateBroadcast(call.state)
    }

    private fun handleIncomingCall(call: Call) {
        // Mã xử lý cuộc gọi đến
        val (ringtone1, ringtone2) = getRingtone(this)
        val ringtoneUri = ringtone1?.toUri() ?: getDefaultRingtoneUri()
        val (flash1, flash2) = getFlashSetting(this)

        ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
        ringtone?.play()

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val phoneNumber = call.details.handle.schemeSpecificPart
        val (isVibrate, isVibrate1) = getVibrateSetting(this)
        if (isVibrate == true) {
            vibrate()
        }
        store.dispatch(AppAction.SetPhoneNumber(phoneNumber))
        call.registerCallback(callCallback)
        sendCallStateBroadcast(call.state)

        startActivity(Intent(this, MyDialerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_HISTORY
            putExtra(EXTRA_CALL_RINGING, true)
        })

        if (flash1 == true) {
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                cameraId = cameraManager.cameraIdList.first { id ->
                    cameraManager.getCameraCharacteristics(id)
                        .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                }
                flashLightOn()
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            } catch (e: NoSuchElementException) {
                Log.e("MyInCallService", "No camera with flash found")
            }
        }
    }

    private fun handleOutgoingCall(call: Call) {
        val phoneNumber = call.details.handle.schemeSpecificPart
        store.dispatch(AppAction.SetPhoneNumber(phoneNumber))
        call.registerCallback(callCallback)
        sendCallStateBroadcast(call.state)

        startActivity(Intent(this, MyDialerActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NO_HISTORY
            putExtra(EXTRA_CALL_RINGING, false)
        })
    }



    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        currentCall = null
        ringtone?.stop()
        if (::vibrator.isInitialized) {
            vibrator.cancel()
        }
        sendCallStateBroadcast(Call.STATE_DISCONNECTED)
        // Tắt flash nhấp nháy
        val (flash1, flash2) = getFlashSetting(this)
        if (flash1 == true) {
            flashLightOff()
        }
    }

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            when (state) {
                Call.STATE_DISCONNECTED -> {
                    currentCall = null
                    ringtone?.stop()
                    sendCallStateBroadcast(state)
                    call.unregisterCallback(this)
                    val (flash1, flash2) = getFlashSetting(this@MyInCallServiceImplementation)
                    if (flash1 == true) {
                        flashLightOff()
                    }
                }
                Call.STATE_ACTIVE -> {
                    sendCallStateBroadcast(state)
                }
            }
        }
    }

    private fun sendCallStateBroadcast(state: Int) {
        Log.d("MyDialerActivity1", "Sending broadcast for state: $state")
        val intent = Intent(ACTION_CALL_STATE_CHANGED).apply {
            putExtra(EXTRA_CALL_STATE, state)
        }
        sendBroadcast(intent)
    }


    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(10000, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibrationEffect)
        } else {
            vibrator.vibrate(10000)
        }
    }

    private fun flashLightOn() {
        isFlashOn = true
        flashLightBlink()
    }

    private fun flashLightOff() {
        handler.removeCallbacksAndMessages(null)
        try {
            cameraManager.setTorchMode(cameraId, false)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        isFlashOn = false
    }

    private fun flashLightBlink() {
        handler.postDelayed({
            try {
                cameraManager.setTorchMode(cameraId, isFlashOn)
                isFlashOn = !isFlashOn
                flashLightBlink()
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }, 500)
    }

    private fun getDefaultRingtoneUri(): Uri {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    }
}
