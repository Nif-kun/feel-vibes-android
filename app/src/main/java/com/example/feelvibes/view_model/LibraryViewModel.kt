package com.example.feelvibes.view_model

import androidx.lifecycle.ViewModel
import com.example.feelvibes.library.LibraryFragment
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.model.PlaylistCollectionModel
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter
import java.util.Dictionary

class LibraryViewModel : ViewModel() {

    val customCollection = PlaylistCollectionModel("CUSTOM_PLAYLIST", PlaylistModel.Type.PLAYLIST)
    val artistCollection = PlaylistCollectionModel(null, PlaylistModel.Type.ARTIST)
    val albumCollection = PlaylistCollectionModel(null, PlaylistModel.Type.ALBUM)
    val tagCollection = PlaylistCollectionModel(null, PlaylistModel.Type.GENRE)

    var currentLibraryTab = LibraryFragment.PLAYLISTS
    var currentPlaylist : PlaylistModel? = null
    var currentMusic : MusicModel? = null

    var selectedAdapter : ItemRecyclerAdapter? = null
    var selectedAdapterPos = -1

    var selectedPlaylistCollection : PlaylistCollectionModel? = null
    var selectedPlaylist : PlaylistModel? = null
    var selectedMusicPos : Int = 0
    var selectedMusic : MusicModel? = null

    var navFromSticky = false

}