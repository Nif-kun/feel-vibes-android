package com.example.feelvibes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PlaylistPresetModel(
    val musicIdentifierList : ArrayList<MusicIdentifier>
) : Parcelable {

    fun hasMusic(musicName : String, musicArtist: String): Boolean {
        return !musicIdentifierList.none { musicIdentifier ->  musicIdentifier.equals(musicName, musicArtist) }
    }

    fun hasMusic(musicIdentifier: MusicIdentifier): Boolean {
        return !musicIdentifierList.none { identifier -> identifier.equals(musicIdentifier)}
    }

}