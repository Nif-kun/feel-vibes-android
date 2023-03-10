package com.example.feelvibes.player.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import com.example.feelvibes.MainActivity
import com.example.feelvibes.R
import com.example.feelvibes.model.MusicModel
import com.example.feelvibes.utils.NotificationHandler

class PlayerNotification(
    context: Context,
    notificationId: Int,
    private val channelId: String,
    channelName: String,
    channelDescription: String
) : NotificationHandler(
    context,
    notificationId,
    channelId,
    channelName,
    channelDescription
) {

    private var musicModel: MusicModel? = null
    private var onGoing: Boolean = false

    fun update(newModel: MusicModel, onGoing: Boolean) {
        this.onGoing = onGoing
        musicModel = newModel
        show(true)
    }

    override fun createNotification(): Notification {
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        var title = "FeelVibe "
        var content = "Content"
        var thumbnail: Bitmap? = null
        musicModel?.let { model ->
            if (onGoing) {
                title += "(Playing)"
                content = "${model.title}\n${model.artist}"
            } else {
                title += "(Paused)"
                content = "${model.title}\n${model.artist}"
            }
            thumbnail = model.thumbnail
        }

        return NotificationCompat.Builder(context, channelId)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.drawable.ic_app_icon)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(activityPendingIntent)
            .setLargeIcon(thumbnail)
            .setAutoCancel(true)
            .setSilent(true)
            .setOngoing(onGoing)
            .build()
    }
}