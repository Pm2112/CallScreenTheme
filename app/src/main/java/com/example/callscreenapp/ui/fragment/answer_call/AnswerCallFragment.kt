package com.example.callscreenapp.ui.fragment.answer_call

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.callscreenapp.R

class AnswerCallFragment : Fragment() {

    companion object {
        fun newInstance() = AnswerCallFragment()
    }

    private val viewModel: AnswerCallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_answer_call, container, false)
    }
}