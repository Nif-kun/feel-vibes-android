package com.example.feelvibes.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DesignCapsuleModel(
    var id:String,
    var name:String,
    var backgroundColor:String = "#FFFFFF",
    var backgroundImagePath:String = "",
    var foregroundImagePath:String = ""
) : Parcelable