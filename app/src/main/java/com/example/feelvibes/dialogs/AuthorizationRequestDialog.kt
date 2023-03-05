package com.example.feelvibes.dialogs

import android.content.DialogInterface
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.databinding.AuthorizationRequestDialogBinding
import com.example.feelvibes.viewbinds.FragmentDialogBind

class AuthorizationRequestDialog: FragmentDialogBind<AuthorizationRequestDialogBinding>(AuthorizationRequestDialogBinding::inflate) {

    private val authenticationDialog = AuthenticationDialog()
    var onDismissListener: (()->Unit)? = null

    var popBack = false
    private var authenticating = false

    override fun onReady() {
        onLoginEvent()
        onSignUpEvent()
        onSkipFeatureEvent()
        onAuthDismissedEvent()
    }

    private fun onLoginEvent() {
        binding.loginBtn.setOnClickListener {
            authenticating = true
            dismiss()
            authenticationDialog.startInSignUp = false
            authenticationDialog.show(mainActivity.supportFragmentManager, "AuthenticationDialog")
        }
    }

    private fun onSignUpEvent() {
        binding.signUpBtn.setOnClickListener {
            authenticating = true
            dismiss()
            authenticationDialog.startInSignUp = true
            authenticationDialog.show(mainActivity.supportFragmentManager, "AuthenticationDialog")
        }
    }

    private fun onSkipFeatureEvent() {
        binding.skipFeatureBtn.setOnClickListener {
            if (popBack) {
                dismiss()
                findNavController().popBackStack()
            } else {
                dismiss()
            }
        }
    }

    private fun onAuthDismissedEvent() {
        authenticationDialog.onDismissListener = {
            authenticating = false
            onDismissListener?.invoke()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (popBack && !authenticating) {
            findNavController().popBackStack()
        }
    }

}