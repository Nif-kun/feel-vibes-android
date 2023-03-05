package com.example.feelvibes.model

import android.app.Activity
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
class DesignModel(
    override val id:String,
    override var name:String,
    val backgroundColor:String = "#FFFFFF",
    var backgroundImagePath:String = "",
    var foregroundImagePath:String = ""
) : ProjectModel(id, name) {

    fun getCapsuleModel(): DesignCapsuleModel {
        return DesignCapsuleModel(
            id,
            name,
            backgroundColor,
            backgroundImagePath,
            foregroundImagePath
        )
    }

    fun getBackgroundPath(activity: Activity): String? {
        return try {
            val file = File(activity.filesDir, backgroundImagePath)
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getForegroundPath(activity: Activity): String? {
        return try {
            val file = File(activity.filesDir, foregroundImagePath)
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}