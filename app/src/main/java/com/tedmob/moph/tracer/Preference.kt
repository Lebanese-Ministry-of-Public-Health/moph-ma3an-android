package com.tedmob.moph.tracer

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object Preference {
    private const val PREF_ID = "Tracer_pref"
    private const val IS_ONBOARDED = "IS_ONBOARDED"
    private const val SHOULD_START_MONITORING = "SHOULD_START_MONITORING"
    private const val CHECK_POINT = "CHECK_POINT"
    private const val HANDSHAKE_PIN = "HANDSHAKE_PIN"

    private const val NEXT_FETCH_TIME = "NEXT_FETCH_TIME"
    private const val EXPIRY_TIME = "EXPIRY_TIME"
    private const val LAST_FETCH_TIME = "LAST_FETCH_TIME"

    private const val LAST_PURGE_TIME = "LAST_PURGE_TIME"

    private const val ANNOUNCEMENT = "ANNOUNCEMENT"

    private const val PREFERRED_LANGUAGE = "PREFERRED_LANGUAGE"

    private var preferences: SharedPreferences? = null


    fun putHandShakePin(context: Context, value: String) {
        getPrefs(context).edit { putString(HANDSHAKE_PIN, value) }
    }

    fun getHandShakePin(context: Context): String =
        getPrefs(context).getString(HANDSHAKE_PIN, "AERTVC") ?: "AERTVC"

    fun putIsOnBoarded(context: Context, value: Boolean) {
        getPrefs(context).edit { putBoolean(IS_ONBOARDED, value) }
    }

    fun isOnBoarded(context: Context): Boolean = getPrefs(context).getBoolean(IS_ONBOARDED, false)

    fun putShouldStartMonitoring(context: Context, value: Boolean) {
        getPrefs(context).edit { putBoolean(SHOULD_START_MONITORING, value) }
    }

    fun shouldStartMonitoring(context: Context): Boolean = getPrefs(context).getBoolean(SHOULD_START_MONITORING, false)

    fun putCheckpoint(context: Context, value: Int) {
        getPrefs(context).edit { putInt(CHECK_POINT, value) }
    }

    fun getCheckpoint(context: Context): Int = getPrefs(context).getInt(CHECK_POINT, 0)

    fun putLastFetchTimeInMillis(context: Context, time: Long) {
        getPrefs(context).edit { putLong(LAST_FETCH_TIME, time) }
    }

    fun getLastFetchTimeInMillis(context: Context): Long = getPrefs(context).getLong(LAST_FETCH_TIME, 0)

    fun putNextFetchTimeInMillis(context: Context, time: Long) {
        getPrefs(context).edit { putLong(NEXT_FETCH_TIME, time) }
    }

    fun getNextFetchTimeInMillis(context: Context): Long = getPrefs(context).getLong(NEXT_FETCH_TIME, 0)

    fun putExpiryTimeInMillis(context: Context, time: Long) {
        getPrefs(context).edit { putLong(EXPIRY_TIME, time) }
    }

    fun getExpiryTimeInMillis(context: Context): Long = getPrefs(context).getLong(EXPIRY_TIME, 0)

    fun putAnnouncement(context: Context, announcement: String) {
        getPrefs(context).edit { putString(ANNOUNCEMENT, announcement) }
    }

    fun getAnnouncement(context: Context): String = getPrefs(context).getString(ANNOUNCEMENT, "") ?: ""

    fun putLastPurgeTime(context: Context, lastPurgeTime: Long) {
        getPrefs(context).edit { putLong(LAST_PURGE_TIME, lastPurgeTime) }
    }

    fun getLastPurgeTime(context: Context): Long = getPrefs(context).getLong(LAST_PURGE_TIME, 0)

    fun getPreferredLanguage(context: Context): Int = getPrefs(context).getInt(PREFERRED_LANGUAGE, -1)

    fun setPreferredLanguage(context: Context, position: Int) {
        getPrefs(context).edit { putInt(PREFERRED_LANGUAGE, position) }
    }

    fun registerListener(
        context: Context,
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        getPrefs(context).registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(
        context: Context,
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        getPrefs(context).unregisterOnSharedPreferenceChangeListener(listener)
    }


    private fun getPrefs(context: Context): SharedPreferences {
        if (preferences == null) {
            preferences = context.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
        }
        return preferences!!
    }
}
