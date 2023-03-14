package com.example.feelvibes.home.recycler

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.feelvibes.R
import com.example.feelvibes.dialogs.AuthorizationRequestDialog
import com.example.feelvibes.dialogs.ConfirmationAlertDialog
import com.example.feelvibes.model.CommentModel
import com.example.feelvibes.utils.FVFireStoreHandler
import com.example.feelvibes.utils.ShortLib
import com.google.android.material.imageview.ShapeableImageView

class CommentRecyclerAdapter(
    private val activity: Activity,
    private val commentRecyclerEvent: CommentRecyclerEvent,
    private val ownerId: String,
    private val postId: String,
    private val userId: String?,
    private val comments: ArrayList<CommentModel>
): RecyclerView.Adapter<CommentRecyclerAdapter.ItemViewHolder>()  {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val commentUserPic: ShapeableImageView = itemView.findViewById(R.id.profilePic)
        private val commentUsername: TextView = itemView.findViewById(R.id.postUsernameView)
        private val commentTime: TextView = itemView.findViewById(R.id.postTimeView)
        private val commentText: TextView = itemView.findViewById(R.id.postTextView)

        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteBtn)

        private val likeButton: ImageButton = itemView.findViewById(R.id.likeBtn)
        private val likeCount: TextView = itemView.findViewById(R.id.likeCount)

        val replyIconsLayout: LinearLayout = itemView.findViewById(R.id.commentIconsLayout)
        private val replyButton: ImageButton = itemView.findViewById(R.id.commentBtn)
        private val replyCount: TextView = itemView.findViewById(R.id.commentCount)

        fun loadUser(comment:CommentModel) {
            FVFireStoreHandler.getProfileName(comment.ownerId) { username ->
                if (username != null)
                    commentUsername.text = username
            }
            FVFireStoreHandler.getProfilePicture(comment.ownerId) { uri ->
                if (uri != null) {
                    Glide.with(activity.baseContext)
                        .load(uri)
                        .into(commentUserPic)
                }
            }
            commentUserPic.setOnClickListener {
                commentRecyclerEvent.onUserClick(comment.ownerId)
            }
            commentUsername.setOnClickListener {
                commentRecyclerEvent.onUserClick(comment.ownerId)
            }
        }

        fun loadComment(comment:CommentModel) {
            commentTime.text = ShortLib.getElapsedTime(comment.time) //setup time
            commentText.text = comment.text // setup text
            commentText.visibility = View.VISIBLE
            likeCount.text = comment.likes.size.toString()
            replyCount.text = (comment.replies?.size ?: 0).toString()
            onDelete(comment, comment.ownerId == userId)
            onLike(comment)
            onReply(comment)
            setupCommentIcons(comment.replies == null)
        }

        private fun onDelete(comment:CommentModel, isOwner:Boolean) {
            if (isOwner) {
                deleteButton.visibility = View.VISIBLE
                deleteButton.setOnClickListener {
                    if (activity is AppCompatActivity) {
                        val deleteDialog = ConfirmationAlertDialog()
                        deleteDialog.title = "Remove Comment"
                        deleteDialog.text = "You are about to delete your comment, are you sure?"
                        deleteDialog.confirmListener = {
                            FVFireStoreHandler.removePostComment(ownerId, postId, comment.id) { success, exception ->
                                if (success) {
                                    commentRecyclerEvent.onCommentDelete(comment)
                                } else {
                                    exception?.printStackTrace()
                                }
                            }
                        }
                        deleteDialog.show(activity.supportFragmentManager, "DeletePostConfirmation")
                    }
                }
            }
        }

        private fun onLike(comment: CommentModel) {
            if (userId != null) {
                var likeSize = comment.likes.size
                var liked = false
                for (like in comment.likes) {
                    if (like == userId) {
                        Glide.with(activity.baseContext).load(R.drawable.ic_favorite_24).into(likeButton)
                        liked = true
                        break
                    }
                }
                likeButton.setOnClickListener {
                    if (liked) {
                        FVFireStoreHandler.removeCommentLike(ownerId, postId, comment.id, userId) { success, exception ->
                            if (!success) {
                                exception?.printStackTrace()
                            }
                        }
                        Glide.with(activity.baseContext).load(R.drawable.ic_favorite_border_24).into(likeButton)
                        likeSize--
                        likeCount.text = likeSize.toString()
                        liked = false
                        commentRecyclerEvent.onCommentUnliked(comment, userId)
                    } else {
                        FVFireStoreHandler.addCommentLike(ownerId, postId, comment.id, userId) { success, exception ->
                            if (!success) {
                                exception?.printStackTrace()
                            }
                        }
                        Glide.with(activity.baseContext).load(R.drawable.ic_favorite_24).into(likeButton)
                        likeSize++
                        likeCount.text = likeSize.toString()
                        liked = true
                        commentRecyclerEvent.onCommentLiked(comment, userId)
                    }
                }
            } else {
                val authorizationRequestDialog = AuthorizationRequestDialog()
                if (activity is AppCompatActivity) {
                    authorizationRequestDialog.show(activity.supportFragmentManager, "AuthorizationRequestDialog")
                    authorizationRequestDialog.onDismissListener = {
                        commentRecyclerEvent.onQueueRefresh()
                    }
                }
            }
        }

        private fun onReply(comment: CommentModel) {
            replyButton.setOnClickListener {
                commentRecyclerEvent.onReplyClick(comment, comment.replies ?: arrayListOf())
            }
        }

        private fun setupCommentIcons(isReplies:Boolean) {
            if (isReplies) {
                replyIconsLayout.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(activity.baseContext)
        val view : View = inflater.inflate(R.layout.comment_item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.replyIconsLayout.visibility = View.GONE // TODO disabling for now
        holder.loadUser(comments[position])
        holder.loadComment(comments[position])
    }

    override fun getItemCount(): Int {
        return comments.size
    }

}