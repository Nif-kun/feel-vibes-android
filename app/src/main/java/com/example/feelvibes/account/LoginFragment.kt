package com.example.feelvibes.account

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentLoginBinding
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class LoginFragment : FragmentBind<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private lateinit var accountViewModel : AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
    }

    override fun onReady() {
        onNavRegisterEvent()
        onLoginEvent()
        onForgotPasswordEvent()
    }

    private fun onNavRegisterEvent() {
        if (accountViewModel.onBoardingFinished) {
            binding.loginHintTextViewRight.setOnClickListener {
                findNavController().navigate(R.id.action_homeLoginFragment_to_homeRegisterFragment)
            }
        } else {
            binding.loginHintTextViewRight.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    private fun onLoginEvent() {
        binding.AccountSignInConfirmBtn.setOnClickListener {
            val email = binding.AccountSignInEmailInput.text.toString()
            val password = binding.AccountSignInPasswordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(requireActivity(), "Incomplete credentials!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        mainActivity.mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful
                    val user = mainActivity.mAuth.currentUser
                    accountViewModel.currentUser = user
                    user?.let {
                        if (accountViewModel.onBoardingFinished) {
                            findNavController().navigate(R.id.action_homeLoginFragment_to_homeFragment)
                        } else {
                            findNavController().navigate(R.id.action_onBoardingFragment_to_main_nav)
                        }
                    }
                } else {
                    // Login failed
                    task.exception?.printStackTrace()
                    Toast.makeText(requireActivity(), "Incorrect credentials", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun onForgotPasswordEvent() {
        binding.forgotPasswordBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.reset_password_layout, null)
            var dialog: AlertDialog? = null
            val dialogBuilder = AlertDialog.Builder(requireActivity())
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
                                Toast.makeText(requireActivity(), "Reset key has been sent to your email", Toast.LENGTH_SHORT).show()
                                button.isEnabled = true
                                dialog?.dismiss()
                            }
                            .addOnFailureListener {
                                it.printStackTrace()
                                Toast.makeText(requireActivity(), "An error occurred while sending the reset key", Toast.LENGTH_SHORT).show()
                                button.isEnabled = true
                            }
                        requireEmail = false
                    }
                }
                if (requireEmail)
                    Toast.makeText(requireActivity(), "Requires an email!", Toast.LENGTH_SHORT).show()
            }
            dialog = dialogBuilder.create()
            dialog.show()
        }
    }
}