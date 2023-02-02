package com.example.feelvibes.create

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.feelvibes.create.category.ChordsCategory
import com.example.feelvibes.create.category.DesignsCategory
import com.example.feelvibes.create.category.LyricsCategory

class CreateViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DesignsCategory()
            1 -> LyricsCategory()
            2 -> ChordsCategory()
            else -> DesignsCategory()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

}