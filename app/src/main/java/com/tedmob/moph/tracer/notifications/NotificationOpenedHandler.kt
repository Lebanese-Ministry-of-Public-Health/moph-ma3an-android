package com.tedmob.moph.tracer.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal

class NotificationOpenedHandler(private val context: Context) : OneSignal.NotificationOpenedHandler {
    override fun notificationOpened(result: OSNotificationOpenResult) {
        val payload = result.notification.payload

        val additionalData = payload.additionalData

        val notificationData = NotificationData(
            title = payload.title,
            message = payload.body,
            image = payload.bigPicture,
            imageHeight = additionalData?.optDouble("height"),
            imageWidth = additionalData?.optDouble("width")
        )

        //TODO redirect to MainActivity instead with Preference.putAnnouncement?
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            PushActivity.newIntent(context, notificationData).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            },
            0
        )
        pendingIntent.send()
    }
}