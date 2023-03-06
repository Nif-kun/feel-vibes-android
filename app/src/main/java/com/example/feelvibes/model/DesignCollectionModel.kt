package com.example.feelvibes.model

import android.app.Activity
import android.os.Parcelable
import com.example.feelvibes.utils.GsonHandler
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type

@Parcelize
class DesignCollectionModel(
    val id:String,
    val list:ArrayList<ProjectModel> = arrayListOf()
) : Parcelable{


    fun populateFromStored(activity: Activity) {
        val designCapsuleModelListType : Type = object : TypeToken<ArrayList<DesignCapsuleModel>>() {}.type
        val designCapsuleModels = GsonHandler.Load(activity, id, "").data<ArrayList<DesignCapsuleModel>>(designCapsuleModelListType)
        if (designCapsuleModels != null) {
            val newList = arrayListOf<DesignModel>()
            for (model in designCapsuleModels) {
                newList.add(DesignModel(
                    model.id,
                    model.name,
                    model.backgroundColor,
                    model.backgroundImagePath,
                    model.foregroundImagePath
                ))
            }
            update(newList)
        }
    }

    fun saveToStored(activity: Activity) {
        val data = arrayListOf<DesignCapsuleModel>()
        if (list.isNotEmpty()) {
            for (model in list) {
                if (model is DesignModel) {
                    data.add(model.getCapsuleModel())
                }
            }
        }
        GsonHandler.Save(activity, id, data)
    }

    fun update(newList : ArrayList<DesignModel>) {
        list.clear()
        list.addAll(newList)
    }

    fun rawUpdate(newList: ArrayList<ProjectModel>) {
        list.clear()
        list.addAll(newList)
    }

    fun add(model: DesignModel): Boolean {
        return list.add(model)
    }

    fun remove(model: DesignModel): Boolean {
        return list.remove(model)
    }

    fun find(musicPropModel: MusicPropModel?): DesignModel? {
        if (musicPropModel == null)
            return null
        for (model in list) {
            if (musicPropModel.matchDesign(model as DesignModel)) {
                return model
            }
        }
        return null
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

    fun isEmpty(): Boolean {
        return list.isEmpty()
    }

}