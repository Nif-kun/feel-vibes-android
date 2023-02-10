package com.example.feelvibes.library.category

import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentTagBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.LibraryCategoryFragment
import com.example.feelvibes.library.LibraryCategoryHandler
import com.example.feelvibes.library.recycler.adapters.LibraryRecyclerAdapter
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.view_model.LibraryViewModel

class TagCategory :
    LibraryCategoryFragment<FragmentTagBinding>(FragmentTagBinding::inflate),
    RecyclerItemClick {

    private lateinit var libraryViewModel: LibraryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        categoryViewModel = ViewModelProvider(requireActivity())[LibraryCategoryHandler.TagViewModel::class.java]
        libraryViewModel.tagCollection.populateFromLocal(requireActivity())
    }

    override fun onReady() {
        setupRecyclerAdapter()
        onSearchEvent()
    }

    private fun updateAdapter(playlists: ArrayList<PlaylistModel>) {
        binding.tagRecView.adapter = LibraryRecyclerAdapter(
            requireActivity(),
            this,
            playlists,
            true)
    }

    private fun setupRecyclerAdapter() {
        updateAdapter(libraryViewModel.tagCollection.list)
        binding.tagRecView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView = binding.tagRecView
    }

    private fun onSearchEvent() {
        if (mainActivity.getSearchBar().text.isNotEmpty())
            search(mainActivity.getSearchBar().text)
        searchBarTextWatcher = mainActivity.getSearchBar().doOnTextChanged { text, _, _, _ ->
            search(text)
        }
    }

    private fun search(text: CharSequence?) {
        if (text?.isNotEmpty() == true) {
            val newList = libraryViewModel.tagCollection.list.filter {
                it.name.contains(text, true)
            } as ArrayList<PlaylistModel>
            updateAdapter(newList)
        } else {
            updateAdapter(libraryViewModel.tagCollection.list)
        }
    }

    override fun onItemClick(pos: Int) {
        val selectedPlaylist = libraryViewModel.tagCollection.list[pos]
        libraryViewModel.selectedPlaylist = selectedPlaylist
        mainActivity.renameToolBar(selectedPlaylist.name)
        findNavController().navigate(R.id.action_libraryFragment_to_libraryPlaylistFragment)
    }
}