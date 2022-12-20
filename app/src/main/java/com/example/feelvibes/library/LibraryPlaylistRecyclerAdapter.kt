package com.example.feelvibes.library

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.feelvibes.R
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.MusicModel

class LibraryPlaylistRecyclerAdapter(
    private val activity: Activity,
    private val recyclerItemClick: RecyclerItemClick,
    private val musicDataList : ArrayList<MusicModel>
) : RecyclerView.Adapter<LibraryPlaylistRecyclerAdapter.LibraryPlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryPlaylistViewHolder {
        val inflater = LayoutInflater.from(activity.baseContext)
        val view : View = inflater.inflate(R.layout.simple_item_row, parent, false)
        return LibraryPlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryPlaylistViewHolder, position: Int) {
        musicDataList[position].loadThumbnail(activity)
        if (musicDataList[position].thumbnail != null) {
            holder.imageViewThumbnail.setImageBitmap(musicDataList[position].thumbnail)
        } else {
            holder.imageViewThumbnail.setImageResource(R.drawable.ic_album_24)
            holder.imageViewThumbnail.setColorFilter(com.google.android.material.R.color.design_default_color_primary_variant)
        }
        holder.textViewTitle.text = musicDataList[position].title
    }

    override fun getItemCount(): Int {
        return musicDataList.size
    }

    inner class LibraryPlaylistViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val imageViewThumbnail : ImageView = itemView.findViewById(R.id.simple_item_row_thumbnail)
        val textViewTitle : TextView = itemView.findViewById(R.id.simple_item_row_title)

        init {
            itemView.setOnClickListener() {
                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION)
                    recyclerItemClick.onItemClick(pos)
            }
        }
    }

}