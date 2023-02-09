package com.example.feelvibes.create

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentCreateBottomSheetBinding
import com.example.feelvibes.utils.InternalStorageHandler
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind

class CreateBottomSheet : FragmentBottomSheetDialogBind<FragmentCreateBottomSheetBinding>(
    FragmentCreateBottomSheetBinding::inflate
) {

    private lateinit var createViewModel: CreateViewModel
    private var editing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
    }

    override fun onReady() {
        onEditEvent()
        onRemoveEvent()
    }

    private fun onEditEvent() {
        binding.libraryBottomSheetEditBtn.setOnClickListener {
            editing = true
            when(createViewModel.currentCreateTab) {
                CreateFragment.DESIGNS -> findNavController().navigate(R.id.action_createBottomSheet_to_designEditorFragment)
                CreateFragment.LYRICS -> findNavController().navigate(R.id.action_createBottomSheet_to_textEditorFragment)
                CreateFragment.CHORDS -> findNavController().navigate(R.id.action_createBottomSheet_to_textEditorFragment)
            }
        }
    }

    private fun onRemoveEvent() {
        binding.libraryBottomSheetRemoveBtn.setOnClickListener {
            when (createViewModel.currentCreateTab) {
                CreateFragment.DESIGNS -> {
                    if (createViewModel.selectedDesignModel != null) {
                        val removed = createViewModel.designCollection.remove(createViewModel.selectedDesignModel!!)
                        Log.d("CreateFragment", "${createViewModel.selectedDesignModel!!.name} has been removed!")
                        if (removed) {
                            if (createViewModel.selectedDesignModel!!.backgroundImagePath.isNotEmpty())
                                InternalStorageHandler.deleteImage(
                                    mainActivity,
                                    createViewModel.selectedDesignModel!!.id+"_bg",
                                    arrayListOf("gif", "jpg", "jpeg", "png"))
                            if (createViewModel.selectedDesignModel!!.foregroundImagePath.isNotEmpty())
                                InternalStorageHandler.deleteImage(
                                    mainActivity,
                                    createViewModel.selectedDesignModel!!.id+"_fg",
                                    arrayListOf("gif", "jpg", "jpeg", "png"))
                            createViewModel.selectedDesignModel = null
                            createViewModel.designCollection.saveToStored(mainActivity)
                            createViewModel.selectedAdapter?.notifyItemRemoved(createViewModel.selectedItemPos)
                        }
                    }
                }
                CreateFragment.LYRICS -> {
                    if (createViewModel.selectedTextModel != null) {
                        val removed = createViewModel.lyricsCollection.remove(createViewModel.selectedTextModel!!)
                        Log.d("CreateFragment", "${createViewModel.selectedTextModel!!.name} has been removed!")
                        if (removed) {
                            createViewModel.selectedTextModel = null
                            createViewModel.lyricsCollection.saveToStored(mainActivity)
                            createViewModel.selectedAdapter?.notifyItemRemoved(createViewModel.selectedItemPos)
                        }
                    }

                }
                CreateFragment.CHORDS -> {
                    if (createViewModel.selectedTextModel != null) {
                        val removed = createViewModel.chordsCollection.remove(createViewModel.selectedTextModel!!)
                        Log.d("CreateFragment", "${createViewModel.selectedTextModel!!.name} has been removed!")
                        if (removed) {
                            createViewModel.selectedTextModel = null
                            createViewModel.chordsCollection.saveToStored(mainActivity)
                            createViewModel.selectedAdapter?.notifyItemRemoved(createViewModel.selectedItemPos)
                        }
                    }
                }
            }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (!editing) {
            createViewModel.selectedDesignModel = null
            createViewModel.selectedTextModel = null
        }
        createViewModel.selectedAdapter = null
        createViewModel.selectedItemPos = -1
    }

}