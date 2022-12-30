package com.example.feelvibes.home

import com.example.feelvibes.databinding.FragmentHomeBinding
import com.example.feelvibes.viewbinds.FragmentBind

class HomeFragment : FragmentBind<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun onReady() {
        mainActivity.renameToolBar("Home")
    }



}