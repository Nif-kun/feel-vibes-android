package com.example.feelvibes.create.category

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.R
import com.example.feelvibes.create.CreateCategoryFragment
import com.example.feelvibes.create.recycler.CreateRecyclerAdapter
import com.example.feelvibes.databinding.FragmentDesignsCategoryBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.DesignModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter
import com.example.feelvibes.view_model.CreateViewModel

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
            findNavController().navigate(R.id.action_createFragment_to_designEditorFragment)
        }
    }

    override fun onItemClick(pos: Int) {
        createViewModel.selectedDesignModel = createViewModel.designCollection.list[pos] as DesignModel
        createViewModel.selectedAdapter = binding.designsRecView.adapter as ItemRecyclerAdapter
        createViewModel.selectedItemPos = pos
        findNavController().navigate(R.id.action_createFragment_to_createBottomSheet)
    }

}