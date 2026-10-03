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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.security.PasswordGenerator
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
import kotlin.math.roundToInt

@Composable
fun GeneratorScreen() {
    val context = LocalContext.current
    var length by remember { mutableFloatStateOf(20f) }
    var includeUpper by remember { mutableStateOf(true) }
    var includeLower by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    var includeSymbols by remember { mutableStateOf(true) }
    var avoidAmbiguous by remember { mutableStateOf(true) }

    var currentPassword by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<String>() }

    fun refreshPassword() {
        val gen = PasswordGenerator.generate(
            length = length.roundToInt(),
            includeUpper = includeUpper,
            includeLower = includeLower,
            includeNumbers = includeNumbers,
            includeSymbols = includeSymbols,
            avoidAmbiguous = avoidAmbiguous
        )
        currentPassword = gen
        if (gen.isNotEmpty() && !history.contains(gen)) {
            history.add(0, gen)
            if (history.size > 10) history.removeLast()
        }
    }

    LaunchedEffect(length, includeUpper, includeLower, includeNumbers, includeSymbols, avoidAmbiguous) {
        refreshPassword()
    }

    fun copyToClipboard(pwd: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Generated Password", pwd)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Password copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    val strength = PasswordGenerator.calculateStrength(currentPassword)
    val strengthColor = when (strength.score) {
        0 -> RoseDestructive
        1 -> RoseDestructive
        2 -> AmberShield
        3 -> NeonSky
        else -> EmeraldSecure
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
                    text = "PASSWORD GENERATOR",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "High-Entropy Quantum Resistant Keys",
                    fontSize = 12.sp,
                    color = NeonSky
                )
            }
        }

        // Generated Display Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generator_display_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricCyan)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentPassword,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberSurfaceVariant)
                            .padding(horizontal = 14.dp, vertical = 18.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Strength Meter Bar
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "STRENGTH: ${strength.label.uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = strengthColor
                            )
                            Text(
                                text = "${strength.entropyBits} bits entropy",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { ((strength.score + 1) / 5f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = strengthColor,
                            trackColor = CyberSurfaceHighlight
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons: Copy and Regenerate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { copyToClipboard(currentPassword) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = CyberDarkBg),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_generated_button")
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Password", fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = { refreshPassword() },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberSurfaceVariant)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Regenerate", tint = ElectricCyan)
                        }
                    }
                }
            }
        }

        // Configuration Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Character Rules & Entropy",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Length Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Length: ${length.roundToInt()} characters", fontSize = 13.sp, color = TextSecondary)
                        Text(
                            text = if (length >= 20) "Highly Secure" else "Standard",
                            fontSize = 12.sp,
                            color = if (length >= 20) EmeraldSecure else AmberShield
                        )
                    }

                    Slider(
                        value = length,
                        onValueChange = { length = it },
                        valueRange = 8f..48f,
                        steps = 39,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricCyan,
                            activeTrackColor = ElectricCyan,
                            inactiveTrackColor = CyberSurfaceHighlight
                        ),
                        modifier = Modifier.testTag("length_slider")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Character Options
                    CharOptionRow("Uppercase Letters (A-Z)", includeUpper) { includeUpper = it }
                    CharOptionRow("Lowercase Letters (a-z)", includeLower) { includeLower = it }
                    CharOptionRow("Numbers (0-9)", includeNumbers) { includeNumbers = it }
                    CharOptionRow("Symbols (!@#$%^&*)", includeSymbols) { includeSymbols = it }
                    CharOptionRow("Avoid Ambiguous (I, l, 1, O, 0)", avoidAmbiguous) { avoidAmbiguous = it }
                }
            }
        }

        // Session History
        if (history.size > 1) {
            item {
                Text(
                    text = "Recent Keys in Session",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }

            items(history.drop(1)) { pastPwd ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberSurface)
                        .clickable { copyToClipboard(pastPwd) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pastPwd,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary,
                        maxLines = 1
                    )
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun CharOptionRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = TextPrimary)
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = ElectricCyan,
                checkmarkColor = CyberDarkBg,
                uncheckedColor = TextMuted
            )
        )
    }
}
