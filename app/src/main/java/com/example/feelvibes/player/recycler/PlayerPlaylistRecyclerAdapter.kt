package com.example.feelvibes.player.recycler

import android.app.Activity
import android.view.View
import com.example.feelvibes.MainActivity
import com.example.feelvibes.R
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter

class PlayerPlaylistRecyclerAdapter (
    private val activity : Activity,
    recyclerItemClick : RecyclerItemClick,
    private val musicDataList : ArrayList<MusicModel>
) : ItemRecyclerAdapter(activity, recyclerItemClick){

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        viewVisibility(holder.imageViewThumbnail, 0)
        viewVisibility(holder.moreButton, 1)

        // if selected
        holder.moreButton.setBackgroundResource(R.drawable.ic_music_note_24)

        if (activity is MainActivity) {
            if (activity.musicPlayer != null) {
                val sameMusic = musicDataList[position].path.equals(activity.musicPlayer!!.currentMusic?.path, true)
                if (sameMusic)
                    viewVisibility(holder.moreButton, 2)
            }
        }

        holder.textViewTitle.text = musicDataList[position].title
    }

    private fun viewVisibility(view: View, visible: Int = 2) {
        when (visible) {
            0 -> view.visibility = View.GONE
            1 -> view.visibility = View.INVISIBLE
            2 -> view.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return musicDataList.size
    }

}