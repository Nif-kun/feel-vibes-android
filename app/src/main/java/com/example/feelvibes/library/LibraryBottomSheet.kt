package com.example.feelvibes.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentLibraryBottomSheetBinding
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind


class LibraryBottomSheet : FragmentBottomSheetDialogBind<FragmentLibraryBottomSheetBinding>(
    FragmentLibraryBottomSheetBinding::inflate) {

    private lateinit var libraryViewModel : LibraryViewModel
    private var updateStored = false

    private object layoutStateTypes {
        const val DEFAULT = 0
        const val EDITING = 1
    }

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
        // if clicked:
        //  updateStored = true
        binding.libraryBottomSheetEditBtn.setOnClickListener {
            if (libraryViewModel.selectedPlaylist != null) {
                updateStored = true
                // make edit visible.
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
            layoutStateTypes.EDITING -> {  } //show editing layout
            else -> {
                showDefaultLayout()
            }
        }
    }

    private fun showEditLayout() {
        // Create a layout and use include in xml
    }

    private fun hideEditLayout() {

    }

    private fun showDefaultLayout() {
        binding.libraryBottomSheetEditBtn.visibility = View.VISIBLE
        binding.libraryBottomSheetRemoveBtn.visibility = View.VISIBLE
    }

    private fun hideDefaultLayout() {
        binding.libraryBottomSheetEditBtn.visibility = View.GONE
        binding.libraryBottomSheetRemoveBtn.visibility = View.GONE
    }

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

}