package com.example.feelvibes.create

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.feelvibes.databinding.FragmentCreateBinding
import com.example.feelvibes.library.LibraryFragment
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.view_model.HomeViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.android.material.tabs.TabLayout

class CreateFragment : FragmentBind<FragmentCreateBinding>(FragmentCreateBinding::inflate) {

    private lateinit var createViewModel: CreateViewModel
    private lateinit var homeViewModel: HomeViewModel

    companion object {
        const val DESIGNS = 0
        const val LYRICS = 1
        const val CHORDS = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onReady() {
        buildTabs()
        mainActivity.renameToolBar("Create")
        mainActivity.padMainView()
        mainActivity.hideToolBarSettings()
        mainActivity.showToolBar()
        homeViewModel.layoutState = HomeViewModel.Layouts.NONE
    }

    private fun buildTabs(){
        binding.createViewPager.adapter = CreateViewPagerAdapter(this)
        restoreTab()
        binding.createTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.createViewPager.currentItem = tab.position
                createViewModel.currentCreateTab = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Change nav tab based on page
        binding.createViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.createTabLayout.getTabAt(position)?.select()
            }
        })
    }

    private fun restoreTab() {
        if (createViewModel.currentCreateTab != LibraryFragment.PLAYLISTS) {
            binding.createTabLayout.getTabAt(createViewModel.currentCreateTab)!!.select()
            binding.createViewPager.setCurrentItem(createViewModel.currentCreateTab, false)
        }
    }
}