package com.example.feelvibes.library.playlist

import android.content.DialogInterface
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentPlaylistBottomSheetSearchBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.recycler.adapters.SearchRecyclerAdapter
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind


class PlaylistBottomSheetSearch : FragmentBottomSheetDialogBind<FragmentPlaylistBottomSheetSearchBinding>(
    FragmentPlaylistBottomSheetSearchBinding::inflate), RecyclerItemClick {

    private lateinit var libraryViewModel : LibraryViewModel
    private var rawPlaylists = arrayListOf<PlaylistModel>()
    private var playlists = arrayListOf<PlaylistModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
    }

    override fun onReady() {
        rawPlaylists = libraryViewModel.customCollection.listExcept(arrayListOf(
            resources.getString(R.string.favorites),
            resources.getString(R.string.create_playlist))
        )

        setupRecyclerAdapter()
        onSearchEvent()
        onCancelEvent() // Incomplete func due to time constraint
        onConfirmEvent() // Incomplete func due to time constraint
    }

    private fun setupRecyclerAdapter() {
        playlists = rawPlaylists.filter {
            libraryViewModel.selectedMusic != null && !it.has(libraryViewModel.selectedMusic!!.path)
        } as ArrayList<PlaylistModel> // Only gets the Playlists that does not have the music.
        binding.playlistRecView.adapter = SearchRecyclerAdapter(
            requireActivity(),
            this,
            playlists,
            textOnly = true,
            hideMore = true)
        binding.playlistRecView.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun onSearchEvent() {
        binding.searchInput.doOnTextChanged { text, start, before, count ->
            val hasSelectedMusic = libraryViewModel.selectedMusic != null
            playlists = if (text != null && text.isNotEmpty()) {
                rawPlaylists.filter {
                     hasSelectedMusic && !it.has(libraryViewModel.selectedMusic!!.path) && it.name.contains(text, ignoreCase = true)
                } as ArrayList<PlaylistModel>
            } else {
                rawPlaylists.filter {
                    hasSelectedMusic && !it.has(libraryViewModel.selectedMusic!!.path)
                } as ArrayList<PlaylistModel>
            }

            // TODO
            //  This is such a dirty way of doing this but I couldn't care less when I'm deprived of life at this point.
            binding.playlistRecView.adapter = SearchRecyclerAdapter(
                requireActivity(),
                this,
                playlists,
                textOnly = true,
                hideMore = true)
        }
    }

    private fun onCancelEvent() {
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun onConfirmEvent() {
        binding.confirmButton.setOnClickListener {

        }
    }

    override fun onItemClick(pos: Int) {
        if (libraryViewModel.selectedMusic != null) {
            playlists[pos].add(libraryViewModel.selectedMusic!!)
        }
        libraryViewModel.customCollection.saveToStored(
            mainActivity,
            arrayListOf(resources.getString(R.string.favorites), resources.getString(R.string.create_playlist))
        )
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        playlists.clear() // cleaning unused memory
    }

}