package com.example.feelvibes.viewbinds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.feelvibes.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class FragmentBottomSheetDialogBind<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> VB
) : BottomSheetDialogFragment(){

    private var _binding: VB? = null
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
        onReady()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun isActionBarVisible(): Boolean? {
        return (activity as AppCompatActivity).supportActionBar?.isShowing
    }

    fun hideActionBar(){
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    fun showActionbar(){
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    fun renameActionBar(title : String){
        (activity as AppCompatActivity).supportActionBar?.title = title
    }

    open fun onReady() {}

}