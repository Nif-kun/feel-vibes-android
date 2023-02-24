package com.example.feelvibes.layout

import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.example.feelvibes.R
import com.example.feelvibes.databinding.AuthorizationReqLayoutBinding
import com.example.feelvibes.databinding.FragmentHomeBinding

// This thing is like a ballistic missile that will fuck you up if your layout is wrong.
// Refer to existing uses of it. Cuz it will fuck you up with how malleable it is.
class AuthorizationRequestLayout() {
    private lateinit var fragment: Fragment
    private lateinit var layout: ConstraintLayout
    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var skipFeatureBtn: TextView

    var popBack = false

    fun setup(fragment: Fragment, binding: AuthorizationReqLayoutBinding) {
        this.fragment = fragment
        layout = binding.authorizationReqLayout
        loginBtn = binding.loginBtn
        signUpBtn = binding.signUpBtn
        skipFeatureBtn = binding.skipFeatureBtn
        onLoginEvent()
        onSignUpEvent()
        onSkipFeatureEvent()
    }

    private fun onLoginEvent() {
        loginBtn.setOnClickListener {
            fragment.findNavController().navigate(R.id.action_global_homeLoginFragment)
        }
    }

    private fun onSignUpEvent() {
        signUpBtn.setOnClickListener {
            fragment.findNavController().navigate(R.id.action_global_homeRegisterFragment)
        }
    }

    private fun onSkipFeatureEvent() {
        skipFeatureBtn.setOnClickListener {
            if (popBack) {
                fragment.findNavController().popBackStack()
            } else {
                dismiss()
            }
        }
    }

    fun show() {
        layout.visibility = View.VISIBLE
    }
    fun dismiss() {
        layout.visibility = View.GONE
    }
}