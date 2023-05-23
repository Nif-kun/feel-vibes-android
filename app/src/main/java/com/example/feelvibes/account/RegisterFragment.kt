package com.example.feelvibes.account

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentRegisterBinding
import com.example.feelvibes.view_model.AccountViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : FragmentBind<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private lateinit var accountViewModel : AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
    }

    override fun onReady() {
        if (accountViewModel.onBoardingFinished) {
            mainActivity.hideToolBar(true)
            mainActivity.hideMainMenu()
        }
        onCancelEvent()
        onNavLoginEvent()
        onRegisterEvent()
        onShowTermsEvent()
    }

    private fun onCancelEvent() {
        if (accountViewModel.onBoardingFinished) {
            binding.cancelBtn.visibility = View.VISIBLE
            binding.cancelBtn.setOnClickListener {
                mainActivity.showMainMenu()
                findNavController().popBackStack()
            }
        }
    }

    private fun onNavLoginEvent() {
        if (accountViewModel.onBoardingFinished) {
            binding.registerHintTextViewRight.setOnClickListener {
                findNavController().navigate(R.id.action_homeRegisterFragment_to_homeLoginFragment)
            }
        } else {
            binding.registerHintTextViewRight.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun onShowTermsEvent() {
        binding.showTermsBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.terms_layout, null)
            val dialogBuilder = AlertDialog.Builder(requireActivity())
                .setView(dialogView)
                .setTitle("Terms and Privacy Policy")
            val dialog = dialogBuilder.create()
            dialog.show()
        }
    }

    private fun onRegisterEvent() {
        binding.AccountSignUpConfirmBtn.setOnClickListener {
            val firstName = binding.AccountSignUpFirstNameInput.text.toString()
            val lastName = binding.AccountSignUpLastNameInput.text.toString()
            val username = binding.AccountSignUpUsernameInput.text.toString()
            val email = binding.AccountSignUpEmailInput.text.toString()
            val password = binding.AccountSignUpPasswordInput.text.toString()
            val passwordConfirm = binding.AccountSignUpConfirmPasswordInput.text.toString()
            val agreedTerms = binding.agreeTermsBox.isChecked

            val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}\$".toRegex()

            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(
                    mainActivity,
                    "Full name is required!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (username.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    "Username is required!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (username.length < 3) {
                Toast.makeText(
                    requireActivity(),
                    "Username must be at least 3 characters long.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (email.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    "Email is required!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    "Password is required!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!password.matches(passwordRegex)) {
                Toast.makeText(
                    requireActivity(),
                    "Password requires 8 alphabetic and numeric characters.",
                    Toast.LENGTH_LONG
                ).show()
            } else if (password != passwordConfirm) {
                Toast.makeText(
                    requireActivity(),
                    "Confirm password does not match!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!agreedTerms) {
                Toast.makeText(
                    requireActivity(),
                    "Please read and agree to the terms of service.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                binding.AccountSignUpConfirmBtn.isEnabled = false
                Toast.makeText(requireActivity(), "Processing, please wait...", Toast.LENGTH_SHORT).show()
                val fullName = arrayListOf(firstName, lastName)
                registerUser(fullName, username, email, password)
            }

        }
    }

    private fun registerUser(fullName: ArrayList<String>, username: String, email: String, password: String) {
        mainActivity.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Registration successful
                // Note: verify process checks username and stores data if unique.
                //       if the username is not unique, the newly created account is deleted.
                verifyUser(fullName, username, email)
            } else {
                // Registration failed
                if (task.exception is FirebaseAuthUserCollisionException) {
                    Toast.makeText(requireActivity(), "Email is already in use", Toast.LENGTH_SHORT).show()
                } else {
                    task.exception?.printStackTrace()
                    Toast.makeText(requireActivity(), "An error occurred during registration", Toast.LENGTH_SHORT).show()
                }
                binding.AccountSignUpConfirmBtn.isEnabled = true
            }
        }
    }

    private fun verifyUser(fullName: ArrayList<String>, username: String, email: String) {
        val db = FirebaseFirestore.getInstance()
        val user = mainActivity.mAuth.currentUser

        // Final check if username exists in database
        val query = db.collection("usersDisplayData").whereEqualTo("username", username)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result?.documents?.isEmpty() == true) {
                    // Username does not exist, proceed to storing extra data.
                    storeRegistrationData(db, user, username, fullName, email)
                } else {
                    // Username exists, ending registration
                    task.exception?.printStackTrace()
                    Toast.makeText(requireActivity(), "Username already exists", Toast.LENGTH_SHORT).show()
                    user?.delete()
                    binding.AccountSignUpConfirmBtn.isEnabled = true
                }
            } else {
                // Handle the exception from the query
                task.exception?.printStackTrace()
                Toast.makeText(requireActivity(), "An error occurred during registration", Toast.LENGTH_SHORT).show()
                user?.delete()
                binding.AccountSignUpConfirmBtn.isEnabled = true
            }
        }
    }

    private fun storeRegistrationData(db: FirebaseFirestore, user: FirebaseUser?, username: String, fullName: ArrayList<String>, email: String) {
        val userId = mainActivity.mAuth.currentUser?.uid
        val userData = mutableMapOf(
            "firstName" to fullName[0],
            "lastName" to fullName[1],
            "username" to username,
            "email" to email
        )
        // This function is composed of multiple phases of storing.
        if (userId != null) {
            storeDisplayData(db, userId, user, username, userData)
        } else {
            Toast.makeText(requireActivity(), "An error occurred during registration", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireActivity(), "An error occurred during registration", Toast.LENGTH_SHORT).show()
                    user.delete()
                    binding.AccountSignUpConfirmBtn.isEnabled = true
                }
        } else {
            Toast.makeText(requireActivity(), "An error occurred during registration", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireActivity(), "An error occurred during registration", Toast.LENGTH_SHORT).show()
            user.delete()
            binding.AccountSignUpConfirmBtn.isEnabled = true
        }
    }

    // Last phase
    private fun storeUserData(user: FirebaseUser, userRef: DocumentReference, userData: Any) {
        userRef.set(userData)
            .addOnSuccessListener {
                // user data stored successfully
                user.sendEmailVerification()
                Toast.makeText(requireActivity(), "Success: an email verification has been sent.", Toast.LENGTH_LONG).show()
                binding.AccountSignUpConfirmBtn.isEnabled = true
                navigateBack()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(requireActivity(), "An error occurred during registration", Toast.LENGTH_SHORT).show()
                user.delete()
                binding.AccountSignUpConfirmBtn.isEnabled = true
            }
    }

    private fun navigateBack() {
        if (accountViewModel.onBoardingFinished) {
            if (accountViewModel.currentUser != null)
                findNavController().popBackStack()
            else
                findNavController().navigate(R.id.action_homeRegisterFragment_to_homeLoginFragment)
        } else {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}