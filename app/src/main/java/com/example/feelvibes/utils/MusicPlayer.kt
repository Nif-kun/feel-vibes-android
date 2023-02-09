package com.example.feelvibes.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.player.notification.PlayerNotification

class MusicPlayer(
    var context: Context
) {
    var player : MediaPlayer? = null
    var notification = PlayerNotification(
        context,
        101,
        "FV_MP_APP",
        "FeelVibeApp",
        "FeelVibe notification channel."
    )

    private var randIndexGen : ShortLib.RandomIndexGenerator? = null
    var currentPlaylist: PlaylistModel? = null
    var currentIndex = 0
    var currentMusic: MusicModel? = null
    var shuffling = false

    private val onPreparedListeners = mutableListOf<() -> Unit>()
    private val onCompletionListeners = mutableListOf<() -> Unit>()

    fun onPreparedListener(listener: () -> Unit) {
        onCompletionListeners.add(listener)
    }

    fun onCompletionListener(listener: () -> Unit) {
        onCompletionListeners.add(listener)
    }

    fun buildNotification() {
        notification.build()
    }

    fun play() {
        player?.start()
    }

    fun pause() {
        player?.pause()
    }

    fun previous(onPlay: Boolean = true): Boolean {
        if (shuffling) {
            val randIndex = randIndexGen?.nextIndex()
            if (randIndex?.let { setMusic(it, onPlay) } == true) {
                currentIndex = randIndex
                return true
            }
        } else {
            if (setMusic(currentIndex-1, onPlay)) {
                currentIndex--
                return true
            }
        }
        return false
    }

    fun next(onPlay: Boolean = true): Boolean {
        if (shuffling) {
            val randIndex = randIndexGen?.nextIndex()
            if (randIndex?.let { setMusic(it, onPlay) } == true) {
                currentIndex = randIndex
                return true
            }
        } else {
            if (setMusic(currentIndex+1, onPlay)) {
                currentIndex++
                return true
            }
        }
        return false
    }

    fun repeat(toggle: Boolean) {
        player?.isLooping = toggle
    }

    fun shuffle(toggle: Boolean) {
        shuffling = toggle
        if (shuffling) {
            randIndexGen = currentPlaylist?.list?.size?.let { ShortLib.RandomIndexGenerator(it) }
        }
    }

    fun seekTo(msec: Int) {
        player?.seekTo(msec)
    }

    fun currentPosition(): Int {
        if (player != null) {
            return player!!.currentPosition
        }
        return 0
    }

    fun setPlaylist(playlistModel: PlaylistModel, index: Int) {
        val musicModel = playlistModel.list[index]
        currentPlaylist = playlistModel
        currentIndex = index
        setMusic(index)
    }

    fun getPlaylist(): PlaylistModel? {
        return currentPlaylist
    }

    fun setMusic(index: Int, start: Boolean = true): Boolean {
        if (currentPlaylist != null) {
            val musicModel = currentPlaylist!!.getOnIndex(index)
            if (musicModel != null) {
                currentMusic = musicModel
                player?.stop()
                player?.release()
                player = null
                player = MediaPlayer.create(context, Uri.parse(currentMusic!!.path))
                player?.setOnPreparedListener {
                    onPreparedEvent()
                }
                player?.setOnCompletionListener {
                    onCompleteEvent()
                }
                player?.isLooping = false
                if (start) {
                    player?.start()
                    Log.d("MusicPlayer", "Currently playing ${currentMusic?.title}...")
                }
                currentMusic?.let { notification.update(it) }
                return true
            }
        }
        return false
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying == true
    }

    fun isLooping(): Boolean {
        return player?.isLooping == true
    }

    private fun onPreparedEvent() {
        for (listener in onPreparedListeners) {
            listener()
        }
    }

    private fun onCompleteEvent() {
        if (!next()) { // Is last
            player?.seekTo(0)
        }
        for (listener in onCompletionListeners) {
            listener()
        }
    }

}