package org.metalscope.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalTheme

@Composable
fun DataSourcesScreen(
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
                text = "Data Sources & Methodology",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MetalTheme.colors.textPrimary
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            MethodologyCard(
                title = "LBMA Gold & Silver Price Fixing",
                body = "The London Bullion Market Association (LBMA) establishes the globally recognized auction-based benchmark price for physical spot gold and silver. LBMA Gold auctions occur daily at 10:30 and 15:00 GMT/BST. LBMA Silver is settled once daily at 12:00 GMT."
            )

            Spacer(modifier = Modifier.height(14.dp))

            MethodologyCard(
                title = "COMEX & NYMEX Futures Integration",
                body = "Intraday tick indicators and volume metrics reflect continuous pricing from the Chicago Mercantile Exchange (CME Group COMEX / NYMEX) for active near-month futures contracts, normalized to spot equivalents."
            )

            Spacer(modifier = Modifier.height(14.dp))

            MethodologyCard(
                title = "Troy Ounce Conversion Standard",
                body = "All precious metals benchmark contracts are traded and quoted per Troy Ounce (oz t). One troy ounce is defined internationally as exactly 31.1034768 grams (or 0.0311034768 kg). Conversions in MetalScope apply exact IEEE-754 double arithmetic without intermediate truncation."
            )

            Spacer(modifier = Modifier.height(14.dp))

            MethodologyCard(
                title = "Multi-Currency Cross-Rate Synthesis",
                body = "Quotes in EUR, GBP, JPY, AUD, CAD, and VND are calculated using real-time interbank foreign exchange rates multiplied by the canonical USD/oz spot benchmark."
            )

            Spacer(modifier = Modifier.height(14.dp))

            MethodologyCard(
                title = "Demo & Offline Mode Architecture",
                body = "When operating without live external API credentials, MetalScope provides a calibrated stochastic simulation feed calibrated to current bullion market conditions, guaranteeing uninterrupted chart exploration and testing."
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun MethodologyCard(title: String, body: String) {
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MetalGold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MetalTheme.colors.textSecondary,
                lineHeight = 20.sp
            )
        }
    }
}
