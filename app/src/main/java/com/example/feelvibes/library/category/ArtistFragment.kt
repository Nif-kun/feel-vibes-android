package com.example.feelvibes.library.category

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentArtistBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.LibraryCategoryFragment
import com.example.feelvibes.library.recycler.adapters.LibraryRecyclerAdapter

class ArtistFragment :
    LibraryCategoryFragment<FragmentArtistBinding>(FragmentArtistBinding::inflate),
    RecyclerItemClick {

    private lateinit var libraryViewModel : LibraryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        categoryViewModel = ViewModelProvider(requireActivity())[CategoryViewModelHandler.ArtistViewModel::class.java]
        libraryViewModel.artistCollection.populateFromLocal(requireActivity())
    }

    override fun onReady() {
        setupRecyclerAdapter()
    }

    private fun setupRecyclerAdapter() {
        binding.artistRecView.adapter = LibraryRecyclerAdapter(
            requireActivity(),
            this,
            libraryViewModel.artistCollection.list)
        binding.artistRecView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView = binding.artistRecView
    }

    override fun onItemClick(pos: Int) {
        val selectedPlaylist = libraryViewModel.artistCollection.list[pos]
        libraryViewModel.selectedPlaylist = selectedPlaylist
        mainActivity.renameToolBar(selectedPlaylist.name)
        findNavController().navigate(R.id.action_libraryFragment_to_selected_playlist)
    }
}