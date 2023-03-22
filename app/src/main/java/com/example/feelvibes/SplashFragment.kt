package com.example.feelvibes

import android.content.Context
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.databinding.FragmentSplashBinding
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.viewbinds.FragmentBind

class SplashFragment : FragmentBind<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private lateinit var accountViewModel : AccountViewModel

    private fun onBoardingFinished(force:Boolean=false) = requireActivity()
        .getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        .getBoolean("finished", false) || force

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
    }

    override fun onReady() {
        hideActionBar()
        accountViewModel.onBoardingFinished = onBoardingFinished()
        Log.d("Splash", "onBoardingFinished: ${accountViewModel.onBoardingFinished}")
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
            mainActivity.showMainMenu()
            mainActivity.padMainView()
        }
        else {
            findNavController().navigate(R.id.action_splashFragment_to_onBoardingFragment)
        }
    }

}