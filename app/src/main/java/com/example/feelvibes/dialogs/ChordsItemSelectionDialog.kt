package com.example.feelvibes.dialogs

import android.os.Bundle
import com.example.feelvibes.R

class ChordsItemSelectionDialog: ProjectItemSelectionDialog() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        createViewModel.chordsCollection.populateFromStored(requireActivity())
    }

    override fun onReady() {
        if (createViewModel.chordsCollection.list.isEmpty()) {
            createViewModel.chordsCollection.populateFromStored(requireActivity())
        }
        projectList.rawUpdate(createViewModel.chordsCollection.list)
        setupRecyclerAdapter(projectList.list)
        onSearchEvent()
    }

}