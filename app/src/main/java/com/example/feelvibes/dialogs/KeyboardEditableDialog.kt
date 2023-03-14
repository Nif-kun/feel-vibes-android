package com.example.feelvibes.dialogs

import android.content.Context
import android.content.DialogInterface
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.example.feelvibes.databinding.KeyboardEditableTopLayoutBinding
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind

class KeyboardEditableDialog: FragmentBottomSheetDialogBind<KeyboardEditableTopLayoutBinding>(KeyboardEditableTopLayoutBinding::inflate) {

    var onSendListener : ((String?) -> Unit)? = null

    override fun onReady() {
        binding.commentInput.requestFocus()
        showKeyboard()
        onSend()
    }

    private fun showKeyboard() {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun onSend() {
        binding.sendBtn.setOnClickListener {
            if (binding.commentInput.text.toString().isNotBlank()) {
                onSendListener?.invoke(binding.commentInput.text.toString())
                dismiss()
            } else {
                onSendListener?.invoke(null)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.commentInput.windowToken, 0)
    }

}