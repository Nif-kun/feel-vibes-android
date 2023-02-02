package com.example.feelvibes.model

import android.app.Activity
import android.os.Parcelable
import android.util.Log
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
        val designModelListType : Type = object : TypeToken<ArrayList<DesignModel>>() {}.type
        val designModels = GsonHandler.Load(activity, id, "").data<ArrayList<DesignModel>>(designModelListType)
        Log.d("DesignModels", designModels.toString())
        if (designModels != null) {
            update(designModels)
        }
    }

    fun saveToStored(activity: Activity) {
        if (list.isNotEmpty())
            GsonHandler.Save(activity, id, list)
    }

    fun update(newList : ArrayList<DesignModel>) {
        list.clear()
        list.addAll(newList)
    }

}