package com.example.feelvibes.dialogs

import com.example.feelvibes.databinding.DualSelectionDialogBinding
import com.example.feelvibes.viewbinds.FragmentDialogBind

class DualSelectionDialog : FragmentDialogBind<DualSelectionDialogBinding>(DualSelectionDialogBinding::inflate) {

    var onSelectionListener: ((Int)->Unit)? = null // Invokes 3 event: 0 = cancel, 1 = option A, 2 = option B
    var title = "Untitled"
    var selectionLabelA = ""
    var selectionLabelB = ""

    override fun onReady() {
        setupLabels()
        onSelectionEvent()
        onCancelEvent()
    }

    private fun setupLabels() {
        binding.titleView.text = title
        binding.selectionABtn.text = selectionLabelA
        binding.selectionBBtn.text = selectionLabelB
    }

    private fun onSelectionEvent() {
        binding.selectionABtn.setOnClickListener {
            onSelectionListener?.invoke(1)
            dismiss()
        }
        binding.selectionBBtn.setOnClickListener {
            onSelectionListener?.invoke(2)
            dismiss()
        }
    }

    private fun onCancelEvent() {
        binding.cancelBtn.setOnClickListener {
            onSelectionListener?.invoke(0)
            dismiss()
        }
    }

}