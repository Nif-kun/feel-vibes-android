package com.example.feelvibes.home.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.feelvibes.create.category.DesignsCategory
import com.example.feelvibes.create.category.LyricsCategory
import com.example.feelvibes.home.profile.category.PostsCategory
import com.example.feelvibes.home.profile.category.SharedCategory

class ProfileViewPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PostsCategory()
            1 -> SharedCategory()
            else -> PostsCategory()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

}