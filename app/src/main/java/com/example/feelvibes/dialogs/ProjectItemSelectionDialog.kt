package com.example.feelvibes.dialogs

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.create.recycler.CreateRecyclerAdapter
import com.example.feelvibes.databinding.ProjectItemSelectionDialogBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.DesignCollectionModel
import com.example.feelvibes.model.ProjectModel
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind

open class ProjectItemSelectionDialog: FragmentBottomSheetDialogBind<ProjectItemSelectionDialogBinding>(
    ProjectItemSelectionDialogBinding::inflate
), RecyclerItemClick {

    lateinit var createViewModel: CreateViewModel

    var selectedListener: ((ProjectModel)->Unit)? = null
    val projectList = DesignCollectionModel("tempDesignList")
    private val projectListCurrent = DesignCollectionModel("tempDesignListCurrent")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
    }

    fun onSearchEvent() {
        binding.searchInput.doOnTextChanged { text, _, _, _ ->
            if (text?.isNotEmpty() == true) {
                val list = projectList.list.filter { projectModel ->
                    projectModel.name.contains(text)
                } as ArrayList<ProjectModel>
                setupRecyclerAdapter(list)
            } else {
                setupRecyclerAdapter(projectList.list)
            }
        }
    }

    fun setupRecyclerAdapter(list: ArrayList<ProjectModel>) {
        projectListCurrent.rawUpdate(list)
        if (list.isNotEmpty()) {
            binding.noItemAvailableText.visibility = View.GONE
            binding.projectRecyclerView.adapter = CreateRecyclerAdapter(
                requireActivity(),
                this,
                list)
            binding.projectRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        } else {
            binding.noItemAvailableText.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(pos: Int) {
        selectedListener?.invoke(projectListCurrent.list[pos])
        dismiss()
    }

}