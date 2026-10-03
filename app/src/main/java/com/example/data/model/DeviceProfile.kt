package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DevicePlatform(val displayName: String) {
    ANDROID("Android (Google / Samsung)"),
    IOS("Apple iOS (iPhone / iPad)")
}

enum class ProxyProtocol(val displayName: String) {
    HTTP("HTTP / HTTPS"),
    SOCKS5("SOCKS5")
}

@Entity(tableName = "device_profiles")
data class DeviceProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val platform: String = DevicePlatform.ANDROID.name,
    val deviceModel: String,
    val userAgent: String,
    val viewportWidth: Int = 412,
    val viewportHeight: Int = 915,
    val devicePixelRatio: Float = 3.5f,
    val timeZone: String = "America/New_York",
    val locale: String = "en-US",
    val networkCarrier: String = "Verizon 5G UW",
    // LD Cloud style attributes
    val cityState: String = "New York, NY",
    val batteryLevel: Int = 92,
    val ramGb: Int = 12,
    val storageGb: Int = 256,
    val androidVersion: String = "Android 14",
    val buildNumber: String = "UP1A.231005.007",
    val status: String = "RUNNING", // RUNNING, IDLE, STANDBY
    // Proxy configuration
    val proxyHost: String = "",
    val proxyPort: Int = 8080,
    val proxyProtocol: String = ProxyProtocol.HTTP.name,
    val proxyUsername: String = "",
    val encryptedProxyPassword: String = "",
    val proxyTag: String = "USA Residential ISP",
    val isActive: Boolean = false,
    val lastTestedAt: Long = 0L,
    val lastTestStatus: String = "Untested",
    val createdAt: Long = System.currentTimeMillis()
)
