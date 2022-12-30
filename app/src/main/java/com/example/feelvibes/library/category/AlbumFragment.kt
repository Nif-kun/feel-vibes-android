package com.example.feelvibes.library.category

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.MainActivityViewModel
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentAlbumBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.LibraryCategoryFragment
import com.example.feelvibes.library.recycler.adapters.LibraryRecyclerAdapter
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.utils.MusicDataHandler

class AlbumFragment :
    LibraryCategoryFragment<FragmentAlbumBinding>(FragmentAlbumBinding::inflate),
    RecyclerItemClick {

    private lateinit var mainActivityViewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        categoryViewModel = ViewModelProvider(requireActivity())[CategoryViewModelHandler.AlbumViewModel::class.java]
        mainActivityViewModel.updateAlbumPlaylistDataList(
            MusicDataHandler.Collect(
                requireActivity(),
                PlaylistModel.Type.ALBUM,
                true)
                .sortedData)
    }

    override fun onReady() {
        binding.albumRecView.adapter = LibraryRecyclerAdapter(
            requireActivity(),
            this,
            mainActivityViewModel.albumPlaylistDataList)
        binding.albumRecView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView = binding.albumRecView
    }

    override fun onItemClick(pos: Int) {
        val selectedPlaylist = mainActivityViewModel.albumPlaylistDataList[pos]
        mainActivityViewModel.selectedPlaylist = selectedPlaylist
        mainActivity.renameToolBar(selectedPlaylist.name)
        findNavController().navigate(R.id.action_libraryFragment_to_selected_playlist)
    }
}