package com.example.feelvibes.player.bottom.sheets

import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.databinding.FragmentPlayerPlaylistBottomSheetBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.player.recycler.PlayerPlaylistRecyclerAdapter
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.view_model.PlayerViewModel
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind


class PlayerPlaylistBottomSheet : FragmentBottomSheetDialogBind<FragmentPlayerPlaylistBottomSheetBinding>(
    FragmentPlayerPlaylistBottomSheetBinding::inflate), RecyclerItemClick {

    private lateinit var libraryViewModel : LibraryViewModel
    private lateinit var playerViewModel: PlayerViewModel
    private var musicList = arrayListOf<MusicModel>()
    private var awake = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        playerViewModel = ViewModelProvider(requireActivity())[PlayerViewModel::class.java]
    }

    override fun onReady() {
        super.onReady()
        setupRecyclerAdapter()
        onSearchEvent()
        setupLabel()
        onCompleteEvent()
    }

    private fun setupLabel() {
        mainActivity.musicPlayer?.let { model ->
            model.currentPlaylist?.let { binding.title.text = it.name }
        }
    }

    private fun setupRecyclerAdapter() {
        mainActivity.musicPlayer?.let { x -> x.currentPlaylist?.let { y -> musicList = y.list } } // It just sets musicList with value
        updateAdapter()
        binding.playlistRecView.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun updateAdapter() {
        binding.playlistRecView.adapter = PlayerPlaylistRecyclerAdapter(
            requireActivity(),
            this,
            musicList)
    }

    private fun onSearchEvent() {
        binding.searchInput.doOnTextChanged { text, start, before, count ->
            var rawList = arrayListOf<MusicModel>()
            mainActivity.musicPlayer?.let { x -> x.currentPlaylist?.let { y -> rawList = y.list } }
            musicList = rawList.filter {
                if (text != null) {
                    it.title.contains(text, true)
                } else
                    false
            } as ArrayList<MusicModel>
            updateAdapter()
        }
    }

    private fun onCompleteEvent() {
        if (awake && mainActivity.musicPlayer != null) {
            mainActivity.musicPlayer!!.onCompletionListener {
                // Almost everytime a NullException will occur. It works. I just don't know why I'm getting it.
                try {
                    updateAdapter()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    override fun onItemClick(pos: Int) {
        val safeIndex: Int? = mainActivity.musicPlayer?.currentPlaylist?.indexOf(musicList[pos])
        if (safeIndex != null) {
            mainActivity.musicPlayer?.setMusic(safeIndex) {
                mainActivity.musicPlayer?.currentIndex = safeIndex
                playerViewModel.playerFragment?.updateViews()
                updateAdapter()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        awake = true
    }

    override fun onPause() {
        super.onPause()
        awake = false
    }

    override fun onStart() {
        super.onStart()
        awake = true
    }

    override fun onStop() {
        super.onStop()
        awake = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        awake = false
        playerViewModel.playerFragment = null
    }
}