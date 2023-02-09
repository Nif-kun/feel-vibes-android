package com.example.feelvibes.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService

abstract class NotificationHandler(
    val context: Context,
    private val notificationId: Int,
    private val channelId: String,
    private val channelName: String,
    private val channelDescription: String
) {

    fun build() {
        createChannel()
    }

    abstract fun createNotification(): Notification


    private fun createChannel() {
        val notificationManager: NotificationManager = getSystemService(context, NotificationManager::class.java) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        // Register the channel with the system
        notificationManager.createNotificationChannel(channel)
    }

    fun show(update: Boolean = false) {
        if (!update) {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, createNotification())
            }
        } else if (visible()) {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, createNotification())
            }
        }
    }

    fun dismiss() {
        val notificationManager: NotificationManager = getSystemService(context, NotificationManager::class.java) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    fun visible(): Boolean {
        val notificationManager: NotificationManager = getSystemService(context, NotificationManager::class.java) as NotificationManager
        for (notification in notificationManager.activeNotifications) {
            if (notification.id == notificationId)
                return true
        }
        return false
    }

}