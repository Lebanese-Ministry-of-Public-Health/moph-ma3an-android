package com.tedmob.moph.tracer.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.tedmob.moph.tracer.MainActivity
import com.tedmob.moph.tracer.R
import com.tedmob.moph.tracer.onboarding.OnboardingActivity
import com.tedmob.moph.tracer.services.BluetoothMonitoringService.Companion.PENDING_ACTIVITY
import com.tedmob.moph.tracer.services.BluetoothMonitoringService.Companion.PENDING_WIZARD_REQ_CODE

object NotificationTemplates {

    fun getStartupNotification(context: Context, channel: String): Notification {
        val builder = NotificationCompat.Builder(context, channel)
            .setContentTitle("Setting things up")
            .setContentText("Tracer is setting up its antennas")
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_LOW)
            .setSmallIcon(R.drawable.ic_notification_setting)
            .setWhen(System.currentTimeMillis())
            .setSound(null)
            .setVibrate(null)
            .setColor(ContextCompat.getColor(context, R.color.notification_tint))

        return builder.build()
    }

    fun getRunningNotification(context: Context, channel: String): Notification {
        val intent = Intent(context, MainActivity::class.java)

        val activityPendingIntent = PendingIntent.getActivity(
            context, PENDING_ACTIVITY,
            intent, 0
        )

        val builder = NotificationCompat.Builder(context, channel)
            .setContentTitle(context.getText(R.string.service_ok_title))
            .setContentText(context.getText(R.string.service_ok_body))
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_LOW)
            .setSmallIcon(R.drawable.ic_notification_service)
            .setContentIntent(activityPendingIntent)
            .setTicker(context.getText(R.string.service_ok_body))
            .setStyle(NotificationCompat.BigTextStyle().bigText(context.getText(R.string.service_ok_body)))
            .setWhen(System.currentTimeMillis())
            .setSound(null)
            .setVibrate(null)
            .setColor(ContextCompat.getColor(context, R.color.notification_tint))

        return builder.build()
    }

    fun lackingThingsNotification(context: Context, channel: String): Notification {
        val intent = Intent(context, OnboardingActivity::class.java)
        intent.putExtra("page", 3)

        val activityPendingIntent = PendingIntent.getActivity(
            context, PENDING_WIZARD_REQ_CODE,
            intent, 0
        )

        val builder = NotificationCompat.Builder(context, channel)
            .setContentTitle(context.getText(R.string.service_not_ok_title))
            .setContentText(context.getText(R.string.service_not_ok_body))
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_LOW)
            .setSmallIcon(R.drawable.ic_notification_warning)
            .setTicker(context.getText(R.string.service_not_ok_body))
            .setStyle(NotificationCompat.BigTextStyle().bigText(context.getText(R.string.service_not_ok_body)))
            .addAction(R.drawable.ic_notification_setting, context.getText(R.string.service_not_ok_action), activityPendingIntent)
            .setContentIntent(activityPendingIntent)
            .setWhen(System.currentTimeMillis())
            .setSound(null)
            .setVibrate(null)
            .setColor(ContextCompat.getColor(context, R.color.notification_tint))

        return builder.build()
    }
}
