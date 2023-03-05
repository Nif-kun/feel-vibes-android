package com.example.feelvibes.dialogs

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.R
import com.example.feelvibes.databinding.MusicItemSelectionDialogBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.recycler.adapters.PlaylistRecyclerAdapter
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.utils.MusicDataHandler
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind

class MusicItemSelectionDialog: FragmentBottomSheetDialogBind<MusicItemSelectionDialogBinding>(
    MusicItemSelectionDialogBinding::inflate
), RecyclerItemClick {

    var selectedListener: ((MusicModel)->Unit)? = null

    private val playlist = PlaylistModel("tempPlaylist")
    private val playlistCurrent = PlaylistModel("tempPlaylistCurrent")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onReady() {
        playlist.update(MusicDataHandler.Collect(requireActivity()).data)
        setupRecyclerAdapter(playlist.list)
        onSearchEvent()
    }

    private fun onSearchEvent() {
        binding.searchInput.doOnTextChanged { text, _, _, _ ->
            if (text?.isNotEmpty() == true) {
                val list = playlist.list.filter { musicModel ->
                    musicModel.title.contains(text)
                } as ArrayList
                setupRecyclerAdapter(list)
            } else {
                setupRecyclerAdapter(playlist.list)
            }
        }
    }

    private fun setupRecyclerAdapter(musicList: ArrayList<MusicModel>) {
        playlistCurrent.update(playlist.list)
        if (musicList.isNotEmpty()) {
            binding.noItemAvailableText.visibility = View.GONE
            binding.musicRecyclerView.adapter = PlaylistRecyclerAdapter(
                requireActivity(),
                this,
                musicList,
                false)
            binding.musicRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        } else {
            binding.noItemAvailableText.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(pos: Int) {
        selectedListener?.invoke(playlistCurrent.list[pos])
        dismiss()
    }

}