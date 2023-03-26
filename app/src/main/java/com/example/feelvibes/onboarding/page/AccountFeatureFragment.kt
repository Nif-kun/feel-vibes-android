package com.example.feelvibes.onboarding.page

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentAccountFeatureBinding
import com.example.feelvibes.view_model.OnBoardingViewModel
import com.example.feelvibes.viewbinds.FragmentBind

class AccountFeatureFragment : FragmentBind<FragmentAccountFeatureBinding>(FragmentAccountFeatureBinding::inflate) {

    private lateinit var onBoardingViewModel: OnBoardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBoardingViewModel = ViewModelProvider(requireActivity())[OnBoardingViewModel::class.java]
    }

    override fun onReady() {
        onSkipEvent()
        onLoginEvent()
    }

    private fun onSkipEvent() {
        binding.skip.setOnClickListener{
            onBoardingFinish()
            mainActivity.showMainMenu()
            mainActivity.padMainView()
            findNavController().navigate(R.id.action_onBoardingFragment_to_main_nav)
        }
    }

    private fun onBoardingFinish() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("finished", true)
        editor.apply()
    }

    private fun onLoginEvent() {
        onBoardingViewModel.loginListener = {
            onBoardingFinish()
            mainActivity.showMainMenu()
            findNavController().navigate(R.id.action_onBoardingFragment_to_main_nav)
        }
    }

}