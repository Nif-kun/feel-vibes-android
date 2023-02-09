package com.example.feelvibes.utils

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import pl.droidsonroids.gif.GifDrawable
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class InternalStorageHandler {
    companion object {

        fun saveImage(activity: Activity, filename: String, bmp: Bitmap): Boolean {
            val compressType = when (filename.substringAfterLast(".", "").lowercase()) {
                "png" -> Bitmap.CompressFormat.PNG
                "jpg" -> Bitmap.CompressFormat.JPEG
                "jpeg" -> Bitmap.CompressFormat.JPEG
                else -> Bitmap.CompressFormat.PNG
            }
            return try {
                activity.openFileOutput(filename, MODE_PRIVATE).use { stream ->
                    if (!bmp.compress(compressType, 95, stream)) {
                        throw IOException("Couldn't save bitmap.")
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun loadImage(activity: Activity, filename: String): Bitmap? {
            return try {
                val file = File(activity.filesDir, filename)
                BitmapFactory.decodeFile(file.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun deleteImage(activity: Activity, filename: String): Boolean {
            return try {
                activity.deleteFile(filename)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun deleteImage(activity: Activity, filename: String, ofTypes: ArrayList<String>): Boolean {
            var completed = false
            for (type in ofTypes) {
                completed = deleteImage(activity,"$filename.$type")
            }
            return completed
        }

        // Tested: returns a FileNotFoundException
        fun saveFile(context: Context, file: File?, fileName: String) {
            context.openFileOutput(fileName, MODE_PRIVATE).use {
                file?.inputStream()?.copyTo(it)
            }
        }

        // Tested: works properly!
        fun loadGifDrawable(context: Context, fileName: String): GifDrawable? {
            return try {
                val fileInputStream = context.openFileInput(fileName)
                val bufferedInputStream = BufferedInputStream(fileInputStream)
                GifDrawable(bufferedInputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        // Tested: works properly!
        fun copyFileFromUri(context: Context, uri: Uri, fileName: String) {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val fileOutputStream = context.openFileOutput(fileName, MODE_PRIVATE)
            inputStream?.copyTo(fileOutputStream)
            inputStream?.close()
            fileOutputStream.close()
        }

        // Tested: produces null-pointer exception
        fun saveGifDrawableAsFile(context: Context, gifDrawable: GifDrawable, fileName: String) {
            val inputStream = context.resources.openRawResource(gifDrawable.constantState?.newDrawable()?.toBitmap()!!.generationId)
            val byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len = inputStream.read(buffer)
            while (len != -1) {
                byteArrayOutputStream.write(buffer, 0, len)
                len = inputStream.read(buffer)
            }
            val byteArray = byteArrayOutputStream.toByteArray()
            val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(byteArray)
            fileOutputStream.close()
            inputStream.close()
        }

        // Untested
        fun loadGifDrawableFromFile(context: Context, fileName: String): GifDrawable {
            val fileInputStream = context.openFileInput(fileName)
            val byteArrayOutputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var len = fileInputStream.read(buffer)
            while (len != -1) {
                byteArrayOutputStream.write(buffer, 0, len)
                len = fileInputStream.read(buffer)
            }
            val byteArray = byteArrayOutputStream.toByteArray()
            val gifDrawable = GifDrawable(byteArray)
            fileInputStream.close()
            return gifDrawable
        }

    }
}