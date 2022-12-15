package com.example.feelvibes.library

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.feelvibes.library.category.AlbumFragment
import com.example.feelvibes.library.category.ArtistFragment
import com.example.feelvibes.library.category.PlaylistFragment
import com.example.feelvibes.library.category.TagFragment

class LibraryViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PlaylistFragment()
            1 -> ArtistFragment()
            2 -> AlbumFragment()
            3 -> TagFragment()
            else -> PlaylistFragment()
        }
    }

    override fun getItemCount(): Int {
        return 4
    }

}