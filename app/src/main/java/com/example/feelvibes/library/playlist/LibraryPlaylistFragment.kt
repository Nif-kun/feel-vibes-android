package com.example.feelvibes.library.playlist

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.MainActivityViewModel
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentLibraryPlaylistBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.recycler.adapters.PlaylistRecyclerAdapter
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter
import com.example.feelvibes.viewbinds.FragmentBind


class LibraryPlaylistFragment :
    FragmentBind<FragmentLibraryPlaylistBinding>(FragmentLibraryPlaylistBinding::inflate),
    RecyclerItemClick {

    private var onMoreClickPos = -1
    private lateinit var mainActivityViewModel : MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
    }

    override fun onReady() {
        mainActivity.showToolBarBack()
        setupRecyclerAdapter()
    }

    private fun setupRecyclerAdapter() {
        if (mainActivityViewModel.selectedPlaylist != null) {
            binding.libraryPlaylistRecView.adapter = PlaylistRecyclerAdapter(
                requireActivity(),
                this,
                mainActivityViewModel.selectedPlaylist!!.musicDataList)
            binding.libraryPlaylistRecView.layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    override fun onItemClick(pos: Int) {
        if (mainActivityViewModel.selectedPlaylist != null) {
            Log.d("SelectedMusic", mainActivityViewModel.selectedPlaylist!!.musicDataList[pos].title)
            // Do the music player nav here
        }
    }

    override fun onMoreClick(pos: Int) {
        super.onMoreClick(pos)
        onMoreClickPos = pos
        if (mainActivityViewModel.selectedPlaylist != null) {
            mainActivityViewModel.selectedMusic = mainActivityViewModel.selectedPlaylist!!.musicDataList[pos]
            val isPlaylist = mainActivityViewModel.selectedPlaylist!!.type == PlaylistModel.Type.PLAYLIST
            val isDefault = mainActivityViewModel.selectedPlaylist!!.type == PlaylistModel.Type.DEFAULT
            if (isPlaylist || isDefault) {
                mainActivityViewModel.selectedAdapter = binding.libraryPlaylistRecView.adapter as ItemRecyclerAdapter
                mainActivityViewModel.selectedAdapterPos = pos
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



}