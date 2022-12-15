package com.example.feelvibes.home

import com.example.feelvibes.FragmentBind
import com.example.feelvibes.databinding.FragmentHomeBinding

class HomeFragment : FragmentBind<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun onReady() {
        renameActionBar("Home")
    }



}