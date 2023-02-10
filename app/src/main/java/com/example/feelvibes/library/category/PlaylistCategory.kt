package com.example.feelvibes.library.category

import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentPlaylistBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.LibraryCategoryFragment
import com.example.feelvibes.library.LibraryCategoryHandler
import com.example.feelvibes.library.LibraryCreatePlaylistDialog
import com.example.feelvibes.library.recycler.adapters.LibraryRecyclerAdapter
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter
import com.example.feelvibes.view_model.LibraryViewModel

class PlaylistCategory :
    LibraryCategoryFragment<FragmentPlaylistBinding>(FragmentPlaylistBinding::inflate),
    RecyclerItemClick {

    private lateinit var libraryViewModel : LibraryViewModel

    // TODO
    //  Unplanned: thumbnail is automatically applied if there is a musicModel
    //  inside the playlistModel, and is initially using the default thumbnail.
    //  For now, this will be considered as feature. Hilariously.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        libraryViewModel.customCollection.populateFromStored(requireActivity())
        // The buttons were setup here before the adapter to ensure that they are placed on the top.
        setupCreatePlaylistButton()
        setupFavoritesPlaylist()
    }

    override fun onReady() {
        setupRecyclerAdapter()
        onSearchEvent()
    }

    private fun setupCreatePlaylistButton() {
        libraryViewModel.customCollection.push(PlaylistModel(
            resources.getString(R.string.create_playlist),
            PlaylistModel.Type.BUTTON
        ))
    }

    private fun setupFavoritesPlaylist() {
        // Check from shared preferences to populate the playlist
        libraryViewModel.customCollection.push(PlaylistModel(
            resources.getString(R.string.favorites),
            PlaylistModel.Type.DEFAULT
        ), true, 1)
        // Load Favorites playlist data
        libraryViewModel.customCollection.find(resources.getString(R.string.favorites))?.load(mainActivity)
    }

    private fun updateAdapter(playlists: ArrayList<PlaylistModel>) {
        binding.playlistRecView.adapter = LibraryRecyclerAdapter(
            requireActivity(),
            this,
            playlists,
            textOnly = false,
            hideMore = false
        )
    }

    private fun setupRecyclerAdapter() {
        updateAdapter(libraryViewModel.customCollection.list)
        binding.playlistRecView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView = binding.playlistRecView
    }

    private fun onSearchEvent() {
        mainActivity.searchBar?.doOnTextChanged { text, start, before, count ->
            if (text?.isNotEmpty() == true) {
                val playlistCollection = libraryViewModel.customCollection.listExcept(arrayListOf(
                    resources.getString(R.string.create_playlist))
                )
                val newList = playlistCollection.filter {
                    it.name.contains(text, true)
                } as ArrayList<PlaylistModel>
                updateAdapter(newList)
            } else {
                updateAdapter(libraryViewModel.customCollection.list)
            }
        }
    }

    override fun onItemClick(pos: Int) {
        val selectedPlaylist = libraryViewModel.customCollection.list[pos]
        val isCustomButton = selectedPlaylist.type == PlaylistModel.Type.BUTTON
        val isCreateButton = isCustomButton && selectedPlaylist.name.equals(getString(R.string.create_playlist), true)
        if (isCreateButton) {
            val dialog = LibraryCreatePlaylistDialog()
            dialog.show(mainActivity.supportFragmentManager, "createPlaylistDialog")
        } else {
            libraryViewModel.selectedPlaylist = selectedPlaylist
            mainActivity.renameToolBar(selectedPlaylist.name)
            findNavController().navigate(R.id.action_libraryFragment_to_libraryPlaylistFragment)
        }
    }

    override fun onMoreClick(pos: Int) {
        super.onMoreClick(pos)
        libraryViewModel.selectedPlaylistCollection = libraryViewModel.customCollection
        libraryViewModel.selectedPlaylist = libraryViewModel.customCollection.list[pos]
        libraryViewModel.selectedAdapter = binding.playlistRecView.adapter as ItemRecyclerAdapter
        libraryViewModel.selectedAdapterPos = pos
        findNavController().navigate(R.id.action_libraryFragment_to_libraryBottomSheet)
    }
}