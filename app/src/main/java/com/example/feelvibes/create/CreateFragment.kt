package com.example.feelvibes.create

import com.example.feelvibes.databinding.FragmentCreateBinding
import com.example.feelvibes.viewbinds.FragmentBind

class CreateFragment : FragmentBind<FragmentCreateBinding>(FragmentCreateBinding::inflate) {

    //TODO: implement WRITE and READ external file request when first time clicking create button.

    override fun onReady() {
        mainActivity.renameToolBar("Create")
    }
}