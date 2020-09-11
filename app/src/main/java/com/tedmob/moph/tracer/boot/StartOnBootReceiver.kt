package com.tedmob.moph.tracer.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tedmob.moph.tracer.Preference
import com.tedmob.moph.tracer.Utils
import com.tedmob.moph.tracer.logging.CentralLog

class StartOnBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            CentralLog.d("StartOnBootReceiver", "boot completed received")

            try {
                //can i try a scheduled service start here?
                CentralLog.d("StartOnBootReceiver", "Attempting to start service")
                if (Preference.shouldStartMonitoring(context)) {
                    Utils.scheduleStartMonitoringService(context, 500)
                }
            } catch (e: Throwable) {
                CentralLog.e("StartOnBootReceiver", e.localizedMessage)
                e.printStackTrace()
            }
        }
    }
}
