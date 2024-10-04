package com.example.callscreenapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.callscreenapp.ui.fragment.onboard.OnboardFragment
import com.example.callscreenapp.ui.fragment.onboard.OnboardOneFragment
import com.example.callscreenapp.ui.fragment.onboard.OnboardTwoFragment


@Suppress("DEPRECATION")
class OnboardPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> OnboardFragment()
            1 -> OnboardOneFragment()
            2 -> OnboardTwoFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    override fun getCount(): Int {
        return NUM_PAGES
    }

    companion object {
        private const val NUM_PAGES = 3
    }
}