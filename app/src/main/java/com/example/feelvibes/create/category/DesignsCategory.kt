package com.example.feelvibes.create.category

import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.R
import com.example.feelvibes.create.CreateCategoryFragment
import com.example.feelvibes.create.recycler.CreateRecyclerAdapter
import com.example.feelvibes.databinding.FragmentDesignsCategoryBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.DesignModel
import com.example.feelvibes.model.ProjectModel
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
        onSearchEvent()
    }

    private fun updateAdapter(projectModels: ArrayList<ProjectModel>) {
        binding.designsRecView.adapter = CreateRecyclerAdapter(
            requireActivity(),
            recyclerItemClick = this,
            projectModels
        )
    }

    private fun setupRecyclerAdapter() {
        updateAdapter(createViewModel.designCollection.list)
        binding.designsRecView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView = binding.designsRecView
    }

    private fun onSearchEvent() {
        if (mainActivity.getSearchBar().text.isNotEmpty())
            search(mainActivity.getSearchBar().text)
        searchBarTextWatcher = mainActivity.getSearchBar().doOnTextChanged { text, _, _, _ ->
            search(text)
        }
    }

    private fun search(text: CharSequence?) {
        if (text?.isNotEmpty() == true) {
            val newList = createViewModel.designCollection.list.filter {
                it.name.contains(text, true)
            } as ArrayList<ProjectModel>
            updateAdapter(newList)
        } else {
            updateAdapter(createViewModel.designCollection.list)
        }
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