package com.example.feelvibes.model

import android.app.Activity
import android.os.Parcelable
import com.example.feelvibes.utils.GsonHandler
import com.example.feelvibes.utils.MusicDataHandler
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type

@Parcelize
class PlaylistCollectionModel(
    val id : String? = null,
    val type : String = PlaylistModel.Type.NONE,
    val list : ArrayList<PlaylistModel> = ArrayList()
) : Parcelable {

    fun populateFromLocal(activity: Activity, sortedOnly : Boolean = true) {
        update(MusicDataHandler.Collect(activity, type, sortedOnly).sortedData)
    }

    fun populateFromStored(activity: Activity, sortedOnly: Boolean = true) {
        if (id != null && id.isNotEmpty()) {
            val playlistCapsuleListType : Type = object : TypeToken<ArrayList<PlaylistCapsuleModel>>() {}.type
            val playlistCapsules = GsonHandler.Load(activity, id, "").data<ArrayList<PlaylistCapsuleModel>>(playlistCapsuleListType)
            update(MusicDataHandler.Collect(activity, type, sortedOnly, playlistCapsules).sortedData)
        }
    }

    fun saveToStored(activity: Activity, exclude : ArrayList<String>? = null) {
        if (id != null && list.size > 0) {
            val playlistCapsules = ArrayList<PlaylistCapsuleModel>()
            list.forEach {
                if (exclude != null) {
                    if (!exclude.contains(it.name)) {
                        playlistCapsules.add(PlaylistCapsuleModel(it.name, it.getMusicPathList(), it.thumbnailUri.toString()))
                    }
                } else {
                    playlistCapsules.add(PlaylistCapsuleModel(it.name, it.getMusicPathList(), it.thumbnailUri.toString()))
                }
            }
            GsonHandler.Save(activity, id, playlistCapsules)
        }
    }

    fun update(newList : ArrayList<PlaylistModel>) {
        list.clear()
        list.addAll(newList)
    }

    fun add(playlistModel: PlaylistModel, checkRepeat : Boolean = true): Boolean {
        if (checkRepeat) {
            if (!has(playlistModel.name)) {
                list.add(playlistModel)
                return true
            }
        } else {
            list.add(playlistModel)
            return true
        }
        return false
    }

    fun addAll(collection : ArrayList<PlaylistModel>, checkRepeat: Boolean = true) {
        if (checkRepeat) {
            val filteredCollection = ArrayList<PlaylistModel>()
            for (x in collection) {
                var flag = false
                for (y in collection) {
                    if (x.name.equals(y.name, true)) {
                        flag = true
                        break
                    }
                }
                if (flag)
                    continue
                filteredCollection.add(x)
            }
            list.addAll(filteredCollection)
        } else
            list.addAll(collection)
    }

    fun push(playlistModel: PlaylistModel, checkRepeat : Boolean = true, index : Int = 0): Boolean {
        if (checkRepeat) {
            if (!has(playlistModel.name)) {
                list.add(index, playlistModel)
                return true
            }
        } else {
            list.add(index, playlistModel)
            return true
        }
        return false
    }

    fun find(name : String): PlaylistModel? {
        return when(name.isNotEmpty()) {
            true -> list.find { i -> i.name == name}
            else -> null
        }
    }

    fun has(name : String): Boolean {
        return when(name.isNotEmpty()) {
            true -> (find(name) != null)
            else -> false
        }
    }

    fun addIn(name : String, musicModel: MusicModel) {
        find(name)?.add(musicModel)
    }

    fun removeIn(name : String, musicModel: MusicModel) {
        find(name)?.remove(musicModel)
    }

    fun removeIn(name : String, path : String) {
        find(name)?.remove(path)
    }



    // do save and load if has id

    // save collects all the PlaylistModel by their name, along with their musicDataList that is a list of Paths.
}