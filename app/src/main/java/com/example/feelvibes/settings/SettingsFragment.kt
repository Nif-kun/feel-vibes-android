package com.example.feelvibes.settings

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.databinding.FragmentSettingsBinding
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.viewbinds.FragmentBind


class SettingsFragment : FragmentBind<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private lateinit var accountViewModel : AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
    }

    override fun onReady() {
        super.onReady()
        onLogoutEvent()
    }

    private fun onLogoutEvent() {
        binding.signOutBtn.setOnClickListener {
            mainActivity.mAuth.signOut()
            accountViewModel.currentUser = null
            findNavController().popBackStack()
        }
    }

}