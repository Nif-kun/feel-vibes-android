package com.example.feelvibes.home.search

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.feelvibes.home.search.category.*

class PostSearchViewPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchMusicCategory()
            1 -> SearchDesignCategory()
            2 -> SearchChordsCategory()
            3 -> SearchLyricsCategory()
            else -> SearchMusicCategory()
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

}