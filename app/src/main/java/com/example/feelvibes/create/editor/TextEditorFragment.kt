package com.example.feelvibes.create.editor

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.create.CreateFragment
import com.example.feelvibes.databinding.FragmentTextEditorBinding
import com.example.feelvibes.model.TextModel
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.viewbinds.FragmentBind

class TextEditorFragment : FragmentBind<FragmentTextEditorBinding>(
    FragmentTextEditorBinding::inflate
) {

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
        setupViews()
        onCancelEvent()
        onSaveEvent()
    }

    private fun setupViews() {
        if (createViewModel.selectedTextModel != null) {
            binding.textEdit.setText(createViewModel.selectedTextModel!!.text )
            binding.titleInput.setText(createViewModel.selectedTextModel!!.name)
        }
    }

    private fun onCancelEvent() {
        binding.cancelBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun onSaveEvent() {
        binding.saveBtn.setOnClickListener {
            var titleExists = false
            // collectionList is highly assumptive, can be dangerous, but do I give a flying fuck? Definitely not lmao.
            val collectionList = if (createViewModel.currentCreateTab == CreateFragment.LYRICS) {
                createViewModel.lyricsCollection.list
            } else {
                createViewModel.chordsCollection.list
            }
            for (project in collectionList) {
                if (project.name.equals(binding.titleInput.text.toString(), ignoreCase = true))
                    titleExists = true
            }
            val hasTitle = binding.titleInput.text.isNotEmpty()
            if (hasTitle) {
                if (createViewModel.selectedTextModel != null) {
                    createViewModel.selectedTextModel!!.text = binding.textEdit.text.toString()
                    if (createViewModel.currentCreateTab == CreateFragment.LYRICS) {
                        createViewModel.lyricsCollection.saveToStored(requireActivity())
                    }
                    if (createViewModel.currentCreateTab == CreateFragment.CHORDS) {
                        createViewModel.chordsCollection.saveToStored(requireActivity())
                    }
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                } else if (!titleExists) {
                    val model = TextModel(
                        binding.titleInput.text.toString(),
                        binding.titleInput.text.toString(),
                        binding.textEdit.text.toString()
                    )
                    if (createViewModel.currentCreateTab == CreateFragment.LYRICS) {
                        createViewModel.lyricsCollection.list.add(model)
                        createViewModel.lyricsCollection.saveToStored(requireActivity())
                    }
                    if (createViewModel.currentCreateTab == CreateFragment.CHORDS) {
                        createViewModel.chordsCollection.list.add(model)
                        createViewModel.chordsCollection.saveToStored(requireActivity())
                    }
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Title already exists!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Requires a title!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.showToolBar()
        mainActivity.showMainMenu()
        mainActivity.padMainView()
        if (mainActivity.musicPlayer?.isPlaying() == true)
            mainActivity.showStickyPlayer()

        // Cleanup
        createViewModel.selectedTextModel = null
    }

}