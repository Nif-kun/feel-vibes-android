package com.example.feelvibes.model

import kotlinx.parcelize.Parcelize

@Parcelize
class DesignModel(
    override val id:String,
    override var name:String,
    val backgroundColor:String = "#FFFFFF",
    var backgroundImagePath:String = "",
    var foregroundImagePath:String = ""
) : ProjectModel(id, name) {

    override fun getData(): MutableMap<String, MutableMap<String, String>> {
        return mutableMapOf(
            "id" to mutableMapOf(
                "name" to name,
                "backgroundColor" to backgroundColor,
                "backgroundImagePath" to backgroundImagePath,
                "foregroundImagePath" to foregroundImagePath
            )
        )
    }

    fun getCapsuleModel(): DesignCapsuleModel {
        return DesignCapsuleModel(
            id,
            name,
            backgroundColor,
            backgroundImagePath,
            foregroundImagePath
        )
    }
}