package com.example.feelvibes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PostModel(
    val userId: String,
    val id: String
): Parcelable