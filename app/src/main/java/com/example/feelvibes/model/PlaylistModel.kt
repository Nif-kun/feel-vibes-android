package com.example.feelvibes.model

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import com.example.feelvibes.utils.GsonHandler
import com.example.feelvibes.utils.MusicDataHandler
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class PlaylistModel(
    var name: String,
    val type: String = Type.NONE,
    var thumbnail: Bitmap? = null,
    val list: ArrayList<MusicModel> = ArrayList(),
    private val preset: ArrayList<String>? = null, // list<MusicModel>().paths, they are paths.
    var thumbnailUri : Uri? = null
) : Parcelable {

    @IgnoredOnParcel
    private val playlistSuffixId = "_PLAYLIST_DATA"


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
        if (thumbnail == null) {
            for (musicData in list) {
                musicData.loadThumbnail(activity)
                if (musicData.thumbnail != null) {
                    thumbnail = musicData.thumbnail
                    break
                }
            }
        }
    }

    fun update(newList: ArrayList<MusicModel>) {
        list.clear()
        list.addAll(newList)
    }

    fun add(musicModel : MusicModel, checkRepeat : Boolean = true) {
        if (checkRepeat) {
            if (!has(musicModel.path))
                list.add(musicModel)
        } else
            list.add(musicModel)
    }

    fun addAll(newList : ArrayList<MusicModel>, checkRepeat : Boolean = true) {
        if (checkRepeat) {
            val filteredList = ArrayList<MusicModel>()
            for (x in newList) {
                var flag = false
                for (y in list) {
                    if (x.path == y.path) {
                        flag = true
                        break
                    }
                }
                if (flag)
                    continue
                filteredList.add(x)
            }
            list.addAll(filteredList)
        } else
            list.addAll(newList)
    }

    fun remove(musicModel: MusicModel) {
        list.remove(musicModel)
    }

    fun remove(path: String) {
        list.removeIf { i -> i.path == path }
    }

    fun find(path: String): MusicModel? {
        return when(path.isNotEmpty()) {
            true -> list.find { i -> i.path == path }
            else -> null
        }
    }

    fun has(path: String): Boolean {
        return when(path.isNotEmpty()) {
            true -> (find(path) != null)
            else -> false
        }
    }

    fun updateMusicDataList(list : ArrayList<MusicModel>){
        this.list.clear()
        this.list.addAll(list)
    }

    fun save(activity: Activity) {
        GsonHandler.Save(activity, name+playlistSuffixId, getCapsule())
        // TODO save thumbnailUri
    }

    fun load(activity: Activity) {
        val playlistPreset = GsonHandler
            .Load(activity, name+playlistSuffixId)
            .data<PlaylistCapsuleModel>(PlaylistCapsuleModel::class.java)

        if (playlistPreset != null) {
            val newList = MusicDataHandler.Collect(activity, Type.PLAYLIST,true, arrayListOf(playlistPreset)).sortedData
            if (newList.size > 0)
                update(newList[0].list)
        }
    }

    fun getCapsule(): PlaylistCapsuleModel {
        return PlaylistCapsuleModel(
            name,
            getMusicPathList(),
            null
        )
    }

    fun getDataMap(): MutableMap<String, ArrayList<String>> {
        return mutableMapOf(
            name to getMusicPathList()
        )
    }

    fun getMusicPathList(): ArrayList<String> {
        val paths = ArrayList<String>()
        list.forEach { paths.add(it.path) }
        return paths
    }

    fun containsPreset(path : String): Boolean {
        return preset?.find { i -> i == path } != null
    }

    fun match(playlistModel: PlaylistModel?): Boolean {
        if (playlistModel != null) {
            val sameName = name.equals(playlistModel.name, true)
            val sameType = type == playlistModel.type
            return (sameName && sameType)
        }
        return false
    }

    fun getOnIndex(index: Int): MusicModel? {
        val size = list.size - 1
        return if (index > - 1 && index <= size)
            list[index]
        else
            null
    }

    fun indexOf(musicModel: MusicModel): Int {
        var index = 0
        for (model in list) {
            if (model.path.equals(musicModel.path, true)) {
                return index
            }
            index++
        }
        return index
    }

    // create a save function which constructs a preset
    //create a read function that sets the list based on the given json or config data.

}