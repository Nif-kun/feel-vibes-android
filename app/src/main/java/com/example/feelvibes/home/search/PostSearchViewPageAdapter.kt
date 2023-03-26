package com.example.feelvibes.home.search

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.feelvibes.home.search.category.*

class PostSearchViewPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchPeopleCategory()
            1 -> SearchHashtagCategory()
            2 -> SearchMusicCategory()
            3 -> SearchDesignCategory()
            4 -> SearchChordsCategory()
            5 -> SearchLyricsCategory()
            else -> SearchPeopleCategory()
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

}