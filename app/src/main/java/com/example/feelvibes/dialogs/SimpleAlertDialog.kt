package com.example.feelvibes.dialogs

import android.content.DialogInterface
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.databinding.SimpleAlertDialogBinding
import com.example.feelvibes.viewbinds.FragmentDialogBind

class SimpleAlertDialog: FragmentDialogBind<SimpleAlertDialogBinding>(SimpleAlertDialogBinding::inflate) {

    var dismissListener: (()->Unit)? = null

    var titleEnabled = true
    var returnEnabled = true
    var popBack = false
    var title = "Untitled"
    var text = ""
    var buttonLabel = "Return"

    override fun onReady() {
        binding.titleView.text = title
        binding.contentView.text = text
        binding.returnBtn.text = buttonLabel
        if (!titleEnabled) {
            binding.titleView.visibility = View.GONE
        }
        if (!returnEnabled) {
            binding.returnBtn.visibility = View.GONE
        } else {
            binding.returnBtn.setOnClickListener {
                if (popBack) {
                    dismiss()
                    findNavController().popBackStack()
                } else {
                    dismiss()
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (popBack) {
            findNavController().popBackStack()
        }
    }

}