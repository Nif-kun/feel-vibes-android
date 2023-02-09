package com.example.feelvibes.create.category

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.R
import com.example.feelvibes.create.CreateCategoryFragment
import com.example.feelvibes.create.recycler.CreateRecyclerAdapter
import com.example.feelvibes.databinding.FragmentChordsCategoryBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.TextModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter
import com.example.feelvibes.view_model.CreateViewModel

class ChordsCategory :
    CreateCategoryFragment<FragmentChordsCategoryBinding>(FragmentChordsCategoryBinding::inflate),
    RecyclerItemClick {

    private lateinit var createViewModel: CreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
        createViewModel.chordsCollection.populateFromStored(requireActivity())
    }

    override fun onReady() {
        onCreateChordsEvent()
        setupRecyclerAdapter()
    }

    private fun setupRecyclerAdapter() {
        binding.chordsRecView.adapter = CreateRecyclerAdapter(
            requireActivity(),
            recyclerItemClick = this,
            createViewModel.chordsCollection.list
        )
        binding.chordsRecView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView = binding.chordsRecView
    }

    private fun onCreateChordsEvent() {
        binding.createChordsButton.setOnClickListener {
            findNavController().navigate(R.id.action_createFragment_to_textEditorFragment)
        }
    }

    override fun onItemClick(pos: Int) {
        createViewModel.selectedTextModel = createViewModel.chordsCollection.list[pos] as TextModel
        createViewModel.selectedAdapter = binding.chordsRecView.adapter as ItemRecyclerAdapter
        createViewModel.selectedItemPos = pos
        findNavController().navigate(R.id.action_createFragment_to_createBottomSheet)
    }

}