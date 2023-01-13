package com.example.feelvibes.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.Objects
import kotlin.reflect.KType

class GsonHandler {
    class Save(
        activity: Activity,
        id : String,
        item: Any
    ) {
        init {
            val storedData : SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
            val dataEditor : SharedPreferences.Editor = storedData.edit()
            val gson = Gson()
            val json = gson.toJson(item)
            dataEditor.putString(id, json)
            dataEditor.apply()
        }
    }

    class Load(
        activity: Activity,
        id : String,
        default : String = ""
    ) {
        private var json : String?
        init {
            val storedData : SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)
            json = storedData.getString(id, default)
        }

        fun <T> data(typeOfT : Type): T? {
            if (json != null && json!!.isNotEmpty()) {
                val gson = Gson()
                return gson.fromJson(json, typeOfT)
            }
            return null
        }


        /*
        fun data(classOfT: Class<Any>): Type? {
            if (json != null && json!!.isNotEmpty()) {
                val gson = Gson()
                return gson.fromJson<Type>(json, classOfT)
            }
            return null
        }

         */
    }
}