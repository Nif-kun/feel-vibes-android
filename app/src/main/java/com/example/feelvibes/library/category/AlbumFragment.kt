package com.example.feelvibes.library.category

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.FragmentBind
import com.example.feelvibes.MainActivityViewModel
import com.example.feelvibes.databinding.FragmentAlbumBinding
import com.example.feelvibes.library.LibraryRecyclerAdapter
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.utils.MusicDataHandler

class AlbumFragment : FragmentBind<FragmentAlbumBinding>(FragmentAlbumBinding::inflate) {

    override fun onReady() {
        val viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        viewModel.updateAlbumPlaylistDataList(MusicDataHandler.Collect(requireActivity(), PlaylistModel.Type.ALBUM, true).sortedData)
        binding.albumRecView.adapter = LibraryRecyclerAdapter(requireActivity(), viewModel.albumPlaylistDataList)
        binding.albumRecView.layoutManager = LinearLayoutManager(requireActivity())
    }
}