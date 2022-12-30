package com.example.feelvibes

import android.content.Context
import android.os.Build.VERSION
import android.os.Handler
import android.os.Looper
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.databinding.FragmentSplashBinding
import com.example.feelvibes.viewbinds.FragmentBind

class SplashFragment : FragmentBind<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private fun onBoardingFinished() = requireActivity()
        .getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        .getBoolean("finished", false)

    override fun onReady() {
        hideActionBar()
        if (VERSION.SDK_INT < 31) {
            Handler(Looper.getMainLooper()).postDelayed({
                displayOnBoarding(onBoardingFinished())
            }, 1800)
        } else {
            displayOnBoarding(onBoardingFinished())
        }
    }

    private fun displayOnBoarding(flag : Boolean){
        if (flag) {
            findNavController().navigate(R.id.action_splashFragment_to_main_nav)
            mainActivity.showToolBar()
            mainActivity.showMainMenu()
            mainActivity.padMainView()
        }
        else {
            findNavController().navigate(R.id.action_splashFragment_to_onBoardingFragment)
        }
    }

}