package com.example.feelvibes.library.playlist

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentLibraryPlaylistBottomSheetBinding
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind


class PlaylistBottomSheet : FragmentBottomSheetDialogBind<FragmentLibraryPlaylistBottomSheetBinding>(
    FragmentLibraryPlaylistBottomSheetBinding::inflate) {

    private lateinit var libraryViewModel : LibraryViewModel
    private var updateAdapter = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
    }

    override fun onReady() {
        // Setup buttons
        val isPlaylist = libraryViewModel.selectedPlaylist?.type == PlaylistModel.Type.PLAYLIST
        removeFromPlaylistVisibility(isPlaylist)
        onClickFavoriteEvent()
        onClickAddToPlaylistEvent()
        onClickRemoveFromPlaylistEvent()
    }

    private fun onClickFavoriteEvent() {
        binding.bottomSheetFavoriteBtn.addOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                button.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_favorite_24, null)
                button.text = getString(R.string.unfavorite)
                if (libraryViewModel.selectedMusic != null) {
                    libraryViewModel.customCollection.addIn(
                        resources.getString(R.string.favorites),
                        libraryViewModel.selectedMusic!!
                    )

                    // If inside Favorites playlist
                    // Note: it is false as it does not matter when inside Favorites playlist; It's already inside.
                    if (libraryViewModel.selectedPlaylist?.type == PlaylistModel.Type.DEFAULT)
                        updateAdapter = false
                }
            } else {
                button.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_favorite_border_24, null)
                button.text = getString(R.string.favorite)
                if (libraryViewModel.selectedMusic != null) {
                    libraryViewModel.customCollection.removeIn(
                        resources.getString(R.string.favorites),
                        libraryViewModel.selectedMusic!!
                    )

                    // If inside Favorites playlist
                    if (libraryViewModel.selectedPlaylist?.type == PlaylistModel.Type.DEFAULT)
                        updateAdapter = true
                }
            }
        }
    }

    private fun isFavorite(): Boolean {
        return libraryViewModel.customCollection
            .find(resources.getString(R.string.favorites))
            ?.has(libraryViewModel.selectedMusic?.path ?: "") ?: false
    }

    private fun onClickAddToPlaylistEvent() {
        binding.bottomSheetAddToPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryPlaylistBottomSheetFragment_to_playlistBottomSheetPlaylists)
        }
    }

    private fun onClickRemoveFromPlaylistEvent() {
        binding.bottomSheetRemoveFromPlaylistButton.setOnClickListener {
            libraryViewModel.selectedPlaylist?.list?.remove(libraryViewModel.selectedMusic)
            updateAdapter = true
            libraryViewModel.customCollection.saveToStored(
                mainActivity,
                arrayListOf(resources.getString(R.string.favorites), resources.getString(R.string.create_playlist))
            )
            removeFromPlaylistVisibility(false)
        }
    }

    fun addToPlaylistVisibility(visible : Boolean = true) {
        if (visible)
            binding.bottomSheetAddToPlaylistButton.visibility = View.VISIBLE
        else
            binding.bottomSheetAddToPlaylistButton.visibility = View.GONE
    }

    fun removeFromPlaylistVisibility(visible : Boolean = true) {
        if (visible)
            binding.bottomSheetRemoveFromPlaylistButton.visibility = View.VISIBLE
        else
            binding.bottomSheetRemoveFromPlaylistButton.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        binding.bottomSheetFavoriteBtn.isChecked = isFavorite()
        // Coming from PlaylistBottomSheetSearch:
        // check here if the size of playlist increased. Then apply the change, the same as with updateAdapter on onStop()
    }

    override fun onStop() {
        super.onStop()

        // Saves Favorites on fragment close
        libraryViewModel.customCollection.find(resources.getString(R.string.favorites))?.save(mainActivity)

        // This can only occur if selected playlist is PLAYLIST or DEFAULT type.
        if (updateAdapter) {
            libraryViewModel.selectedAdapter!!.notifyItemRemoved(libraryViewModel.selectedAdapterPos)
            libraryViewModel.selectedAdapter = null
            libraryViewModel.selectedAdapterPos = -1
            updateAdapter = false
        }
        libraryViewModel.selectedMusic = null
    }

}