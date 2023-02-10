package com.example.feelvibes.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.marginTop
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.feelvibes.utils.CategoryViewModelHandler
import com.example.feelvibes.viewbinds.FragmentBind

abstract class LibraryCategoryFragment<VB : ViewBinding>(
    bindingInflater: (inflater: LayoutInflater) -> VB,
) : FragmentBind<VB>(bindingInflater) {
    var categoryViewModel : CategoryViewModelHandler.CategoryViewModel? = null
    var recyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryViewModel = ViewModelProvider(requireActivity())[LibraryCategoryHandler.PlaylistViewModel::class.java]
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        loadScrollState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveScrollState()
    }

    override fun onPause() {
        super.onPause()
        saveScrollState()
    }

    override fun onResume() {
        super.onResume()
        loadScrollState()
    }

    // TODO
    //  Scroll saving and loading has been a pain in the ass because Google can't be bothered to make a system that works.
    //  In the end, it can only partially work. It worked before, but then it didn't. I can't figure out Google's messy system.
    //  Too many inconsistencies, deprecation, and just undeniably unnecessary complexity that could've been easily avoided.
    //  Fuck Google and its many fucking bullshit. This is the reason why Android Studio tutorials are hit and miss, outdated garbage.
    //  Even the documentation has little to no sample codes nor proper description and usage.

    private fun saveScrollState() {
        if (recyclerView != null && categoryViewModel != null) {
            categoryViewModel!!.currentTabItem = (recyclerView!!.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val topView : View? = recyclerView?.getChildAt(0)
            if (topView != null) {
                categoryViewModel!!.recycleViewScrollOffset = (topView.top - topView.marginTop) - recyclerView!!.paddingTop
            }
        }
    }

    private fun loadScrollState() {
        if (recyclerView != null && categoryViewModel != null && categoryViewModel!!.currentTabItem != -1) {
            (recyclerView!!.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                categoryViewModel!!.currentTabItem,
                categoryViewModel!!.recycleViewScrollOffset)
        }
    }

}