package com.example.feelvibes.library.playlist

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.feelvibes.MainActivityViewModel
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentLibraryPlaylistBottomSheetBinding
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind


class LibraryPlaylistBottomSheetFragment : FragmentBottomSheetDialogBind<FragmentLibraryPlaylistBottomSheetBinding>(
    FragmentLibraryPlaylistBottomSheetBinding::inflate) {

    private lateinit var mainActivityViewModel : MainActivityViewModel
    private var updateAdapter = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
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
                if (mainActivityViewModel.selectedMusic != null) {
                    mainActivityViewModel.addFavoritePlaylist(mainActivityViewModel.selectedMusic!!)

                    // If inside Favorites playlist
                    if (mainActivityViewModel.selectedPlaylist?.type == PlaylistModel.Type.DEFAULT)
                        updateAdapter = false
                }
            } else {
                button.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_favorite_border_24, null)
                button.text = getString(R.string.favorite)
                if (mainActivityViewModel.selectedMusic != null) {
                    mainActivityViewModel.removeFavoritePlaylist(mainActivityViewModel.selectedMusic!!)

                    // If inside Favorites playlist
                    if (mainActivityViewModel.selectedPlaylist?.type == PlaylistModel.Type.DEFAULT)
                        updateAdapter = true
                }
            }
        }
    }

    private fun isFavorite(): Boolean {
        val musicModel = mainActivityViewModel.favoritePlaylistModel.musicDataList.find {
                i -> i.path == mainActivityViewModel.selectedMusic?.path
        }
        return musicModel != null
    }

    override fun onResume() {
        super.onResume()
        binding.bottomSheetFavoriteBtn.isChecked = isFavorite()
    }

    override fun onStop() {
        super.onStop()

        // This can only occur if selected playlist is PLAYLIST or DEFAULT type.
        if (updateAdapter) {
            mainActivityViewModel.selectedAdapter!!.notifyItemRemoved(mainActivityViewModel.selectedAdapterPos)
            mainActivityViewModel.selectedAdapter = null
            mainActivityViewModel.selectedAdapterPos = -1
            updateAdapter = false
        }
        mainActivityViewModel.selectedMusic = null
    }

}