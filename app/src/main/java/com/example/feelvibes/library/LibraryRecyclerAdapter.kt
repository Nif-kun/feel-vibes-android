package com.example.feelvibes.library

import android.app.Activity
import android.opengl.Visibility
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.feelvibes.R
import com.example.feelvibes.model.PlaylistModel

class LibraryRecyclerAdapter(
    private val activity : Activity,
    private val playlistDataList : ArrayList<PlaylistModel>,
    private val textOnly : Boolean = false
) : RecyclerView.Adapter<LibraryRecyclerAdapter.LibraryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val inflater = LayoutInflater.from(activity.baseContext)
        val view : View = inflater.inflate(R.layout.simple_item_row, parent, false)
        return LibraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        if (textOnly)
            holder.imageViewThumbnail.visibility = ImageView.GONE
        else {
            val isAlbumType = playlistDataList[position].type == PlaylistModel.Type.ALBUM
            if (isAlbumType)
                playlistDataList[position].loadAlbumThumbnail(activity)

            val thumbnail = playlistDataList[position].thumbnail
            if (thumbnail != null)
                holder.imageViewThumbnail.setImageBitmap(playlistDataList[position].thumbnail)
            else if (isAlbumType) {
                holder.imageViewThumbnail.setImageResource(R.drawable.ic_album_24)
                holder.imageViewThumbnail.setColorFilter(com.google.android.material.R.color.design_default_color_primary_variant)
            } else {
                holder.imageViewThumbnail.setImageResource(R.drawable.ic_account_circle_24)
                holder.imageViewThumbnail.setColorFilter(com.google.android.material.R.color.design_default_color_primary_variant)
            }
        }
        holder.textViewTitle.text = playlistDataList[position].name
    }

    override fun getItemCount(): Int {
        return playlistDataList.size
    }

    class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageViewThumbnail : ImageView = itemView.findViewById(R.id.simple_item_row_thumbnail)
        val textViewTitle : TextView = itemView.findViewById(R.id.simple_item_row_title)
    }
}