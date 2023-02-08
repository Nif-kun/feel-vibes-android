package com.example.feelvibes.library.playlist

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentLibraryPlaylistBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.recycler.adapters.PlaylistRecyclerAdapter
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter
import com.example.feelvibes.viewbinds.FragmentBind


class PlaylistFragment :
    FragmentBind<FragmentLibraryPlaylistBinding>(FragmentLibraryPlaylistBinding::inflate),
    RecyclerItemClick {

    private lateinit var libraryViewModel : LibraryViewModel
    private var onMoreClickPos = -1
    private var selectedMusic = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
    }

    override fun onReady() {
        mainActivity.showToolBarBack()
        setupRecyclerAdapter()
    }

    private fun setupRecyclerAdapter() {
        if (libraryViewModel.selectedPlaylist != null) {
            binding.libraryPlaylistRecView.adapter = PlaylistRecyclerAdapter(
                requireActivity(),
                this,
                libraryViewModel.selectedPlaylist!!.list)
            binding.libraryPlaylistRecView.layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    override fun onItemClick(pos: Int) {
        if (libraryViewModel.selectedPlaylist != null) {
            libraryViewModel.selectedMusic = libraryViewModel.selectedPlaylist!!.list[pos]
            libraryViewModel.selectedMusicPos = pos
            selectedMusic = true
            findNavController().navigate(R.id.action_libraryPlaylistFragment_to_playerFragment)
        }
    }

    override fun onMoreClick(pos: Int) {
        super.onMoreClick(pos)
        onMoreClickPos = pos
        if (libraryViewModel.selectedPlaylist != null) {
            libraryViewModel.selectedMusic = libraryViewModel.selectedPlaylist!!.list[pos]
            val isPlaylist = libraryViewModel.selectedPlaylist!!.type == PlaylistModel.Type.PLAYLIST
            val isDefault = libraryViewModel.selectedPlaylist!!.type == PlaylistModel.Type.DEFAULT
            if (isPlaylist || isDefault) {
                libraryViewModel.selectedAdapter = binding.libraryPlaylistRecView.adapter as ItemRecyclerAdapter
                libraryViewModel.selectedAdapterPos = pos
            }
        }
        findNavController().navigate(R.id.action_libraryPlaylistFragment_to_libraryPlaylistBottomSheetFragment)
    }

    override fun onResume() {
        super.onResume()
        mainActivity.showToolBarBack()
    }

    override fun onPause() {
        super.onPause()
        mainActivity.hideToolBarBack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!selectedMusic && !libraryViewModel.navFromSticky) {
            libraryViewModel.selectedPlaylist = null
        }
    }

}