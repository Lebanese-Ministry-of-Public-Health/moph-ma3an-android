package com.tedmob.moph.tracer.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tedmob.moph.tracer.Preference
import com.tedmob.moph.tracer.Utils
import com.tedmob.moph.tracer.logging.CentralLog

class UpgradeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if (Intent.ACTION_MY_PACKAGE_REPLACED != intent!!.action) return
            // Start your service here.
            context?.let {
                CentralLog.i("UpgradeReceiver", "Starting service from upgrade receiver")
                if (Preference.shouldStartMonitoring(it)) {
                    Utils.startBluetoothMonitoringService(context)
                } else {
                    Utils.stopBluetoothMonitoringService(context)
                }
            }
        } catch (e: Exception) {
            CentralLog.e("UpgradeReceiver", "Unable to handle upgrade: ${e.localizedMessage}")
        }
    }
}
