package com.example.feelvibes.library

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.feelvibes.MainActivityViewModel
import com.example.feelvibes.databinding.FragmentLibraryBinding
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class LibraryFragment : FragmentBind<FragmentLibraryBinding>(FragmentLibraryBinding::inflate) {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    companion object {
        const val PLAYLISTS = 0
        const val ARTISTS = 1
        const val ALBUMS = 2
        const val TAGS = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
    }

    override fun onReady(){
        buildTabs()
        mainActivity.renameToolBar("Library")
    }

    private fun buildTabs(){
        binding.libraryViewPager.adapter = LibraryViewPagerAdapter(this)
        restoreTab()
        binding.libraryTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.libraryViewPager.currentItem = tab.position
                mainActivityViewModel.currentLibraryTab = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Change nav tab based on page
        binding.libraryViewPager.registerOnPageChangeCallback(object: OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.libraryTabLayout.getTabAt(position)?.select()
            }
        })
    }

    private fun restoreTab() {
        if (mainActivityViewModel.currentLibraryTab != PLAYLISTS) {
            binding.libraryTabLayout.getTabAt(mainActivityViewModel.currentLibraryTab)!!.select()
            binding.libraryViewPager.setCurrentItem(mainActivityViewModel.currentLibraryTab, false)
        }
    }
}