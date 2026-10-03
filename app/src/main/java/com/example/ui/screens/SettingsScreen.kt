package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.autofill.AutofillManager
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.security.SecurityPreferences
import com.example.ui.theme.AmberShield
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    securityPrefs: SecurityPreferences,
    onLockNow: () -> Unit,
    onSetNewPin: (String) -> Unit
) {
    val context = LocalContext.current
    var isBiometricEnabled by remember { mutableStateOf(securityPrefs.isBiometricEnabled) }
    var autoLockTimeout by remember { mutableLongStateOf(securityPrefs.autoLockTimeoutSeconds) }
    var showPinDialog by remember { mutableStateOf(false) }
    var showTimeoutMenu by remember { mutableStateOf(false) }

    val autofillManager = context.getSystemService(AutofillManager::class.java)
    val isAutofillActive = autofillManager?.hasEnabledAutofillServices() ?: false

    fun openAutofillSettings() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE).apply {
                    data = android.net.Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            try {
                // Fallback to general settings
                val intent = Intent(Settings.ACTION_SETTINGS)
                context.startActivity(intent)
            } catch (err: Exception) {
                Toast.makeText(context, "Open Android Settings > Passwords & Autofill to enable.", Toast.LENGTH_LONG).show()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "VAULT SECURITY SETTINGS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "System Autofill, Biometrics & Hardware Enclave",
                    fontSize = 12.sp,
                    color = NeonSky
                )
            }
        }

        // Section A: Android Autofill Service Provider
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_autofill_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isAutofillActive) EmeraldSecure else CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isAutofillActive) EmeraldContainer else CyberSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isAutofillActive) Icons.Default.CheckCircle else Icons.Default.Key,
                                    contentDescription = "Autofill",
                                    tint = if (isAutofillActive) EmeraldSecure else ElectricCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Android Autofill Service", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(
                                    text = if (isAutofillActive) "Active Provider" else "Provider Disabled",
                                    fontSize = 12.sp,
                                    color = if (isAutofillActive) EmeraldSecure else AmberShield
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAutofillActive) EmeraldContainer else CyberSurfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isAutofillActive) "ONLINE" else "SETUP NEEDED",
                                fontSize = 10.sp,
                                color = if (isAutofillActive) EmeraldSecure else AmberShield,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Allows SecureVault USA to natively populate login credentials inside apps and browsers when forms appear. Zero-knowledge sandboxed.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { openAutofillSettings() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAutofillActive) CyberSurfaceVariant else ElectricCyan,
                            contentColor = if (isAutofillActive) TextPrimary else CyberDarkBg
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("enable_autofill_button")
                    ) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isAutofillActive) "Open Autofill System Config" else "Set as Primary Autofill Service", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section B: Biometrics & Auto-lock
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Access Control & Auto-Lock", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Biometric Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Fingerprint, contentDescription = "Biometrics", tint = ElectricCyan, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Biometric Authentication", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                                Text("Unlock with Fingerprint or Face ID", fontSize = 11.sp, color = TextMuted)
                            }
                        }

                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = {
                                isBiometricEnabled = it
                                securityPrefs.isBiometricEnabled = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElectricCyan,
                                checkedTrackColor = ElectricCyan.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = CyberSurfaceHighlight
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Auto-Lock Timeout
                    ExposedDropdownMenuBox(
                        expanded = showTimeoutMenu,
                        onExpandedChange = { showTimeoutMenu = !showTimeoutMenu },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val timeoutLabel = when (autoLockTimeout) {
                            0L -> "Immediately on App Switch"
                            60L -> "1 Minute Inactivity"
                            300L -> "5 Minutes Inactivity"
                            900L -> "15 Minutes Inactivity"
                            else -> "Never (Device Session)"
                        }

                        OutlinedTextField(
                            readOnly = true,
                            value = timeoutLabel,
                            onValueChange = {},
                            label = { Text("Auto-Lock Inactivity Timeout") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Timer, contentDescription = "Timeout", tint = ElectricCyan, modifier = Modifier.size(20.dp))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTimeoutMenu) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = showTimeoutMenu,
                            onDismissRequest = { showTimeoutMenu = false }
                        ) {
                            val options = listOf(
                                0L to "Immediately on App Switch",
                                60L to "1 Minute Inactivity",
                                300L to "5 Minutes Inactivity",
                                900L to "15 Minutes Inactivity",
                                -1L to "Never (Device Session)"
                            )
                            options.forEach { (sec, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        autoLockTimeout = sec
                                        securityPrefs.autoLockTimeoutSeconds = sec
                                        showTimeoutMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Change PIN button
                    Button(
                        onClick = { showPinDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant, contentColor = TextPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "PIN", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Change Master 4-Digit PIN", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lock Vault Now Button
                    Button(
                        onClick = onLockNow,
                        colors = ButtonDefaults.buttonColors(containerColor = RoseDestructive.copy(alpha = 0.15f), contentColor = RoseDestructive),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lock_vault_now_button")
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = "Lock Now", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lock Vault Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section C: Hardware Enclave & Cryptography Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = "Security Specs", tint = EmeraldSecure, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cryptographic Hardware Architecture", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TechSpecRow("Cipher Engine", "AES-256-GCM Authenticated")
                    TechSpecRow("Master Key Storage", "Android Keystore (StrongBox / TEE)")
                    TechSpecRow("Auth Tag Integrity", "128-bit Tamper-Evident GCM")
                    TechSpecRow("Database Persistence", "Room with AES Encrypted Records")
                    TechSpecRow("Memory Security", "Transient In-Memory Decryption")
                    TechSpecRow("Profile Isolation", "USA Fingerprint & Proxy Container")
                }
            }
        }
    }

    if (showPinDialog) {
        ChangePinDialog(
            onDismiss = { showPinDialog = false },
            onSavePin = { pin ->
                onSetNewPin(pin)
                showPinDialog = false
                Toast.makeText(context, "New Master PIN configured.", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun TechSpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextMuted)
        Text(text = value, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ChangePinDialog(
    onDismiss: () -> Unit,
    onSavePin: (String) -> Unit
) {
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Master Vault PIN", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 4) newPin = it },
                    label = { Text("New 4-Digit PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { if (it.length <= 4) confirmPin = it },
                    label = { Text("Confirm 4-Digit PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(error ?: "", color = RoseDestructive, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPin.length != 4) {
                        error = "PIN must be exactly 4 digits"
                    } else if (newPin != confirmPin) {
                        error = "PINs do not match"
                    } else {
                        onSavePin(newPin)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = CyberDarkBg)
            ) {
                Text("Update PIN", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
        containerColor = CyberSurface
    )
}
