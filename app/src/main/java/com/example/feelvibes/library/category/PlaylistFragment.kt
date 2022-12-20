package com.example.feelvibes.library.category

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.MainActivityViewModel
import com.example.feelvibes.databinding.FragmentPlaylistBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.LibraryCategoryFragment
import com.example.feelvibes.library.LibraryRecyclerAdapter
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.utils.MusicDataHandler

class PlaylistFragment :
    LibraryCategoryFragment<FragmentPlaylistBinding>(FragmentPlaylistBinding::inflate),
    RecyclerItemClick {

    private lateinit var mainActivityViewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        categoryViewModel = ViewModelProvider(requireActivity())[CategoryViewModelHandler.PlaylistViewModel::class.java]
        mainActivityViewModel.updateAlbumPlaylistDataList(
            MusicDataHandler.Collect(
                requireActivity(),
                PlaylistModel.Type.ALBUM,
                true)
                .sortedData)
    }

    override fun onReady() {
        binding.createPlaylistThumbnail.setColorFilter(com.google.android.material.R.color.design_default_color_primary_variant)
        binding.playlistRecView.adapter = LibraryRecyclerAdapter(
            requireActivity(),
            this,
            mainActivityViewModel.customPlaylistDataList)
        binding.playlistRecView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView = binding.playlistRecView
    }

    override fun onItemClick(pos: Int) {
        mainActivityViewModel.selectedPlaylist = mainActivityViewModel.customPlaylistDataList[pos]
    }

}