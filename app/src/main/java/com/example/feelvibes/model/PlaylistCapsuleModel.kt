package com.example.feelvibes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PlaylistCapsuleModel(
    val name : String,
    val paths : ArrayList<String>,
    val thumbnailUri : String?
) : Parcelable