package com.example.callscreenapp.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.telecom.Call
import android.telecom.VideoProfile
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.callscreenapp.Impl.CallServiceListener
import com.example.callscreenapp.R
//import com.example.callscreenapp.data.realm
import com.example.callscreenapp.database.ContactDb
import com.example.callscreenapp.model.ContactData
import com.example.callscreenapp.process.displayImageFromInternalStorage
import com.example.callscreenapp.process.getDialerDataOne
import com.example.callscreenapp.process.getDialerDataTwo
import com.example.callscreenapp.process.jsonToList
import com.example.callscreenapp.redux.store.store
import com.example.callscreenapp.service.CallStateReceiver
import com.example.callscreenapp.service.MyInCallServiceImplementation
import com.example.callscreenapp.service.MyInCallServiceImplementation.Companion.ACTION_CALL_STATE_CHANGED
import com.example.callscreenapp.service.MyInCallServiceImplementation.Companion.EXTRA_CALL_STATE
import com.example.callscreenapp.ui.fragment.answer_call.AnswerCallFragment
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.ext.query
import pl.bclogic.pulsator4droid.library.PulsatorLayout
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
class MyDialerActivity : AppCompatActivity(), CallServiceListener {
    private val animatorSetMap: MutableMap<View, AnimatorSet> = mutableMapOf()
    private val translationAnimatorMap: MutableMap<View, ObjectAnimator> = mutableMapOf()

    private lateinit var callStateReceiver: BroadcastReceiver
    private var currentCall: Call? = null
    override fun onStart() {
        super.onStart()
        currentCall = MyInCallServiceImplementation.currentCall
    }

