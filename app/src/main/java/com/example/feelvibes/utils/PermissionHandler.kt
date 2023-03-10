package com.example.feelvibes.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHandler {
    abstract class Permission(
        private val _activity: Activity,
        private val type: String,
        private val request: Boolean,
        private val permissionCode : Number){

        val activity = _activity
        val context : Context = activity.baseContext
        val hasPermission = ContextCompat.checkSelfPermission(context, type) == PackageManager.PERMISSION_GRANTED

        fun check() {
            if (!hasPermission && request)
                ActivityCompat.requestPermissions(activity, arrayOf(type), permissionCode as Int)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    class ReadMediaAudio(activity: Activity, request: Boolean = false) :
        Permission(activity, Manifest.permission.READ_MEDIA_AUDIO, request, 1)

    class ReadExternalStorage(activity: Activity, request: Boolean = false) :
        Permission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, request, 2)

    class WriteExternalStorage(activity: Activity, request: Boolean = false) :
        Permission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, request, 3)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    class ReadMediaImages(activity: Activity, request: Boolean = false) :
        Permission(activity, Manifest.permission.READ_MEDIA_IMAGES, request, 4)

    class ManageDocuments(activity: Activity, request: Boolean = false) :
        Permission(activity, Manifest.permission.MANAGE_DOCUMENTS, request, 5)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    class Notification(activity: Activity, request: Boolean = false) :
        Permission(activity, Manifest.permission.POST_NOTIFICATIONS, request, 6)
}