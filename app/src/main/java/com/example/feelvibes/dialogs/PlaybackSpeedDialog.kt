package com.example.feelvibes.dialogs

import com.example.feelvibes.databinding.PlaybackSpeedDialogBinding
import com.example.feelvibes.viewbinds.FragmentDialogBind

class PlaybackSpeedDialog: FragmentDialogBind<PlaybackSpeedDialogBinding>(PlaybackSpeedDialogBinding::inflate) {

    var onSelectionListener: ((Float)->Unit)? = null
    var currentPlaybackSpeed = "1.0x"

    override fun onReady() {
        setupLabel()
        onSelectionEvent()
    }

    private fun setupLabel() {
        binding.currentSpeedLabel.text = currentPlaybackSpeed
    }

    private fun onSelectionEvent() {
        binding.slowestSpeedOptionBtn.setOnClickListener {
            onSelectionListener?.invoke(0.25f)
            dismiss()
        }
        binding.verySlowSpeedOptionBtn.setOnClickListener {
            onSelectionListener?.invoke(0.5f)
            dismiss()
        }
        binding.slowSpeedOptionBtn.setOnClickListener {
            onSelectionListener?.invoke(0.75f)
            dismiss()
        }
        binding.normalSpeedOptionBtn.setOnClickListener {
            onSelectionListener?.invoke(1.0f)
            dismiss()
        }
        binding.fastSpeedOptionBtn.setOnClickListener {
            onSelectionListener?.invoke(1.25f)
            dismiss()
        }
        binding.veryFastSpeedOptionBtn.setOnClickListener {
            onSelectionListener?.invoke(1.5f)
            dismiss()
        }
        binding.fastestSpeedOptionBtn.setOnClickListener {
            onSelectionListener?.invoke(1.75f)
            dismiss()
        }
        binding.ultraFastSpeedOptionBtn.setOnClickListener {
            onSelectionListener?.invoke(2.0f)
            dismiss()
        }
    }

}