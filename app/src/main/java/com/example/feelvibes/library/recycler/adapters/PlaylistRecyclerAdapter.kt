package com.example.feelvibes.library.recycler.adapters

import android.app.Activity
import android.view.View
import com.example.feelvibes.R
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter

class PlaylistRecyclerAdapter(
    private val activity: Activity,
    recyclerItemClick: RecyclerItemClick,
    private val musicDataList : ArrayList<MusicModel>,
    id: Int = -1
) : ItemRecyclerAdapter(id, activity, recyclerItemClick) {

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (holder.moreButton.visibility == View.GONE)
            holder.moreButton.visibility = View.VISIBLE

        musicDataList[position].loadThumbnail(activity)
        if (musicDataList[position].thumbnail != null) {
            holder.imageViewThumbnail.setImageBitmap(musicDataList[position].thumbnail)
        } else {
            holder.imageViewThumbnail.setImageResource(R.drawable.ic_album_24)
            //holder.imageViewThumbnail.setColorFilter(com.google.android.material.R.color.design_default_color_primary_variant)
        }
        holder.textViewTitle.text = musicDataList[position].title
    }

    override fun getItemCount(): Int {
        return musicDataList.size
    }
}