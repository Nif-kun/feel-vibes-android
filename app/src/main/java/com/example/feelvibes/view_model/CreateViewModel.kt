package com.example.feelvibes.view_model

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.feelvibes.create.CreateFragment
import com.example.feelvibes.create.editor.DesignEditorFragment
import com.example.feelvibes.model.DesignCollectionModel
import com.example.feelvibes.model.DesignModel
import com.example.feelvibes.model.TextCollectionModel
import com.example.feelvibes.model.TextModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter
import pl.droidsonroids.gif.GifDrawable

class CreateViewModel: ViewModel() {

    val designCollection = DesignCollectionModel("DESIGN_COLLECTION")
    val lyricsCollection = TextCollectionModel("LYRICS_COLLECTION")
    val chordsCollection = TextCollectionModel("CHORDS_COLLECTION")
    // lyricsCollection
    // chordsCollection

    var currentCreateTab = CreateFragment.DESIGNS

    var selectedAdapter: ItemRecyclerAdapter? = null
    var selectedItemPos: Int = -1

    var selectedDesignModel: DesignModel? = null
    var selectedTextModel: TextModel? = null

    // On Editor
    var selectedBackgroundBitmap: Bitmap? = null
    var selectedForegroundBitmap: Bitmap? = null
    var selectedBackgroundDrawable: GifDrawable? = null
    var selectedForegroundDrawable: GifDrawable? = null
    // probably an adapter for removing or adding idk.

    // Editor
    var designEditorFragment: DesignEditorFragment? = null

}