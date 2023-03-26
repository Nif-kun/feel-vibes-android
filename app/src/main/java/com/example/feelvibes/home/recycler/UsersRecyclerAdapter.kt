package com.example.feelvibes.home.recycler

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.feelvibes.R
import com.example.feelvibes.utils.FVFireStoreHandler
import com.google.android.material.imageview.ShapeableImageView

class UsersRecyclerAdapter(
    val activity: Activity,
    val eventRecycler : UsersRecyclerEvent,
    private val userIDs : ArrayList<String>,
): RecyclerView.Adapter<UsersRecyclerAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val userPic: ShapeableImageView = itemView.findViewById(R.id.profilePic)
        private val userName: TextView = itemView.findViewById(R.id.username)

        fun setup(id:String) {
            FVFireStoreHandler.getProfilePicture(id) { uri ->
                if (uri != null) {
                    Glide.with(activity.baseContext)
                        .load(uri)
                        .into(userPic)
                }
                else {
                    Glide.with(activity.baseContext)
                        .load(R.drawable.ic_person_24)
                        .into(userPic)
                }
            }
            FVFireStoreHandler.getProfileName(id) { name ->
                if (name != null) {
                    userName.text = name
                }
            }

            userPic.setOnClickListener {
                eventRecycler.onUserClick(id)
            }
            userName.setOnClickListener {
                eventRecycler.onUserClick(id)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(activity.baseContext)
        val view : View = inflater.inflate(R.layout.user_item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setup(userIDs[position])
    }

    override fun getItemCount(): Int {
        return userIDs.size
    }

}