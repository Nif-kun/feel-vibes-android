package com.example.feelvibes.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.feelvibes.library.category.CategoryViewModelHandler
import com.example.feelvibes.viewbinds.FragmentBind

abstract class LibraryCategoryFragment<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> VB,
) : FragmentBind<VB>(bindingInflater) {
    var categoryViewModel : CategoryViewModelHandler.CategoryViewModel? = null
    var recyclerView : RecyclerView? = null

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