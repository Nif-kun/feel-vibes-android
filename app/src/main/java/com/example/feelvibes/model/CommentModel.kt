package com.example.feelvibes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class CommentModel(
    open val id: String,
    open val ownerId: String,
    open val time: String,
    open val text: String,
    open val likes: ArrayList<String>,
    open var replies: ArrayList<ReplyModel>?
): Parcelable