package com.example.feelvibes.services

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.feelvibes.R
import com.example.feelvibes.utils.MusicPlayer

class BackgroundSoundService : Service(){

    private val binder = BackgroundSoundBinder()
    var player : MusicPlayer? = MusicPlayer(this)

    inner class BackgroundSoundBinder : Binder() {
        fun getService(): BackgroundSoundService = this@BackgroundSoundService
    }

    override fun onDestroy() {
        player = null
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

}
