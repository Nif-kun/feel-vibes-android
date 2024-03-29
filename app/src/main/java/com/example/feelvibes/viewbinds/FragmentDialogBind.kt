package com.example.feelvibes.viewbinds

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.example.feelvibes.MainActivity

abstract class FragmentDialogBind<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> VB,
) : DialogFragment() {

    var _binding: VB? = null
    val binding get() : VB = _binding as VB
    lateinit var mainActivity : MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requireActivity() is MainActivity)
            mainActivity = requireActivity() as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater.invoke(inflater)
        if (_binding == null)
            throw IllegalArgumentException("Binding cannot be null!")
        if (dialog != null && dialog!!.window != null) {
            val window = dialog!!.window!!
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        onReady()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun onReady() {}

}