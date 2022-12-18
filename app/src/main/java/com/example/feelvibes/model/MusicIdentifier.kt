package com.example.feelvibes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class MusicIdentifier(
    val name : String,
    val artist : String
) : Parcelable{

    fun equals(musicName : String, musicArtist: String): Boolean {
        return name.equals(musicName, true) && artist.equals(musicArtist, true)
    }

    fun equals(musicIdentifier : MusicIdentifier): Boolean {
        return musicIdentifier.equals(name, artist)
    }

}