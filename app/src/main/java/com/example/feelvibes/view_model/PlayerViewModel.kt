package com.example.feelvibes.view_model

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.lifecycle.ViewModel
import com.example.feelvibes.player.PlayerFragment
import pl.droidsonroids.gif.GifImageView

class PlayerViewModel : ViewModel() {

    // I'm so fucking done so I'm slapping in the whole fragment, have fun lmao
    var playerFragment: PlayerFragment? = null

    @SuppressLint("StaticFieldLeak")
    var backgroundView: GifImageView? = null
    @SuppressLint("StaticFieldLeak")
    var foregroundView: GifImageView? = null
    @SuppressLint("StaticFieldLeak")
    var textView: TextView? = null

    var textOnChords = false

}