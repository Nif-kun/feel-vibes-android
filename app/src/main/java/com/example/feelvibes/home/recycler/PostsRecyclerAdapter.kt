package com.example.feelvibes.home.recycler

import android.annotation.SuppressLint
import android.app.Activity
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.feelvibes.R
import com.example.feelvibes.dialogs.AuthorizationRequestDialog
import com.example.feelvibes.dialogs.ConfirmationAlertDialog
import com.example.feelvibes.model.DesignModel
import com.example.feelvibes.model.PostModel
import com.example.feelvibes.model.TextModel
import com.example.feelvibes.utils.FVFireStoreHandler
import com.example.feelvibes.utils.ShortLib
import com.example.feelvibes.view_model.CreateViewModel
import com.google.android.material.imageview.ShapeableImageView

class PostsRecyclerAdapter(
    private val activity : Activity,
    private val postRecyclerEvent: PostRecyclerEvent,
    private val posts : ArrayList<PostModel>,
    private val userId: String? = null,
    private val createViewModel: CreateViewModel? = null
): RecyclerView.Adapter<PostsRecyclerAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val postUserPic: ShapeableImageView = itemView.findViewById(R.id.profilePic)
        private val postUsername: TextView = itemView.findViewById(R.id.postUsernameView)

        private val postTime: TextView = itemView.findViewById(R.id.postTimeView)
        private val postText: TextView = itemView.findViewById(R.id.postTextView)

        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteBtn)
        private val reportButton: ImageButton = itemView.findViewById(R.id.reportBtn)
        private val likeButton: ImageButton = itemView.findViewById(R.id.likeBtn)
        private val commentButton: ImageButton = itemView.findViewById(R.id.commentBtn)

        private val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        private val commentCount: TextView = itemView.findViewById(R.id.commentCount)

        private val musicItem = PostItem(
            itemView.findViewById(R.id.musicItem),
            itemView.findViewById(R.id.musicTitle),
            itemView.findViewById(R.id.musicDownload)
        )

        private val designItem = PostItem(
            itemView.findViewById(R.id.designItem),
            itemView.findViewById(R.id.designTitle),
            itemView.findViewById(R.id.designDownload)
        )

        private val chordsItem = PostItem(
            itemView.findViewById(R.id.chordsItem),
            itemView.findViewById(R.id.chordsTitle),
            itemView.findViewById(R.id.chordsDownload)
        )

        private val lyricsItem = PostItem(
            itemView.findViewById(R.id.lyricsItem),
            itemView.findViewById(R.id.lyricsTitle),
            itemView.findViewById(R.id.lyricsDownload)
        )

        fun loadUser(userId: String) {
            FVFireStoreHandler.getProfileName(userId) { username ->
                if (username != null)
                    postUsername.text = username
            }
            FVFireStoreHandler.getProfilePicture(userId) { uri ->
                if (uri != null) {
                    Glide.with(activity.baseContext)
                        .load(uri)
                        .into(postUserPic)
                }
                else {
                    Glide.with(activity.baseContext)
                        .load(R.drawable.ic_person_24)
                        .into(postUserPic)
                }
            }
            postUserPic.setOnClickListener {
                postRecyclerEvent.onUserClick(userId)
            }
            postUsername.setOnClickListener {
                postRecyclerEvent.onUserClick(userId)
            }
        }

        @SuppressLint("SetTextI18n")
        fun loadPost(ownerId: String, postId: String, userId: String?, isOwner: Boolean = false) {
            FVFireStoreHandler.getPost(ownerId, postId) { map ->

                // timeView
                val time = map?.get("time") as? String
                if (time != null) {
                    postTime.text = ShortLib.getElapsedTime(time)
                    postTime.visibility = View.VISIBLE
                } else {
                    postTime.visibility = View.GONE
                }

                // textView
                val text = map?.get("text") as? String
                if (text != null) {
                    val wordFilter = activity.resources.getStringArray(R.array.word_filter)
                    var limit = false
                    for (filter in wordFilter) {
                        if (text.contains(filter, true))
                            limit = true
                    }
                    if (limit) {
                        postText.setTextColor(activity.resources.getColor(R.color.neutral, null))
                        postText.text = activity.resources.getString(R.string.post_filter_warn)
                        postText.setOnClickListener {
                            postText.setTextAppearance(R.style.TextDayNight)
                            postText.text = text
                        }
                    } else {
                        postText.setTextAppearance(R.style.TextDayNight)
                        postText.text = text
                    }
                    postText.visibility = View.VISIBLE
                } else {
                    postText.visibility = View.GONE
                }

                // Delete button
                if (isOwner && map != null) {
                    deleteButton.visibility = View.VISIBLE
                    deleteButton.setOnClickListener {
                        if (time != null) {
                            val postFolderId = time.replace("-", "").replace(":", "").replace("/", "")
                            postRecyclerEvent.onDeletePostClick(ownerId, postId, postFolderId)
                        }
                    }
                } else {
                    deleteButton.visibility = View.GONE
                }

                // report button
                if (!isOwner && userId != null) {
                    reportButton.visibility = View.VISIBLE
                    reportButton.setOnClickListener {
                        val confirmationAlertDialog = ConfirmationAlertDialog()
                        confirmationAlertDialog.title = "Report Post"
                        confirmationAlertDialog.text = "You are about to report a post that goes against ToS, are you sure?"
                        if (activity is AppCompatActivity) {
                            confirmationAlertDialog.show(activity.supportFragmentManager, "AuthorizationRequestDialog")
                            confirmationAlertDialog.confirmListener = {
                                FVFireStoreHandler.reportPost(ownerId, postId, userId) { success, exception ->
                                    if (success) {
                                        Toast.makeText(activity.baseContext, "Report submitted!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        exception?.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    reportButton.visibility = View.GONE
                }

                // Like button
                val likes = map?.get("likes") as? ArrayList<*>
                var likeSize = likes?.size ?: 0
                var liked = false
                if (!likes.isNullOrEmpty()) {
                    likeCount.text = likes.size.toString()
                    for (like in likes) {
                        if (like is String && like.equals(userId)) {
                            Glide.with(activity.baseContext).load(R.drawable.ic_favorite_24).into(likeButton)
                            liked = true
                        }
                    }
                }
                likeButton.setOnClickListener {
                    if (userId != null) {
                        // Inorder to look responsive, the value is instantly applied visually and does not wait for success.
                        if (liked) {
                            FVFireStoreHandler.removePostLike(ownerId, postId, userId) { success, exception ->
                                if (!success) {
                                    exception?.printStackTrace()
                                }
                            }
                            Glide.with(activity.baseContext).load(R.drawable.ic_favorite_border_24).into(likeButton)
                            if (likes != null) {
                                likeSize--
                                likeCount.text = likeSize.toString()
                            } else {
                                likeCount.text = likeSize.toString()
                            }
                            liked = false
                        } else {
                            FVFireStoreHandler.addPostLike(ownerId, postId, userId) { success, exception ->
                                if (!success) {
                                    exception?.printStackTrace()
                                }
                            }
                            Glide.with(activity.baseContext).load(R.drawable.ic_favorite_24).into(likeButton)
                            if (likes != null) {
                                likeSize++
                                likeCount.text = likeSize.toString()
                            } else {
                                likeCount.text = likeSize.toString()
                            }
                            liked = true

                        }
                    } else {
                        val authorizationRequestDialog = AuthorizationRequestDialog()
                        if (activity is AppCompatActivity) {
                            authorizationRequestDialog.show(activity.supportFragmentManager, "AuthorizationRequestDialog")
                            authorizationRequestDialog.onDismissListener = {
                                postRecyclerEvent.onQueueRefresh()
                            }
                        }
                    }
                }

                // Comment button
                val comments = map?.get("comments") as? HashMap<*,*>
                if (!comments.isNullOrEmpty()) {
                    commentCount.text = comments.size.toString()
                } else {
                    commentCount.text = "0"
                }
                commentButton.setOnClickListener {
                    postRecyclerEvent.onCommentClick(ownerId, postId, userId, comments)
                }

                // musicItem
                val music = map?.get("music") as? MutableMap<*, *>
                if (music != null) {
                    musicItem.show()
                    val title = if (music["title"] != null) {
                        music["title"] as String
                    } else { "Untitled" }
                    musicItem.setName(title)
                    musicItem.setOnClickListener {
                        val url = music["url"] as? String
                        if (url != null) {
                            postRecyclerEvent.onMusicClick(
                                url,
                                music["track"] as? String,
                                title,
                                (music["duration"] as? String).toString(),
                                music["artist"] as? String,
                                music["album"] as? String,
                                music["genre"] as? String
                            )
                        }
                    }

                    // Download event
                    musicItem.setOnDownloadListener {
                        val url = music["url"] as? String
                        if (url != null)
                            postRecyclerEvent.onMusicDownload(url, title)
                    }
                } else {
                    musicItem.hide()
                }

                val design = map?.get("design") as? MutableMap<*, *>
                if (design != null) {
                    designItem.show()
                    val name = if (design["name"] != null) {
                        design["name"] as String
                    } else { "Untitled" }
                    designItem.setName(name)
                    designItem.setOnClickListener {
                        postRecyclerEvent.onDesignClick(postId, name, (design["foreground"] as? String), (design["background"] as? String))
                    }

                    // Download Event
                    val designModel = DesignModel(
                        postId,
                        name,
                        "#FFFFFF",
                        (design["background"] as? String) ?: "",
                        (design["foreground"] as? String) ?: ""

                    )
                    if (createViewModel?.designCollection?.isEmpty() == true)
                        createViewModel.designCollection.populateFromStored(activity)
                    if (createViewModel?.designCollection?.has(designModel) == true)
                        designItem.setDownloadButtonState(activity,
                            PostItem.DownloadStates.DOWNLOADED
                        )
                    else {
                        designItem.setOnDownloadListener {
                            postRecyclerEvent.onDesignDownload(designModel)
                            designItem.setDownloadButtonState(activity,
                                PostItem.DownloadStates.DOWNLOADED
                            )
                        }
                    }
                } else {
                    designItem.hide()
                }

                val chords = map?.get("chords") as? MutableMap<*, *>
                if (chords != null) {
                    chordsItem.show()
                    val name = if (chords["name"] != null) {
                        chords["name"] as String
                    } else { "Untitled" }
                    val chordsText = chords["text"] as? String
                    chordsItem.setName(name)
                    chordsItem.setOnClickListener {
                        postRecyclerEvent.onChordsClick(postId, name, chordsText)
                    }

                    // Download event
                    val textModel = TextModel(postId, name, chordsText ?: "Error: text is missing!")
                    if (createViewModel?.chordsCollection?.isEmpty() == true)
                        createViewModel.chordsCollection.populateFromStored(activity)
                    if (createViewModel?.chordsCollection?.has(textModel) == true)
                        chordsItem.setDownloadButtonState(activity,
                            PostItem.DownloadStates.DOWNLOADED
                        )
                    else {
                        chordsItem.setOnDownloadListener {
                            postRecyclerEvent.onChordsDownload(textModel)
                            chordsItem.setDownloadButtonState(activity,
                                PostItem.DownloadStates.DOWNLOADED
                            )
                        }
                    }
                } else {
                    chordsItem.hide()
                }

                val lyrics = map?.get("lyrics") as? MutableMap<*, *>
                if (lyrics != null) {
                    lyricsItem.show()
                    val name = if (lyrics["name"] != null) {
                        lyrics["name"] as String
                    } else { "Untitled" }
                    val lyricsText = lyrics["text"] as? String
                    lyricsItem.setName(name)
                    lyricsItem.setOnClickListener {
                        postRecyclerEvent.onLyricsClick(postId, name, lyricsText)
                    }

                    // Download event
                    val textModel = TextModel(postId, name, lyricsText ?: "Error: text is missing!")
                    if (createViewModel?.lyricsCollection?.isEmpty() == true)
                        createViewModel.lyricsCollection.populateFromStored(activity)
                    if (createViewModel?.lyricsCollection?.has(textModel) == true)
                        lyricsItem.setDownloadButtonState(activity,
                            PostItem.DownloadStates.DOWNLOADED
                        )
                    else {
                        lyricsItem.setOnDownloadListener {
                            postRecyclerEvent.onLyricsDownload(textModel)
                            lyricsItem.setDownloadButtonState(activity,
                                PostItem.DownloadStates.DOWNLOADED
                            )
                        }
                    }
                } else {
                    lyricsItem.hide()
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(activity.baseContext)
        val view : View = inflater.inflate(R.layout.post_item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val ownerId = posts[position].userId
        val postId = posts[position].id
        holder.loadUser(ownerId)
        holder.loadPost(ownerId, postId, this.userId, this.userId == ownerId)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

}