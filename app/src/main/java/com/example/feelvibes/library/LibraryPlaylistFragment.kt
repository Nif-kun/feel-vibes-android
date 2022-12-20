package com.example.feelvibes.library

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.FragmentBind
import com.example.feelvibes.MainActivityViewModel
import com.example.feelvibes.databinding.FragmentLibraryPlaylistBinding
import com.example.feelvibes.interfaces.RecyclerItemClick


class LibraryPlaylistFragment :
    FragmentBind<FragmentLibraryPlaylistBinding>(FragmentLibraryPlaylistBinding::inflate),
    RecyclerItemClick {

    private lateinit var viewModel : MainActivityViewModel

    override fun onReady() {
        viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        if (viewModel.selectedPlaylist != null) {
            binding.libraryPlaylistRecView.adapter = LibraryPlaylistRecyclerAdapter(
                requireActivity(),
                this,
                viewModel.selectedPlaylist!!.musicDataList)
            binding.libraryPlaylistRecView.layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    override fun onItemClick(pos: Int) {
        if (viewModel.selectedPlaylist != null) {
            Log.d("SelectedMusic", viewModel.selectedPlaylist!!.musicDataList[pos].title)
            findNavController().popBackStack()
        }
    }
}