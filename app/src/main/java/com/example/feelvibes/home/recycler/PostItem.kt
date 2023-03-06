package com.example.feelvibes.home.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.feelvibes.R
import com.example.feelvibes.utils.ShortLib

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
    fun setDownloadButtonState(context: Context, state:Int) {
        when (state) {
            DownloadStates.DEFAULT -> {
                Glide
                    .with(context)
                    .load(R.drawable.ic_outline_download_for_offline_24)
                    .override(ShortLib.dpToPx(24f, context).toInt(), ShortLib.dpToPx(24f, context).toInt())
                    .into(musicDownload)
            }
            DownloadStates.DOWNLOADING -> {
                Glide
                    .with(context)
                    .load(R.drawable.ic_outline_downloading_24)
                    .override(ShortLib.dpToPx(24f, context).toInt(), ShortLib.dpToPx(24f, context).toInt())
                    .into(musicDownload)
            }
            DownloadStates.DOWNLOADED -> {
                Glide
                    .with(context)
                    .load(R.drawable.ic_download_for_offline_24)
                    .override(ShortLib.dpToPx(24f, context).toInt(), ShortLib.dpToPx(24f, context).toInt())
                    .into(musicDownload)
            }
        }
    }

}