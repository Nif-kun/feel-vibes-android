package com.example.feelvibes.model

import kotlinx.parcelize.Parcelize

@Parcelize
class TextModel(
    override var id:String,
    override var name:String,
    var text:String
) : ProjectModel(id, name) {

    fun toCapsule(): TextCapsuleModel {
        return TextCapsuleModel(
            id,
            name,
            text
        )
    }

}