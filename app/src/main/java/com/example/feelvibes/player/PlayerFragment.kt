package com.example.feelvibes.player

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.feelvibes.R
import com.example.feelvibes.databinding.FragmentPlayerBinding
import com.example.feelvibes.model.DesignModel
import com.example.feelvibes.model.MusicPropModel
import com.example.feelvibes.utils.InternalStorageHandler
import com.example.feelvibes.utils.ShortLib
import com.example.feelvibes.view_model.CreateViewModel
import com.example.feelvibes.view_model.LibraryViewModel
import com.example.feelvibes.view_model.PlayerViewModel
import com.example.feelvibes.viewbinds.FragmentBind
import pl.droidsonroids.gif.GifDrawable


class PlayerFragment : FragmentBind<FragmentPlayerBinding>(FragmentPlayerBinding::inflate) {

    // TODO
    //  > Select and show design/lyrics/chords
    //  > Have it show in notification

    private lateinit var createViewModel: CreateViewModel
    private lateinit var libraryViewModel: LibraryViewModel
    private lateinit var playerViewModel: PlayerViewModel
    private var awake = false // Just follow the usage, tl;dr it is meant to stop updating views when unfocused.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryViewModel = ViewModelProvider(requireActivity())[LibraryViewModel::class.java]
        createViewModel = ViewModelProvider(requireActivity())[CreateViewModel::class.java]
        playerViewModel = ViewModelProvider(requireActivity())[PlayerViewModel::class.java]
    }

    override fun onReady() {
        mainActivity.hideMainMenu()
        mainActivity.hideToolBar()
        mainActivity.unpadMainView()
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

    private fun updateViews() {
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
        if (mainActivity.musicPlayer?.currentMusic != null) {
            val musicPropModel = MusicPropModel(mainActivity.musicPlayer?.currentMusic!!.path)
            musicPropModel.loadFromStored(requireActivity())
            if (createViewModel.designCollection.list.size < 1) {
                createViewModel.designCollection.populateFromStored(requireActivity())
            }

            var designModel : DesignModel? = null
            for (model in createViewModel.designCollection.list) {
                if (musicPropModel.matchDesign(model as DesignModel)) {
                    designModel = model
                    break
                }
            }
            designModel?.let {
                ShortLib.viewImageSetter(requireActivity(), binding.designInclude.backgroundImageView, designModel.backgroundImagePath)
                ShortLib.viewImageSetter(requireActivity(), binding.designInclude.foregroundImageView, designModel.foregroundImagePath)
            }
        }
    }

    private fun setupDesign(forcePause: Boolean = false) {
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
                // change/get value here
                binding.textView.text = "This is on lyrics"
                playerViewModel.textOnChords = false
            } else if (binding.textScrollLayout.visibility == View.VISIBLE) {
                if (playerViewModel.textOnChords) {
                    binding.textScrollLayout.visibility = View.GONE
                    binding.coverArtView.visibility = View.VISIBLE
                } else {
                    playerViewModel.textOnChords = true
                    // change/get value here
                    binding.textView.text = "This is on chords"
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
    }

    private fun onShowPlaylistEvent() {
        binding.showPlaylistBtn.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_playerPlaylistBottomSheet)
        }
        // show bottomFragment for playlist
    }

    private fun onMoreEvent() {
        binding.moreBtn.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_playerMoreBottomSheet)
            playerViewModel.backgroundView = binding.designInclude.backgroundImageView
            playerViewModel.foregroundView = binding.designInclude.foregroundImageView
            playerViewModel.selectedDesignModel = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.showMainMenu()
        mainActivity.showToolBar()
        mainActivity.padMainView()
        mainActivity.setupStickyPlayer()
        if (mainActivity.musicPlayer?.currentMusic != null)
            mainActivity.showStickyPlayer()
        libraryViewModel.navFromSticky = false
        playerViewModel.textOnChords = false
    }

}