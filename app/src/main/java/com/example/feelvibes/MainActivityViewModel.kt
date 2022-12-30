package com.example.feelvibes

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.example.feelvibes.library.LibraryFragment
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.model.PlaylistModel
import com.example.feelvibes.recycler.adapter.ItemRecyclerAdapter

class MainActivityViewModel : ViewModel() {

    private val storedCustomPlaylist = ArrayList<PlaylistModel>()
    private val createdCustomPlaylist = ArrayList<PlaylistModel>()

    val favoritePlaylistModel = PlaylistModel(
            name = "Favorites",
            type = PlaylistModel.Type.DEFAULT
    )

    val customPlaylistDataList = ArrayList<PlaylistModel>()
    val artistPlaylistDataList = ArrayList<PlaylistModel>()
    val albumPlaylistDataList = ArrayList<PlaylistModel>()
    val tagPlaylistDataList = ArrayList<PlaylistModel>()

    var currentLibraryTab = LibraryFragment.PLAYLISTS
    var currentPlaylist : PlaylistModel? = null
    var currentMusic : MusicModel? = null

    var selectedAdapter : ItemRecyclerAdapter? = null
    var selectedAdapterPos = -1

    var selectedPlaylist : PlaylistModel? = null
    var selectedMusic : MusicModel? = null

    fun updateCustomPlaylistDataList(list: ArrayList<PlaylistModel>, res: Resources) {
        storedCustomPlaylist.clear()
        storedCustomPlaylist.addAll(list.filter { i -> i.type == PlaylistModel.Type.PLAYLIST })
        customPlaylistDataList.clear()
        addCreatePlaylistButton(res)
        customPlaylistDataList.add(favoritePlaylistModel)
        customPlaylistDataList.addAll(storedCustomPlaylist)
        customPlaylistDataList.addAll(createdCustomPlaylist)
    }

    private fun addCreatePlaylistButton(res: Resources) {
        customPlaylistDataList.add(
            PlaylistModel(
                name = res.getString(R.string.create_playlist),
                type = PlaylistModel.Type.BUTTON)
        )
    }

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


    fun addCustomPlaylist(playlistModel : PlaylistModel) {
        customPlaylistDataList.add(playlistModel)
    }

    fun removeCustomPlaylist(playlistModel : PlaylistModel) {
        customPlaylistDataList.remove(playlistModel)
    }

    fun addCustomCreatedPlaylist(playlistModel : PlaylistModel) {
        if (createdCustomPlaylist.find { i -> i == playlistModel } == null) {
            createdCustomPlaylist.add(playlistModel)
            customPlaylistDataList.add(playlistModel)
        }
    }

    fun removeCustomCreatedPlaylist(playlistModel: PlaylistModel) {
        createdCustomPlaylist.remove(playlistModel)
        customPlaylistDataList.remove(playlistModel)
    }


    fun addFavoritePlaylist(musicModel: MusicModel) {
        if (favoritePlaylistModel.musicDataList.find { i -> i.path == musicModel.path } == null)
            favoritePlaylistModel.musicDataList.add(musicModel)
    }

    fun removeFavoritePlaylist(musicModel: MusicModel) {
        favoritePlaylistModel.musicDataList.removeIf { i -> i.path == musicModel.path }
    }


    fun findCustomPlaylist(name : String): PlaylistModel? {
        return customPlaylistDataList.find { i -> i.name.equals(name, true) }
    }

    fun findCustomPlaylist(playlistModel : PlaylistModel): PlaylistModel? {
        return customPlaylistDataList.find { i -> i == playlistModel }
    }

}