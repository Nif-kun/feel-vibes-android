package com.example.feelvibes.library

import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.feelvibes.FragmentBind
import com.example.feelvibes.MainActivityViewModel
import com.example.feelvibes.databinding.FragmentLibraryBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class LibraryFragment : FragmentBind<FragmentLibraryBinding>(FragmentLibraryBinding::inflate) {

    //TODO [Ref:0-MainActivity.kt]
    // Attempt to display in RecyclerView from
    // any category of LibraryFragment's layout. E.g. Playlist.
    // If it works, attempt to filter by category.

    override fun onReady(){
        renameActionBar("Library")
        buildTabs()
        val viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        // TODO do RecycleView set here | Ref: 0
        //  Suggestion: create a PlaylistModel and store that instead.
        //              variables include: musicDataList, coverArt, name, and type (String)
        //  Suggestion: have viewModel store ArrayList<PlaylistModel> instead
    }

    private fun buildTabs(){
        binding.libraryViewPager.adapter = LibraryViewPagerAdapter(this)
        binding.libraryTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.libraryViewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.libraryViewPager.registerOnPageChangeCallback(object: OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.libraryTabLayout.getTabAt(position)?.select()
            }
        })
    }

    private fun buildRows(){
        //add the building of rows.
    }
}