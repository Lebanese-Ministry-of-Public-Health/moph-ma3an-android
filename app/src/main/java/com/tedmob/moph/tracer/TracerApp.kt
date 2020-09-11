package com.tedmob.moph.tracer

import android.app.Application
import android.content.Context
import android.os.Build
import com.onesignal.OneSignal
import com.tedmob.moph.tracer.idmanager.TempIDManager
import com.tedmob.moph.tracer.logging.CentralLog
import com.tedmob.moph.tracer.notifications.NotificationOpenedHandler
import com.tedmob.moph.tracer.services.BluetoothMonitoringService
import com.tedmob.moph.tracer.streetpass.CentralDevice
import com.tedmob.moph.tracer.streetpass.PeripheralDevice

class TracerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContext = applicationContext

        initOneSignal()
    }

    private fun initOneSignal() {
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .setNotificationOpenedHandler(NotificationOpenedHandler(this))
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
        OneSignal.setLocationShared(false)

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val defaultChannelId = BuildConfig.PUSH_NOTIFICATION_CHANNEL_NAME
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                defaultChannelId,
                BuildConfig.PUSH_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }*/
    }


    companion object {
        private val TAG = "TracerApp"
        const val ORG = BuildConfig.ORG

        lateinit var AppContext: Context

        fun thisDeviceMsg(): String {
            BluetoothMonitoringService.broadcastMessage?.let {
                CentralLog.i(TAG, "Retrieved BM for storage: $it")

                if (!it.isValidForCurrentTime()) {

                    val fetch = TempIDManager.retrieveTemporaryID(AppContext)
                    fetch?.let {
                        CentralLog.i(TAG, "Grab New Temp ID")
                        BluetoothMonitoringService.broadcastMessage = it
                    }

                    if (fetch == null) {
                        CentralLog.e(TAG, "Failed to grab new Temp ID")
                    }

                }
            }
            return BluetoothMonitoringService.broadcastMessage?.tempID ?: "Missing TempID"
        }

        fun asPeripheralDevice(): PeripheralDevice = PeripheralDevice(Build.MODEL, "SELF")

        fun asCentralDevice(): CentralDevice = CentralDevice(Build.MODEL, "SELF")
    }
}
