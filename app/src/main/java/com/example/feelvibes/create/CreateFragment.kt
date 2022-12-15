package com.example.feelvibes.create

import com.example.feelvibes.FragmentBind
import com.example.feelvibes.databinding.FragmentCreateBinding

class CreateFragment : FragmentBind<FragmentCreateBinding>(FragmentCreateBinding::inflate) {

    //TODO: implement WRITE and READ external file request when first time clicking create button.

    override fun onReady() {
        renameActionBar("Create")
    }
}