    private var callStartTime: Long = 0
    private val callTimeHandler = Handler()
    private lateinit var callTimeUpdater: Runnable

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_dialer)

        val listContact: List<ContactData> = jsonToList(this, "contact.json") ?: emptyList()
        Log.d("listContact", "listContact: $listContact")
        val sharedPref = getSharedPreferences("save_animation", Context.MODE_PRIVATE)
        val sharedPrefs = getSharedPreferences("save_show_avatar", Context.MODE_PRIVATE)

        val phoneNumber1 = store.state.phoneNumber

        val iconAvatar: ImageView = findViewById(R.id.avatar_image)
        val image: ImageView = findViewById(R.id.dialer_image)
        val btnAnswer: ImageView = findViewById(R.id.btn_answer)
        val btnReject: ImageView = findViewById(R.id.btn_reject)
        val phoneNumber: TextView = findViewById(R.id.phone_number)
        val phoneName: TextView = findViewById(R.id.phone_name)

        val pulsatorGreen = findViewById<PulsatorLayout>(R.id.pulsator_green)
        val pulsatorRed = findViewById<PulsatorLayout>(R.id.pulsator_red)
        var checkContact = false
        var itemContact = ContactData("", "", "", "", "", "", "")

        val showAvatar = sharedPrefs.getBoolean("save_show_avatar", false)
        Log.d("showAvatar", "showAvatar: $showAvatar")
        if (showAvatar) {
            iconAvatar.visibility = VISIBLE
        } else {
            iconAvatar.visibility = GONE
        }

        listContact.forEach { item ->
            if (item.phoneNumber == phoneNumber1) {
                checkContact = true
                itemContact = item
                Log.d("itemContact", "list contact $item.toString()")
            }
        }

        val animator = sharedPref.getString("save_animation", "default")
        Log.d("save_animation", "sv ${animator.toString()}")
        when (checkContact) {
            true -> {
                Log.d(
                    "zzzxxxyyy",
                    "itemContact: ${itemContact.phoneNumber}, ${itemContact.phoneName}, ${itemContact.avatarTheme}, ${itemContact.backgroundTheme}, ${itemContact.btnAnswer}, ${itemContact.btnReject}, ${itemContact.animation}"
                )
                displayImageFromInternalStorage(this, iconAvatar, itemContact.avatarTheme)
                displayImageFromInternalStorage(this, image, itemContact.backgroundTheme)
                displayImageFromInternalStorage(this, btnAnswer, itemContact.btnAnswer)
                displayImageFromInternalStorage(this, btnReject, itemContact.btnReject)
                phoneName.text = itemContact.phoneName
                phoneNumber.text = itemContact.phoneNumber
                Log.d("animation_ccc", "${itemContact.phoneName}, ${itemContact.animation}")
                when (animator) {
                    "effect" -> {
                        pulsatorGreen.start()
                        pulsatorRed.start()
                        startContinuousScaleAnimation(btnAnswer)
                        startContinuousScaleAnimation(btnReject)
                    }
                    "motion" -> {
                        startTranslationAnimation(btnAnswer, btnReject)
                    }
                    "default" -> {}
                    "" -> {}
                }
            }

            false -> {
                Log.d(
                    "zzzxxxyyy",
                    "itemContact: ${itemContact.phoneNumber}, ${itemContact.phoneName}, ${itemContact.avatarTheme}, ${itemContact.backgroundTheme}, ${itemContact.btnAnswer}, ${itemContact.btnReject}, ${itemContact.animation}"
                )
                displayImageFromInternalStorage(this, iconAvatar, "avatar_theme")
                displayImageFromInternalStorage(this, image, "background_theme")
                displayImageFromInternalStorage(this, btnAnswer, "icon_answer")
                displayImageFromInternalStorage(this, btnReject, "icon_reject")
                phoneNumber.text = phoneNumber1
                Log.d("animation_ccc", "${itemContact.phoneName}, ${itemContact.animation}")
                when (animator) {
                    "effect" -> {
                        pulsatorGreen.start()
                        pulsatorRed.start()
                        startContinuousScaleAnimation(btnAnswer)
                        startContinuousScaleAnimation(btnReject)
                    }
                    "motion" -> {
                        startTranslationAnimation(btnAnswer, btnReject)
                    }
                    "default" -> {}
                    "" -> {}
                }
            }
        }



        setupUI()
        setupReceivers()

        val isRinging =
            intent.getBooleanExtra(MyInCallServiceImplementation.EXTRA_CALL_RINGING, false)
        if (isRinging) {
            setClickListeners()
        } else {
            setCallClickListeners()
        }


        val btnRejectCall: ImageView = findViewById(R.id.btn_reject_call)
        btnRejectCall.setOnClickListener() {
            endCall()
        }


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

    private fun setupUI() {
        supportActionBar?.hide()
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun setupReceivers() {
//        callStateReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                val state = intent?.getIntExtra(MyInCallServiceImplementation.EXTRA_CALL_STATE, -1)
//                Log.d("MyDialerActivity1", "Received call state: $state")
//                when (state) {
//                    Call.STATE_DISCONNECTED -> {
//                        Log.d("MyDialerActivity1", "Call disconnected, finishing activity.")
//                        finish() // Kết thúc activity khi cuộc gọi bị ngắt
//                    }
//                    Call.STATE_ACTIVE -> {
//                        Log.d("MyDialerActivity1", "Call active.")
//
//                    }
//                }
//
//            }
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            registerReceiver(
//                callStateReceiver,
//                IntentFilter(MyInCallServiceImplementation.ACTION_CALL_STATE_CHANGED),
//                RECEIVER_NOT_EXPORTED
//            )
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupReceivers() {
        callStateReceiver = CallStateReceiver(this)

        val intentFilter = IntentFilter(ACTION_CALL_STATE_CHANGED)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                callStateReceiver,
                intentFilter,
                RECEIVER_EXPORTED
            )
        }
    }


    private fun setClickListeners() {
        findViewById<ImageView>(R.id.btn_answer).setOnClickListener {
            answerCall()
            startCallTimeUpdater()
            val btnRejectCall: ImageView = findViewById(R.id.btn_reject_call)
            val btnReject: ImageView = findViewById(R.id.btn_reject)
            val btnAnswer: ImageView = findViewById(R.id.btn_answer)
            val btnMute: ImageView = findViewById(R.id.btn_mute)
            val btnVolume: ImageView = findViewById(R.id.btn_volume)
            btnMute.visibility = VISIBLE
            btnVolume.visibility = VISIBLE
            btnReject.visibility = GONE
            btnRejectCall.visibility = VISIBLE
            btnAnswer.visibility = GONE
        }
        findViewById<ImageView>(R.id.btn_reject).setOnClickListener { rejectCall() }
    }

    private fun setCallClickListeners() {
        val btnRejectCall: ImageView = findViewById(R.id.btn_reject_call)
        val btnReject: ImageView = findViewById(R.id.btn_reject)
        val btnAnswer: ImageView = findViewById(R.id.btn_answer)
        val btnMute: ImageView = findViewById(R.id.btn_mute)
        val btnVolume: ImageView = findViewById(R.id.btn_volume)
        val btnDisconnect: ImageView = findViewById(R.id.btn_disconnect)
        btnMute.visibility = GONE
        btnVolume.visibility = GONE
        btnReject.visibility = GONE
        btnRejectCall.visibility = GONE
        btnAnswer.visibility = GONE
        btnDisconnect.visibility = VISIBLE

        btnDisconnect.setOnClickListener {
            disconnectCall()
        }
    }

    private fun answerCall() = currentCall?.answer(VideoProfile.STATE_AUDIO_ONLY)
    private fun rejectCall() {
        currentCall?.reject(false, "")
        finish()
        exitProcess(0)
    }

    override fun onDisconnected() {
        finish()
    }

    override fun onActive() {
        startCallTimeUpdater()
    }

    private fun endCall() {
        currentCall?.disconnect()
        stopCallTimeUpdater()  // Dừng cập nhật thời gian cuộc gọi
    }

    private fun disconnectCall() {
        currentCall?.disconnect()
    }

    private fun startCallTimeUpdater() {
        callStartTime = SystemClock.elapsedRealtime()
        callTimeUpdater = object : Runnable {
            override fun run() {
                val elapsedTime = SystemClock.elapsedRealtime() - callStartTime
                updateCallTime(elapsedTime)
                callTimeHandler.postDelayed(this, 1000)
            }
        }
        callTimeHandler.postDelayed(callTimeUpdater, 0)
    }

    private fun updateCallTime(timeInMillis: Long) {
        val seconds = (timeInMillis / 1000) % 60
        val minutes = (timeInMillis / 60000) % 60
        val hours = timeInMillis / 3600000
        val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        val callTimeTextView: TextView = findViewById(R.id.call_time)
        callTimeTextView.text = timeString
    }

    private fun stopCallTimeUpdater() {
        callTimeHandler.removeCallbacks(callTimeUpdater)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(callStateReceiver)
    }
}
