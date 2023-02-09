package com.example.feelvibes.player.recycler

import android.app.Activity
import android.view.View
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.ProjectModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter

class ProjectRecyclerAdapter(
    private var activity : Activity,
    recyclerItemClick : RecyclerItemClick,
    private val projectList : ArrayList<ProjectModel>
) : ItemRecyclerAdapter(activity, recyclerItemClick){

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        viewVisibility(holder.imageViewThumbnail, 0)
        viewVisibility(holder.moreButton, 0)
        holder.textViewTitle.text = projectList[position].name
    }

    private fun viewVisibility(view: View, visible: Int = 2) {
        when (visible) {
            0 -> view.visibility = View.GONE
            1 -> view.visibility = View.INVISIBLE
            2 -> view.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return projectList.size
    }

}