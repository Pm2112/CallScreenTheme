package com.example.callscreenapp.ui.fragment.play_ringtone

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.net.toUri
import com.example.callscreenapp.AppOwner
import com.example.callscreenapp.R
import com.example.callscreenapp.native_ads.NativeAdsLoader
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@Suppress("DEPRECATION")
class PlayRingtoneFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_RINGTONE_URL = "ringtone_url"
        private const val ARG_RINGTONE_NAME = "ringtone_name"

        fun newInstance(ringtonePath: String, ringtoneName: String): PlayRingtoneFragment {
            val fragment = PlayRingtoneFragment()
            val bundle = Bundle().apply {
                putString(ARG_RINGTONE_URL, ringtonePath)
                putString(ARG_RINGTONE_NAME, ringtoneName)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    private val viewModel: PlayRingtoneViewModel by viewModels()

    private var mediaPlayer: MediaPlayer? = null
    private var ringtoneUrl: String = ""
    private lateinit var adLoader: AdLoader


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_play_ringtone, container, false)
    }

    @SuppressLint("SetTextI18n", "CutPasteId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        val adContainer: FrameLayout = view.findViewById(R.id.play_ringtone_ads)
        adLoader = AdLoader.Builder(requireActivity(), AppOwner.getContext().getString(R.string.native_ad_unit_id))
            .forNativeAd { nativeAd ->
                val adView =
                    layoutInflater.inflate(R.layout.layout_native_ads, null) as NativeAdView
                populateNativeAdView(nativeAd, adView)
                adContainer.removeAllViews()
                adContainer.addView(adView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdLoader", "Failed to load ad: ${adError.message}")
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())

        ringtoneUrl = arguments?.getString(ARG_RINGTONE_URL) ?: ""
        val ringtoneName = arguments?.getString(ARG_RINGTONE_NAME) ?: ""

        mediaPlayer = MediaPlayer().apply {
            setDataSource(ringtoneUrl)
            prepare()
        }

        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)
        val timeTextView = view.findViewById<TextView>(R.id.timeTextView)
        val timeCountTextView = view.findViewById<TextView>(R.id.timeCountTextView)

        val ringtoneTitle: TextView = view.findViewById(R.id.fragment_ringtone_title)
        ringtoneTitle.text = ringtoneName

        mediaPlayer?.setOnPreparedListener {
            seekBar.max = mediaPlayer?.duration ?: 0
            val durationMinutes = mediaPlayer?.duration?.div(1000)?.div(60)
            val durationSeconds = mediaPlayer?.duration?.div(1000)?.rem(60)
            timeCountTextView.text = String.format("%02d:%02d", durationMinutes, durationSeconds)
        }

        mediaPlayer?.setOnCompletionListener {
            // Reset thời gian và seekbar khi audio hoàn thành
            seekBar.progress = 0
            timeTextView.text = "00:00"
            view.findViewById<ImageView>(R.id.play_ringtone).isSelected = false // Reset trạng thái của nút
        }

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                seekBar.progress = mediaPlayer?.currentPosition ?: 0
                handler.postDelayed(this, 1000)
            }
        }, 0)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val minutes = progress / 60000
                    val seconds = (progress % 60000 / 1000)
                    timeTextView.text = String.format("%02d:%02d", minutes, seconds)
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Không cần xử lý
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Không cần xử lý
            }
        })

        val btnPlay: ImageView = view.findViewById(R.id.play_ringtone)
        btnPlay.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                pauseAudio(view)
                btnPlay.isSelected = false
            } else {
                playAudio(view, timeTextView)
                btnPlay.isSelected = true
            }
        }
    }

    private fun playAudio(view: View, timeTextView: TextView) {
        mediaPlayer?.start()
        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)
        seekBar.max = mediaPlayer?.duration ?: 0

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val currentPosition = mediaPlayer?.currentPosition ?: 0
                val minutes = currentPosition / 60000
                val seconds = (currentPosition % 60000 / 1000)
                timeTextView.text = String.format("%02d:%02d", minutes, seconds)

                seekBar.progress = currentPosition
                handler.postDelayed(this, 1000)
            }
        }, 0)
    }

    private fun pauseAudio(view: View) {
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Giải phóng tài nguyên khi hoạt động bị hủy
        mediaPlayer?.release()
        mediaPlayer = null
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
