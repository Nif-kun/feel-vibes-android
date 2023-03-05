package com.example.feelvibes.dialogs

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.feelvibes.R
import com.example.feelvibes.view_model.CreateViewModel

class DesignItemSelectionDialog: ProjectItemSelectionDialog() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        createViewModel.designCollection.populateFromStored(requireActivity())
    }

    override fun onReady() {
        if (createViewModel.designCollection.list.isEmpty()) {
            createViewModel.designCollection.populateFromStored(requireActivity())
        }
        projectList.rawUpdate(createViewModel.designCollection.list)
        setupRecyclerAdapter(projectList.list)
        onSearchEvent()
    }

}