package com.example.feelvibes.home

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentHomeBinding
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : FragmentBind<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var accountViewModel : AccountViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
    }

    override fun onReady() {
        mainActivity.renameToolBar("Home")
        mainActivity.showToolBar(true)
        mainActivity.checkAuth(true)
    }

}