package com.example.feelvibes.onboarding

import androidx.fragment.app.Fragment
import com.example.feelvibes.FragmentBind
import com.example.feelvibes.databinding.FragmentOnboardingBinding
import com.example.feelvibes.onboarding.page.*

class OnBoardingFragment : FragmentBind<FragmentOnboardingBinding>(FragmentOnboardingBinding::inflate) {

    override fun onReady() {
        val fragmentList = arrayListOf<Fragment>(
            MusicFeatureFragment(),
            ShareFeatureFragment(),
            DesignFeatureFragment(),
            AccountFeatureFragment()
        )
        val adapter = OnBoardingViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        binding.onboardingViewPager.adapter = adapter
    }
}