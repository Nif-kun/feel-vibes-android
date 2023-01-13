package com.example.feelvibes.model

import android.app.Activity
import android.content.ContentUris
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Size
import kotlinx.parcelize.Parcelize

@Parcelize
class MusicModel(
    val path: String,
    val track : String?,
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
            thumbnail = try {
                activity.contentResolver.loadThumbnail(contentUri, Size(480, 480), null)
            } catch (_:Exception) { null }
        }
    }

    fun getValueByPlaylistType(type : String): String {
        return when(type) {
            PlaylistModel.Type.ARTIST -> {
                return when(artist != null) {
                    true -> artist.toString()
                    else -> PlaylistModel.Type.NONE
                }
            }
            PlaylistModel.Type.ALBUM -> {
                return when(album != null) {
                    true -> album.toString()
                    else -> PlaylistModel.Type.NONE
                }
            }
            PlaylistModel.Type.GENRE -> {
                return when(genre != null) {
                    true -> genre.toString()
                    else -> PlaylistModel.Type.NONE
                }
            }
            else -> { PlaylistModel.Type.NONE }
        }
    }

}