package com.example.feelvibes.view_model

import androidx.lifecycle.ViewModel
import com.example.feelvibes.create.CreateFragment
import com.example.feelvibes.library.LibraryFragment
import com.example.feelvibes.model.DesignCollectionModel

class CreateViewModel: ViewModel() {

    val designCollection = DesignCollectionModel("DESIGN_COLLECTION")
    // lyricsCollection
    // chordsCollection

    var currentCreateTab = CreateFragment.DESIGNS

    // selectedDesign
    // selectedLyric
    // selectedChord

    // probably an adapter for removing or adding idk.

}