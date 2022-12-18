package com.example.feelvibes.onboarding.page

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.*
import com.example.feelvibes.databinding.FragmentAccountFeatureBinding
import com.example.feelvibes.utils.MusicDataHandler

class AccountFeatureFragment : FragmentBind<FragmentAccountFeatureBinding>(FragmentAccountFeatureBinding::inflate) {

    override fun onReady() {
        binding.skip.setOnClickListener{
            onBoardingFinish()
            findNavController().navigate(R.id.action_onBoardingFragment_to_mainFragment)
        }
    }

    private fun onBoardingFinish() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("finished", true)
        editor.apply()
    }

}