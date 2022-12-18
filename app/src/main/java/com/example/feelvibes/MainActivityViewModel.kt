package com.example.feelvibes

import androidx.lifecycle.ViewModel
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.model.PlaylistModel

class MainActivityViewModel : ViewModel() {

    val artistPlaylistDataList = ArrayList<PlaylistModel>()
    val albumPlaylistDataList = ArrayList<PlaylistModel>()
    val tagPlaylistDataList = ArrayList<PlaylistModel>()

    fun updateArtistPlaylistDataList(list : ArrayList<PlaylistModel>) {
        artistPlaylistDataList.clear()
        artistPlaylistDataList.addAll(list.filter { i -> i.type == PlaylistModel.Type.ARTIST })
    }

    fun updateAlbumPlaylistDataList(list : ArrayList<PlaylistModel>) {
        albumPlaylistDataList.clear()
        albumPlaylistDataList.addAll(list.filter { i -> i.type == PlaylistModel.Type.ALBUM })
    }

    fun updateTagPlaylistDataList(list : ArrayList<PlaylistModel>) {
        tagPlaylistDataList.clear()
        tagPlaylistDataList.addAll(list.filter { i -> i.type == PlaylistModel.Type.GENRE })
    }

}