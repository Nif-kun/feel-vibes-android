package com.example.feelvibes.dialogs

import android.content.DialogInterface
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.databinding.ConfirmationAlertDialogBinding
import com.example.feelvibes.viewbinds.FragmentDialogBind

class ConfirmationAlertDialog: FragmentDialogBind<ConfirmationAlertDialogBinding>(ConfirmationAlertDialogBinding::inflate) {

    var dismissListener: (()->Unit)? = null
    var confirmListener: (()->Unit)? = null
    var cancelListener: (()->Unit)? = null

    var titleEnabled = true
    var popBack = false
    var title = "Untitled"
    var text = ""
    var cancelLabel = "Cancel"
    var confirmLabel = "Confirm"

    override fun onReady() {
        binding.titleView.text = title
        binding.contentView.text = text
        binding.confirmBtn.text = confirmLabel
        binding.cancelBtn.text = cancelLabel
        if (!titleEnabled) {
            binding.titleView.visibility = View.GONE
        }
        binding.cancelBtn.setOnClickListener {
            cancelListener?.invoke()
            if (popBack) {
                dismiss()
                findNavController().popBackStack()
            } else {
                dismiss()
            }
        }
        binding.confirmBtn.setOnClickListener {
            confirmListener?.invoke()
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (popBack) {
            findNavController().popBackStack()
        }
        dismissListener?.invoke()
    }

}