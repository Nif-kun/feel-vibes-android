package com.example.feelvibes.library.category

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.FragmentBind
import com.example.feelvibes.MainActivityViewModel
import com.example.feelvibes.databinding.FragmentArtistBinding
import com.example.feelvibes.library.LibraryRecyclerAdapter
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.utils.MusicDataHandler

class ArtistFragment : FragmentBind<FragmentArtistBinding>(FragmentArtistBinding::inflate) {

    override fun onReady() {
        val viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        viewModel.updateArtistPlaylistDataList(MusicDataHandler.Collect(requireActivity(), PlaylistModel.Type.ARTIST, true).sortedData)
        binding.artistRecView.adapter = LibraryRecyclerAdapter(requireActivity(), viewModel.artistPlaylistDataList)
        binding.artistRecView.layoutManager = LinearLayoutManager(requireActivity())
    }
}