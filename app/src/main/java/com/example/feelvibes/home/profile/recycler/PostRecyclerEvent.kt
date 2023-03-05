package com.example.feelvibes.home.profile.recycler

interface PostRecyclerEvent {

    fun onMusicClick(
        url: String,
        track: String?,
        title: String,
        duration: String,
        artist: String?,
        album: String?,
        genre: String?
    ) {}
    fun onDesignClick(name: String, author: String, foregroundUrl: String?, backgroundUrl: String?) {}
    fun onChordsClick(name: String, author: String, chords: String?) {}
    fun onLyricsClick(name: String, author: String, lyrics: String?) {}

    fun onMusicDownload(title: String, id: String, url: String) {}
    fun onDesignDownload(name: String, id: String, foregroundUrl: String, backgroundUrl: String) {}
    fun onChordsDownload(name: String, id: String, chords: String) {}
    fun onLyricsDownload(name: String, id: String, lyrics: String) {}

}