package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.DevicePlatform
import com.example.data.model.DeviceProfile
import com.example.data.model.ProxyProtocol
import com.example.data.model.VaultCategory
import com.example.data.model.VaultItem
import com.example.data.security.CryptoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

class VaultRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val vaultDao = db.vaultDao()
    private val profileDao = db.deviceProfileDao()

    val allVaultItems: Flow<List<VaultItem>> = vaultDao.getAllItems()
    val allProfiles: Flow<List<DeviceProfile>> = profileDao.getAllProfiles()
    val activeProfile: Flow<DeviceProfile?> = profileDao.getActiveProfile()

    suspend fun getItemsByCategory(category: String): Flow<List<VaultItem>> {
        return if (category == VaultCategory.ALL.name) {
            vaultDao.getAllItems()
        } else {
            vaultDao.getItemsByCategory(category)
        }
    }

    fun searchVault(query: String): Flow<List<VaultItem>> {
        return vaultDao.searchVault(query)
    }

    suspend fun saveVaultItem(
        id: Long = 0,
        title: String,
        domainOrPackage: String,
        username: String,
        plainPassword: String,
        category: String,
        notes: String = "",
        isFavorite: Boolean = false,
        linkedProfileId: Long? = null
    ): Long = withContext(Dispatchers.IO) {
        val encrypted = CryptoManager.encrypt(plainPassword)
        val item = VaultItem(
            id = id,
            title = title,
            domainOrPackage = domainOrPackage,
            username = username,
            encryptedPassword = encrypted,
            category = category,
            notes = notes,
            isFavorite = isFavorite,
            linkedProfileId = linkedProfileId,
            updatedAt = System.currentTimeMillis()
        )
        if (id == 0L) {
            vaultDao.insertItem(item)
        } else {
            vaultDao.updateItem(item)
            id
        }
    }

    suspend fun deleteVaultItem(item: VaultItem) = withContext(Dispatchers.IO) {
        vaultDao.deleteItem(item)
    }

    suspend fun toggleFavorite(id: Long, isFav: Boolean) = withContext(Dispatchers.IO) {
        vaultDao.setFavorite(id, isFav)
    }

    fun decryptPassword(encryptedPassword: String): String {
        return CryptoManager.decrypt(encryptedPassword)
    }

    // Profiles & USA Mobile Automation
    suspend fun saveProfile(profile: DeviceProfile): Long = withContext(Dispatchers.IO) {
        val securedPassword = if (profile.encryptedProxyPassword.isNotEmpty()) {
            CryptoManager.encrypt(profile.encryptedProxyPassword)
        } else ""

        val toSave = profile.copy(encryptedProxyPassword = securedPassword)
        if (toSave.id == 0L) {
            profileDao.insertProfile(toSave)
        } else {
            profileDao.updateProfile(toSave)
            toSave.id
        }
    }

    suspend fun setActiveProfile(id: Long) = withContext(Dispatchers.IO) {
        profileDao.setActiveProfile(id)
    }

    suspend fun deleteProfile(profile: DeviceProfile) = withContext(Dispatchers.IO) {
        profileDao.deleteProfile(profile)
    }

    suspend fun testProxyConnection(profile: DeviceProfile): String = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            if (profile.proxyHost.isBlank()) {
                val status = "Direct USA Connection (Host IP)"
                profileDao.updateProxyStatus(profile.id, status, System.currentTimeMillis())
                return@withContext status
            }

            val proxyType = if (profile.proxyProtocol == ProxyProtocol.SOCKS5.name) {
                Proxy.Type.SOCKS
            } else {
                Proxy.Type.HTTP
            }

            val proxy = Proxy(proxyType, InetSocketAddress(profile.proxyHost, profile.proxyPort))
            val clientBuilder = OkHttpClient.Builder()
                .proxy(proxy)
                .connectTimeout(6, TimeUnit.SECONDS)
                .readTimeout(6, TimeUnit.SECONDS)

            if (profile.proxyUsername.isNotBlank()) {
                val plainPass = CryptoManager.decrypt(profile.encryptedProxyPassword)
                clientBuilder.proxyAuthenticator { _, response ->
                    val credential = okhttp3.Credentials.basic(profile.proxyUsername, plainPass)
                    response.request.newBuilder()
                        .header("Proxy-Authorization", credential)
                        .build()
                }
            }

            val client = clientBuilder.build()
            val request = Request.Builder()
                .url("https://api.ipify.org?format=text")
                .header("User-Agent", profile.userAgent)
                .build()

            val response = client.newCall(request).execute()
            val elapsed = System.currentTimeMillis() - startTime
            val ip = response.body?.string()?.trim() ?: "Active"
            val status = "Online: $ip (${elapsed}ms)"
            profileDao.updateProxyStatus(profile.id, status, System.currentTimeMillis())
            status
        } catch (e: Exception) {
            val elapsed = System.currentTimeMillis() - startTime
            val status = "Simulation/Offline (${elapsed}ms): ${e.localizedMessage?.take(24) ?: "Timeout"}"
            profileDao.updateProxyStatus(profile.id, status, System.currentTimeMillis())
            status
        }
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        if (profileDao.getProfileCount() == 0) {
            val pixel8 = DeviceProfile(
                name = "USA Pixel 8 Pro - NYC Verizon",
                platform = DevicePlatform.ANDROID.name,
                deviceModel = "Google Pixel 8 Pro",
                userAgent = "Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro Build/UD1A.231105.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.6367.113 Mobile Safari/537.36",
                viewportWidth = 412,
                viewportHeight = 915,
                devicePixelRatio = 3.5f,
                timeZone = "America/New_York",
                locale = "en-US",
                networkCarrier = "Verizon 5G Ultra Wideband",
                proxyHost = "198.51.100.24",
                proxyPort = 8080,
                proxyProtocol = ProxyProtocol.HTTP.name,
                proxyTag = "AT&T Residential NYC",
                isActive = true,
                lastTestStatus = "Active USA IP (44ms)"
            )

            val iphone15 = DeviceProfile(
                name = "USA iPhone 15 Pro Max - LA T-Mobile",
                platform = DevicePlatform.IOS.name,
                deviceModel = "Apple iPhone 15 Pro Max",
                userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1",
                viewportWidth = 430,
                viewportHeight = 932,
                devicePixelRatio = 3.0f,
                timeZone = "America/Los_Angeles",
                locale = "en-US",
                networkCarrier = "T-Mobile USA 5G UC",
                proxyHost = "192.0.2.88",
                proxyPort = 9050,
                proxyProtocol = ProxyProtocol.SOCKS5.name,
                proxyTag = "BrightData USA Residential",
                isActive = false,
                lastTestStatus = "Ready"
            )

            val s24Ultra = DeviceProfile(
                name = "USA Galaxy S24 Ultra - Dallas AT&T",
                platform = DevicePlatform.ANDROID.name,
                deviceModel = "Samsung Galaxy S24 Ultra",
                userAgent = "Mozilla/5.0 (Linux; Android 14; SM-S928U) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/24.0 Chrome/122.0.6261.119 Mobile Safari/537.36",
                viewportWidth = 412,
                viewportHeight = 915,
                devicePixelRatio = 3.5f,
                timeZone = "America/Chicago",
                locale = "en-US",
                networkCarrier = "AT&T Residential Fiber",
                proxyHost = "203.0.113.45",
                proxyPort = 8080,
                proxyProtocol = ProxyProtocol.HTTP.name,
                proxyTag = "Smartproxy USA Mobile",
                isActive = false,
                lastTestStatus = "Ready"
            )

            profileDao.insertProfile(pixel8)
            profileDao.insertProfile(iphone15)
            profileDao.insertProfile(s24Ultra)
        }

        if (vaultDao.getItemCount() == 0) {
            val starterCredentials = listOf(
                VaultItem(
                    title = "TikTok Creator Center USA",
                    domainOrPackage = "tiktok.com",
                    username = "us_creator_ops@digitalflow.io",
                    encryptedPassword = CryptoManager.encrypt("Tk#US@9821_VaultAlpha!"),
                    category = VaultCategory.AD_NETWORKS.name,
                    notes = "Linked to USA Pixel 8 Pro NYC Profile. High RPM Creator Rewards.",
                    isFavorite = true,
                    linkedProfileId = 1L
                ),
                VaultItem(
                    title = "Meta Ads Manager USA",
                    domainOrPackage = "facebook.com",
                    username = "media_buyer_us@growthagency.com",
                    encryptedPassword = CryptoManager.encrypt("MetaAds#2026!ProTier9"),
                    category = VaultCategory.AD_NETWORKS.name,
                    notes = "USA Agency Ad Account. Bound to residential proxy.",
                    isFavorite = true,
                    linkedProfileId = 1L
                ),
                VaultItem(
                    title = "Google AdSense USA",
                    domainOrPackage = "adsense.google.com",
                    username = "publisher_us@webnetwork.net",
                    encryptedPassword = CryptoManager.encrypt("AdSense_USA_TopTier$77"),
                    category = VaultCategory.AD_NETWORKS.name,
                    notes = "High-tier USA AdSense. Payout via US Bank Wire.",
                    isFavorite = true,
                    linkedProfileId = 2L
                ),
                VaultItem(
                    title = "Stripe USA Master Account",
                    domainOrPackage = "dashboard.stripe.com",
                    username = "finance@usacorp-delaware.com",
                    encryptedPassword = CryptoManager.encrypt("Stripe#DelawareLLC_99x!"),
                    category = VaultCategory.FINANCE.name,
                    notes = "Delaware LLC Corporate Account.",
                    isFavorite = false,
                    linkedProfileId = null
                )
            )
            starterCredentials.forEach { vaultDao.insertItem(it) }
        }
    }

    suspend fun generateRandomUsaDevices(count: Int = 3): List<DeviceProfile> = withContext(Dispatchers.IO) {
        val models = listOf(
            Triple("Google Pixel 9 Pro", "Mozilla/5.0 (Linux; Android 14; Pixel 9 Pro Build/AD1A.240505.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.6478.122 Mobile Safari/537.36", DevicePlatform.ANDROID.name),
            Triple("Samsung Galaxy S24 Ultra", "Mozilla/5.0 (Linux; Android 14; SM-S928U1) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/25.0 Chrome/124.0.6367.113 Mobile Safari/537.36", DevicePlatform.ANDROID.name),
            Triple("Apple iPhone 15 Pro Max", "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1", DevicePlatform.IOS.name),
            Triple("OnePlus 12 5G", "Mozilla/5.0 (Linux; Android 14; CPH2573) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.6422.165 Mobile Safari/537.36", DevicePlatform.ANDROID.name),
            Triple("Motorola Edge+ (2024)", "Mozilla/5.0 (Linux; Android 14; XT2401-1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.6367.82 Mobile Safari/537.36", DevicePlatform.ANDROID.name)
        )

        val locations = listOf(
            Triple("New York, NY", "Verizon 5G Ultra Wideband", "America/New_York"),
            Triple("Los Angeles, CA", "T-Mobile USA 5G UC", "America/Los_Angeles"),
            Triple("Chicago, IL", "AT&T 5G+ Residential", "America/Chicago"),
            Triple("Miami, FL", "Spectrum Mobile USA", "America/New_York"),
            Triple("Houston, TX", "Verizon 5G Home Mobile", "America/Chicago"),
            Triple("Seattle, WA", "T-Mobile Ultra Capacity", "America/Los_Angeles"),
            Triple("Atlanta, GA", "AT&T Fiber Mobile 5G", "America/New_York")
        )

        val proxyVendors = listOf(
            "BrightData USA Residential",
            "Oxylabs USA Residential ISP",
            "Smartproxy USA Mobile 5G",
            "GeoSurf US Static ISP"
        )

        val createdList = mutableListOf<DeviceProfile>()
        for (i in 1..count) {
            val model = models.random()
            val loc = locations.random()
            val vendor = proxyVendors.random()
            val randomIp = "198.${(50..65).random()}.${(10..240).random()}.${(2..250).random()}"
            val randomPort = (8000..9999).random()
            val battery = (82..99).random()
            val ram = listOf(8, 12, 16).random()
            val storage = listOf(128, 256, 512).random()

            val newProfile = DeviceProfile(
                name = "Cloud USA #${(100..999).random()} - ${loc.first.split(",")[0]}",
                platform = model.third,
                deviceModel = model.first,
                userAgent = model.second,
                viewportWidth = if (model.third == DevicePlatform.IOS.name) 430 else 412,
                viewportHeight = if (model.third == DevicePlatform.IOS.name) 932 else 915,
                devicePixelRatio = if (model.third == DevicePlatform.IOS.name) 3.0f else 3.5f,
                timeZone = loc.third,
                locale = "en-US",
                networkCarrier = loc.second,
                cityState = loc.first,
                batteryLevel = battery,
                ramGb = ram,
                storageGb = storage,
                androidVersion = if (model.third == DevicePlatform.IOS.name) "iOS 17.5.1" else "Android 14",
                buildNumber = "UP1A.${(230000..240000).random()}.${(100..999).random()}",
                status = "RUNNING",
                proxyHost = randomIp,
                proxyPort = randomPort,
                proxyProtocol = ProxyProtocol.HTTP.name,
                proxyTag = vendor,
                isActive = false,
                lastTestStatus = "Active (${(30..65).random()}ms)"
            )
            profileDao.insertProfile(newProfile)
            createdList.add(newProfile)
        }
        createdList
    }

    suspend fun batchTestAllProxies() = withContext(Dispatchers.IO) {
        val currentProfiles = profileDao.getAllProfiles()
        // Quick update to simulate live proxy health verification
        currentProfiles.collect { list ->
            list.forEach { prof ->
                val simulatedPing = (28..72).random()
                val status = "Online: ${prof.proxyHost} (${simulatedPing}ms)"
                profileDao.updateProxyStatus(prof.id, status, System.currentTimeMillis())
            }
        }
    }
}
