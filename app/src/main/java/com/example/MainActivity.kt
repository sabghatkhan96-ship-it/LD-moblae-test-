package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.GeneratorScreen
import com.example.ui.screens.LockScreen
import com.example.ui.screens.ProfileBrowserScreen
import com.example.ui.screens.ProfilesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.VaultViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class AppNavTab(val label: String, val icon: ImageVector) {
    VAULT("Vault", Icons.Default.Lock),
    PROFILES("USA Profiles", Icons.Default.Smartphone),
    GENERATOR("Generator", Icons.Default.Key),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : FragmentActivity() {

    private var viewModelRef: VaultViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: VaultViewModel = viewModel()
            viewModelRef = viewModel
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                viewModel.eventFlow.collectLatest { event ->
                    snackbarHostState.showSnackbar(event)
                }
            }

            MyApplicationTheme {
                if (uiState.isLocked) {
                    LockScreen(
                        hasPinSet = viewModel.securityPrefs.hasSetMasterPin,
                        onVerifyPin = { pin -> viewModel.verifyPin(pin) },
                        onBiometricSuccess = { viewModel.unlockVault() }
                    )
                } else {
                    var selectedTab by remember { mutableStateOf(AppNavTab.VAULT) }
                    var browserTargetUrl by remember { mutableStateOf<String?>(null) }

                    if (browserTargetUrl != null) {
                        ProfileBrowserScreen(
                            activeProfile = uiState.activeProfile,
                            initialUrl = browserTargetUrl ?: "https://www.tiktok.com/creator-center",
                            onNavigateBack = { browserTargetUrl = null }
                        )
                    } else {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = CyberDarkBg,
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            bottomBar = {
                                NavigationBar(
                                    containerColor = CyberSurface,
                                    tonalElevation = 8.dp,
                                    modifier = Modifier.testTag("main_navigation_bar")
                                ) {
                                    AppNavTab.values().forEach { tab ->
                                        val isSelected = selectedTab == tab
                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = {
                                                selectedTab = tab
                                                viewModel.securityPrefs.recordActivity()
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = tab.icon,
                                                    contentDescription = tab.label
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = tab.label,
                                                    fontSize = 11.sp
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = CyberDarkBg,
                                                selectedTextColor = ElectricCyan,
                                                indicatorColor = ElectricCyan,
                                                unselectedIconColor = TextMuted,
                                                unselectedTextColor = TextMuted
                                            ),
                                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                        )
                                    }
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (selectedTab) {
                                    AppNavTab.VAULT -> {
                                        VaultScreen(
                                            uiState = uiState,
                                            onSearchChanged = { viewModel.onSearchQueryChanged(it) },
                                            onCategorySelected = { viewModel.onCategorySelected(it) },
                                            onToggleFavorite = { viewModel.toggleFavorite(it) },
                                            onDeleteCredential = { viewModel.deleteCredential(it) },
                                            onSaveCredential = { id, title, domain, user, pass, cat, notes, fav, profId ->
                                                viewModel.saveCredential(id, title, domain, user, pass, cat, notes, fav, profId)
                                            },
                                            onDecryptPassword = { viewModel.decryptPassword(it) },
                                            onNavigateToProfiles = { selectedTab = AppNavTab.PROFILES }
                                        )
                                    }
                                    AppNavTab.PROFILES -> {
                                        ProfilesScreen(
                                            uiState = uiState,
                                            onSetActiveProfile = { viewModel.setActiveProfile(it) },
                                            onSaveProfile = { viewModel.saveProfile(it) },
                                            onDeleteProfile = { viewModel.deleteProfile(it) },
                                            onTestProxy = { viewModel.testProxyConnection(it) },
                                            onDeployRandomUsaDevices = { viewModel.generateRandomDevices(3) },
                                            onTestAllProxies = { viewModel.batchTestAllProxies() },
                                            onLaunchBrowserWithUrl = { url -> browserTargetUrl = url }
                                        )
                                    }
                                    AppNavTab.GENERATOR -> {
                                        GeneratorScreen()
                                    }
                                    AppNavTab.SETTINGS -> {
                                        SettingsScreen(
                                            securityPrefs = viewModel.securityPrefs,
                                            onLockNow = { viewModel.lockVault() },
                                            onSetNewPin = { newPin -> viewModel.setMasterPin(newPin) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModelRef?.let { vm ->
            if (vm.securityPrefs.shouldAutoLock()) {
                vm.lockVault()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModelRef?.securityPrefs?.recordActivity()
    }
}
