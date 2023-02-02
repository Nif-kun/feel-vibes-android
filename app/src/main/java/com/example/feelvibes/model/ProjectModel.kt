package com.example.feelvibes.model

import android.os.Parcelable


abstract class ProjectModel(
    open val id:String,
    open val name:String
) : Parcelable {

    abstract fun getData(): MutableMap<String, MutableMap<String, String>>
}