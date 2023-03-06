package com.example.feelvibes.model

import android.app.Activity
import android.os.Parcelable
import com.example.feelvibes.utils.GsonHandler
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type

@Parcelize
class TextCollectionModel(
    val id:String,
    val list:ArrayList<ProjectModel> = arrayListOf()
) : Parcelable {

    fun populateFromStored(activity: Activity) {
        val textCapsuleModelType : Type = object : TypeToken<ArrayList<TextCapsuleModel>>() {}.type
        val textCapsuleModels = GsonHandler.Load(activity, id, "").data<ArrayList<TextCapsuleModel>>(textCapsuleModelType)
        if (textCapsuleModels != null) {
            val newList = arrayListOf<TextModel>()
            for (model in textCapsuleModels) {
                newList.add(model.toRaw())
            }
            update(newList)
        }
    }

    fun saveToStored(activity: Activity) {
        val data = arrayListOf<TextCapsuleModel>()
        if (list.isNotEmpty()) {
            for (model in list) {
                if (model is TextModel) {
                    data.add(model.toCapsule())
                }
            }
        }
        GsonHandler.Save(activity, id, data)
    }

    fun update(newList : ArrayList<TextModel>) {
        list.clear()
        list.addAll(newList)
    }

    fun add(model: TextModel): Boolean {
        return list.add(model)
    }

    fun remove(model: TextModel): Boolean {
        return list.remove(model)
    }

    fun find(projectModel: ProjectModel): ProjectModel? {
        for (model in list) {
            if (model.id == projectModel.id)
                return model
        }
        return null
    }

    fun has(projectModel: ProjectModel): Boolean {
        if (find(projectModel) != null)
            return true
        return false
    }

    fun findLyrics(musicPropModel: MusicPropModel?): TextModel? {
        if (musicPropModel  == null)
            return null
        for (model in list) {
            if (musicPropModel.matchLyrics(model as TextModel)) {
                return model
            }
        }
        return null
    }

    fun findChords(musicPropModel: MusicPropModel?): TextModel? {
        if (musicPropModel  == null)
            return null
        for (model in list) {
            if (musicPropModel.matchChords(model as TextModel)) {
                return model
            }
        }
        return null
    }

    fun isEmpty(): Boolean {
        return list.isEmpty()
    }

}