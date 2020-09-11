package com.tedmob.moph.tracer.notifications

import com.onesignal.NotificationExtenderService
import com.onesignal.OSNotificationReceivedResult

class NotificationService : NotificationExtenderService() {
    override fun onNotificationProcessing(notification: OSNotificationReceivedResult): Boolean {
        return false
    }
}