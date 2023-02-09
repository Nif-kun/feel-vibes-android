package com.example.feelvibes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TextCapsuleModel(
    var id:String,
    var name:String,
    var text:String
) : Parcelable {

    fun toRaw(): TextModel {
        return TextModel(
            id,
            name,
            text
        )
    }

}