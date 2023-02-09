package com.example.feelvibes.create.category

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.R
import com.example.feelvibes.create.CreateCategoryFragment
import com.example.feelvibes.create.recycler.CreateRecyclerAdapter
import com.example.feelvibes.databinding.FragmentLyricsCategoryBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.TextModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter
import com.example.feelvibes.view_model.CreateViewModel


class LyricsCategory :
    CreateCategoryFragment<FragmentLyricsCategoryBinding>(FragmentLyricsCategoryBinding::inflate),
    RecyclerItemClick {

    private lateinit var createViewModel: CreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
        createViewModel.lyricsCollection.populateFromStored(requireActivity())
    }

    override fun onReady() {
        onCreateLyricsEvent()
        setupRecyclerAdapter()
    }

    private fun setupRecyclerAdapter() {
        binding.lyricsRecView.adapter = CreateRecyclerAdapter(
            requireActivity(),
            recyclerItemClick = this,
            createViewModel.lyricsCollection.list
        )
        binding.lyricsRecView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView = binding.lyricsRecView
    }

    private fun onCreateLyricsEvent() {
        binding.createLyricsButton.setOnClickListener {
            findNavController().navigate(R.id.action_createFragment_to_textEditorFragment)
        }
    }

    override fun onItemClick(pos: Int) {
        createViewModel.selectedTextModel = createViewModel.lyricsCollection.list[pos] as TextModel
        createViewModel.selectedAdapter = binding.lyricsRecView.adapter as ItemRecyclerAdapter
        createViewModel.selectedItemPos = pos
        findNavController().navigate(R.id.action_createFragment_to_createBottomSheet)
    }

}