package com.example.feelvibes.model

import kotlinx.parcelize.Parcelize

@Parcelize
class DesignModel(
    override val id:String,
    override val name:String,
    val backgroundColor:String = "#FFFFFF",
    val backgroundImagePath:String = "",
    val foregroundImagePath:String = ""
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
}