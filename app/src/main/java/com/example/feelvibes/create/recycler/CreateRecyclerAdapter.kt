package com.example.feelvibes.create.recycler

import android.app.Activity
import android.view.View
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.ProjectModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter

class CreateRecyclerAdapter(
    activity: Activity,
    recyclerItemClick: RecyclerItemClick,
    private val projectModels : ArrayList<ProjectModel> = arrayListOf()
) : ItemRecyclerAdapter(activity, recyclerItemClick){

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        viewVisibility(holder.imageViewThumbnail, false)
        viewVisibility(holder.moreButton, false)
        holder.textViewTitle.text = projectModels[position].name
    }

    private fun viewVisibility(view: View, visible: Boolean = false) {
        if (visible)
            view.visibility = View.VISIBLE
        else
            view.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return projectModels.size
    }
}