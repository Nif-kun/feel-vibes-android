package com.example.feelvibes.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class MusicModel(
    val path: String,
    val track : String,
    val title: String,
    val duration: String,
    val artist: String,
    val album: String,
    val genre : String?,
    var albumArt: Bitmap? = null
) : Parcelable