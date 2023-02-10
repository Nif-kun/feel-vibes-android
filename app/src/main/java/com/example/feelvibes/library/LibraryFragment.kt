package com.example.feelvibes.library

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.feelvibes.databinding.FragmentLibraryBinding
import com.example.feelvibes.utils.PermissionHandler
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class LibraryFragment : FragmentBind<FragmentLibraryBinding>(FragmentLibraryBinding::inflate) {

    private lateinit var libraryViewModel: LibraryViewModel

    companion object {
        const val PLAYLISTS = 0
        const val ARTISTS = 1
        const val ALBUMS = 2
        const val TAGS = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        // Check and request permission for media storage or storage in general if lower version.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            PermissionHandler.ReadMediaAudio(requireActivity(), true).check()
        else
            PermissionHandler.ReadExternalStorage(requireActivity(), true).check()
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
                libraryViewModel.currentLibraryTab = tab.position
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
        if (libraryViewModel.currentLibraryTab != PLAYLISTS) {
            binding.libraryTabLayout.getTabAt(libraryViewModel.currentLibraryTab)!!.select()
            binding.libraryViewPager.setCurrentItem(libraryViewModel.currentLibraryTab, false)
        }
    }
}