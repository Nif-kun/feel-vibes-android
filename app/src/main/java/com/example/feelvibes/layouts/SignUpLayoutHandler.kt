package com.example.feelvibes.layouts

import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.feelvibes.MainActivity
import com.example.feelvibes.R
import com.example.feelvibes.databinding.SignupLayoutBinding
import com.example.feelvibes.dialogs.AuthenticationDialog
import com.example.feelvibes.view_model.AccountViewModel
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SignUpLayoutHandler {

    private lateinit var accountViewModel : AccountViewModel
    private lateinit var binding: SignupLayoutBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var dialog: AuthenticationDialog

    fun setupOnCreate(viewModelStoreOwner: ViewModelStoreOwner) {
        accountViewModel = ViewModelProvider(viewModelStoreOwner)[AccountViewModel::class.java]
    }

    fun setupOnReady(binding: SignupLayoutBinding, mainActivity: MainActivity, dialog: AuthenticationDialog) {
        this.binding = binding
        this.mainActivity = mainActivity
        this.dialog = dialog
        onCancelEvent()
        onNavLoginEvent()
        onRegisterEvent()
        onShowTermsEvent()
    }

    private fun onCancelEvent() {
        binding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun onNavLoginEvent() {
        binding.registerHintTextViewRight.setOnClickListener {
            dialog.showLoginLayout()
        }
    }

    private fun onShowTermsEvent() {
        binding.showTermsBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(mainActivity).inflate(R.layout.terms_layout, null)
            val dialogBuilder = AlertDialog.Builder(mainActivity)
                .setView(dialogView)
                .setTitle("Terms and Privacy Policy")
            val dialog = dialogBuilder.create()
            dialog.show()
        }
    }

    private fun onRegisterEvent() {
        binding.AccountSignUpConfirmBtn.setOnClickListener {
            val username = binding.AccountSignUpUsernameInput.text.toString()
            val email = binding.AccountSignUpEmailInput.text.toString()
            val password = binding.AccountSignUpPasswordInput.text.toString()
            val passwordConfirm = binding.AccountSignUpConfirmPasswordInput.text.toString()
            val agreedTerms = binding.agreeTermsBox.isChecked

            if (username.isEmpty()) {
                Toast.makeText(
                    mainActivity,
                    "Username is required!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (username.length < 3) {
                Toast.makeText(
                    mainActivity,
                    "Username must be at least 3 characters long.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (email.isEmpty()) {
                Toast.makeText(
                    mainActivity,
                    "Email is required!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.isEmpty()) {
                Toast.makeText(
                    mainActivity,
                    "Password is required!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.length < 6) {
                Toast.makeText(
                    mainActivity,
                    "Password must be at least 6 characters long.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password != passwordConfirm) {
                Toast.makeText(
                    mainActivity,
                    "Confirm password does not match!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!agreedTerms) {
                Toast.makeText(
                    mainActivity,
                    "Please read and agree to the terms of service.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.AccountSignUpConfirmBtn.isEnabled = false
                Toast.makeText(mainActivity, "Processing, please wait...", Toast.LENGTH_SHORT).show()
                registerUser(username, email, password)
            }

        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        mainActivity.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Registration successful
                // Note: verify process checks username and stores data if unique.
                //       if the username is not unique, the newly created account is deleted.
                verifyUser(username, email)
            } else {
                // Registration failed
                if (task.exception is FirebaseAuthUserCollisionException) {
                    Toast.makeText(mainActivity, "Email is already in use", Toast.LENGTH_SHORT).show()
                } else {
                    task.exception?.printStackTrace()
                    Toast.makeText(mainActivity, "An error occurred during registration", Toast.LENGTH_SHORT).show()
                }
                binding.AccountSignUpConfirmBtn.isEnabled = true
            }
        }
    }

    private fun verifyUser(username: String, email: String) {
        val db = FirebaseFirestore.getInstance()
        val user = mainActivity.mAuth.currentUser

        // Final check if username exists in database
        val query = db.collection("usersDisplayData").whereEqualTo("username", username)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result?.documents?.isEmpty() == true) {
                    // Username does not exist, proceed to storing extra data.
                    storeRegistrationData(db, user, username, email)
                } else {
                    // Username exists, ending registration
                    task.exception?.printStackTrace()
                    Toast.makeText(mainActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                    user?.delete()
                    binding.AccountSignUpConfirmBtn.isEnabled = true
                }
            } else {
                // Handle the exception from the query
                task.exception?.printStackTrace()
                Toast.makeText(mainActivity, "An error occurred during registration", Toast.LENGTH_SHORT).show()
                user?.delete()
                binding.AccountSignUpConfirmBtn.isEnabled = true
            }
        }
    }

    private fun storeRegistrationData(db: FirebaseFirestore, user: FirebaseUser?, username: String, email: String) {
        val userId = mainActivity.mAuth.currentUser?.uid
        val userData = mutableMapOf(
            "username" to username,
            "email" to email
        )
        // This function is composed of multiple phases of storing.
        if (userId != null) {
            storeDisplayData(db, userId, user, username, userData)
        } else {
            Toast.makeText(mainActivity, "An error occurred during registration", Toast.LENGTH_SHORT).show()
            binding.AccountSignUpConfirmBtn.isEnabled = true
        }

    }

    // First phase
    private fun storeDisplayData(db: FirebaseFirestore, userId: String?, user: FirebaseUser?, username: String, userData: Any) {
        val userDisplayDataRef = userId?.let { db.collection("usersDisplayData").document(it) }
        val userRef = userId?.let { db.collection("users").document(it) }
        if (user != null && userRef != null && userDisplayDataRef != null) {
            userDisplayDataRef.set(mutableMapOf("username" to username))
                .addOnSuccessListener {
                    updateDisplayName(user, userRef, username, userData)
                }
                .addOnFailureListener { e ->
                    // user data stored failed
                    e.printStackTrace()
                    Toast.makeText(mainActivity, "An error occurred during registration", Toast.LENGTH_SHORT).show()
                    user.delete()
                    binding.AccountSignUpConfirmBtn.isEnabled = true
                }
        } else {
            Toast.makeText(mainActivity, "An error occurred during registration", Toast.LENGTH_SHORT).show()
            binding.AccountSignUpConfirmBtn.isEnabled = true
        }
    }

    // Second phase
    private fun updateDisplayName(user: FirebaseUser, userRef: DocumentReference, username: String, userData: Any) {
        user.updateProfile(userProfileChangeRequest {
            displayName = username
        }).addOnCompleteListener {
            storeUserData(user, userRef, userData)
        }.addOnFailureListener { e ->
            e.printStackTrace()
            Toast.makeText(mainActivity, "An error occurred during registration", Toast.LENGTH_SHORT).show()
            user.delete()
            binding.AccountSignUpConfirmBtn.isEnabled = true
        }
    }

    // Last phase
    private fun storeUserData(user: FirebaseUser, userRef: DocumentReference, userData: Any) {
        userRef.set(userData)
            .addOnSuccessListener {
                // user data stored successfully
                accountViewModel.currentUser = user
                Toast.makeText(mainActivity, "Account created!", Toast.LENGTH_SHORT).show()
                binding.AccountSignUpConfirmBtn.isEnabled = true
                dialog.showLoginLayout() // returns to login once registration is successful
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(mainActivity, "An error occurred during registration", Toast.LENGTH_SHORT).show()
                user.delete()
                binding.AccountSignUpConfirmBtn.isEnabled = true
            }
    }
}