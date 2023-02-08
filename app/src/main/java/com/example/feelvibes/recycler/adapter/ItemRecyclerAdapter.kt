package com.example.feelvibes.recycler.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.feelvibes.R
import com.example.feelvibes.interfaces.RecyclerItemClick

abstract class ItemRecyclerAdapter(
    private val activity : Activity,
    private val recyclerItemClick : RecyclerItemClick,
) : RecyclerView.Adapter<ItemRecyclerAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(activity.baseContext)
        val view : View = inflater.inflate(R.layout.simple_item_row, parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val layout : ConstraintLayout = itemView.findViewById(R.id.recyclerItemLayout)
        val imageViewThumbnail : ImageView = itemView.findViewById(R.id.simple_item_row_thumbnail)
        val textViewTitle : TextView = itemView.findViewById(R.id.simple_item_row_title)
        val moreButton : ImageButton = itemView.findViewById(R.id.simple_item_row_more)

        init {
            itemView.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION)
                    recyclerItemClick.onItemClick(pos)
            }

            moreButton.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION)
                    recyclerItemClick.onMoreClick(pos)
            }

        }
    }
}