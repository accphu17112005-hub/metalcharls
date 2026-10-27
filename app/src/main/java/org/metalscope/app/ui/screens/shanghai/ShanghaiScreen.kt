package org.metalscope.app.ui.screens.shanghai

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import org.metalscope.app.theme.ChangePositive
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalSilver
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.ui.components.MarketTab
import org.metalscope.app.ui.components.MarketTabBar
import org.metalscope.app.ui.components.TimeRangeSelector
import org.metalscope.app.ui.viewmodel.MainUiState
import org.metalscope.app.ui.viewmodel.MainViewModel
import org.metalscope.app.util.CurrencyFormatter

@Composable
fun ShanghaiScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTab: (MarketTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMetal by remember { mutableStateOf(MetalType.SILVER) }
    var selectedTimeRange by remember { mutableStateOf(TimeRange.ONE_MONTH) }
    var showPremiumOverlay by remember { mutableStateOf(true) }

    val data = remember(selectedMetal, selectedTimeRange) {
        viewModel.getShanghaiData(selectedMetal, selectedTimeRange)
    }

    val isSilver = selectedMetal == MetalType.SILVER
    val metalColor = if (isSilver) MetalSilver else MetalGold
    val chinaColor = Color(0xFFEF4444) // China Red / SGE

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
                    text = "Shanghai Premium (SGE)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
                Text(
                    text = "China Gold Exchange vs COMEX",
                    style = MaterialTheme.typography.labelSmall,
                    color = MetalTheme.colors.textSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🇨🇳 SGE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF87171)
                )
            }
        }

        // Quick Tab Bar
        MarketTabBar(
            selectedTab = MarketTab.SHANGHAI,
            onTabSelected = onNavigateToTab
        )

        // Scrollable content
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
                            text = "${metal.displayName} (${metal.symbol})",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) pillColor else MetalTheme.colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hero Summary Card
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
                        text = "The Shanghai ${selectedMetal.displayName.lowercase()} price is currently",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MetalTheme.colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$${String.format("%.2f", data.shanghaiPriceUsdPerOz)}",
                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 34.sp),
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFF87171)
                        )
                        Text(
                            text = "/ oz",
                            style = MaterialTheme.typography.titleMedium,
                            color = MetalTheme.colors.textSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
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
                                .background(ChangePositive.copy(alpha = 0.2f))
                                .border(1.dp, ChangePositive, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NorthEast,
                                    contentDescription = null,
                                    tint = ChangePositive,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "+${String.format("%.2f", data.premiumPercent)}% China Premium",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChangePositive
                                )
                            }
                        }

                        Text(
                            text = "vs US Spot $${String.format("%.2f", data.westernPriceUsdPerOz)}/oz",
                            style = MaterialTheme.typography.labelMedium,
                            color = MetalTheme.colors.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary Metrics Pill Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "SGE Domestic Rate",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textMuted
                            )
                            Text(
                                text = "${String.format("%.2f", data.sgePriceCnyPerGram)} CNY/g",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MetalTheme.colors.textPrimary
                            )
                        }

                        Column {
                            Text(
                                text = "Premium Spread",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textMuted
                            )
                            Text(
                                text = "+$${String.format("%.2f", data.premiumUsdPerOz)} / oz",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ChangePositive
                            )
                        }

                        Column {
                            Text(
                                text = "USD/CNY Forex",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textMuted
                            )
                            Text(
                                text = "¥${data.usdCnyRate}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MetalTheme.colors.textPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price Comparison Chart Card
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Shanghai vs Western Price",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MetalTheme.colors.textPrimary
                            )
                            Text(
                                text = "Comparison in USD/oz",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textSecondary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Premium %",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (showPremiumOverlay) ChangePositive else MetalTheme.colors.textMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = showPremiumOverlay,
                                onCheckedChange = { showPremiumOverlay = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ChangePositive,
                                    checkedTrackColor = ChangePositive.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(chinaColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Shanghai (SGE)",
                                style = MaterialTheme.typography.labelSmall,
                                color = chinaColor
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(metalColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Western Spot (COMEX)",
                                style = MaterialTheme.typography.labelSmall,
                                color = metalColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Comparison Canvas Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        val points = data.comparisonHistory
                        if (points.isNotEmpty()) {
                            val minVal = points.minOf { minOf(it.westernPriceUsd, it.shanghaiPriceUsd) } * 0.98
                            val maxVal = points.maxOf { maxOf(it.westernPriceUsd, it.shanghaiPriceUsd) } * 1.02
                            val span = (maxVal - minVal).coerceAtLeast(0.01)

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                // Draw horizontal grid lines
                                for (step in 1..3) {
                                    val y = h * (step / 4f)
                                    drawLine(
                                        color = Color(0xFF283449).copy(alpha = 0.5f),
                                        start = Offset(0f, y),
                                        end = Offset(w, y),
                                        strokeWidth = 1f
                                    )
                                }

                                val shanghaiPath = Path()
                                val westernPath = Path()

                                points.forEachIndexed { i, pt ->
                                    val x = (i.toFloat() / (points.size - 1)) * w
                                    val yShanghai = h - (((pt.shanghaiPriceUsd - minVal) / span) * h).toFloat()
                                    val yWest = h - (((pt.westernPriceUsd - minVal) / span) * h).toFloat()

                                    if (i == 0) {
                                        shanghaiPath.moveTo(x, yShanghai)
                                        westernPath.moveTo(x, yWest)
                                    } else {
                                        shanghaiPath.lineTo(x, yShanghai)
                                        westernPath.lineTo(x, yWest)
                                    }
                                }

                                // Draw Western line
                                drawPath(
                                    path = westernPath,
                                    color = metalColor,
                                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                                )

                                // Draw Shanghai line
                                drawPath(
                                    path = shanghaiPath,
                                    color = chinaColor,
                                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Time Range Selector
                    TimeRangeSelector(
                        selectedTimeRange = selectedTimeRange,
                        onSelect = { selectedTimeRange = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SHFE Inventory vs Price Card
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
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SHFE Inventory & Warrant Stocks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MetalTheme.colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Current Warehouse Stock",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textMuted
                            )
                            Text(
                                text = "${data.shfeInventoryTonnes} tonnes",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MetalTheme.colors.textPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "30-Day Change",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textMuted
                            )
                            Text(
                                text = "${data.shfeInventory30dChangeTonnes} tonnes",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (data.shfeInventory30dChangeTonnes < 0) Color(0xFFF87171) else ChangePositive
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Warehouse warrant inventory on the Shanghai Futures Exchange (SHFE) has drained as physical industrial consumption outpaces registered domestic refiners.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MetalTheme.colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Market Explanations & Structural Dynamics
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
                            text = "Why Shanghai Trades at a Premium",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MetalTheme.colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ShanghaiInsightBullet(
                        title = "13% Value-Added Tax (VAT)",
                        description = "China imposes a 13% VAT on domestic silver transactions. Importers cannot easily reclaim VAT on re-exports, establishing a structural pricing floor."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ShanghaiInsightBullet(
                        title = "PBOC Import Quotas",
                        description = "The People's Bank of China allocates strict import licenses to commercial banks. When domestic physical demand spikes, quotas restrict supply and drive up premiums."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ShanghaiInsightBullet(
                        title = "Physical Delivery vs Paper Arbitrage",
                        description = "Shanghai Gold Exchange contracts require 100% physical delivery in certified bars. COMEX is largely paper cash-settled, preventing cross-border price equalisation."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ShanghaiInsightBullet(
                        title = "Solar Photovoltaic Dominance",
                        description = "China manufactures over 80% of global solar panels, consuming over 190 million ounces of silver paste annually, creating intense local spot bidding."
                    )
                }
            }
        }
    }
}

@Composable
private fun ShanghaiInsightBullet(
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
