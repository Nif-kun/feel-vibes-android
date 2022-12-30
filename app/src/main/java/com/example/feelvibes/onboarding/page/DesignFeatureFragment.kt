package com.example.feelvibes.onboarding.page

import androidx.viewpager2.widget.ViewPager2
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentDesignFeatureBinding
import com.example.feelvibes.viewbinds.FragmentBind

class DesignFeatureFragment : FragmentBind<FragmentDesignFeatureBinding>(FragmentDesignFeatureBinding::inflate) {
    override fun onReady() {
        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.onboarding_view_pager)
        binding.next.setOnClickListener {
            viewPager.currentItem = 3
        }
    }
}