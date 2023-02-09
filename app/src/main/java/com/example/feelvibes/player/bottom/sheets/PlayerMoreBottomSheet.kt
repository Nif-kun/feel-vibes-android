package com.example.feelvibes.player.bottom.sheets

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentPlayerMoreBottomSheetBinding
import com.example.feelvibes.interfaces.RecyclerItemClick
import com.example.feelvibes.model.DesignModel
import com.example.feelvibes.model.MusicPropModel
import com.example.feelvibes.model.ProjectModel
import com.example.feelvibes.model.TextModel
import com.example.feelvibes.player.PlayerFragment
import com.example.feelvibes.player.recycler.ProjectRecyclerAdapter
import com.example.feelvibes.utils.ShortLib
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.view_model.PlayerViewModel
import com.example.feelvibes.viewbinds.FragmentBottomSheetDialogBind

class PlayerMoreBottomSheet : FragmentBottomSheetDialogBind<FragmentPlayerMoreBottomSheetBinding>(
    FragmentPlayerMoreBottomSheetBinding::inflate
), RecyclerItemClick {

    object LayoutState {
        const val MAIN = 0
        const val SEARCH = 1
    }

    object EditTypes {
        const val DESIGN = 0
        const val LYRICS = 1
        const val CHORDS = 2
    }

    private lateinit var createViewModel: CreateViewModel
    private lateinit var playerViewModel: PlayerViewModel
    private var projectList = arrayListOf<ProjectModel>()

    private var editState = EditTypes.DESIGN
    private var musicPropModel: MusicPropModel? = null
    private var edited = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
        playerViewModel = ViewModelProvider(requireActivity())[PlayerViewModel::class.java]
        if (mainActivity.musicPlayer?.currentMusic != null) {
            musicPropModel = MusicPropModel(mainActivity.musicPlayer?.currentMusic!!.path)
            musicPropModel!!.loadFromStored(requireActivity())
        }
        if (createViewModel.designCollection.list.size < 1) {
            createViewModel.designCollection.populateFromStored(requireActivity())
        }
        if (createViewModel.lyricsCollection.list.size < 1) {
            createViewModel.lyricsCollection.populateFromStored(requireActivity())
        }
        if (createViewModel.chordsCollection.list.size < 1) {
            createViewModel.chordsCollection.populateFromStored(requireActivity())
        }
    }

    override fun onReady() {
        // Main
        setupLabels()
        onDesignEditEvent()
        onLyricsEditEvent()
        onChordsEditEvent()

        // Search
        setupRecyclerAdapter() // Required to be before any of the onEvents of Main
        onSearchEvent()
        onCancelEvent()
        onClearEvent()
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

    // Note:
    //  A bit sneaky but setup labels also check whether the actual project still exists and deletes it from save if not.
    private fun setupLabels() {
        if (musicPropModel != null) {
            val designModel = createViewModel.designCollection.find(musicPropModel)
            if (designModel != null) {
                if (musicPropModel!!.designName.isNotEmpty()) {
                    binding.selectedDesignLabel.text = musicPropModel!!.designName
                }
            } else {
                musicPropModel!!.changeDesign(DesignModel("", ""))
                edited = true
            }

            val lyricsModel = createViewModel.lyricsCollection.findLyrics(musicPropModel)
            if (lyricsModel != null) {
                if (musicPropModel!!.lyricsName.isNotEmpty()) {
                    binding.selectedLyricsLabel.text = musicPropModel!!.lyricsName
                }
            } else {
                musicPropModel!!.changeLyrics(TextModel("", "", ""))
                edited = true
            }

            val chordsModel = createViewModel.chordsCollection.findChords(musicPropModel)
            if (chordsModel != null) {
                if (musicPropModel!!.chordsName.isNotEmpty()) {
                    binding.selectedChordsLabel.text = musicPropModel!!.chordsName
                }
            } else {
                musicPropModel!!.changeChords(TextModel("", "", ""))
                edited = true
            }
        }
    }

    private fun onDesignEditEvent() {
        // setup initial value
        binding.title.text = "Design"
        binding.designEditBtn.setOnClickListener {
            changeLayout(LayoutState.SEARCH)
            editState = EditTypes.DESIGN
            projectList = createViewModel.designCollection.list
            updateAdapter()
        }
    }

    private fun onLyricsEditEvent() {
        // setup initial value
        binding.title.text = "Lyrics"
        binding.lyricsEditBtn.setOnClickListener {
            changeLayout(LayoutState.SEARCH)
            editState = EditTypes.LYRICS
            projectList = createViewModel.lyricsCollection.list
            updateAdapter()
        }
    }

    private fun onChordsEditEvent() {
        // setup initial value
        binding.title.text = "Chords"
        binding.chordsEditBtn.setOnClickListener {
            changeLayout(LayoutState.SEARCH)
            editState = EditTypes.CHORDS
            projectList = createViewModel.chordsCollection.list
            updateAdapter()
        }
    }

    private fun setupRecyclerAdapter() {
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
            projectList = createViewModel.designCollection.list.filter {
                if (text != null) {
                    it.name.contains(text, true)
                } else
                    false
            } as ArrayList<ProjectModel>
            updateAdapter()
        }
    }

    private fun onCancelEvent() {
        binding.cancelBtn.setOnClickListener {
            changeLayout(LayoutState.MAIN)
        }
    }

    private fun onClearEvent() {
        binding.clearBtn.setOnClickListener {
            when (editState) {
                EditTypes.DESIGN -> {
                    musicPropModel?.changeDesign(DesignModel("", ""))
                    playerViewModel.backgroundView?.setImageDrawable(null)
                    playerViewModel.foregroundView?.setImageResource(R.drawable.ic_app_icon)
                }
                EditTypes.LYRICS -> {
                    musicPropModel?.changeLyrics(TextModel("", "", ""))
                    if (!playerViewModel.textOnChords)
                        playerViewModel.textView?.text = PlayerFragment.missingLyrics
                }
                EditTypes.CHORDS -> {
                    musicPropModel?.changeChords(TextModel("", "", ""))
                    if (playerViewModel.textOnChords) {
                        playerViewModel.textView?.text = PlayerFragment.missingChords
                    }
                }
            }
            changeLayout(LayoutState.MAIN)
            edited = true
        }
    }

    override fun onItemClick(pos: Int) {
        val isIndexSafe = pos <= projectList.size - 1 && pos > -1
        if (isIndexSafe) {
            when (editState) {
                EditTypes.DESIGN -> {
                    val designModel = projectList[pos] as DesignModel
                    playerViewModel.backgroundView?.let { imageView ->
                        ShortLib.viewImageSetter(
                            requireActivity(),
                            imageView,
                            designModel.backgroundImagePath)
                    }
                    playerViewModel.foregroundView?.let { imageView ->
                        ShortLib.viewImageSetter(
                            requireActivity(),
                            imageView,
                            designModel.foregroundImagePath)
                    }
                    musicPropModel?.changeDesign(designModel)
                    edited = true
                    binding.selectedDesignLabel.text = designModel.name
                }
                EditTypes.LYRICS -> {
                    val textModel = projectList[pos] as TextModel
                    playerViewModel.textView?.let {
                        if (!playerViewModel.textOnChords)
                            it.text = textModel.text
                    }
                    musicPropModel?.changeLyrics(textModel)
                    edited = true
                    binding.selectedLyricsLabel.text = textModel.name
                }
                EditTypes.CHORDS -> {
                    val textModel = projectList[pos] as TextModel
                    playerViewModel.textView?.let {
                        if (playerViewModel.textOnChords)
                            it.text = textModel.text
                    }
                    musicPropModel?.changeChords(textModel)
                    edited = true
                    binding.selectedChordsLabel.text = textModel.name
                }
            }
        }
        changeLayout(LayoutState.MAIN)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (edited) {
            musicPropModel?.saveToStored(requireActivity())
        }
        playerViewModel.backgroundView = null
        playerViewModel.foregroundView = null
        playerViewModel.textView = null

    }

}