package org.metalscope.app.ui.screens.comex

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.metalscope.app.data.model.MetalType
import org.metalscope.app.data.model.TimeRange
import org.metalscope.app.theme.ChangeNegative
import org.metalscope.app.theme.ChangePositive
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalSilver
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.ui.components.MarketTab
import org.metalscope.app.ui.components.MarketTabBar
import org.metalscope.app.ui.components.TimeRangeSelector
import org.metalscope.app.ui.viewmodel.MainUiState
import org.metalscope.app.ui.viewmodel.MainViewModel

@Composable
fun ComexSilverScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTab: (MarketTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMetal by remember { mutableStateOf(MetalType.SILVER) }
    var selectedTimeRange by remember { mutableStateOf(TimeRange.ONE_MONTH) }

    val data = remember(selectedMetal, selectedTimeRange) {
        viewModel.getComexInventoryData(selectedMetal, selectedTimeRange)
    }

    val isSilver = selectedMetal == MetalType.SILVER
    val metalColor = if (isSilver) MetalSilver else MetalGold
    val registeredColor = Color(0xFFEF4444)
    val eligibleColor = Color(0xFF3B82F6)

    val registeredRatio = (data.registeredOunces / data.totalVaultOunces).toFloat().coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MetalTheme.colors.background)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MetalTheme.colors.textPrimary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "COMEX Inventory & Vaults",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
                Text(
                    text = "Registered vs Eligible Vault Stocks",
                    style = MaterialTheme.typography.labelSmall,
                    color = MetalTheme.colors.textSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF3B82F6).copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🏛️ COMEX",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF60A5FA)
                )
            }
        }

        // Quick Tab Bar
        MarketTabBar(
            selectedTab = MarketTab.COMEX,
            onTabSelected = onNavigateToTab
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Metal Switcher (Silver / Gold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(MetalType.SILVER, MetalType.GOLD).forEach { metal ->
                    val isSel = metal == selectedMetal
                    val pillColor = if (metal == MetalType.SILVER) MetalSilver else MetalGold
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSel) pillColor.copy(alpha = 0.2f) else MetalTheme.colors.elevated
                            )
                            .border(
                                width = if (isSel) 1.5.dp else 1.dp,
                                color = if (isSel) pillColor else MetalTheme.colors.border,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedMetal = metal }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${metal.displayName} Inventory",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) pillColor else MetalTheme.colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hero Inventory Overview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MetalTheme.colors.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MetalTheme.colors.border),
                    width = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Total ${selectedMetal.displayName} in COMEX Approved Vaults",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MetalTheme.colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${String.format("%.1f", data.totalVaultOunces / 1_000_000.0)}M",
                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp),
                            fontWeight = FontWeight.Black,
                            color = MetalTheme.colors.textPrimary
                        )
                        Text(
                            text = "troy ounces",
                            style = MaterialTheme.typography.titleMedium,
                            color = MetalTheme.colors.textSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ChangeNegative.copy(alpha = 0.2f))
                                .border(1.dp, ChangeNegative, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SouthEast,
                                    contentDescription = null,
                                    tint = ChangeNegative,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${String.format("%.1f", data.dailyVaultNetChangeOunces / 1000.0)}k oz Net Outflow (24h)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChangeNegative
                                )
                            }
                        }

                        Text(
                            text = "Spot: $${String.format("%.2f", data.priceUsdPerOz)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MetalTheme.colors.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stacked Inventory Bar (Registered vs Eligible)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(registeredColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Registered: ${String.format("%.1f", data.registeredOunces / 1_000_000.0)}M oz (${String.format("%.1f", registeredRatio * 100)}%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = registeredColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(eligibleColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Eligible: ${String.format("%.1f", data.eligibleOunces / 1_000_000.0)}M oz (${String.format("%.1f", (1f - registeredRatio) * 100)}%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = eligibleColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Visual segmented bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MetalTheme.colors.elevated)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(registeredRatio)
                                    .background(registeredColor)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f - registeredRatio)
                                    .background(eligibleColor)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Registered Drawdown Tracker Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MetalTheme.colors.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MetalTheme.colors.border),
                    width = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Registered Drawdown Tracker",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MetalTheme.colors.textPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${data.drawdownAthPercent}% ATH",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF87171)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Peak Registered",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textMuted
                            )
                            Text(
                                text = "${String.format("%.1f", data.peakRegisteredOunces / 1_000_000.0)}M oz",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MetalTheme.colors.textPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Current Registered",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textMuted
                            )
                            Text(
                                text = "${String.format("%.1f", data.registeredOunces / 1_000_000.0)}M oz",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF87171)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "30-Day Change",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textMuted
                            )
                            Text(
                                text = "${String.format("%.1f", data.change30dOunces / 1_000_000.0)}M oz",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (data.change30dOunces < 0) Color(0xFFF87171) else ChangePositive
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Historical Vault Inventory Chart
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MetalTheme.colors.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MetalTheme.colors.border),
                    width = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "COMEX Vault Drain History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MetalTheme.colors.textPrimary
                    )
                    Text(
                        text = "Tracking deliverable registered ounces over time",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetalTheme.colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        val points = data.history
                        if (points.isNotEmpty()) {
                            val minVal = points.minOf { it.registeredOunces } * 0.95
                            val maxVal = points.maxOf { it.registeredOunces } * 1.05
                            val span = (maxVal - minVal).coerceAtLeast(0.01)

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                for (step in 1..3) {
                                    val y = h * (step / 4f)
                                    drawLine(
                                        color = Color(0xFF283449).copy(alpha = 0.5f),
                                        start = Offset(0f, y),
                                        end = Offset(w, y),
                                        strokeWidth = 1f
                                    )
                                }

                                val regPath = Path()
                                points.forEachIndexed { i, pt ->
                                    val x = (i.toFloat() / (points.size - 1)) * w
                                    val yReg = h - (((pt.registeredOunces - minVal) / span) * h).toFloat()
                                    if (i == 0) regPath.moveTo(x, yReg) else regPath.lineTo(x, yReg)
                                }

                                drawPath(
                                    path = regPath,
                                    color = registeredColor,
                                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TimeRangeSelector(
                        selectedTimeRange = selectedTimeRange,
                        onSelect = { selectedTimeRange = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // COMEX Vaults Breakdown by Depository
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MetalTheme.colors.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MetalTheme.colors.border),
                    width = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Approved COMEX Vault Depositories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MetalTheme.colors.textPrimary
                    )
                    Text(
                        text = "Physical holdings across approved vault operators",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetalTheme.colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    data.vaults.forEach { vault ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = vault.vaultName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MetalTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = vault.location,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MetalTheme.colors.textMuted
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${String.format("%.1f", vault.totalOunces / 1_000_000.0)}M oz",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MetalTheme.colors.textPrimary
                                    )
                                    Text(
                                        text = "${vault.sharePercent}% share",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MetalTheme.colors.textSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (vault.sharePercent / 100f).toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = Color(0xFF60A5FA),
                                trackColor = MetalTheme.colors.elevated
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recent Deliveries History Table
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MetalTheme.colors.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MetalTheme.colors.border),
                    width = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Daily Delivery Notices & History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MetalTheme.colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    data.recentDeliveries.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.dateStr,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MetalTheme.colors.textPrimary
                                )
                                Text(
                                    text = "Month: ${item.contractMonth} · ${item.contractsIssued} contracts",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MetalTheme.colors.textSecondary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${String.format("%,.0f", item.netOuncesDelivered)} oz",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ChangePositive
                                )
                                Text(
                                    text = "Total: ${String.format("%.1f", item.cumulativeMonthOunces / 1_000_000.0)}M oz",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MetalTheme.colors.textMuted
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Educational Explanations
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MetalTheme.colors.surface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MetalTheme.colors.border),
                    width = 1.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MetalGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Understanding COMEX Vault Categories",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MetalTheme.colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ComexInsightBullet(
                        title = "Registered vs. Eligible Silver",
                        description = "Eligible silver meets COMEX fineness (.999) and brand standards but is simply stored for private owners. Registered silver is backed by warehouse warrants and is available for delivery against futures contracts."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ComexInsightBullet(
                        title = "Why Registered Drawdowns Matter",
                        description = "When registered inventory drops below 100M ounces while paper open interest remains high, the paper-to-physical ratio stretches. This makes short positions vulnerable to physical delivery demands and short squeezes."
                    )
                }
            }
        }
    }
}

@Composable
private fun ComexInsightBullet(
    title: String,
    description: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MetalTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MetalTheme.colors.textSecondary,
            lineHeight = 18.sp
        )
    }
}
