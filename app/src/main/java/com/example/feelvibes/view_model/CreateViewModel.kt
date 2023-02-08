package com.example.feelvibes.view_model

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.feelvibes.create.CreateFragment
import com.example.feelvibes.library.LibraryFragment
import com.example.feelvibes.model.DesignCollectionModel
import com.example.feelvibes.model.DesignModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter
import pl.droidsonroids.gif.GifDrawable
import java.io.File

class CreateViewModel: ViewModel() {

    val designCollection = DesignCollectionModel("DESIGN_COLLECTION")
    // lyricsCollection
    // chordsCollection

    var currentCreateTab = CreateFragment.DESIGNS

    var selectedAdapter: ItemRecyclerAdapter? = null
    var selectedItemPos: Int = -1

    var selectedDesignModel: DesignModel? = null
    // selectedLyric
    // selectedChord

    // On Editor
    var selectedBackgroundBitmap: Bitmap? = null
    var selectedForegroundBitmap: Bitmap? = null
    var selectedBackgroundDrawable: GifDrawable? = null
    var selectedForegroundDrawable: GifDrawable? = null
    // probably an adapter for removing or adding idk.

    //

}