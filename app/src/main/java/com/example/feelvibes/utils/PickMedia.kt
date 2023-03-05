package com.example.feelvibes.utils

import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class PickMedia {

    private lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>
    var pickedListener: ((Uri) -> Unit)? = null

    object pickTypes {
        const val IMAGE_ONLY = 0
        const val VIDEO_ONLY = 1
        const val IMAGE_AND_VIDEO = 2
    }

    fun setup(fragment: Fragment) {
        pickMedia = fragment.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                pickedListener?.invoke(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    fun setup(componentActivity: ComponentActivity) {
        pickMedia = componentActivity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                pickedListener?.invoke(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    fun launch(pickType: Int = 0) {
        try {
            val type = when (pickType) {
                pickTypes.IMAGE_ONLY -> ActivityResultContracts.PickVisualMedia.ImageOnly
                pickTypes.VIDEO_ONLY -> ActivityResultContracts.PickVisualMedia.VideoOnly
                pickTypes.IMAGE_AND_VIDEO -> ActivityResultContracts.PickVisualMedia.ImageAndVideo
                else -> ActivityResultContracts.PickVisualMedia.ImageOnly
            }
            pickMedia.launch(PickVisualMediaRequest(type))
        } catch (e:Exception) {
            Log.d("PickMedia", "PickMedia was launched before setup.")
            e.printStackTrace()
        }

    }
}