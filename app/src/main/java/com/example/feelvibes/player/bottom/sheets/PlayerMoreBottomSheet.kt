package com.example.feelvibes.player.bottom.sheets

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentPlayerMoreBottomSheetBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.*
import com.example.feelvibes.player.recycler.PlayerPlaylistRecyclerAdapter
import com.example.feelvibes.player.recycler.ProjectRecyclerAdapter
import com.example.feelvibes.utils.GsonHandler
import com.example.feelvibes.utils.ShortLib
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.view_model.PlayerViewModel
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind

class PlayerMoreBottomSheet : FragmentBottomSheetDialogBind<FragmentPlayerMoreBottomSheetBinding>(
    FragmentPlayerMoreBottomSheetBinding::inflate
), RecyclerItemClick {

    object LayoutState {
        const val MAIN = 0
        const val SEARCH = 1
    }

    private lateinit var createViewModel: CreateViewModel
    private lateinit var playerViewModel: PlayerViewModel
    private var projectList = arrayListOf<ProjectModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
        playerViewModel = ViewModelProvider(requireActivity())[PlayerViewModel::class.java]
    }

    override fun onReady() {
        // Main
        setupLabels()
        onDesignEditEvent()
        onLyricsEditEvent()
        onChordsEditEvent()

        // Search
        setupRecyclerAdapter()
        onSearchEvent()
        onCancelEvent()
    }

    private fun changeLayout(state: Int) {
        when(state) {
            LayoutState.MAIN -> {
                binding.playerMoreSearchLayout.visibility = View.GONE
                binding.playerMoreLayout.visibility = View.VISIBLE
            }
            LayoutState.SEARCH -> {
                binding.playerMoreLayout.visibility = View.GONE
                binding.playerMoreSearchLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setupLabels() {
        if (mainActivity.musicPlayer?.currentMusic != null) {
            val musicPropModel = MusicPropModel(mainActivity.musicPlayer?.currentMusic!!.path)
            musicPropModel.loadFromStored(requireActivity())
            if (createViewModel.designCollection.list.size < 1) {
                createViewModel.designCollection.populateFromStored(requireActivity())
            }

            if (musicPropModel.designName.isNotEmpty())
                binding.selectedDesignLabel.text = musicPropModel.designName
        }
    }

    private fun onDesignEditEvent() {
        // setup initial value
        binding.title.text = "Design"
        binding.designEditBtn.setOnClickListener {
            changeLayout(LayoutState.SEARCH)
        }
    }

    private fun onLyricsEditEvent() {
        // setup initial value
        binding.title.text = "Lyrics"
        binding.lyricsEditBtn.setOnClickListener {
            changeLayout(LayoutState.SEARCH)
        }
    }

    private fun onChordsEditEvent() {
        // setup initial value
        binding.title.text = "Chords"
        binding.chordsEditBtn.setOnClickListener {
            changeLayout(LayoutState.SEARCH)
        }
    }

    private fun setupRecyclerAdapter() {
        // make a recycler adapter for this that takes a list of ProjectModels
        createViewModel.designCollection.populateFromStored(mainActivity)
        projectList = createViewModel.designCollection.list
        updateAdapter()
        binding.playlistRecView.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun updateAdapter() {
        binding.playlistRecView.adapter = ProjectRecyclerAdapter(
            requireActivity(),
            this,
            projectList)
    }

    private fun onSearchEvent() {
        binding.searchInput.doOnTextChanged { text, start, before, count ->
            createViewModel.designCollection.populateFromStored(mainActivity)
            projectList = createViewModel.designCollection.list.filter {
                if (text != null) {
                    it.name.contains(text, true)
                } else
                    false
            } as ArrayList<ProjectModel>
            // TODO
            //  This is such a dirty way of doing this but I couldn't care less when I'm deprived of life at this point.
            updateAdapter()
        }
    }

    private fun onCancelEvent() {
        binding.cancelBtn.setOnClickListener {
            changeLayout(LayoutState.MAIN)
        }
    }

    override fun onItemClick(pos: Int) {
        playerViewModel.selectedDesignModel = projectList[pos] as DesignModel
        if (playerViewModel.selectedDesignModel != null) {
            playerViewModel.backgroundView?.let {
                ShortLib.viewImageSetter(
                    requireActivity(),
                    it, playerViewModel.selectedDesignModel!!.backgroundImagePath)
            }
            playerViewModel.foregroundView?.let {
                ShortLib.viewImageSetter(
                    requireActivity(),
                    it, playerViewModel.selectedDesignModel!!.foregroundImagePath)
            }
        }



        playerViewModel.selectedDesignModel?.name.let {
            binding.selectedDesignLabel.text = it
            playerViewModel.backgroundView
        }
        changeLayout(LayoutState.MAIN)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mainActivity.musicPlayer?.currentMusic != null) {
            val musicPropModel = MusicPropModel(mainActivity.musicPlayer?.currentMusic!!.path)
            musicPropModel.loadFromStored(requireActivity())

            var designId = ""
            var designName = ""
            playerViewModel.selectedDesignModel?.let {
                designId = it.id
                designName = it.name
            }
            var lyricsId = ""
            var lyricsName = ""
            // --
            var chordsId = ""
            var chordsName = ""

            val newMusicPropModel = MusicPropModel(
                mainActivity.musicPlayer!!.currentMusic!!.path,
                designId,
                designName,
                lyricsId,
                lyricsName,
                chordsId,
                chordsName
            )

            // If not the same, save it.
            if (!newMusicPropModel.designChanged(musicPropModel)) {
                newMusicPropModel.saveToStored(requireActivity())
            }
        }

        playerViewModel.backgroundView = null
        playerViewModel.foregroundView = null

    }

}