package com.example.feelvibes.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.databinding.AuthenticationDialogBinding
import com.example.feelvibes.layouts.LoginLayoutHandler
import com.example.feelvibes.layouts.SignUpLayoutHandler
import com.example.feelvibes.viewbinds.FragmentDialogBind

class AuthenticationDialog: FragmentDialogBind<AuthenticationDialogBinding>(AuthenticationDialogBinding::inflate) {

    private val loginHandler = LoginLayoutHandler()
    private val signUpHandler = SignUpLayoutHandler()

    var onDismissListener: (()->Unit)? = null
    var popBack = false
    var startInSignUp: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginHandler.setupOnCreate(mainActivity)
        signUpHandler.setupOnCreate(mainActivity)
    }

    override fun onReady() {
        if (startInSignUp) {
            showSignUpLayout()
        }
        loginHandler.setupOnReady(binding.loginInclude, mainActivity, this)
        signUpHandler.setupOnReady(binding.signupInclude, mainActivity, this)
    }

    fun showLoginLayout() {
        if (binding.signupInclude.signUpLayout.visibility == View.VISIBLE)
            binding.signupInclude.signUpLayout.visibility = View.GONE
        binding.loginInclude.loginLayout.visibility = View.VISIBLE
    }

    fun showSignUpLayout() {
        if (binding.loginInclude.loginLayout.visibility == View.VISIBLE)
            binding.loginInclude.loginLayout.visibility = View.GONE
        binding.signupInclude.signUpLayout.visibility = View.VISIBLE

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (popBack)
            findNavController().popBackStack()
        onDismissListener?.invoke()
    }
}