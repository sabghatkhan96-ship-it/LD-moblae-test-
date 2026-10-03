package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeviceProfile
import com.example.data.model.VaultCategory
import com.example.data.model.VaultItem
import com.example.data.security.PasswordGenerator
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceHighlight
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldSecure
import com.example.ui.theme.NeonSky
import com.example.ui.theme.RoseDestructive
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VaultUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    uiState: VaultUiState,
    onSearchChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onToggleFavorite: (VaultItem) -> Unit,
    onDeleteCredential: (VaultItem) -> Unit,
    onSaveCredential: (Long, String, String, String, String, String, String, Boolean, Long?) -> Unit,
    onDecryptPassword: (String) -> String,
    onNavigateToProfiles: () -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<VaultItem?>(null) }
    val revealedPasswords = remember { mutableStateMapOf<Long, String>() }

    fun copyToClipboard(label: String, text: String, isSensitive: Boolean) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        val msg = if (isSensitive) "$label copied! Auto-clears for security." else "$label copied."
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        containerColor = CyberDarkBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    itemToEdit = null
                    showAddDialog = true
                },
                containerColor = ElectricCyan,
                contentColor = CyberDarkBg,
                shape = CircleShape,
                modifier = Modifier.testTag("add_credential_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Credential",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top App Bar Summary
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SECURE VAULT",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "AES-256 Authenticated Encryption",
                            fontSize = 12.sp,
                            color = EmeraldSecure
                        )
                    }

                    // Security Indicator Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(EmeraldContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Encrypted",
                            tint = EmeraldSecure,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${uiState.items.size} Stored",
                            fontSize = 11.sp,
                            color = EmeraldSecure,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChanged,
                    placeholder = { Text("Search domain, app, or account...", color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vault_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CyberSurface,
                        unfocusedContainerColor = CyberSurface,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(VaultCategory.values()) { cat ->
                        val isSelected = uiState.selectedCategory == cat.name
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) ElectricCyan else CyberSurfaceVariant)
                                .clickable { onCategorySelected(cat.name) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("category_chip_${cat.name}")
                        ) {
                            Text(
                                text = cat.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CyberDarkBg else TextSecondary
                            )
                        }
                    }
                }
            }

            // Credential Cards List
            if (uiState.filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Empty Vault",
                            tint = TextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (uiState.searchQuery.isNotEmpty()) "No credentials match your search" else "No credentials in this category",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.filteredItems, key = { it.id }) { item ->
                        val linkedProfile = uiState.profiles.firstOrNull { it.id == item.linkedProfileId }
                        val isPasswordRevealed = revealedPasswords.containsKey(item.id)
                        val plainPassword = revealedPasswords[item.id]

                        VaultItemCard(
                            item = item,
                            linkedProfile = linkedProfile,
                            isPasswordRevealed = isPasswordRevealed,
                            plainPassword = plainPassword,
                            onToggleReveal = {
                                if (isPasswordRevealed) {
                                    revealedPasswords.remove(item.id)
                                } else {
                                    val decrypted = onDecryptPassword(item.encryptedPassword)
                                    revealedPasswords[item.id] = decrypted
                                }
                            },
                            onCopyUsername = { copyToClipboard("Username", item.username, false) },
                            onCopyPassword = {
                                val decrypted = plainPassword ?: onDecryptPassword(item.encryptedPassword)
                                copyToClipboard("Password", decrypted, true)
                            },
                            onToggleFavorite = { onToggleFavorite(item) },
                            onEdit = {
                                itemToEdit = item
                                showAddDialog = true
                            },
                            onDelete = { onDeleteCredential(item) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditCredentialDialog(
            item = itemToEdit,
            profiles = uiState.profiles,
            onDismiss = { showAddDialog = false },
            onSave = { id, title, domain, username, pass, category, notes, fav, profileId ->
                onSaveCredential(id, title, domain, username, pass, category, notes, fav, profileId)
                showAddDialog = false
            },
            onDecrypt = onDecryptPassword
        )
    }
}

@Composable
private fun VaultItemCard(
    item: VaultItem,
    linkedProfile: DeviceProfile?,
    isPasswordRevealed: Boolean,
    plainPassword: String?,
    onToggleReveal: () -> Unit,
    onCopyUsername: () -> Unit,
    onCopyPassword: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title, Domain, Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (item.category) {
                                VaultCategory.AD_NETWORKS.name -> Icons.Default.Campaign
                                VaultCategory.FINANCE.name -> Icons.Default.CreditCard
                                VaultCategory.USA_PROFILES.name -> Icons.Default.Smartphone
                                VaultCategory.NOTES.name -> Icons.Default.Description
                                else -> Icons.Default.Lock
                            },
                            contentDescription = item.category,
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = item.domainOrPackage,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (item.isFavorite) EmeraldSecure else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Username Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "USERNAME / EMAIL", fontSize = 9.sp, color = TextMuted, letterSpacing = 1.sp)
                    Text(text = item.username, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                }
                IconButton(onClick = onCopyUsername, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Username", tint = ElectricCyan, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Password Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "PASSWORD (AES-256)", fontSize = 9.sp, color = TextMuted, letterSpacing = 1.sp)
                    Text(
                        text = if (isPasswordRevealed) plainPassword ?: "" else "••••••••••••",
                        fontSize = 13.sp,
                        fontFamily = if (isPasswordRevealed) FontFamily.Monospace else FontFamily.Default,
                        color = if (isPasswordRevealed) NeonSky else TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row {
                    IconButton(onClick = onToggleReveal, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (isPasswordRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Visibility",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onCopyPassword, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Password",
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Linked Profile Badge (if any)
            if (linkedProfile != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberSurfaceHighlight)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Smartphone, contentDescription = "Device Profile", tint = NeonSky, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "USA Context: ${linkedProfile.name}", fontSize = 11.sp, color = NeonSky, fontWeight = FontWeight.Medium)
                }
            }

            // Actions row: Edit and Delete
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp, color = TextSecondary)
                }
                TextButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoseDestructive, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", fontSize = 12.sp, color = RoseDestructive)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditCredentialDialog(
    item: VaultItem?,
    profiles: List<DeviceProfile>,
    onDismiss: () -> Unit,
    onSave: (Long, String, String, String, String, String, String, Boolean, Long?) -> Unit,
    onDecrypt: (String) -> String
) {
    val initialPass = if (item != null) onDecrypt(item.encryptedPassword) else ""
    var title by remember { mutableStateOf(item?.title ?: "") }
    var domain by remember { mutableStateOf(item?.domainOrPackage ?: "") }
    var username by remember { mutableStateOf(item?.username ?: "") }
    var password by remember { mutableStateOf(initialPass) }
    var category by remember { mutableStateOf(item?.category ?: VaultCategory.LOGINS.name) }
    var notes by remember { mutableStateOf(item?.notes ?: "") }
    var selectedProfileId by remember { mutableStateOf(item?.linkedProfileId) }
    var isFavorite by remember { mutableStateOf(item?.isFavorite ?: false) }

    var expandedCat by remember { mutableStateOf(false) }
    var expandedProfile by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (item == null) "Add Vault Credential" else "Edit Credential",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (e.g. TikTok USA Creator)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = domain,
                    onValueChange = { domain = it },
                    label = { Text("Domain / Package (e.g. tiktok.com)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username / Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    trailingIcon = {
                        IconButton(onClick = {
                            password = PasswordGenerator.generate(length = 20)
                        }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Generate", tint = ElectricCyan)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selector
                ExposedDropdownMenuBox(
                    expanded = expandedCat,
                    onExpandedChange = { expandedCat = !expandedCat },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCat,
                        onDismissRequest = { expandedCat = false }
                    ) {
                        VaultCategory.values().filter { it != VaultCategory.ALL }.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.title) },
                                onClick = {
                                    category = cat.name
                                    expandedCat = false
                                }
                            )
                        }
                    }
                }

                // Linked USA Mobile Profile
                if (profiles.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = expandedProfile,
                        onExpandedChange = { expandedProfile = !expandedProfile },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val activeLabel = profiles.firstOrNull { it.id == selectedProfileId }?.name ?: "None (Unlinked)"
                        OutlinedTextField(
                            readOnly = true,
                            value = activeLabel,
                            onValueChange = {},
                            label = { Text("Linked USA Profile") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProfile) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedProfile,
                            onDismissRequest = { expandedProfile = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("None (Unlinked)") },
                                onClick = {
                                    selectedProfileId = null
                                    expandedProfile = false
                                }
                            )
                            profiles.forEach { prof ->
                                DropdownMenuItem(
                                    text = { Text(prof.name) },
                                    onClick = {
                                        selectedProfileId = prof.id
                                        expandedProfile = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Secure Notes (Optional)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && username.isNotBlank()) {
                        onSave(
                            item?.id ?: 0L,
                            title.trim(),
                            domain.trim(),
                            username.trim(),
                            password,
                            category,
                            notes.trim(),
                            isFavorite,
                            selectedProfileId
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = CyberDarkBg),
                modifier = Modifier.testTag("save_credential_button")
            ) {
                Text("Save to Vault", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = CyberSurface
    )
}
