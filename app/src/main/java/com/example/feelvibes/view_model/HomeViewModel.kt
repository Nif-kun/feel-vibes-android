package com.example.feelvibes.view_model

import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {

    object Layouts {
        const val NONE = -1
        const val HOME = 0
        const val PROFILE = 1
    }

    var layoutState = Layouts.NONE

}