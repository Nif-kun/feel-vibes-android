package com.example.feelvibes.home.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentPostSearchBinding
import com.example.feelvibes.home.profile.ProfileViewPageAdapter
import com.example.feelvibes.view_model.HomeViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.android.material.tabs.TabLayout

class PostSearchFragment : FragmentBind<FragmentPostSearchBinding>(FragmentPostSearchBinding::inflate) {

    private lateinit var homeViewModel: HomeViewModel

    private var profileClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    override fun onReady() {
        buildTabs()
        if (!mainActivity.isToolBarVisible(true))
            mainActivity.showToolBar(true)
        mainActivity.showSearchBar()
        mainActivity.getSearchBar().setText(homeViewModel.searchTextBuffer)
        mainActivity.onHomeLogoToolBarClicked {
            findNavController().navigate(R.id.action_global_homeFragment)
        }
        mainActivity.onProfileClickedListener {
            profileClicked = true
            null
        }
    }

    private fun buildTabs(){
        binding.postSearchViewPager.adapter = PostSearchViewPageAdapter(this)
        binding.postSearchTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.postSearchViewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Change nav tab based on page
        binding.postSearchViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.postSearchTabLayout.getTabAt(position)?.select()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!profileClicked)
            homeViewModel.searchTextBuffer = ""
    }

}