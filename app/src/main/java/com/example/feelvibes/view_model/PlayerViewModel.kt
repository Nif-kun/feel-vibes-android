package com.example.feelvibes.view_model

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.example.feelvibes.model.DesignModel
import pl.droidsonroids.gif.GifImageView

class PlayerViewModel : ViewModel() {

    var selectedDesignModel: DesignModel? = null
    // selectedLyric
    // selectedChord

    @SuppressLint("StaticFieldLeak")
    var backgroundView: GifImageView? = null
    @SuppressLint("StaticFieldLeak")
    var foregroundView: GifImageView? = null
    @SuppressLint("StaticFieldLeak")
    var textView: TextView? = null

    var textOnChords = false

}