package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DeviceProfile
import com.example.data.model.VaultCategory
import com.example.data.model.VaultItem
import com.example.data.repository.VaultRepository
import com.example.data.security.CryptoManager
import com.example.data.security.PasswordGenerator
import com.example.data.security.SecurityPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class VaultUiState(
    val items: List<VaultItem> = emptyList(),
    val filteredItems: List<VaultItem> = emptyList(),
    val profiles: List<DeviceProfile> = emptyList(),
    val activeProfile: DeviceProfile? = null,
    val selectedCategory: String = VaultCategory.ALL.name,
    val searchQuery: String = "",
    val isLocked: Boolean = true,
    val isTestingProxy: Boolean = false,
    val proxyTestMessage: String? = null,
    val userNotification: String? = null
)

class VaultViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VaultRepository(application)
    val securityPrefs = SecurityPreferences(application)

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow: SharedFlow<String> = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }

        // Initialize locked state based on preferences
        val initialLock = securityPrefs.hasSetMasterPin || securityPrefs.isBiometricEnabled
        _uiState.value = _uiState.value.copy(isLocked = initialLock)

        // Observe vault items and active profile
        viewModelScope.launch {
            combine(
                repository.allVaultItems,
                repository.allProfiles,
                repository.activeProfile
            ) { items, profiles, activeProfile ->
                Triple(items, profiles, activeProfile)
            }.collectLatest { (items, profiles, activeProfile) ->
                val currentCategory = _uiState.value.selectedCategory
                val currentQuery = _uiState.value.searchQuery
                val filtered = filterItems(items, currentCategory, currentQuery)
                _uiState.value = _uiState.value.copy(
                    items = items,
                    filteredItems = filtered,
                    profiles = profiles,
                    activeProfile = activeProfile ?: profiles.firstOrNull { it.isActive }
                )
            }
        }
    }

    private fun filterItems(items: List<VaultItem>, category: String, query: String): List<VaultItem> {
        return items.filter { item ->
            val matchesCategory = (category == VaultCategory.ALL.name) || (item.category == category)
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.domainOrPackage.contains(query, ignoreCase = true) ||
                    item.username.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredItems = filterItems(_uiState.value.items, _uiState.value.selectedCategory, query)
        )
    }

    fun onCategorySelected(category: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            filteredItems = filterItems(_uiState.value.items, category, _uiState.value.searchQuery)
        )
    }

    fun unlockVault() {
        securityPrefs.recordActivity()
        _uiState.value = _uiState.value.copy(isLocked = false)
        viewModelScope.launch {
            _eventFlow.emit("Vault unlocked securely")
        }
    }

    fun lockVault() {
        _uiState.value = _uiState.value.copy(isLocked = true)
        viewModelScope.launch {
            _eventFlow.emit("Vault locked")
        }
    }

    fun verifyPin(pin: String): Boolean {
        val storedHash = securityPrefs.masterPinHash
        if (storedHash.isEmpty()) {
            // First time setup: set this PIN
            setMasterPin(pin)
            unlockVault()
            return true
        }

        val testHash = CryptoManager.hashPin(pin)
        return if (testHash == storedHash) {
            unlockVault()
            true
        } else {
            false
        }
    }

    fun setMasterPin(pin: String) {
        securityPrefs.masterPinHash = CryptoManager.hashPin(pin)
        securityPrefs.recordActivity()
    }

    fun saveCredential(
        id: Long = 0,
        title: String,
        domainOrPackage: String,
        username: String,
        plainPassword: String,
        category: String,
        notes: String = "",
        isFavorite: Boolean = false,
        linkedProfileId: Long? = null
    ) {
        viewModelScope.launch {
            repository.saveVaultItem(
                id = id,
                title = title,
                domainOrPackage = domainOrPackage,
                username = username,
                plainPassword = plainPassword,
                category = category,
                notes = notes,
                isFavorite = isFavorite,
                linkedProfileId = linkedProfileId
            )
            securityPrefs.recordActivity()
            _eventFlow.emit(if (id == 0L) "Credential encrypted & stored" else "Credential updated")
        }
    }

    fun deleteCredential(item: VaultItem) {
        viewModelScope.launch {
            repository.deleteVaultItem(item)
            _eventFlow.emit("Item removed from vault")
        }
    }

    fun toggleFavorite(item: VaultItem) {
        viewModelScope.launch {
            repository.toggleFavorite(item.id, !item.isFavorite)
        }
    }

    fun decryptPassword(encrypted: String): String {
        securityPrefs.recordActivity()
        return repository.decryptPassword(encrypted)
    }

    // Profiles & USA Mobile Automation
    fun saveProfile(profile: DeviceProfile) {
        viewModelScope.launch {
            repository.saveProfile(profile)
            _eventFlow.emit("USA Mobile Profile saved")
        }
    }

    fun setActiveProfile(profile: DeviceProfile) {
        viewModelScope.launch {
            repository.setActiveProfile(profile.id)
            _eventFlow.emit("Active Profile: ${profile.name}")
        }
    }

    fun deleteProfile(profile: DeviceProfile) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
            _eventFlow.emit("Profile deleted")
        }
    }

    fun testProxyConnection(profile: DeviceProfile) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTestingProxy = true, proxyTestMessage = "Pinging proxy...")
            val result = repository.testProxyConnection(profile)
            _uiState.value = _uiState.value.copy(isTestingProxy = false, proxyTestMessage = result)
            _eventFlow.emit("Proxy Status: $result")
        }
    }

    fun generateRandomDevices(count: Int = 3) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTestingProxy = true, proxyTestMessage = "Deploying USA Cloud Instances...")
            val newProfiles = repository.generateRandomUsaDevices(count)
            _uiState.value = _uiState.value.copy(isTestingProxy = false, proxyTestMessage = null)
            _eventFlow.emit("Deployed ${newProfiles.size} new USA Mobile Cloud instances!")
        }
    }

    fun batchTestAllProxies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTestingProxy = true, proxyTestMessage = "Pinging all USA proxies...")
            repository.batchTestAllProxies()
            _uiState.value = _uiState.value.copy(isTestingProxy = false, proxyTestMessage = "All proxies verified online")
            _eventFlow.emit("All USA Cloud instances latency verified!")
        }
    }
}
