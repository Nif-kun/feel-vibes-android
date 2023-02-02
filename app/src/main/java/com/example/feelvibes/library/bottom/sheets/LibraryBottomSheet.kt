package com.example.feelvibes.library.bottom.sheets

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentLibraryBottomSheetBinding
import com.example.feelvibes.library.LibraryCreatePlaylistDialog
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind


class LibraryBottomSheet : FragmentBottomSheetDialogBind<FragmentLibraryBottomSheetBinding>(
    FragmentLibraryBottomSheetBinding::inflate) {

    private object LayoutStateTypes {
        const val DEFAULT = 0
        const val EDITING = 1
    }

    private lateinit var libraryViewModel : LibraryViewModel
    private var updateStored = false
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
    }

    override fun onReady() {
        setupButtons()
    }

    private fun setupButtons() {
        setupEditButton()
        setupRemoveButton()
    }

    private fun setupEditButton() {
        binding.libraryBottomSheetEditBtn.setOnClickListener {
            if (libraryViewModel.selectedPlaylist != null) {
                // Commenting out functions below due to time constraints:
                //layoutState(layoutStateTypes.EDITING)
                //updateStored = true
                findNavController().popBackStack()
                val dialog = LibraryCreatePlaylistDialog()
                isEditing = true
                dialog.show(mainActivity.supportFragmentManager, "createPlaylistDialog")
            }
        }
    }

    private fun setupRemoveButton() {
        binding.libraryBottomSheetRemoveBtn.setOnClickListener {
            if (libraryViewModel.selectedPlaylist != null) {
                libraryViewModel.selectedPlaylistCollection?.list?.remove(libraryViewModel.selectedPlaylist)
                libraryViewModel.selectedAdapter!!.notifyItemRemoved(libraryViewModel.selectedAdapterPos)
                updateStored = true
                findNavController().popBackStack()
            }
        }
    }

    private fun layoutState(state : Int) {
        when(state) {
            LayoutStateTypes.EDITING -> {
                hideDefaultLayout()
                showEditLayout()
            } else -> {
                hideEditLayout()
                showDefaultLayout()
            }
        }
    }

    private fun showEditLayout() {
        binding.includeCreatePlaylist.createPlaylistLayout.visibility = View.VISIBLE
    }

    private fun hideEditLayout() {
        binding.includeCreatePlaylist.createPlaylistLayout.visibility = View.GONE
    }

    private fun showDefaultLayout() {
        binding.libraryBottomSheetEditBtn.visibility = View.VISIBLE
        binding.libraryBottomSheetRemoveBtn.visibility = View.VISIBLE
    }

    private fun hideDefaultLayout() {
        binding.libraryBottomSheetEditBtn.visibility = View.GONE
        binding.libraryBottomSheetRemoveBtn.visibility = View.GONE
    }

    // Commenting out functions below due to time constraints:
    /*
    override fun onResume() {
        super.onResume()
        // Set playlist data to views when editing
    }

    override fun onStop() {
        super.onStop()

        if (updateStored) {
            libraryViewModel.customCollection.saveToStored(
                requireActivity(),
                arrayListOf(resources.getString(R.string.favorites), resources.getString(R.string.create_playlist)))
        }
        libraryViewModel.selectedAdapter = null
        libraryViewModel.selectedAdapterPos = -1
        libraryViewModel.selectedPlaylist = null
    }
    */

    override fun onStop() {
        super.onStop()
        if (updateStored) {
            libraryViewModel.customCollection.saveToStored(
                requireActivity(),
                arrayListOf(resources.getString(R.string.favorites), resources.getString(R.string.create_playlist)))
        }
        libraryViewModel.selectedPlaylistCollection = null
        if (!isEditing) {
            libraryViewModel.selectedAdapter = null
            libraryViewModel.selectedAdapterPos = -1
            libraryViewModel.selectedPlaylist = null
        }
    }
}