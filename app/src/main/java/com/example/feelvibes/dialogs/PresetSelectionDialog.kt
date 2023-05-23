package com.example.feelvibes.dialogs

import android.net.Uri
import android.view.View
import com.example.feelvibes.databinding.PresetSelectionDialogBinding
import com.example.feelvibes.viewbinds.FragmentDialogBind

class PresetSelectionDialog : FragmentDialogBind<PresetSelectionDialogBinding>(PresetSelectionDialogBinding::inflate) {

    var onSelectionListener: ((Uri)->Unit)? = null
    var presetSelectionsUri = ArrayList<Uri?>(6).apply {
        for (i in 0 until 6) {
            add(null)
        }
    }

    override fun onReady() {
        setupSelection()
        onSelectionEvent()
        onCancelEvent()
    }

    private fun setupSelection() {
        if (presetSelectionsUri[0] != null) {
            binding.selectionABtn.setImageURI(presetSelectionsUri[0])
            binding.selectionABtn.visibility = View.VISIBLE
        }
        if (presetSelectionsUri[1] != null) {
            binding.selectionBBtn.setImageURI(presetSelectionsUri[1])
            binding.selectionBBtn.visibility = View.VISIBLE
        }
        if (presetSelectionsUri[2] != null) {
            binding.selectionCBtn.setImageURI(presetSelectionsUri[2])
            binding.selectionCBtn.visibility = View.VISIBLE
        }
        if (presetSelectionsUri[3] != null) {
            binding.selectionDBtn.setImageURI(presetSelectionsUri[3])
            binding.selectionDBtn.visibility = View.VISIBLE
        }
        if (presetSelectionsUri[4] != null) {
            binding.selectionEBtn.setImageURI(presetSelectionsUri[4])
            binding.selectionEBtn.visibility = View.VISIBLE
        }
        if (presetSelectionsUri[5] != null) {
            binding.selectionFBtn.setImageURI(presetSelectionsUri[5])
            binding.selectionFBtn.visibility = View.VISIBLE
        }
    }

    private fun onSelectionEvent() {
        binding.selectionABtn.setOnClickListener {
            if (presetSelectionsUri[0] != null) {
                onSelectionListener?.invoke(presetSelectionsUri[0]!!)
            }
            dismiss()
        }
        binding.selectionBBtn.setOnClickListener {
            if (presetSelectionsUri[1] != null) {
                onSelectionListener?.invoke(presetSelectionsUri[1]!!)
            }
            dismiss()
        }
        binding.selectionCBtn.setOnClickListener {
            if (presetSelectionsUri[2] != null) {
                onSelectionListener?.invoke(presetSelectionsUri[2]!!)
            }
            dismiss()
        }
        binding.selectionDBtn.setOnClickListener {
            if (presetSelectionsUri[3] != null) {
                onSelectionListener?.invoke(presetSelectionsUri[3]!!)
            }
            dismiss()
        }
        binding.selectionEBtn.setOnClickListener {
            if (presetSelectionsUri[4] != null) {
                onSelectionListener?.invoke(presetSelectionsUri[4]!!)
            }
            dismiss()
        }
        binding.selectionFBtn.setOnClickListener {
            if (presetSelectionsUri[5] != null) {
                onSelectionListener?.invoke(presetSelectionsUri[5]!!)
            }
            dismiss()
        }

    }

    private fun onCancelEvent() {
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }
}