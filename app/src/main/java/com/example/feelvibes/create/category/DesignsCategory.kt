package com.example.feelvibes.create.category

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.create.CreateCategoryFragment
import com.example.feelvibes.create.recycler.CreateRecyclerAdapter
import com.example.feelvibes.databinding.FragmentDesignsCategoryBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.LibraryCategoryHandler
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.view_model.LibraryViewModel

class DesignsCategory :
    CreateCategoryFragment<FragmentDesignsCategoryBinding>(FragmentDesignsCategoryBinding::inflate),
    RecyclerItemClick{

    private lateinit var createViewModel: CreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
        createViewModel.designCollection.populateFromStored(requireActivity())
    }

    override fun onReady() {
        onCreateDesignEvent()
        setupRecyclerAdapter()
    }

    private fun setupRecyclerAdapter() {
        binding.designsRecView.adapter = CreateRecyclerAdapter(
            requireActivity(),
            recyclerItemClick = this,
            createViewModel.designCollection.list
        )
        binding.designsRecView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView = binding.designsRecView
    }

    private fun onCreateDesignEvent() {
        binding.createDesignButton.setOnClickListener {

        }
    }

    override fun onItemClick(pos: Int) {
        // When clicked, opens bottom sheet, edit and delete, copy the library one.
    }

}