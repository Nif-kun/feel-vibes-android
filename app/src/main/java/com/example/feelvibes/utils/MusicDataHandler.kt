package com.example.feelvibes.utils

import android.app.Activity
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import com.example.feelvibes.model.MusicModel
import java.io.File

class MusicDataHandler {
     class Collect(private val activity : Activity) {

        val data = ArrayList<MusicModel>()

        init {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && PermissionHandler.ReadMediaAudio(activity).hasPermission) {
                data.clear()
                data.addAll(collect())
            }
            else if (PermissionHandler.ReadExternalStorage(activity).hasPermission) {
                data.clear()
                data.addAll(collect())
            }
        }

        private fun collect(): ArrayList<MusicModel> {
            val musicDataList = ArrayList<MusicModel>()
            val projection = arrayListOf(
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.GENRE)
            val selection : String = MediaStore.Audio.Media.IS_MUSIC+" != 0"
            val sortOrder : String = MediaStore.Audio.Media.TRACK+" ASC"
            val cursor : Cursor = activity.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection.toTypedArray(), selection, null, sortOrder)!!
            while (cursor.moveToNext()) {
                val musicData = MusicModel(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)
                )
                if (File(musicData.path).exists()) {
                    // Crashes due to EXTRA_SIZE thingy.
                    //musicData.albumArt = activity.contentResolver.loadThumbnail(musicData.path.toUri(),  Size.parseSize(ContentResolver.EXTRA_SIZE), null)
                    musicDataList.add(musicData)
                }
                // TODO [Ref:0-LibraryFragment.kt]
                //  Start segregating inside this loop. Return an ArrayList<PlaylistModel> instead
            }
            cursor.close()
            return musicDataList
        }
    }
}