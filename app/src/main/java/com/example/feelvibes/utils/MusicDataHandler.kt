package com.example.feelvibes.utils

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.model.PlaylistCapsuleModel
import com.example.feelvibes.model.PlaylistModel
import java.io.File


class MusicDataHandler {
    class Collect(
        private val activity : Activity,
        private val sortFilter: String = PlaylistModel.Type.NONE,
        private val sortedOnly : Boolean = false,
        private val playlistPreset : ArrayList<PlaylistCapsuleModel>? = null) {

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

            if (playlistPreset != null) {
                for (preset in playlistPreset) {
                    var thumbnailUri : Uri? = null
                    if (preset.thumbnailUri != null && preset.thumbnailUri.isNotEmpty()) {
                        thumbnailUri = Uri.parse(preset.thumbnailUri)
                    }
                    playlistDataList.add(
                        PlaylistModel(preset.name, PlaylistModel.Type.PLAYLIST, null, ArrayList(), preset.paths, thumbnailUri)
                    )
                }
            }

            // TODO
            //  playlistDataList will be populated if playlistPreset exists, it will refer PlaylistModel value from it.
            //  If already implemented, playlistModel should have a settable preset array of Strings to store paths.
            //  Or better yet, have the playlistModel load then use it as playlistPreset, turning playlistDataList into a var
            //  Refer to PlaylistFragment ^^^

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
                    if (sortFilter == PlaylistModel.Type.PLAYLIST) {
                        playlistDataList.forEach {
                            if (it.containsPreset(musicData.path))
                                it.add(musicData)
                        }
                    } else if (sortFilter != PlaylistModel.Type.NONE) { // Creates default categories for sortedData
                        val playlistName = musicData.getValueByPlaylistType(sortFilter)
                        val isNullString = playlistName == PlaylistModel.Type.NONE
                        var playlist : PlaylistModel? = playlistDataList.find {
                                playlistModel -> playlistModel.name.equals(playlistName, true)
                        }
                        if (playlist != null) {
                            playlist.add(musicData)
                        } else if (!isNullString){
                            playlist = PlaylistModel(playlistName, sortFilter)
                            playlist.add(musicData)
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