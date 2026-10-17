package org.metalscope.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.metalscope.app.theme.ChangeNegative
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalTheme

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MetalTheme.colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MetalTheme.colors.textPrimary
                )
            }
            Text(
                text = "About & Legal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MetalTheme.colors.textPrimary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // App Emblem
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(MetalGold.copy(alpha = 0.15f), CircleShape)
                    .border(2.dp, MetalGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Au",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
                    fontWeight = FontWeight.Black,
                    color = MetalGold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "MetalScope",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MetalTheme.colors.textPrimary
            )

            Text(
                text = "Precious Metals. Clear Insights.",
                style = MaterialTheme.typography.titleMedium,
                color = MetalGold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Version 1.0.0 (Native Android & Compose)",
                style = MaterialTheme.typography.labelSmall,
                color = MetalTheme.colors.textMuted
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Financial Disclaimer Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MetalTheme.colors.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(ChangeNegative.copy(alpha = 0.5f)),
                    width = 1.dp
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "IMPORTANT FINANCIAL RISK DISCLAIMER",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ChangeNegative
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "MetalScope provides market data, charts, and ratios for informational, educational, and analytical purposes only. Nothing contained within this application constitutes financial, investment, legal, or tax advice.\n\nPrecious metals spot prices and physical bullion products carry substantial risk and volatility. Past performance does not guarantee future results. Consult a licensed financial advisor before making any investment decisions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MetalTheme.colors.textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms & Privacy
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MetalTheme.colors.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MetalTheme.colors.border),
                    width = 1.dp
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Privacy & Local Persistence",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MetalGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "MetalScope is built offline-first. Your preferences, watchlists, currency choices, and units are persisted exclusively on your local device using AndroidX Jetpack DataStore. No personal identity, account creation, or tracking telemetry is required.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MetalTheme.colors.textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
