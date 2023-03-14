package com.example.feelvibes.home.recycler

import com.example.feelvibes.model.CommentModel
import com.example.feelvibes.model.ReplyModel

interface CommentRecyclerEvent {

    fun onUserClick(userId: String)
    fun onCommentDelete(commentModel: CommentModel)
    fun onCommentLiked(commentModel: CommentModel, userId: String)
    fun onCommentUnliked(commentModel: CommentModel, userId: String)
    fun onQueueRefresh()

    fun onReplyClick(commentModel: CommentModel, replies: ArrayList<ReplyModel>) {}

}