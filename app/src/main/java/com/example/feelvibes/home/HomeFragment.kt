package com.example.feelvibes.home

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentHomeBinding
import com.example.feelvibes.layout.AuthorizationRequestLayout
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.view_model.HomeViewModel
import com.example.feelvibes.viewbinds.FragmentBind

class HomeFragment : FragmentBind<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var accountViewModel : AccountViewModel
    private val authorizationRequestLayout = AuthorizationRequestLayout()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
    }

    override fun onReady() {
        homeViewModel.layoutState = HomeViewModel.Layouts.HOME
        mainActivity.renameToolBar("Home")
        mainActivity.unpadMainView()
        mainActivity.showToolBar(isHome = true)
        authorizationRequestLayout.setup(this, binding.authorizationReqInclude)
        onProfileClickedEvent()
    }

    private fun onProfileClickedEvent() {
        mainActivity.profileClickedListener = {
            mainActivity.checkAuth {
                try {
                    if (it == null)
                        authorizationRequestLayout.show()
                    else {
                        accountViewModel.selectedUserId = accountViewModel.currentUser?.uid
                        findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}