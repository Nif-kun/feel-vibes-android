package com.example.feelvibes.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
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
    var playbackSpeed = 1.0f

    private val onPreparedListeners = mutableListOf<() -> Unit>()
    private val onCompletionListeners = mutableListOf<() -> Unit>()
    private val onPlayListeners = mutableListOf<() -> Unit>()
    private val onNextListener = mutableListOf<(Boolean) -> Unit>()

    fun onPreparedListener(listener: () -> Unit) {
        onPreparedListeners.add(listener)
    }

    fun onCompletionListener(listener: () -> Unit) {
        onCompletionListeners.add(listener)
    }

    fun setOnPlayListener(listener: () -> Unit) {
        onPlayListeners.add(listener)
    }

    fun setOnNextListener(listener: (Boolean) -> Unit) {
        onNextListener.add(listener)
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

    fun previous(onPlay: Boolean = true, callback: (Boolean) -> Unit) {
        if (shuffling) {
            val randIndex = randIndexGen?.nextIndex()
            if (randIndex != null) {
                setMusic(randIndex, onPlay) {
                    if (it) {
                        currentIndex = randIndex
                        callback(true)
                    } else
                        callback(false)
                }
            } else
                callback(false)
        } else {
            setMusic(currentIndex-1, onPlay) {
                if (it) {
                    currentIndex--
                    callback(true)
                } else
                    callback(false)
            }
        }
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

    fun next(onPlay: Boolean = true, callback: (Boolean) -> Unit) {
        if (shuffling) {
            val randIndex = randIndexGen?.nextIndex()
            if (randIndex != null) {
                setMusic(randIndex, onPlay) {
                    if (it) {
                        currentIndex = randIndex
                        callback(true)
                    } else
                        callback(false)
                }
            } else
                callback(false)
        } else {
            setMusic(currentIndex+1, onPlay) {
                if (it) {
                    currentIndex++
                    callback(true)
                } else
                    callback(false)
            }
        }
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

    fun setSpeed(speed: Float) {
        playbackSpeed = speed
        if (player?.isPlaying == true) {
            //pause()
            player!!.playbackParams = player!!.playbackParams.setSpeed(playbackSpeed)
            //play()
        }
    }

    fun setPlaylist(playlistModel: PlaylistModel, index: Int) {
        currentPlaylist = playlistModel
        currentIndex = index
        setMusic(index) {
            if (it)
                Log.d("MusicPlayer", "Player music was set successfully!")
            else
                Log.d("MusicPlayer", "Player music failed to set!")
        }
    }

    fun setPlaylist(playlistModel: PlaylistModel, index: Int, callback: (Boolean) -> Unit) {
        currentPlaylist = playlistModel
        currentIndex = index
        setMusic(index) {
            if (it) {
                Log.d("MusicPlayer", "Player music was set successfully!")
                callback(true)
            } else {
                Log.d("MusicPlayer", "Player music failed to set!")
                callback(false)
            }
        }
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
                    onPlayEvent()
                    Log.d("MusicPlayer", "Currently playing ${currentMusic?.title}...")
                }
                currentMusic?.let { notification.update(it, isPlaying()) }
                return true
            }
        }
        return false
    }

    fun setMusic(index: Int, start: Boolean = true, callback: (Boolean)->Unit) {
        if (currentPlaylist != null) {
            val musicModel = currentPlaylist!!.getOnIndex(index)
            if (musicModel != null) {
                currentMusic = musicModel
                player?.stop()
                player?.release()
                player = null
                player = MediaPlayer()
                try {
                    player?.reset()
                    player?.setDataSource(context, Uri.parse(currentMusic!!.path))
                    player?.prepareAsync()
                    player?.setOnPreparedListener {
                        onPreparedEvent()
                        player?.isLooping = false // stop loop by default.
                        if (start) {
                            //player?.playbackParams = player!!.playbackParams.setSpeed(1f) TODO Playback speed
                            player?.start()
                            Log.d("MusicPlayer", "Currently playing ${currentMusic?.title}...")
                            onPlayEvent()
                        }
                        currentMusic?.let { notification.update(it, isPlaying()) }
                        callback(true)
                    }
                    player?.setOnCompletionListener {
                        onCompleteEvent()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(false)
                }
            } else
                callback(false)
        } else
            callback(false)
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

    private fun onPlayEvent() {
        for (listener in onPlayListeners) {
            listener()
        }
    }

    private fun onCompleteEvent() {
        next {
            if (!it) {
                player?.seekTo(0)
                currentMusic?.let { model -> notification.update(model, isPlaying()) }
            }
            for (listener in onNextListener) {
                listener(it)
            }
        }
        for (listener in onCompletionListeners) {
            listener()
        }
    }

}