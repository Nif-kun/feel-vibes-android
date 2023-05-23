package com.example.feelvibes.dialogs

import android.content.DialogInterface
import android.util.Log
import android.widget.Toast
import com.example.feelvibes.databinding.ProfileEditDialogBinding
import com.example.feelvibes.utils.FVFireStoreHandler
import com.example.feelvibes.viewbinds.FragmentDialogBind

class EditProfileDialog: FragmentDialogBind<ProfileEditDialogBinding>(ProfileEditDialogBinding::inflate) {

    var dismissListener: (()->Unit)? = null
    var confirmListener: ((firstName:String, lastName:String, username:String, bio:String)->Unit)? = null
    var cancelListener: (()->Unit)? = null

    var userId: String? = null
    var firstName = ""
    var lastName = ""
    var username = ""
    var bio = ""

    private var confirmInvokeMax = 0
    private var confirmInvokeCount = 0

    override fun onReady() {
        binding.firstNameInput.setText(firstName)
        binding.lastNameInput.setText(lastName)
        binding.usernameInput.setText(username)
        binding.bioInput.setText(bio)
        onConfirm()
        onCancel()
    }

    // TODO
    //  There's a bug when confirming and getting a warning for same username.
    //  Once you change it or revert the username while having other parts of the input updatable, it will just stay stuck.
    //  I can't be bothered right now so yolo lmao.
    private fun onConfirm() {
        binding.confirmBtn.setOnClickListener {
            val confirmationAlertDialog = ConfirmationAlertDialog()
            confirmationAlertDialog.title = "Confirm Edit"
            confirmationAlertDialog.text = "Are you sure with the changes to your profile?"
            confirmationAlertDialog.confirmListener = {
                binding.confirmBtn.isEnabled = false
                binding.cancelBtn.isEnabled = false
                val firstNameInput = binding.firstNameInput.text ?: ""
                val lastNameInput = binding.lastNameInput.text ?: ""
                val usernameInput = binding.usernameInput.text ?: ""
                val bioInput = binding.bioInput.text ?: ""
                countMaxInvokeRequirement(firstNameInput.toString(), lastNameInput.toString(), usernameInput.toString(), bioInput.toString())
                if (firstNameInput.length >= 3 && firstNameInput.toString() != firstName) {
                    if (userId != null) {
                        Log.d("EditProfileDialog", "firstName occurs")
                        FVFireStoreHandler.setProfileFirstName(userId!!, firstNameInput.toString()) { success, exception ->
                            if (success)
                                confirmInvoke(1, firstNameInput.toString(), lastNameInput.toString(), usernameInput.toString(), bioInput.toString())
                            else {
                                exception?.printStackTrace()
                                confirmInvoke(1, firstNameInput.toString(), lastNameInput.toString(), usernameInput.toString(), bioInput.toString())
                            }
                        }
                    }
                }
                if (lastNameInput.length >= 3 && lastNameInput.toString() != lastName) {
                    if (userId != null) {
                        Log.d("EditProfileDialog", "lastName occurs")
                        FVFireStoreHandler.setProfileLastName(userId!!, lastNameInput.toString()) { success, exception ->
                            if (success)
                                confirmInvoke(1, firstNameInput.toString(), lastNameInput.toString(), usernameInput.toString(), bioInput.toString())
                            else {
                                exception?.printStackTrace()
                                confirmInvoke(1, firstNameInput.toString(), lastNameInput.toString(), usernameInput.toString(), bioInput.toString())
                            }
                        }
                    }
                }
                if (usernameInput.length >= 3 && usernameInput.toString() != username) {
                    if (userId != null) {
                        Log.d("EditProfileDialog", "username occurs")
                        FVFireStoreHandler.setProfileUsername(userId!!, usernameInput.toString()) { success, exception, extra ->
                            if (success)
                                confirmInvoke(1, firstNameInput.toString(), lastNameInput.toString(), usernameInput.toString(), bioInput.toString())
                            else if (extra != null) {
                                exception?.printStackTrace()
                                Toast.makeText(requireActivity(), extra, Toast.LENGTH_SHORT).show()
                                binding.confirmBtn.isEnabled = true
                                binding.cancelBtn.isEnabled = true
                                confirmInvokeCount = 0
                            } else {
                                exception?.printStackTrace()
                                confirmInvoke(1, firstNameInput.toString(), lastNameInput.toString(), usernameInput.toString(), bioInput.toString())
                            }
                        }
                    }
                }
                if (bioInput.toString() != bio) {
                    if (userId != null) {
                        Log.d("EditProfileDialog", "bio occurs")
                        FVFireStoreHandler.setProfileBio(userId!!, bioInput.toString()) { success, exception ->
                            if (success)
                                confirmInvoke(1, firstNameInput.toString(), lastNameInput.toString(), usernameInput.toString(), bioInput.toString())
                            else {
                                exception?.printStackTrace()
                                confirmInvoke(1, firstNameInput.toString(), lastNameInput.toString(), usernameInput.toString(), bioInput.toString())
                            }
                        }
                    }
                }
                if (confirmInvokeMax <= 0)
                    dismiss()
            }
            confirmationAlertDialog.show(requireActivity().supportFragmentManager, "ConfirmationAlertDialog")
        }
    }

    private fun countMaxInvokeRequirement(firstNameInput: String, lastNameInput: String, usernameInput: String, bioInput: String) {
        if (firstNameInput.length >= 3 && firstNameInput != firstName) {
            confirmInvokeMax++
        }
        if (lastNameInput.length >= 3 && lastNameInput != lastName) {
            confirmInvokeMax++
        }
        if (usernameInput.length >= 3 && usernameInput != username) {
            confirmInvokeMax++
        }
        if (bioInput != bio) {
            confirmInvokeMax++
        }
    }

    private fun confirmInvoke(count: Int, firstNameInput: String, lastNameInput: String, usernameInput: String, bioInput: String) {
        confirmInvokeCount += count
        if (confirmInvokeCount >= confirmInvokeMax) {
            confirmListener?.invoke(firstNameInput, lastNameInput, usernameInput, bioInput)
            if (_binding != null) {
                binding.confirmBtn.isEnabled = true
                binding.cancelBtn.isEnabled = true
            }
            dismiss()
        }
    }

    private fun onCancel() {
        binding.cancelBtn.setOnClickListener {
            cancelListener?.invoke()
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        dismissListener?.invoke()
    }

}