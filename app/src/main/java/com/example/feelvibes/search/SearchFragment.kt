package com.example.feelvibes.search

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentSearchBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.recycler.adapters.LibraryRecyclerAdapter
import com.example.feelvibes.library.recycler.adapters.PlaylistRecyclerAdapter
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.player.recycler.PlayerPlaylistRecyclerAdapter
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter
import com.example.feelvibes.utils.MusicDataHandler
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.viewbinds.FragmentBind

class SearchFragment : FragmentBind<FragmentSearchBinding>(FragmentSearchBinding::inflate), RecyclerItemClick {

    private lateinit var createViewModel: CreateViewModel
    private lateinit var libraryViewModel: LibraryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
    }

    override fun onReady() {
        mainActivity.renameToolBar("Search")
        onSearchEvent()
        setupAdapters()
    }

    private fun setupAdapters() {
        binding.songsRecView.layoutManager = LinearLayoutManager(requireActivity())
        binding.playlistsRecView.layoutManager = LinearLayoutManager(requireActivity())
        binding.artistsRecView.layoutManager = LinearLayoutManager(requireActivity())
        binding.albumsRecView.layoutManager = LinearLayoutManager(requireActivity())
        binding.tagsRecView.layoutManager = LinearLayoutManager(requireActivity())
        binding.designsRecView.layoutManager = LinearLayoutManager(requireActivity())
        binding.lyricsRecView.layoutManager = LinearLayoutManager(requireActivity())
        binding.chordsRecView.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun onSearchEvent() {
        binding.searchInput.doOnTextChanged { text, start, before, count ->
            if (text?.isNotEmpty() == true) {
                updateSongsAdapter(text)
                updatePlaylistsAdapter(text)
                updateArtistsAdapter(text)
                updateAlbumsAdapter(text)
                updateTagsAdapter(text)
            } else {
                binding.nestedScrollView.visibility = View.GONE
            }
        }
    }

    private fun updateSongsAdapter(text: CharSequence) {
        val musicDataList = MusicDataHandler.Collect(requireActivity()).data.filter {
            it.title.contains(text, true)
        } as ArrayList<MusicModel>
        if (musicDataList.size > 0) {
            viewVisibility(binding.songsLabel, true)
            viewVisibility(binding.songsRecView, true)
            binding.songsRecView.adapter = PlaylistRecyclerAdapter(
                requireActivity(),
                this,
                musicDataList,
                0)
        } else {
            viewVisibility(binding.songsLabel, false)
            viewVisibility(binding.songsRecView, false)
        }
    }

    private fun updatePlaylistsAdapter(text: CharSequence) {
        if (libraryViewModel.customCollection.list.size < 1) {
            libraryViewModel.customCollection.populateFromStored(requireActivity())
        }
        val playlistCollection = libraryViewModel.customCollection.listExcept(arrayListOf(
            resources.getString(R.string.favorites),
            resources.getString(R.string.create_playlist))
        )
        val playlists = playlistCollection.filter {
            it.name.contains(text, true)
        } as ArrayList<PlaylistModel>
        if (playlists.size > 0) {
            viewVisibility(binding.playlistsLabel, true)
            viewVisibility(binding.playlistsRecView, true)
            binding.playlistsRecView.adapter = LibraryRecyclerAdapter(
                requireActivity(),
                this,
                playlists,
                textOnly = false,
                hideMore = false,
                1
            )
        } else {
            viewVisibility(binding.playlistsLabel, false)
            viewVisibility(binding.playlistsRecView, false)
        }
    }

    private fun updateArtistsAdapter(text: CharSequence) {
        if (libraryViewModel.artistCollection.list.size < 1) {
            libraryViewModel.artistCollection.populateFromStored(requireActivity())
        }
        val playlists = libraryViewModel.artistCollection.list.filter {
            it.name.contains(text, true)
        } as ArrayList<PlaylistModel>
        if (playlists.size > 0) {
            viewVisibility(binding.artistsLabel, true)
            viewVisibility(binding.artistsRecView, true)
            binding.artistsRecView.adapter = LibraryRecyclerAdapter(
                requireActivity(),
                this,
                playlists,
                textOnly = false,
                hideMore = true,
                2
            )
        } else {
            viewVisibility(binding.artistsLabel, false)
            viewVisibility(binding.artistsRecView, false)
        }
    }

    private fun updateAlbumsAdapter(text: CharSequence) {
        if (libraryViewModel.albumCollection.list.size < 1) {
            libraryViewModel.albumCollection.populateFromStored(requireActivity())
        }
        val playlists = libraryViewModel.albumCollection.list.filter {
            it.name.contains(text, true)
        } as ArrayList<PlaylistModel>
        if (playlists.size > 0) {
            viewVisibility(binding.albumsLabel, true)
            viewVisibility(binding.albumsRecView, true)
            binding.albumsRecView.adapter = LibraryRecyclerAdapter(
                requireActivity(),
                this,
                playlists,
                textOnly = false,
                hideMore = true,
                3
            )
        } else {
            viewVisibility(binding.albumsLabel, false)
            viewVisibility(binding.albumsRecView, false)
        }
    }

    private fun updateTagsAdapter(text: CharSequence) {
        if (libraryViewModel.tagCollection.list.size < 1) {
            libraryViewModel.tagCollection.populateFromStored(requireActivity())
        }
        val playlists = libraryViewModel.tagCollection.list.filter {
            it.name.contains(text, true)
        } as ArrayList<PlaylistModel>
        if (playlists.size > 0) {
            viewVisibility(binding.tagsLabel, true)
            viewVisibility(binding.tagsRecView, true)
            binding.tagsRecView.adapter = LibraryRecyclerAdapter(
                requireActivity(),
                this,
                playlists,
                textOnly = true,
                hideMore = true,
                4
            )
        } else {
            viewVisibility(binding.tagsLabel, false)
            viewVisibility(binding.tagsRecView, false)
        }
    }

    private fun viewVisibility(view: View, visible: Boolean) {
        if (visible) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    override fun onItemClick(pos: Int, id: Int) {
        when(id) {
            0 -> {

            }
            1 -> {

            }
            2 -> {

            }
            3 -> {

            }
            4 -> {

            }
        }
    }


}