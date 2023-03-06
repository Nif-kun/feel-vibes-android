package com.example.feelvibes.home.viewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.create.CreateFragment
import com.example.feelvibes.databinding.FragmentTextViewerBinding
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.rpc.context.AttributeContext.Resource


class TextViewerFragment : FragmentBind<FragmentTextViewerBinding>(FragmentTextViewerBinding::inflate) {

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
        setupTextViews()

        // Hide download if downloaded already
        if (createViewModel.chordsCollection.isEmpty())
            createViewModel.chordsCollection.populateFromStored(requireActivity())
        if (createViewModel.selectedTextModel != null) {
            when (createViewModel.currentCreateTab) {
                CreateFragment.CHORDS -> {
                    if (createViewModel.chordsCollection.has(createViewModel.selectedTextModel!!)) {
                        binding.downloadBtn.visibility = View.GONE
                    }
                }
                CreateFragment.LYRICS -> {
                    if (createViewModel.lyricsCollection.has(createViewModel.selectedTextModel!!)) {
                        binding.downloadBtn.visibility = View.GONE
                    }
                }
            }

        }
    }

    private fun onBackEvent() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun onDownloadEvent() {
        binding.downloadBtn.setOnClickListener {
            if (createViewModel.selectedTextModel != null) {
                when (createViewModel.currentCreateTab) {
                    CreateFragment.CHORDS -> {
                        if (createViewModel.chordsCollection.isEmpty())
                            createViewModel.chordsCollection.populateFromStored(requireActivity())
                        if (!createViewModel.chordsCollection.has(createViewModel.selectedTextModel!!)) {
                            createViewModel.chordsCollection.add(createViewModel.selectedTextModel!!)
                            createViewModel.chordsCollection.saveToStored(requireActivity())
                            binding.downloadBtn.visibility = View.GONE
                            Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    CreateFragment.LYRICS -> {
                        if (createViewModel.lyricsCollection.isEmpty())
                            createViewModel.lyricsCollection.populateFromStored(requireActivity())
                        if (!createViewModel.lyricsCollection.has(createViewModel.selectedTextModel!!)) {
                            createViewModel.lyricsCollection.add(createViewModel.selectedTextModel!!)
                            createViewModel.lyricsCollection.saveToStored(requireActivity())
                            binding.downloadBtn.visibility = View.GONE
                            Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupTextViews() {
        binding.titleView.text = createViewModel.selectedTextModel?.name ?: "Untitled"
        binding.textView.text = createViewModel.selectedTextModel?.text ?: "Error: text is missing!"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        createViewModel.selectedTextModel = null
        mainActivity.showMainMenu()
    }

}