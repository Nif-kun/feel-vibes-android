package com.example.feelvibes.account

import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentRegisterBinding
import com.example.feelvibes.viewbinds.FragmentBind

class RegisterFragment : FragmentBind<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    override fun onReady() {
        binding.registerHintTextViewRight.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

}