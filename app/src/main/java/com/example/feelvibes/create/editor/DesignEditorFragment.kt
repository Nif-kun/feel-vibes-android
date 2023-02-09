package com.example.feelvibes.create.editor

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentDesignEditorBinding
import com.example.feelvibes.utils.InternalStorageHandler
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import pl.droidsonroids.gif.GifDrawable

class DesignEditorFragment : FragmentBind<FragmentDesignEditorBinding>(FragmentDesignEditorBinding::inflate) {

    private lateinit var createViewModel: CreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
    }

    override fun onReady() {
        mainActivity.hideToolBar()
        mainActivity.hideMainMenu()
        mainActivity.unpadMainView()
        mainActivity.hideStickyPlayer()
        setupBackgroundFromLoad()
        setupForegroundFromLoad()
        findNavController().navigate(R.id.action_designEditorFragment_to_designEditorBottomSheet)
        onDrawerButtonEvent()
    }

    private fun setupBackgroundFromLoad() {
        if (createViewModel.selectedDesignModel != null) {
            if (createViewModel.selectedDesignModel!!.backgroundImagePath.contains("gif", true)) {
                binding.backgroundImageView.setImageDrawable(
                    InternalStorageHandler.loadGifDrawable(
                        context = mainActivity,
                        fileName = createViewModel.selectedDesignModel!!.backgroundImagePath))
            } else {
                binding.backgroundImageView.setImageBitmap(
                    InternalStorageHandler.loadImage(
                        activity = mainActivity,
                        filename = createViewModel.selectedDesignModel!!.backgroundImagePath))
            }
        }
    }

    private fun setupForegroundFromLoad() {
        if (createViewModel.selectedDesignModel != null) {
            if (createViewModel.selectedDesignModel!!.foregroundImagePath.contains("gif", true)) {
                binding.foregroundImageView.setImageDrawable(
                    InternalStorageHandler.loadGifDrawable(
                        context = mainActivity,
                        fileName = createViewModel.selectedDesignModel!!.foregroundImagePath))
            } else {
                binding.foregroundImageView.setImageBitmap(
                    InternalStorageHandler.loadImage(
                        activity = mainActivity,
                        filename = createViewModel.selectedDesignModel!!.foregroundImagePath))
            }
        }
    }

    private fun onDrawerButtonEvent() {
        binding.drawerButton.setOnClickListener {
            findNavController().navigate(R.id.action_designEditorFragment_to_designEditorBottomSheet)
            createViewModel.selectedBackgroundBitmap = null
            createViewModel.selectedForegroundBitmap = null
            createViewModel.selectedBackgroundDrawable = null
            createViewModel.selectedForegroundDrawable = null
        }
    }

    private fun setupBackgroundView() {
        if (createViewModel.selectedBackgroundDrawable != null) {
            binding.backgroundImageView.setImageDrawable(createViewModel.selectedBackgroundDrawable)

        } else if (createViewModel.selectedBackgroundBitmap != null) {
            binding.backgroundImageView.setImageBitmap(createViewModel.selectedBackgroundBitmap)
        }
        if (binding.backgroundImageView.drawable != null && binding.backgroundImageView.drawable is GifDrawable)
            (binding.backgroundImageView.drawable as GifDrawable).reset()
    }

    private fun setupForegroundView() {
        if (createViewModel.selectedForegroundDrawable != null) {
            binding.foregroundImageView.setImageDrawable(createViewModel.selectedForegroundDrawable)
            (binding.foregroundImageView.drawable as GifDrawable).reset()
        } else if (createViewModel.selectedForegroundBitmap != null) {
            binding.foregroundImageView.setImageBitmap(createViewModel.selectedForegroundBitmap)
        }
        if (binding.foregroundImageView.drawable != null && binding.foregroundImageView.drawable is GifDrawable)
            (binding.foregroundImageView.drawable as GifDrawable).reset()
    }

    override fun onResume() {
        super.onResume()
        setupBackgroundView()
        setupForegroundView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.showToolBar()
        mainActivity.showMainMenu()
        mainActivity.padMainView()
        if (mainActivity.musicPlayer?.isPlaying() == true)
            mainActivity.showStickyPlayer()

        // Cleanup
        createViewModel.selectedDesignModel = null
        createViewModel.selectedBackgroundBitmap = null
        createViewModel.selectedForegroundBitmap = null
        createViewModel.selectedBackgroundDrawable = null
        createViewModel.selectedForegroundDrawable = null
    }

}