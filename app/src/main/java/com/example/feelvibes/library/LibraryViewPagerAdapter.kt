package com.example.feelvibes.library

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.feelvibes.library.category.AlbumCategory
import com.example.feelvibes.library.category.ArtistCategory
import com.example.feelvibes.library.category.PlaylistCategory
import com.example.feelvibes.library.category.TagCategory

class LibraryViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PlaylistCategory()
            1 -> ArtistCategory()
            2 -> AlbumCategory()
            3 -> TagCategory()
            else -> PlaylistCategory()
        }
    }

    override fun getItemCount(): Int {
        return 4
    }

}