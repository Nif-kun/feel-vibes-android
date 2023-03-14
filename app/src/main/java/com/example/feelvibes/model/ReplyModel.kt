package com.example.feelvibes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ReplyModel(
    override val id: String,
    override val ownerId: String,
    override val time: String,
    override val text: String,
    override val likes: ArrayList<String>,
): CommentModel(id, ownerId, time, text, likes, null)