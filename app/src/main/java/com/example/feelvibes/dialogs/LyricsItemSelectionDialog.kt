package com.example.feelvibes.dialogs

import android.os.Bundle
import com.example.feelvibes.R

class LyricsItemSelectionDialog: ProjectItemSelectionDialog() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
        createViewModel.lyricsCollection.populateFromStored(requireActivity())
    }

    override fun onReady() {
        if (createViewModel.lyricsCollection.list.isEmpty()) {
            createViewModel.lyricsCollection.populateFromStored(requireActivity())
        }
        projectList.rawUpdate(createViewModel.lyricsCollection.list)
        setupRecyclerAdapter(projectList.list)
        onSearchEvent()
    }

}