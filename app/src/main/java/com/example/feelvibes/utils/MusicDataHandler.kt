package com.example.feelvibes.utils

import android.app.Activity
import android.content.ContentUris
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.model.PlaylistModel
import java.io.File


class MusicDataHandler {
    class Collect(
        private val activity : Activity,
        private val sortFilter: String = PlaylistModel.Type.NONE,
        private val sortedOnly : Boolean = false,
        private val playlistPreset : MutableMap<String, ArrayList<String>>? = null) {

        val data = ArrayList<MusicModel>()
        val sortedData = ArrayList<PlaylistModel>()

        init {
            // Checks version to identify which permission check to be used.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && PermissionHandler.ReadMediaAudio(activity).hasPermission) {
                collect()
            }
            else if (PermissionHandler.ReadExternalStorage(activity).hasPermission) {
                collect()
            }
        }

        private fun collect() {
            val musicDataList = ArrayList<MusicModel>()
            val playlistDataList = ArrayList<PlaylistModel>()
            //TODO
            // Use playlistPreset to create PlaylistModel items to be added in playlistDataList.
            // Example below shows how to filter the playlist preset
            // playlistPreset!!.filterValues { i: ArrayList<String> ->  i.contains("wow")}
            // In process:
            //    create playlist from preset >
            //    use filter to find if song exists in the preset >
            //    using the key of the filtered map, filter the playlistDataList >
            //    from the filtered playlistDataList, store the musicData for each.

            // Construct cursor arguments
            val projection = arrayListOf(
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.GENRE,
                MediaStore.Audio.Media.ALBUM_ID)
            val selection : String = MediaStore.Audio.Media.IS_MUSIC+" != 0"
            val sortOrder : String = MediaStore.Audio.Media.TRACK+" ASC"
            val cursor : Cursor = activity.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection.toTypedArray(),
                selection,
                null,
                sortOrder)!!

            // Start file scan
            while (cursor.moveToNext()) {
                val musicData = MusicModel(
                    path = cursor.getString(0),
                    track = cursor.getString(1),
                    title = cursor.getString(2),
                    duration = cursor.getString(3),
                    artist = cursor.getString(4),
                    album = cursor.getString(5),
                    genre = cursor.getString(6),
                    albumId = cursor.getLong(7)
                )
                if (File(musicData.path).exists()) {
                    if (sortFilter != PlaylistModel.Type.NONE) { // Creates default categories for sortedData
                        val playlistName = musicData.getValueByPlaylistType(sortFilter)
                        val isGenreType = sortFilter == PlaylistModel.Type.GENRE
                        val isNullString = playlistName == PlaylistModel.Type.NONE
                        var playlist : PlaylistModel? = playlistDataList.find {
                                playlistModel -> playlistModel.name.equals(playlistName, true)
                        }
                        if (playlist != null) {
                            playlist.musicDataList.add(musicData)
                        } else if (!(isGenreType && isNullString)){
                            playlist = PlaylistModel(playlistName, sortFilter)
                            playlist.musicDataList.add(musicData)
                            playlistDataList.add(playlist)
                        }
                    }
                    if (!sortedOnly)
                        musicDataList.add(musicData)
                }
            }
            cursor.close()

            // Apply gathered data
            if (sortFilter != PlaylistModel.Type.NONE)
                sortedData.addAll(playlistDataList)
            if (sortedOnly)
                return // cuts off the function before running the bottom code.
            data.addAll(musicDataList)
        }
    }
}