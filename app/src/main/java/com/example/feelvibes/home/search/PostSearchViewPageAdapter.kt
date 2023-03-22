package com.example.feelvibes.home.search

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.feelvibes.home.search.category.*

class PostSearchViewPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchHashtagCategory()
            1 -> SearchMusicCategory()
            2 -> SearchDesignCategory()
            3 -> SearchChordsCategory()
            4 -> SearchLyricsCategory()
            else -> SearchHashtagCategory()
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

}