package com.example.feelvibes.utils

import androidx.lifecycle.ViewModel

abstract class CategoryViewModelHandler {
    open class CategoryViewModel : ViewModel() {
        var currentTabItem = -1
        var recycleViewScrollOffset = -1
    }
}