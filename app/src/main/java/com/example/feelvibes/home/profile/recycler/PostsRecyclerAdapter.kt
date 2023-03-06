package com.example.feelvibes.home.profile.recycler

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.feelvibes.R
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
    private val createViewModel: CreateViewModel? = null
): RecyclerView.Adapter<PostsRecyclerAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val postUserPic: ShapeableImageView = itemView.findViewById(R.id.profilePic)
        val postUsername: TextView = itemView.findViewById(R.id.postUsernameView)

        val postTime: TextView = itemView.findViewById(R.id.postTimeView)
        val postText: TextView = itemView.findViewById(R.id.postTextView)

        val musicItem = PostItem(
            itemView.findViewById(R.id.musicItem),
            itemView.findViewById(R.id.musicTitle),
            itemView.findViewById(R.id.musicDownload)
        )

        val designItem = PostItem(
            itemView.findViewById(R.id.designItem),
            itemView.findViewById(R.id.designTitle),
            itemView.findViewById(R.id.designDownload)
        )

        val chordsItem = PostItem(
            itemView.findViewById(R.id.chordsItem),
            itemView.findViewById(R.id.chordsTitle),
            itemView.findViewById(R.id.chordsDownload)
        )

        val lyricsItem = PostItem(
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
                        .into(postUserPic);
                }
            }
            postUserPic.setOnClickListener {
                postRecyclerEvent.onUserClick(userId)
            }
            postUsername.setOnClickListener {
                postRecyclerEvent.onUserClick(userId)
            }
        }

        fun loadPost(userId: String, postId: String) {
            FVFireStoreHandler.getPost(userId, postId) { map ->

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
                    postText.text = text
                    postText.visibility = View.VISIBLE
                } else {
                    postText.visibility = View.GONE
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
                        designItem.setDownloadButtonState(activity, PostItem.DownloadStates.DOWNLOADED)
                    else {
                        designItem.setOnDownloadListener {
                            postRecyclerEvent.onDesignDownload(designModel)
                            designItem.setDownloadButtonState(activity, PostItem.DownloadStates.DOWNLOADED)
                        }
                    }
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
                        chordsItem.setDownloadButtonState(activity, PostItem.DownloadStates.DOWNLOADED)
                    else {
                        chordsItem.setOnDownloadListener {
                            postRecyclerEvent.onChordsDownload(textModel)
                            chordsItem.setDownloadButtonState(activity, PostItem.DownloadStates.DOWNLOADED)
                        }
                    }
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
                        lyricsItem.setDownloadButtonState(activity, PostItem.DownloadStates.DOWNLOADED)
                    else {
                        lyricsItem.setOnDownloadListener {
                            postRecyclerEvent.onLyricsDownload(textModel)
                            lyricsItem.setDownloadButtonState(activity, PostItem.DownloadStates.DOWNLOADED)
                        }
                    }
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
        val userId = posts[position].userId
        val postId = posts[position].id
        holder.loadUser(userId)
        holder.loadPost(userId, postId)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

}