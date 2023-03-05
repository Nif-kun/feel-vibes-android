package com.example.feelvibes.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import java.io.FileDescriptor
import java.io.IOException
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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

        fun getFileType(activity: Activity, uri: Uri): String? {
            val mimeType = activity.contentResolver?.getType(uri)
            if (mimeType != null) {
                if (mimeType.contains("jpg", true) || mimeType.contains("jpeg", true)) {
                    return "jpg"
                } else if (mimeType.contains("png", true)) {
                    return "png"
                } else if (mimeType.contains("gif", true)) {
                    return "gif"
                }
            }
            return null
        }

        fun pxToDp(px: Float, context: Context): Float {
            return px / context.resources.displayMetrics.density
        }

        fun dpToPx(dp: Float, context: Context): Float {
            return dp * context.resources.displayMetrics.density
        }

        fun getElapsedTime(dateTimeString: String): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm:ss")
            val dateTime = LocalDateTime.parse(dateTimeString, formatter)
            val currentDateTime = LocalDateTime.now()
            val duration = Duration.between(dateTime, currentDateTime)
            var result = ""
            when {
                duration.toDays() >= 365 -> {
                    val years = duration.toDays() / 365
                    var scale = "year"
                    if (years > 1)
                        scale = "years"
                    result = "$years $scale ago"
                }
                duration.toDays() >= 30 -> {
                    val months = duration.toDays() / 30
                    var scale = "month"
                    if (months > 1)
                        scale = "months"
                    result = "$months $scale ago"
                }
                duration.toDays() >= 7 -> {
                    val weeks = duration.toDays() / 7
                    var scale = "week"
                    if (weeks > 1)
                        scale = "weeks"
                    result = "$weeks $scale ago"
                }
                duration.toDays() >= 1 -> {
                    val days = duration.toDays()
                    var scale = "day"
                    if (days > 1)
                        scale = "days"
                    result = "$days $scale ago"
                }
                duration.toHours() >= 1 -> {
                    val hours = duration.toHours()
                    var scale = "hour"
                    if (hours > 1)
                        scale = "hours"
                    result = "$hours $scale ago"
                }
                duration.toMinutes() >= 1 -> {
                    val minutes = duration.toMinutes()
                    var scale = "minute"
                    if (minutes > 1)
                        scale = "minutes"
                    result = "$minutes $scale ago"
                }
                else -> {
                    val seconds = duration.toMinutes()
                    var scale = "second"
                    if (seconds > 1)
                        scale = "seconds"
                    result = "$seconds $scale ago"
                }
            }
            return result
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