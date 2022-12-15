package com.example.feelvibes.onboarding.page

import androidx.viewpager2.widget.ViewPager2
import com.example.feelvibes.FragmentBind
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentShareFeatureBinding

class ShareFeatureFragment : FragmentBind<FragmentShareFeatureBinding>(FragmentShareFeatureBinding::inflate) {
    override fun onReady() {
        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.onboarding_view_pager)
        binding.next.setOnClickListener {
            viewPager.currentItem = 2
        }
    }
}