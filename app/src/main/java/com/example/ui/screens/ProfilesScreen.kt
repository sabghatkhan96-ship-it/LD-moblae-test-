package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DevicePlatform
import com.example.data.model.DeviceProfile
import com.example.data.model.ProxyProtocol
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
import com.example.ui.theme.UsaBlue
import com.example.ui.viewmodel.VaultUiState

@Composable
fun ProfilesScreen(
    uiState: VaultUiState,
    onSetActiveProfile: (DeviceProfile) -> Unit,
    onSaveProfile: (DeviceProfile) -> Unit,
    onDeleteProfile: (DeviceProfile) -> Unit,
    onTestProxy: (DeviceProfile) -> Unit,
    onDeployRandomUsaDevices: () -> Unit,
    onTestAllProxies: () -> Unit,
    onLaunchBrowserWithUrl: (String) -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var profileToEdit by remember { mutableStateOf<DeviceProfile?>(null) }
    var isGridView by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = CyberDarkBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    profileToEdit = null
                    showAddDialog = true
                },
                containerColor = ElectricCyan,
                contentColor = CyberDarkBg,
                shape = CircleShape,
                modifier = Modifier.testTag("add_profile_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Profile",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: USA Cloud Mobile Farm Title & Actions
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "USA CLOUD MOBILE FARM",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(UsaBlue)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("LD-CLOUD HUB 🇺🇸", fontSize = 9.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "Virtual Mobile Instances with USA Residential Proxies",
                                fontSize = 11.sp,
                                color = NeonSky
                            )
                        }

                        // View mode toggle button
                        IconButton(
                            onClick = { isGridView = !isGridView },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberSurfaceVariant)
                        ) {
                            Icon(
                                imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                                contentDescription = "Toggle View",
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Auto-Generate USA Mobile Instances Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onDeployRandomUsaDevices,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricCyan,
                                contentColor = CyberDarkBg
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("+ Auto-Create USA Phones", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onTestAllProxies,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp), tint = ElectricCyan)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test All Latency", fontSize = 11.sp, color = TextPrimary)
                        }
                    }
                }
            }

            // Quick Stats Banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOTAL CLOUD PHONES", fontSize = 9.sp, color = TextMuted)
                        Text("${uiState.profiles.size}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ACTIVE CONTEXT", fontSize = 9.sp, color = TextMuted)
                        Text(uiState.activeProfile?.cityState?.split(",")?.firstOrNull() ?: "USA", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("AVG PROXY PING", fontSize = 9.sp, color = TextMuted)
                        Text("~42ms", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EmeraldSecure)
                    }
                }
            }

            // Quick Launch Bridge for Webmasters & Marketers
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.OpenInBrowser,
                                    contentDescription = "Launch Bridge",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Quick-Launch Portals (USA Context)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickLaunchChip(
                                title = "TikTok Creator",
                                color = Color(0xFFFE2C55),
                                onClick = { onLaunchBrowserWithUrl("https://www.tiktok.com/creator-center") }
                            )
                            QuickLaunchChip(
                                title = "Meta Ads",
                                color = Color(0xFF0081FB),
                                onClick = { onLaunchBrowserWithUrl("https://adsmanager.facebook.com") }
                            )
                            QuickLaunchChip(
                                title = "AdSense USA",
                                color = Color(0xFFF9AB00),
                                onClick = { onLaunchBrowserWithUrl("https://adsense.google.com") }
                            )
                        }
                    }
                }
            }

            // Section Label
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Deployed Virtual Devices (${uiState.profiles.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Text(
                        text = if (isGridView) "Tap to Launch / Stream" else "List View",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            // Render Devices
            if (isGridView) {
                // Multi-device LD Cloud Grid Layout
                items(uiState.profiles.chunked(2), key = { it.first().id }) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (prof in pair) {
                            val isActive = prof.isActive || prof.id == uiState.activeProfile?.id
                            Box(modifier = Modifier.weight(1f)) {
                                LdCloudDeviceCard(
                                    profile = prof,
                                    isActive = isActive,
                                    onSelect = { onSetActiveProfile(prof) },
                                    onLaunch = { onLaunchBrowserWithUrl("https://api.ipify.org") },
                                    onEdit = {
                                        profileToEdit = prof
                                        showAddDialog = true
                                    },
                                    onDelete = { onDeleteProfile(prof) }
                                )
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                // List View
                items(uiState.profiles, key = { it.id }) { prof ->
                    val isActive = prof.isActive || prof.id == uiState.activeProfile?.id
                    ProfileListRowCard(
                        profile = prof,
                        isActive = isActive,
                        onSelect = { onSetActiveProfile(prof) },
                        onEdit = {
                            profileToEdit = prof
                            showAddDialog = true
                        },
                        onDelete = { onDeleteProfile(prof) },
                        onTest = { onTestProxy(prof) },
                        onLaunch = { onLaunchBrowserWithUrl("https://api.ipify.org") }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditProfileDialog(
            profile = profileToEdit,
            onDismiss = { showAddDialog = false },
            onSave = { saved ->
                onSaveProfile(saved)
                showAddDialog = false
            }
        )
    }
}

/**
 * LD Cloud style Virtual Phone Card:
 * Features phone screen bezel, battery %, resolution, residential proxy ping, and quick launch.
 */
@Composable
private fun LdCloudDeviceCard(
    profile: DeviceProfile,
    isActive: Boolean,
    onSelect: () -> Unit,
    onLaunch: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ld_cloud_card_${profile.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isActive) ElectricCyan else CyberBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Virtual Screen Bezel Representation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberDarkBg)
                    .border(1.dp, if (isActive) ElectricCyan.copy(alpha = 0.5f) else CyberSurfaceVariant, RoundedCornerShape(12.dp))
                    .clickable(onClick = onLaunch)
            ) {
                // Status bar inside virtual phone screen
                Column(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = profile.networkCarrier.split(" ").firstOrNull() ?: "5G",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonSky
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${profile.batteryLevel}%", fontSize = 9.sp, color = TextMuted)
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(imageVector = Icons.Default.BatteryFull, contentDescription = null, tint = EmeraldSecure, modifier = Modifier.size(10.dp))
                        }
                    }

                    // Centered Device OS Icon & Name
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (profile.platform == DevicePlatform.IOS.name) Icons.Default.PhoneIphone else Icons.Default.PhoneAndroid,
                            contentDescription = "Device",
                            tint = if (isActive) ElectricCyan else TextSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = profile.deviceModel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = profile.cityState,
                            fontSize = 10.sp,
                            color = EmeraldSecure
                        )
                    }

                    // Bottom info: Proxy IP & ping
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberSurfaceVariant)
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = profile.proxyHost.ifBlank { "Host IP" },
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextMuted,
                            maxLines = 1
                        )
                        Text(
                            text = "Online",
                            fontSize = 8.sp,
                            color = EmeraldSecure,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // If active, show ACTIVE pill in top right corner
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(EmeraldContainer)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text("ACTIVE", fontSize = 8.sp, color = EmeraldSecure, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Card details
            Text(
                text = profile.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${profile.ramGb}GB RAM • ${profile.androidVersion}",
                fontSize = 10.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Set as Active
                if (!isActive) {
                    Text(
                        text = "Set Active",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan,
                        modifier = Modifier
                            .clickable(onClick = onSelect)
                            .padding(vertical = 4.dp)
                    )
                } else {
                    Text(
                        text = "Running",
                        fontSize = 10.sp,
                        color = EmeraldSecure,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(13.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoseDestructive, modifier = Modifier.size(13.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileListRowCard(
    profile: DeviceProfile,
    isActive: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTest: () -> Unit,
    onLaunch: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_card_${profile.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isActive) ElectricCyan else CyberBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onSelect)
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Select Active",
                        tint = if (isActive) ElectricCyan else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = profile.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${profile.deviceModel} • ${profile.cityState} • ${profile.networkCarrier}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoseDestructive, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Proxy & status info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Proxy: ${profile.proxyHost}:${profile.proxyPort} (${profile.proxyTag})",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary
                )
                Text(
                    text = profile.lastTestStatus,
                    fontSize = 10.sp,
                    color = if (profile.lastTestStatus.contains("Online") || profile.lastTestStatus.contains("Active")) EmeraldSecure else TextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onLaunch) {
                    Icon(imageVector = Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(12.dp), tint = ElectricCyan)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Launch in Browser", fontSize = 11.sp, color = ElectricCyan)
                }
                Spacer(modifier = Modifier.width(6.dp))
                if (!isActive) {
                    TextButton(onClick = onSelect) {
                        Text("Switch to this Phone", fontSize = 11.sp, color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickLaunchChip(
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Launch,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(11.dp)
            )
        }
    }
}

@Composable
private fun AddEditProfileDialog(
    profile: DeviceProfile?,
    onDismiss: () -> Unit,
    onSave: (DeviceProfile) -> Unit
) {
    var name by remember { mutableStateOf(profile?.name ?: "") }
    var platform by remember { mutableStateOf(profile?.platform ?: DevicePlatform.ANDROID.name) }
    var deviceModel by remember { mutableStateOf(profile?.deviceModel ?: "Google Pixel 9 Pro") }
    var userAgent by remember {
        mutableStateOf(
            profile?.userAgent
                ?: "Mozilla/5.0 (Linux; Android 14; Pixel 9 Pro Build/AD1A.240505.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.6478.122 Mobile Safari/537.36"
        )
    }
    var width by remember { mutableStateOf(profile?.viewportWidth?.toString() ?: "412") }
    var height by remember { mutableStateOf(profile?.viewportHeight?.toString() ?: "915") }
    var carrier by remember { mutableStateOf(profile?.networkCarrier ?: "Verizon 5G Ultra Wideband") }
    var cityState by remember { mutableStateOf(profile?.cityState ?: "New York, NY") }
    var timeZone by remember { mutableStateOf(profile?.timeZone ?: "America/New_York") }
    var proxyHost by remember { mutableStateOf(profile?.proxyHost ?: "") }
    var proxyPort by remember { mutableStateOf(profile?.proxyPort?.toString() ?: "8080") }
    var proxyUsername by remember { mutableStateOf(profile?.proxyUsername ?: "") }
    var proxyPassword by remember { mutableStateOf(profile?.encryptedProxyPassword ?: "") }
    var proxyTag by remember { mutableStateOf(profile?.proxyTag ?: "BrightData USA Residential") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (profile == null) "Deploy USA Cloud Phone" else "Edit Virtual Phone",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Phone Label (e.g. NYC TikTok Farm #1)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = deviceModel,
                        onValueChange = { deviceModel = it },
                        label = { Text("Hardware Device Model") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = cityState,
                        onValueChange = { cityState = it },
                        label = { Text("USA Location (City, State)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = carrier,
                        onValueChange = { carrier = it },
                        label = { Text("USA Mobile Carrier") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = userAgent,
                        onValueChange = { userAgent = it },
                        label = { Text("Spoofed User-Agent String") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = proxyHost,
                            onValueChange = { proxyHost = it },
                            label = { Text("Proxy IP") },
                            modifier = Modifier.weight(2f)
                        )
                        OutlinedTextField(
                            value = proxyPort,
                            onValueChange = { proxyPort = it },
                            label = { Text("Port") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = proxyUsername,
                        onValueChange = { proxyUsername = it },
                        label = { Text("Proxy Auth Username (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = proxyPassword,
                        onValueChange = { proxyPassword = it },
                        label = { Text("Proxy Password (AES Encrypted)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val updated = (profile ?: DeviceProfile(
                            name = name.trim(),
                            platform = platform,
                            deviceModel = deviceModel.trim(),
                            userAgent = userAgent.trim()
                        )).copy(
                            name = name.trim(),
                            platform = platform,
                            deviceModel = deviceModel.trim(),
                            userAgent = userAgent.trim(),
                            viewportWidth = width.toIntOrNull() ?: 412,
                            viewportHeight = height.toIntOrNull() ?: 915,
                            networkCarrier = carrier.trim(),
                            cityState = cityState.trim(),
                            timeZone = timeZone.trim(),
                            proxyHost = proxyHost.trim(),
                            proxyPort = proxyPort.toIntOrNull() ?: 8080,
                            proxyUsername = proxyUsername.trim(),
                            encryptedProxyPassword = proxyPassword.trim(),
                            proxyTag = proxyTag.trim()
                        )
                        onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = CyberDarkBg)
            ) {
                Text("Deploy Phone", fontWeight = FontWeight.Bold)
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
