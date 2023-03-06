package com.example.feelvibes.home.viewer

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.Global
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentDesignViewerBinding
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.viewbinds.FragmentBind

class DesignViewerFragment : FragmentBind<FragmentDesignViewerBinding>(FragmentDesignViewerBinding::inflate) {

    private lateinit var createViewModel: CreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
    }

    override fun onReady() {
        super.onReady()
        mainActivity.hideMainMenu()
        onBackEvent()
        onDownloadEvent()
        setupImageViews()
    }

    private fun onBackEvent() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun onDownloadEvent() {
        binding.downloadBtn.setOnClickListener {
            if (createViewModel.selectedDesignModel != null) {
                createViewModel.selectedDesignModel!!.saveImagesToInternal(requireActivity(), true)
                if (createViewModel.designCollection.isEmpty())
                    createViewModel.designCollection.populateFromStored(requireActivity())
                createViewModel.designCollection.add(createViewModel.selectedDesignModel!!)
                createViewModel.designCollection.saveToStored(requireActivity())
                Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupImageViews() {
        val foregroundImagePath = createViewModel.selectedDesignModel?.foregroundImagePath
        if (!foregroundImagePath.isNullOrEmpty())
            Glide.with(this).load(Uri.parse(foregroundImagePath)).into(binding.foregroundImageView)

        val backgroundImagePath = createViewModel.selectedDesignModel?.backgroundImagePath
        if (!backgroundImagePath.isNullOrEmpty())
            Glide.with(this).load(Uri.parse(backgroundImagePath)).into(binding.backgroundImageView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        createViewModel.selectedDesignModel = null
        mainActivity.showMainMenu()
    }

}