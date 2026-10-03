package com.example.data.security

import android.content.Context
import android.content.SharedPreferences

class SecurityPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("secure_vault_prefs", Context.MODE_PRIVATE)

    var isBiometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    var autoLockTimeoutSeconds: Long
        get() = prefs.getLong(KEY_AUTO_LOCK_TIMEOUT, 60L) // Default 1 minute
        set(value) = prefs.edit().putLong(KEY_AUTO_LOCK_TIMEOUT, value).apply()

    var masterPinHash: String
        get() = prefs.getString(KEY_PIN_HASH, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PIN_HASH, value).apply()

    var hasSetMasterPin: Boolean
        get() = masterPinHash.isNotEmpty()
        set(_) {}

    var lastActivityTimestamp: Long
        get() = prefs.getLong(KEY_LAST_ACTIVITY, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_ACTIVITY, value).apply()

    fun shouldAutoLock(): Boolean {
        if (!hasSetMasterPin && !isBiometricEnabled) return false
        val timeoutSec = autoLockTimeoutSeconds
        if (timeoutSec < 0) return false // Never
        if (timeoutSec == 0L) return true // Immediate
        val elapsed = (System.currentTimeMillis() - lastActivityTimestamp) / 1000
        return elapsed >= timeoutSec
    }

    fun recordActivity() {
        lastActivityTimestamp = System.currentTimeMillis()
    }

    companion object {
        private const val KEY_BIOMETRIC_ENABLED = "pref_biometric_enabled"
        private const val KEY_AUTO_LOCK_TIMEOUT = "pref_auto_lock_timeout"
        private const val KEY_PIN_HASH = "pref_pin_hash"
        private const val KEY_LAST_ACTIVITY = "pref_last_activity"
    }
}
