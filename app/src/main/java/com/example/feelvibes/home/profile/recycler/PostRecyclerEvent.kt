package com.example.feelvibes.home.profile.recycler

import com.example.feelvibes.model.DesignModel
import com.example.feelvibes.model.TextModel

interface PostRecyclerEvent {

    fun onUserClick(userId: String) {}

    fun onMusicClick(
        url: String,
        track: String?,
        title: String,
        duration: String,
        artist: String?,
        album: String?,
        genre: String?
    ) {}
    fun onDesignClick(id: String, name: String, foregroundUrl: String?, backgroundUrl: String?) {}
    fun onChordsClick(id: String, name: String, chords: String?) {}
    fun onLyricsClick(id: String, name: String, lyrics: String?) {}

    // Deprecate(?)
    fun onMusicDownload(url: String, title: String) {}
    fun onDesignDownload(designModel: DesignModel) {}
    fun onChordsDownload(textModel:TextModel) {}
    fun onLyricsDownload(textModel:TextModel) {}

}