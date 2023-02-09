package com.example.feelvibes.model

import android.app.Activity
import android.os.Parcelable
import com.example.feelvibes.utils.GsonHandler
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type

@Parcelize
class MusicPropModel(
    val path: String,
    private var designId: String = "",
    var designName: String = "",
    private var lyricsId: String = "",
    var lyricsName: String = "",
    private var chordsId: String = "",
    var chordsName: String = ""
) : Parcelable {

    private fun getPrefId(): String {
        return "MPM-ID[$path]"
    }

    fun designChanged(musicPropModel: MusicPropModel): Boolean {
        return (musicPropModel.designId.isNotEmpty() && musicPropModel.designName.isNotEmpty()) && !matchDesign(musicPropModel)
    }

    fun matchDesign(musicPropModel: MusicPropModel): Boolean {
        return designId.equals(musicPropModel.designId, true) && designName.equals(musicPropModel.designName, true)
    }

    fun matchDesign(designModel: DesignModel): Boolean {
        return designId.equals(designModel.id, true) && designName.equals(designModel.name, true)
    }

    fun matchLyrics(textModel: TextModel): Boolean {
        return matchProject(textModel, lyricsName, lyricsId)
    }

    fun matchChords(textModel: TextModel): Boolean {
        return matchProject(textModel, chordsName, chordsId)
    }

    private fun matchProject(projectModel: ProjectModel, name: String, id:String): Boolean {
        return name.equals(projectModel.name, true) && id.equals(projectModel.id, true)
    }

    fun changeDesign(designModel: DesignModel) {
        designId = designModel.id
        designName = designModel.name
    }

    fun changeLyrics(textModel: TextModel) {
        lyricsId = textModel.id
        lyricsName = textModel.name
    }

    fun changeChords(textModel: TextModel) {
        chordsId = textModel.id
        chordsName = textModel.name
    }

    fun saveToStored(activity: Activity) {
        GsonHandler.Save(activity, getPrefId() , this)
    }

    fun loadFromStored(activity: Activity): Boolean {
        val type: Type = object : TypeToken<MusicPropModel>(){}.type
        val newCapsule = GsonHandler.Load(activity, getPrefId(), "").data<MusicPropModel>(type)
        if (newCapsule != null) {
            designId = newCapsule.designId
            designName = newCapsule.designName
            lyricsId = newCapsule.lyricsId
            lyricsName =  newCapsule.lyricsName
            chordsId = newCapsule.chordsId
            chordsName = newCapsule.chordsName
            return true
        }
        return false
    }

}