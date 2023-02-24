package com.example.feelvibes.player

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentPlayerBinding
import com.example.feelvibes.model.DesignModel
import com.example.feelvibes.model.MusicPropModel
import com.example.feelvibes.model.TextModel
import com.example.feelvibes.utils.PermissionHandler
import com.example.feelvibes.utils.ShortLib
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.view_model.HomeViewModel
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.view_model.PlayerViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import pl.droidsonroids.gif.GifDrawable


class PlayerFragment : FragmentBind<FragmentPlayerBinding>(FragmentPlayerBinding::inflate) {

    companion object {
        const val missingLyrics = "This music does not have any lyrics set!"
        const val missingChords = "This music does not have any chords set!"
    }

    private lateinit var createViewModel: CreateViewModel
    private lateinit var libraryViewModel: LibraryViewModel
    private lateinit var playerViewModel: PlayerViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var awake = false // Just follow the usage, tl;dr it is meant to stop updating views when unfocused.

    private var musicPropModel: MusicPropModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
        playerViewModel = ViewModelProvider(requireActivity())[PlayerViewModel::class.java]
        if (createViewModel.designCollection.list.size < 1) {
            createViewModel.designCollection.populateFromStored(requireActivity())
        }
        if (createViewModel.lyricsCollection.list.size < 1) {
            createViewModel.lyricsCollection.populateFromStored(requireActivity())
        }
        if (createViewModel.chordsCollection.list.size < 1) {
            createViewModel.chordsCollection.populateFromStored(requireActivity())
        }
        // Asks for notification permission.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            PermissionHandler.Notification(requireActivity(), true).check()
    }

    override fun onReady() {
        mainActivity.hideMainMenu()
        if (homeViewModel.layoutState == HomeViewModel.Layouts.NONE) {
            mainActivity.unpadMainView()
            mainActivity.hideToolBar()
        } else {
            mainActivity.hideToolBar(true)
        }
        mainActivity.hideStickyPlayer()

        val isCurrentPlaylist = if (libraryViewModel.selectedPlaylist != null) {
            libraryViewModel.selectedPlaylist!!.match(libraryViewModel.currentPlaylist)
        } else { false }
        val isCurrentMusic = libraryViewModel.currentMusic?.path.equals(libraryViewModel.selectedMusic?.path, true)
        if (!isCurrentMusic)
            libraryViewModel.currentMusic = libraryViewModel.selectedMusic
        if (!isCurrentPlaylist)
            libraryViewModel.currentPlaylist = libraryViewModel.selectedPlaylist

        autoPlay(isCurrentMusic, isCurrentPlaylist)

        updateViews()

        onPlayEvent()
        onSeekerBarEvent()
        onSkipPreviousEvent()
        onSkipNextEvent()
        onRepeatEvent()
        onShuffleEvent()
        onSwitchCoverEvent()
        onShowPlaylistEvent()
        onMoreEvent()

        onMusicCompleteEvent()
    }

    override fun onStart() {
        super.onStart()
        updateViews()
        awake = true
    }

    override fun onStop() {
        super.onStop()
        awake = false
    }

    override fun onResume() {
        super.onResume()
        updateViews()
        awake = true
    }

    override fun onPause() {
        super.onPause()
        awake = false
    }

    private fun onSeekerBarEvent() {
        binding.progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // user has started dragging the seeker bar
                if (mainActivity.musicPlayer != null) {
                    mainActivity.musicPlayer!!.pause()
                    if (!mainActivity.musicPlayer!!.isPlaying()) {
                        binding.playBtn.setImageResource(R.drawable.ic_play_arrow_24)
                    }
                }
                setupDesign(true)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    if (mainActivity.musicPlayer != null) {
                        mainActivity.musicPlayer!!.seekTo(progress)
                        binding.currentTime.text = ShortLib.msecToMMSS(progress)
                    }
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (mainActivity.musicPlayer != null) {
                    mainActivity.musicPlayer!!.play()
                    if (mainActivity.musicPlayer!!.isPlaying()) {
                        binding.playBtn.setImageResource(R.drawable.ic_pause_24)
                    }
                }
                setupDesign(false)
            }
        })
    }

    private fun setupSeekerBar() {
        if (mainActivity.musicPlayer != null) {
            val updateSeekerBar = object : Runnable {
                override fun run() {
                    if (awake) {
                        val max = if (mainActivity.musicPlayer!!.currentMusic?.duration?.toInt() != null) {
                            mainActivity.musicPlayer!!.currentMusic?.duration?.toInt()!!
                        } else { 0 }

                        if (binding.progressBar.max != max) {
                            binding.progressBar.max = max
                            binding.maxTime.text = ShortLib.msecToMMSS(max)
                        }
                        binding.progressBar.progress = mainActivity.musicPlayer!!.currentPosition()
                        binding.currentTime.text = ShortLib.msecToMMSS(binding.progressBar.progress)
                        binding.progressBar.postDelayed(this, 50)
                    }
                }
            }
            binding.progressBar.post(updateSeekerBar)
        }
    }

    private fun onMusicCompleteEvent() {
        // Occurs when music automatically moves to the next one.
        mainActivity.musicPlayer?.onCompletionListener {
            if (awake) {
                updateViews()
                if (mainActivity.musicPlayer?.isPlaying() != true) {
                    binding.playBtn.setImageResource(R.drawable.ic_play_arrow_24)
                }
                setupDesign()
            }
        }
    }

    fun updateViews() {
        if (mainActivity.musicPlayer?.currentMusic != null) {
            libraryViewModel.currentMusic = mainActivity.musicPlayer?.currentMusic
        }
        setupMusicProp()
        setupLabels()
        setupCoverArt()
        setupSeekerBar()
    }

    private fun setupLabels() {
        if (libraryViewModel.currentMusic != null) {
            binding.titleLabel.text = libraryViewModel.currentMusic!!.title
            binding.artistLabel.text = libraryViewModel.currentMusic!!.artist
        }
    }

    private fun setupCoverArt() {
        if (libraryViewModel.currentMusic != null && libraryViewModel.currentMusic!!.thumbnail != null)
            binding.coverArtView.setImageBitmap(libraryViewModel.currentMusic!!.thumbnail)
        else if (libraryViewModel.currentPlaylist != null && libraryViewModel.currentPlaylist!!.thumbnail != null)
            binding.coverArtView.setImageBitmap(libraryViewModel.currentPlaylist!!.thumbnail)
        else
            binding.coverArtView.setImageResource(R.drawable.ic_album_24)
    }

    private fun setupMusicProp() {
        // Defaulter to ensure that the ones without value return to default
        // TODO: This doesn't work tbh, idk anymore fuck it.

        // Value Setter
        if (mainActivity.musicPlayer?.currentMusic != null) {
            musicPropModel = MusicPropModel(mainActivity.musicPlayer?.currentMusic!!.path)
            if (musicPropModel?.loadFromStored(requireActivity()) == true) {
                setupDesignModel()
                setupChordsModel()
                setupLyricsModel()
            } else { // Defaults
                if (playerViewModel.textOnChords)
                    binding.textView.text = missingChords
                else
                    binding.textView.text = missingLyrics
                binding.designInclude.backgroundImageView.setImageDrawable(null)
                binding.designInclude.foregroundImageView.setImageResource(R.drawable.ic_app_icon)
            }
        }
    }

    private fun setupDesignModel() {
        val model: DesignModel? = createViewModel.designCollection.find(musicPropModel)
        if (model != null) {
            ShortLib.viewImageSetter(requireActivity(), binding.designInclude.backgroundImageView, model.backgroundImagePath)
            ShortLib.viewImageSetter(requireActivity(), binding.designInclude.foregroundImageView, model.foregroundImagePath)
        } else {
            binding.designInclude.backgroundImageView.setImageDrawable(null)
            binding.designInclude.foregroundImageView.setImageResource(R.drawable.ic_app_icon)
        }
    }

    private fun setupLyricsModel() {
        if (!playerViewModel.textOnChords) {
            binding.textView.text = missingLyrics // Default setter
            val model: TextModel? = createViewModel.lyricsCollection.findLyrics(musicPropModel)
            if (model != null) {
                binding.textView.text = model.text
            } else {
                binding.textView.text = missingLyrics
            }
        }
    }

    private fun setupChordsModel() {
        if (playerViewModel.textOnChords) {
            binding.textView.text = missingChords // Default setter
            val model: TextModel? = createViewModel.chordsCollection.findChords(musicPropModel)
            if (model != null) {
                binding.textView.text = model.text
            } else {
                binding.textView.text = missingChords
            }
        }
    }

    private fun setupDesign(forcePause: Boolean = false) {
        // Do a default for none ones here
        if (binding.designInclude.backgroundImageView.drawable is GifDrawable) {
            if (mainActivity.musicPlayer?.isPlaying() == true && !forcePause) {
                (binding.designInclude.backgroundImageView.drawable as GifDrawable).start()
                (binding.designInclude.foregroundImageView.drawable as GifDrawable).start()
            } else {
                (binding.designInclude.backgroundImageView.drawable as GifDrawable).pause()
                (binding.designInclude.foregroundImageView.drawable as GifDrawable).pause()
            }
        }
    }

    private fun onSwitchCoverEvent() {
        // TODO
        //  It's alive! ALIIIIVEE!! It's a Frankenstine level of bullshit lmao.

        binding.switchCoverView.setOnClickListener {
            if (binding.coverArtView.visibility == View.VISIBLE) {
                binding.coverArtView.visibility = View.GONE
                binding.designInclude.designLayout.visibility = View.VISIBLE
            } else if (binding.designInclude.designLayout.visibility == View.VISIBLE) {
                binding.designInclude.designLayout.visibility = View.GONE
                binding.textScrollLayout.visibility = View.VISIBLE
                playerViewModel.textOnChords = false
                musicPropModel?.loadFromStored(requireActivity()) // This is to ensure that new data is loaded when switching.
                setupLyricsModel()
            } else if (binding.textScrollLayout.visibility == View.VISIBLE) {
                if (playerViewModel.textOnChords) {
                    binding.textScrollLayout.visibility = View.GONE
                    binding.coverArtView.visibility = View.VISIBLE
                } else {
                    playerViewModel.textOnChords = true
                    musicPropModel?.loadFromStored(requireActivity()) // This is to ensure that new data is loaded when switching.
                    setupChordsModel()
                }
            }
        }
    }

    private fun onPlayEvent() {
        // if playing or not, change the drawable.
        binding.playBtn.setOnClickListener {
            if (mainActivity.musicPlayer?.isPlaying() == true) {
                binding.playBtn.setImageResource(R.drawable.ic_play_arrow_24)
                mainActivity.musicPlayer?.pause()
                setupDesign()
            } else {
                binding.playBtn.setImageResource(R.drawable.ic_pause_24)
                mainActivity.musicPlayer?.play()
                setupDesign()
            }
        }
    }

    private fun onSkipPreviousEvent() {
        binding.skipPreviousBtn.setOnClickListener {
            if (mainActivity.musicPlayer != null && mainActivity.musicPlayer!!.previous(mainActivity.musicPlayer!!.isPlaying())) {
                libraryViewModel.currentMusic = mainActivity.musicPlayer!!.currentMusic
                setupLabels()
                setupCoverArt()
                setupMusicProp()
            } else
                Toast.makeText(mainActivity, "This is the first!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onSkipNextEvent() {
        binding.skipNextBtn.setOnClickListener {
            if (mainActivity.musicPlayer != null && mainActivity.musicPlayer!!.next(mainActivity.musicPlayer!!.isPlaying())) {
                libraryViewModel.currentMusic = mainActivity.musicPlayer!!.currentMusic
                setupLabels()
                setupCoverArt()
                setupMusicProp()
            } else
                Toast.makeText(mainActivity, "This is the last!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onRepeatEvent() {
        if (mainActivity.musicPlayer != null) {
            if (mainActivity.musicPlayer!!.isLooping())
                binding.repeatBtn.setImageResource(R.drawable.ic_repeat_on_24)
            binding.repeatBtn.setOnClickListener {
                if (mainActivity.musicPlayer!!.isLooping()) {
                    mainActivity.musicPlayer!!.repeat(false)
                    binding.repeatBtn.setImageResource(R.drawable.ic_repeat_24)
                } else {
                    mainActivity.musicPlayer!!.repeat(true)
                    binding.repeatBtn.setImageResource(R.drawable.ic_repeat_on_24)
                }
            }
        }
    }

    private fun onShuffleEvent() {
        if (mainActivity.musicPlayer != null) {
            if (mainActivity.musicPlayer!!.shuffling)
                binding.shuffleBtn.setImageResource(R.drawable.ic_shuffle_on_24)
            binding.shuffleBtn.setOnClickListener {
                if (mainActivity.musicPlayer!!.shuffling) {
                    mainActivity.musicPlayer!!.shuffle(false)
                    binding.shuffleBtn.setImageResource(R.drawable.ic_shuffle_24)
                } else {
                    mainActivity.musicPlayer!!.shuffle(true)
                    binding.shuffleBtn.setImageResource(R.drawable.ic_shuffle_on_24)
                }
            }
        }
    }

    private fun autoPlay(isCurrentMusic: Boolean = false, isCurrentPlaylist: Boolean = false) {
        if (!libraryViewModel.navFromSticky) {
            if (!isCurrentPlaylist || !isCurrentMusic) {
                if (libraryViewModel.currentPlaylist != null) {
                    mainActivity.musicPlayer?.setPlaylist(libraryViewModel.currentPlaylist!!, libraryViewModel.selectedMusicPos)
                    mainActivity.musicPlayer?.play()
                    libraryViewModel.selectedMusicPos = 0
                }
            }
        }
        if (mainActivity.musicPlayer != null && mainActivity.musicPlayer!!.isPlaying()) {
            binding.playBtn.setImageResource(R.drawable.ic_pause_24)
        }

         // Autoplay occurs first and sets the value for the currentMusic. This is for when view is being made.
        musicPropModel = MusicPropModel(mainActivity.musicPlayer?.currentMusic!!.path)
        musicPropModel?.loadFromStored(requireActivity())
    }

    private fun onShowPlaylistEvent() {
        binding.showPlaylistBtn.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_playerPlaylistBottomSheet)
            playerViewModel.playerFragment = this
        }
    }

    private fun onMoreEvent() {
        binding.moreBtn.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_playerMoreBottomSheet)
            // check here
            playerViewModel.backgroundView = binding.designInclude.backgroundImageView
            playerViewModel.foregroundView = binding.designInclude.foregroundImageView
            playerViewModel.textView = binding.textView
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.showMainMenu()
        // This is such a dirty way of doing it.
        if (homeViewModel.layoutState == HomeViewModel.Layouts.NONE) {
            mainActivity.showToolBar()
            mainActivity.padMainView()
        } else if (homeViewModel.layoutState == HomeViewModel.Layouts.HOME) {
            mainActivity.showToolBar(true)
        }
        mainActivity.setupStickyPlayer()
        if (mainActivity.musicPlayer?.currentMusic != null)
            mainActivity.showStickyPlayer()
        libraryViewModel.navFromSticky = false
        playerViewModel.textOnChords = false
    }

}