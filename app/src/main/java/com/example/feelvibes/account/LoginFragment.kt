package com.example.feelvibes.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.feelvibes.FragmentBind
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentLoginBinding

class LoginFragment : FragmentBind<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    override fun onReady() {
        binding.loginHintTextViewRight.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

}