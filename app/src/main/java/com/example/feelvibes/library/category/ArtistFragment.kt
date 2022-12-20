package com.example.feelvibes.library.category

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.MainActivityViewModel
import com.example.feelvibes.databinding.FragmentArtistBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.LibraryCategoryFragment
import com.example.feelvibes.library.LibraryRecyclerAdapter
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.utils.MusicDataHandler

class ArtistFragment :
    LibraryCategoryFragment<FragmentArtistBinding>(FragmentArtistBinding::inflate),
    RecyclerItemClick {

    private lateinit var mainActivityViewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        categoryViewModel = ViewModelProvider(requireActivity())[CategoryViewModelHandler.ArtistViewModel::class.java]
        mainActivityViewModel.updateArtistPlaylistDataList(
            MusicDataHandler.Collect(
                requireActivity(),
                PlaylistModel.Type.ARTIST,
                true)
                .sortedData)
    }

    override fun onReady() {
        binding.artistRecView.adapter = LibraryRecyclerAdapter(
            requireActivity(),
            this,
            mainActivityViewModel.artistPlaylistDataList)
        binding.artistRecView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView = binding.artistRecView
    }

    override fun onItemClick(pos: Int) {
        mainActivityViewModel.selectedPlaylist = mainActivityViewModel.artistPlaylistDataList[pos]
    }
}