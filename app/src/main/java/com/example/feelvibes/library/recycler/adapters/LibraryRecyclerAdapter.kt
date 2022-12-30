package com.example.feelvibes.library.recycler.adapters

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.example.feelvibes.R
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter

class LibraryRecyclerAdapter(
    private val activity : Activity,
    recyclerItemClick : RecyclerItemClick,
    private val playlistDataList : ArrayList<PlaylistModel>,
    private val textOnly : Boolean = false
) : ItemRecyclerAdapter(activity, recyclerItemClick) {

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        hideMoreButton(true, holder)
        loadThumbnail(textOnly, holder, position)
        holder.textViewTitle.text = playlistDataList[position].name
    }

    override fun getItemCount(): Int {
        return playlistDataList.size
    }

    private fun hideMoreButton(hide : Boolean, holder: ItemViewHolder) {
        if (hide && holder.moreButton.visibility == View.VISIBLE)
            holder.moreButton.visibility = View.GONE
    }

    private fun loadThumbnail(textOnly: Boolean, holder: ItemViewHolder, position: Int) {
        if (textOnly) {
            holder.imageViewThumbnail.visibility = ImageView.GONE
            return // ends the function.
        }

        // This handles null thumbnails even from custom ones.
        when (playlistDataList[position].type) {
            PlaylistModel.Type.BUTTON -> loadCustomButtonThumbnail(holder, position)
            else -> {
                val thumbnail = loadCustomThumbnail(playlistDataList[position])
                if (thumbnail != null && !isFavorite(playlistDataList[position]))
                    holder.imageViewThumbnail.setImageBitmap(thumbnail)
                else
                    loadDefaultThumbnail(playlistDataList[position], holder)
            }
        }
    }

    private fun loadCustomButtonThumbnail(holder: ItemViewHolder, position: Int) {
        val thumbnail = playlistDataList[position].thumbnail
        if (thumbnail != null)
            holder.imageViewThumbnail.setImageBitmap(thumbnail)
        else if (playlistDataList[position].name.equals(activity.getString(R.string.create_playlist), true))
            holder.imageViewThumbnail.setImageResource(R.drawable.ic_baseline_add_box_24)
        holder.imageViewThumbnail.setColorFilter(com.google.android.material.R.color.design_default_color_primary_variant)
    }

    private fun loadCustomThumbnail(playlistModel : PlaylistModel) : Bitmap?{
        playlistModel.loadAlbumThumbnail(activity)
        return playlistModel.thumbnail
    }

    private fun loadDefaultThumbnail(playlistModel : PlaylistModel, holder : ItemViewHolder) {
        when (playlistModel.type) {
            PlaylistModel.Type.PLAYLIST -> holder.imageViewThumbnail.setImageResource(R.drawable.ic_album_24)
            PlaylistModel.Type.ALBUM -> holder.imageViewThumbnail.setImageResource(R.drawable.ic_album_24)
            PlaylistModel.Type.ARTIST -> holder.imageViewThumbnail.setImageResource(R.drawable.ic_account_circle_24)
            PlaylistModel.Type.DEFAULT -> {
                if (playlistModel.name.equals(activity.getString(R.string.favorites), true))
                    holder.imageViewThumbnail.setImageResource(R.drawable.ic_favorite_24)
            }
        }
        holder.imageViewThumbnail.setColorFilter(com.google.android.material.R.color.design_default_color_primary_variant)
    }

    private fun isFavorite(playlistModel: PlaylistModel): Boolean {
        return (playlistModel.type == PlaylistModel.Type.DEFAULT && playlistModel.name.equals(activity.getString(R.string.favorites), true))
    }
}