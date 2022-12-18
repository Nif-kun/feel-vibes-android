package com.example.feelvibes.library.category

import com.example.feelvibes.FragmentBind
import com.example.feelvibes.databinding.FragmentPlaylistBinding

class PlaylistFragment : FragmentBind<FragmentPlaylistBinding>(FragmentPlaylistBinding::inflate) {

    override fun onReady() {

        binding.createPlaylistThumbnail.setColorFilter(com.google.android.material.R.color.design_default_color_primary_variant)
    }

}