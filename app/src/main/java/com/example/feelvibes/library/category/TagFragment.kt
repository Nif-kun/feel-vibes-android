package com.example.feelvibes.library.category

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.FragmentBind
import com.example.feelvibes.MainActivityViewModel
import com.example.feelvibes.databinding.FragmentTagBinding
import com.example.feelvibes.library.LibraryRecyclerAdapter
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.utils.MusicDataHandler

class TagFragment : FragmentBind<FragmentTagBinding>(FragmentTagBinding::inflate) {

    override fun onReady() {
        val viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        viewModel.updateTagPlaylistDataList(MusicDataHandler.Collect(requireActivity(), PlaylistModel.Type.GENRE, true).sortedData)
        binding.tagRecView.adapter = LibraryRecyclerAdapter(requireActivity(), viewModel.tagPlaylistDataList, true)
        binding.tagRecView.layoutManager = LinearLayoutManager(requireActivity())
    }
}