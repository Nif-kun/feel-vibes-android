package com.example.feelvibes.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
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
