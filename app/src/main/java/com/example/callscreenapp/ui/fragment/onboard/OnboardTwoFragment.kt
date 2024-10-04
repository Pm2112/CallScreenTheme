package com.example.callscreenapp.ui.fragment.onboard

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import com.example.callscreenapp.R

class OnboardTwoFragment : Fragment() {

    companion object {
        fun newInstance() = OnboardTwoFragment()
    }

    private val viewModel: OnboardTwoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_onboard_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val videoView: VideoView = view.findViewById(R.id.video_view)
        val videoPath = "android.resource://" + requireActivity().packageName + "/" + R.raw.ae_1
        videoView.setVideoURI(Uri.parse(videoPath))
        videoView.start()
    }
}