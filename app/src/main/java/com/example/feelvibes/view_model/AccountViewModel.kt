package com.example.feelvibes.view_model

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class AccountViewModel: ViewModel() {

    var onBoardingFinished = false
    var currentUser: FirebaseUser? = null

    var selectedUserId: String? = null

    var viewingItem = false

}