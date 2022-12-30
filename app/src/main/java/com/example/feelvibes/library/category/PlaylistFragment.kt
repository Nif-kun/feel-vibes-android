package com.example.feelvibes.library.category

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.MainActivityViewModel
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentPlaylistBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.LibraryCategoryFragment
import com.example.feelvibes.library.recycler.adapters.LibraryRecyclerAdapter
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
        mainActivityViewModel.updateCustomPlaylistDataList(
            MusicDataHandler.Collect(
                requireActivity(),
                PlaylistModel.Type.PLAYLIST,
                true)
                .sortedData,
            resources)
    }

    override fun onReady() {
        setupRecyclerAdapter()
    }

    override fun onItemClick(pos: Int) {
        val selectedPlaylist = mainActivityViewModel.customPlaylistDataList[pos]
        val isCustomButton = selectedPlaylist.type == PlaylistModel.Type.BUTTON
        val isCreateButton = isCustomButton && selectedPlaylist.name.equals(getString(R.string.create_playlist), true)
        if (isCreateButton) {
            mainActivityViewModel.addCustomCreatedPlaylist(PlaylistModel(
                "FOR FUCK SAKES",
                PlaylistModel.Type.PLAYLIST))
            binding.playlistRecView.adapter?.notifyItemInserted(mainActivityViewModel.customPlaylistDataList.size-1)
        } else {
            mainActivityViewModel.selectedPlaylist = selectedPlaylist
            mainActivity.renameToolBar(selectedPlaylist.name)
            findNavController().navigate(R.id.action_libraryFragment_to_selected_playlist)
        }
    }

    private fun setupRecyclerAdapter() {
        binding.playlistRecView.adapter = LibraryRecyclerAdapter(
            requireActivity(),
            this,
            mainActivityViewModel.customPlaylistDataList)
        binding.playlistRecView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView = binding.playlistRecView
    }
}