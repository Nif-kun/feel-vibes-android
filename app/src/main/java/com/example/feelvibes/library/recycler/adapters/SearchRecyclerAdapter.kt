package com.example.feelvibes.library.recycler.adapters

import android.app.Activity
import android.view.View
import com.example.feelvibes.R
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter

class SearchRecyclerAdapter(
    activity: Activity,
    recyclerItemClick: RecyclerItemClick,
    private val playlists: ArrayList<PlaylistModel>,
    private var textOnly: Boolean = false,
    private var hideMore: Boolean = true
) : ItemRecyclerAdapter(activity, recyclerItemClick) {

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        viewVisibility(holder.imageViewThumbnail, !textOnly)
        viewVisibility(holder.moreButton, !hideMore)

        if (playlists[position].thumbnail != null) {
            holder.imageViewThumbnail.setImageBitmap(playlists[position].thumbnail)
        } else {
            holder.imageViewThumbnail.setImageResource(R.drawable.ic_album_24)
            //holder.imageViewThumbnail.setColorFilter(com.google.android.material.R.color.design_default_color_primary_variant)
        }
        holder.textViewTitle.text = playlists[position].name
    }

    private fun viewVisibility(view: View, visible: Boolean = false) {
        if (visible)
            view.visibility = View.VISIBLE
        else
            view.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

}