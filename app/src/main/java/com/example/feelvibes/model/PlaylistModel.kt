package com.example.feelvibes.model

import android.app.Activity
import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class PlaylistModel(
    val name: String,
    val type: String = Type.NONE,
    var thumbnail: Bitmap? = null,
    val musicDataList: ArrayList<MusicModel> = ArrayList(),
    val preset: ArrayList<PlaylistPresetModel>? = null
) : Parcelable {

    object Type {
        const val BUTTON = "button"
        const val NONE = "null"
        const val DEFAULT = "default"
        const val PLAYLIST = "playlist"
        const val ALBUM = "album"
        const val ARTIST = "artist"
        const val GENRE = "genre"
    }

    fun loadAlbumThumbnail(activity : Activity) {
        for (musicData in musicDataList) {
            musicData.loadThumbnail(activity)
            if (musicData.thumbnail != null) {
                thumbnail = musicData.thumbnail
                break
            }
        }
    }

    fun updateMusicDataList(list : ArrayList<MusicModel>){
        musicDataList.clear()
        musicDataList.addAll(list)
    }

    fun hasMusicInPreset(musicName : String, musicArtist: String): Boolean {
        return !preset!!.none { playlistPresetModel ->  playlistPresetModel.hasMusic(musicName, musicArtist)}
    }

    fun hasMusicInPreset(musicIdentifier: MusicIdentifier): Boolean {
        return !preset!!.none { playlistPresetModel ->  playlistPresetModel.hasMusic(musicIdentifier)}
    }

    fun save() {
        if (type.equals(Type.PLAYLIST, true)) {

        }
    }

    fun load() {
        // argument must be the data, either string to be read by a json reader
        // construct preset here
    }

    // create a save function which constructs a preset
    //create a read function that sets the list based on the given json or config data.

}