package com.example.feelvibes.model

import android.app.Activity
import android.content.ContentUris
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Size
import com.example.feelvibes.MainActivity
import kotlinx.parcelize.Parcelize

@Parcelize
class MusicModel(
    val path: String,
    val track : String,
    val title: String,
    val duration: String,
    val artist: String? = null,
    val album: String? = null,
    val genre : String? = null,
    private var albumId : Long? = null,
    var thumbnail : Bitmap? = null
) : Parcelable {

    fun loadThumbnail(activity: Activity){
        if (albumId != null) {
            val contentUri: Uri = ContentUris.withAppendedId(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                albumId!!
            )
            thumbnail = activity.contentResolver.loadThumbnail(contentUri, Size(480, 480), null)
        }
    }

    fun getValueByPlaylistType(type : String): String {
        return when(type) {
            PlaylistModel.Type.ARTIST -> artist.toString()
            PlaylistModel.Type.ALBUM -> album.toString()
            PlaylistModel.Type.GENRE -> genre.toString()
            else -> { PlaylistModel.Type.NONE}
        }
    }

}