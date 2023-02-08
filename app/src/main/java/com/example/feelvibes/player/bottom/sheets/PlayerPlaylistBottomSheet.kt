package com.example.feelvibes.player.bottom.sheets

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentPlayerPlaylistBottomSheetBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.library.recycler.adapters.PlaylistRecyclerAdapter
import com.example.feelvibes.library.recycler.adapters.SearchRecyclerAdapter
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.player.recycler.PlayerPlaylistRecyclerAdapter
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind


class PlayerPlaylistBottomSheet : FragmentBottomSheetDialogBind<FragmentPlayerPlaylistBottomSheetBinding>(
    FragmentPlayerPlaylistBottomSheetBinding::inflate), RecyclerItemClick {

    private lateinit var libraryViewModel : LibraryViewModel
    private var musicList = arrayListOf<MusicModel>()
    private var awake = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
    }

    override fun onReady() {
        super.onReady()
        setupRecyclerAdapter()
        onSearchEvent()
        setupLabel()
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

            // TODO
            //  This is such a dirty way of doing this but I couldn't care less when I'm deprived of life at this point.
            updateAdapter()
        }
    }

    override fun onItemClick(pos: Int) {
        // This looks heavily convoluted and I tell you... it is, lmao.
        // It basically takes the index value of musicList[Pos] inside currentPlaylist.
        // It then uses the index to set the current music of musicPlayer.
        // pos is quite inconsistent at times so I'd rather avoid.
        mainActivity.musicPlayer?.currentPlaylist?.indexOf(musicList[pos])
            ?.let { index -> mainActivity.musicPlayer?.setMusic(index) }
        updateAdapter()
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
    }

}