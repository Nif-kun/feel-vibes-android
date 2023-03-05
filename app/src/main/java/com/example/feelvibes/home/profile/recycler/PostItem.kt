package com.example.feelvibes.home.profile.recycler

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.feelvibes.R

class PostItem(
    private val musicItem: LinearLayout,
    private val musicTitle: TextView,
    private val musicDownload: ImageButton
) {

    object DownloadStates {
        const val DEFAULT = 0
        const val DOWNLOADING = 1
        const val DOWNLOADED = 2
    }

    fun show() {
        musicItem.visibility = View.VISIBLE
    }

    fun hide() {
        musicItem.visibility = View.GONE
    }

    fun setName(name: String) {
        musicTitle.text = name
    }

    fun setOnClickListener(callback: (()->Unit)) {
        musicItem.setOnClickListener {
            callback.invoke()
        }
    }

    fun setOnDownloadListener(callback: (()->Unit)) {
        musicDownload.setOnClickListener {
            callback.invoke()
        }
    }

    @SuppressLint("CheckResult")
    fun setDownloadButtonState(state:Int) {
        when (state) {
            DownloadStates.DEFAULT -> Glide.with(musicDownload).load(R.drawable.ic_outline_download_for_offline_24)
            DownloadStates.DOWNLOADING -> Glide.with(musicDownload).load(R.drawable.ic_outline_downloading_24)
            DownloadStates.DOWNLOADED -> Glide.with(musicDownload).load(R.drawable.ic_download_for_offline_24)
        }
    }

}