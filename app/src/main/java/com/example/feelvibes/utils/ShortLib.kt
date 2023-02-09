package com.example.feelvibes.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import java.io.FileDescriptor
import java.io.IOException
import java.util.*

class ShortLib {

    companion object {

        @Throws(IOException::class)
        fun getBitmapFromUri(uri: Uri, activity: Activity): Bitmap? {
            if (activity.applicationContext.contentResolver != null) {
                val parcelFileDescriptor: ParcelFileDescriptor =
                    activity.applicationContext.contentResolver.openFileDescriptor(uri, "r")!!
                val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
                val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                parcelFileDescriptor.close()
                return image
            }
            return null
        }

        fun msecToMMSS(progress: Int): String {
            val minutes = progress / 1000 / 60
            val seconds = progress / 1000 % 60
            return "${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"
        }

        fun viewImageSetter(activity: Activity, view: ImageView, path: String) {
            if (path.contains("gif", true)) {
                view.setImageDrawable(
                    InternalStorageHandler.loadGifDrawable(
                        context = activity,
                        fileName = path))
            } else {
                view.setImageBitmap(
                    InternalStorageHandler.loadImage(
                        activity = activity,
                        filename = path))
            }
        }

    }

    class RandomIndexGenerator(private val size: Int) {
        private val indices = MutableList(size) { it }
        private val random = Random()

        fun nextIndex(): Int {
            if (indices.isEmpty()) {
                indices.addAll(MutableList(size) { it })
            }
            val index = random.nextInt(indices.size)
            return indices.removeAt(index)
        }
    }

}