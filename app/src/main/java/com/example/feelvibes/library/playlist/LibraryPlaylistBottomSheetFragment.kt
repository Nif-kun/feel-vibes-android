package com.example.feelvibes.library.playlist

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentLibraryPlaylistBottomSheetBinding
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind


class LibraryPlaylistBottomSheetFragment : FragmentBottomSheetDialogBind<FragmentLibraryPlaylistBottomSheetBinding>(
    FragmentLibraryPlaylistBottomSheetBinding::inflate) {

    private lateinit var libraryViewModel : LibraryViewModel
    private var updateAdapter = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
    }

    override fun onReady() {
        setupButtons()
    }

    private fun setupButtons() {
        setupFavoriteButton()
    }

    private fun setupFavoriteButton() {
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

    override fun onResume() {
        super.onResume()
        binding.bottomSheetFavoriteBtn.isChecked = isFavorite()
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