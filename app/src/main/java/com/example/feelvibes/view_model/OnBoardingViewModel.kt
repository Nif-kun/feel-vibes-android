package com.example.feelvibes.view_model

import androidx.lifecycle.ViewModel

class OnBoardingViewModel: ViewModel() {

    var loginListener: (()->Unit)? = null

}