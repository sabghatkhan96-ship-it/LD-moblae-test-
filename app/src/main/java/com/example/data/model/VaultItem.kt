package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class VaultCategory(val title: String, val iconName: String) {
    ALL("All", "Apps"),
    LOGINS("Logins", "Lock"),
    USA_PROFILES("USA Profiles", "Smartphone"),
    AD_NETWORKS("Ad Networks", "Campaign"),
    FINANCE("Finance", "CreditCard"),
    NOTES("Secure Notes", "Description")
}

@Entity(tableName = "vault_items")
data class VaultItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val domainOrPackage: String,
    val username: String,
    val encryptedPassword: String, // Encrypted with Android Keystore AES-256-GCM
    val category: String = VaultCategory.LOGINS.name,
    val notes: String = "",
    val isFavorite: Boolean = false,
    val linkedProfileId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
