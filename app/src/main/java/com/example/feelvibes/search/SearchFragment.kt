package com.example.feelvibes.search

import com.example.feelvibes.databinding.FragmentSearchBinding
import com.example.feelvibes.viewbinds.FragmentBind

class SearchFragment : FragmentBind<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    override fun onReady() {
        mainActivity.renameToolBar("Search")
    }
}