package org.metalscope.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.metalscope.app.data.model.Currency
import org.metalscope.app.data.model.WeightUnit
import org.metalscope.app.theme.ChangeNegative
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.theme.ThemeMode
import org.metalscope.app.ui.viewmodel.MainUiState
import org.metalscope.app.ui.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    onNavigateToDataSources: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onOpenCurrencyDialog: () -> Unit,
    onOpenUnitDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MetalTheme.colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Title Bar
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MetalTheme.colors.textPrimary
            )
            Text(
                text = "Preferences, units, currencies, and market feed configuration",
                style = MaterialTheme.typography.labelSmall,
                color = MetalTheme.colors.textSecondary
            )
        }

        // Section 1: Display & Currency Preferences
        SettingsSectionHeader(title = "MARKET DISPLAY & PREFERENCES")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MetalTheme.colors.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(MetalTheme.colors.border),
                width = 1.dp
            )
        ) {
            Column {
                SettingsActionRow(
                    icon = Icons.Default.MonetizationOn,
                    title = "Primary Currency",
                    subtitle = "${uiState.currency.displayName} (${uiState.currency.code} ${uiState.currency.symbol})",
                    onClick = onOpenCurrencyDialog
                )

                SettingsDivider()

                SettingsActionRow(
                    icon = Icons.Default.Scale,
                    title = "Weight Unit",
                    subtitle = "${uiState.unit.displayName} (${uiState.unit.symbol})",
                    onClick = onOpenUnitDialog
                )

                SettingsDivider()

                // Theme Mode Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = MetalGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "App Theme",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MetalTheme.colors.textPrimary
                            )
                            Text(
                                text = uiState.themeMode.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textSecondary
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ThemeMode.entries.forEach { mode ->
                            val isSelected = uiState.themeMode == mode
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) MetalGold else MetalTheme.colors.elevated)
                                    .clickable { viewModel.setThemeMode(mode) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = mode.name.take(1) + mode.name.drop(1).lowercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) androidx.compose.ui.graphics.Color(0xFF0B1020) else MetalTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: Data Refresh & Haptics
        SettingsSectionHeader(title = "REFRESH & PERFORMANCE")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MetalTheme.colors.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(MetalTheme.colors.border),
                width = 1.dp
            )
        ) {
            Column {
                // Auto Refresh Interval Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            tint = MetalGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Live Auto-Refresh",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MetalTheme.colors.textPrimary
                            )
                            Text(
                                text = if (uiState.refreshInterval > 0) "Every ${uiState.refreshInterval}s" else "Manual refresh only",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textSecondary
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(10, 30, 60).forEach { interval ->
                            val isSelected = uiState.refreshInterval == interval
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) MetalGold else MetalTheme.colors.elevated)
                                    .clickable { viewModel.setRefreshInterval(interval) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${interval}s",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) androidx.compose.ui.graphics.Color(0xFF0B1020) else MetalTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }

                SettingsDivider()

                // Haptics toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = null,
                            tint = MetalGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Haptic Feedback",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MetalTheme.colors.textPrimary
                            )
                            Text(
                                text = "Vibrate on chart scrub & interactions",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textSecondary
                            )
                        }
                    }

                    Switch(
                        checked = uiState.isHapticsEnabled,
                        onCheckedChange = { viewModel.setHapticsEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = androidx.compose.ui.graphics.Color(0xFF0B1020),
                            checkedTrackColor = MetalGold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: Information & Documentation
        SettingsSectionHeader(title = "METHODOLOGY & INFORMATION")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MetalTheme.colors.surface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(MetalTheme.colors.border),
                width = 1.dp
            )
        ) {
            Column {
                SettingsActionRow(
                    icon = Icons.Default.MenuBook,
                    title = "Data Sources & Methodology",
                    subtitle = "LBMA, COMEX, spot calculations, and fixing times",
                    onClick = onNavigateToDataSources
                )

                SettingsDivider()

                SettingsActionRow(
                    icon = Icons.Default.Info,
                    title = "About & Risk Disclaimer",
                    subtitle = "Version 1.0.0 • Terms, privacy & financial notice",
                    onClick = onNavigateToAbout
                )

                SettingsDivider()

                SettingsActionRow(
                    icon = Icons.Default.DeleteOutline,
                    title = "Clear Cached Market Data",
                    subtitle = "Reset preferences and local cache",
                    onClick = { showClearDialog = true },
                    titleColor = ChangeNegative
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(text = "Clear Cached Data?") },
            text = { Text(text = "This will reset all locally stored preferences, watchlists, and cached time series data.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDialog = false
                    }
                ) {
                    Text(text = "Clear", color = ChangeNegative, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(text = "Cancel")
                }
            },
            containerColor = MetalTheme.colors.surface
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MetalTheme.colors.textSecondary,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
    )
}

@Composable
fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    titleColor: androidx.compose.ui.graphics.Color = MetalTheme.colors.textPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MetalGold,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MetalTheme.colors.textSecondary
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MetalTheme.colors.textMuted
        )
    }
}

@Composable
fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MetalTheme.colors.border)
    )
}
