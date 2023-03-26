package com.example.feelvibes.layouts

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.feelvibes.MainActivity
import com.example.feelvibes.R
import com.example.feelvibes.databinding.LoginLayoutBinding
import com.example.feelvibes.dialogs.AuthenticationDialog
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.view_model.OnBoardingViewModel
import com.google.android.material.textfield.TextInputEditText
import java.util.regex.Pattern

class LoginLayoutHandler {

    private lateinit var accountViewModel : AccountViewModel
    private lateinit var onBoardingViewModel: OnBoardingViewModel
    private lateinit var binding: LoginLayoutBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var dialog: AuthenticationDialog

    fun setupOnCreate(viewModelStoreOwner: ViewModelStoreOwner) {
        accountViewModel = ViewModelProvider(viewModelStoreOwner)[AccountViewModel::class.java]
        onBoardingViewModel = ViewModelProvider(viewModelStoreOwner)[OnBoardingViewModel::class.java]
    }

    fun setupOnReady(binding: LoginLayoutBinding, mainActivity: MainActivity, dialog: AuthenticationDialog) {
        this.binding = binding
        this.mainActivity = mainActivity
        this.dialog = dialog
        onCancelEvent()
        onNavRegisterEvent()
        onLoginEvent()
        onForgotPasswordEvent()
    }

    private fun onCancelEvent() {
        if (accountViewModel.onBoardingFinished) {
            binding.cancelBtn.visibility = View.VISIBLE
            binding.cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun onNavRegisterEvent() {
        binding.loginHintTextViewRight.setOnClickListener {
            dialog.showSignUpLayout()
        }
    }

    private fun onLoginEvent() {
        binding.AccountSignInConfirmBtn.setOnClickListener {
            val email = binding.AccountSignInEmailInput.text.toString()
            val password = binding.AccountSignInPasswordInput.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(mainActivity, "Incomplete credentials!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        mainActivity.mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    val user = mainActivity.mAuth.currentUser
                    if (user?.isEmailVerified == true) {
                        accountViewModel.currentUser = user
                        dialog.dismiss()
                    } else {
                        Toast.makeText(mainActivity, "Account has not been verified, please check your email.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Login failed
                    task.exception?.printStackTrace()
                    Toast.makeText(mainActivity, "Incorrect credentials", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun onForgotPasswordEvent() {
        binding.forgotPasswordBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(mainActivity).inflate(R.layout.reset_password_layout, null)
            var dialog: AlertDialog? = null
            val dialogBuilder = AlertDialog.Builder(mainActivity)
                .setView(dialogView)
                .setTitle("Reset Password")
            val emailInputView = dialogView.findViewById<TextInputEditText>(R.id.emailInput)
            val confirmBtnView = dialogView.findViewById<Button>(R.id.confirmBtn)

            confirmBtnView.setOnClickListener { button ->
                button.isEnabled = false
                var requireEmail = true
                if (emailInputView.text?.isNotEmpty() == true) {
                    val email = emailInputView.text.toString()
                    val pattern = Pattern.compile("^[A-Za-z](.*)([@])(.+)(\\.)(.+)")
                    val matcher = pattern.matcher(email)
                    if (matcher.matches()) {
                        mainActivity.mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener {
                                Toast.makeText(mainActivity, "Reset key has been sent to your email", Toast.LENGTH_SHORT).show()
                                button.isEnabled = true
                                dialog?.dismiss()
                            }
                            .addOnFailureListener {
                                it.printStackTrace()
                                Toast.makeText(mainActivity, "An error occurred while sending the reset key", Toast.LENGTH_SHORT).show()
                                button.isEnabled = true
                            }
                        requireEmail = false
                    }
                }
                if (requireEmail)
                    Toast.makeText(mainActivity, "Requires an email!", Toast.LENGTH_SHORT).show()
            }
            dialog = dialogBuilder.create()
            dialog.show()
        }
    }
}