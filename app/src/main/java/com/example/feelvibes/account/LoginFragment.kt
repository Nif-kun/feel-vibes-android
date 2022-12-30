package com.example.feelvibes.account

import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentLoginBinding
import com.example.feelvibes.viewbinds.FragmentBind

class LoginFragment : FragmentBind<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    override fun onReady() {
        binding.loginHintTextViewRight.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

}