package org.metalscope.app.ui.screens.india

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
import androidx.compose.material.icons.filled.ReceiptLong
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

@Composable
fun IndiaScreen(
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
        viewModel.getIndiaData(selectedMetal, selectedTimeRange)
    }

    val isSilver = selectedMetal == MetalType.SILVER
    val metalColor = if (isSilver) MetalSilver else MetalGold
    val indiaColor = Color(0xFFF59E0B) // Saffron / Gold

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
                    text = "India Premium (MCX)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
                Text(
                    text = "MCX Futures vs Western Benchmark",
                    style = MaterialTheme.typography.labelSmall,
                    color = MetalTheme.colors.textSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🇮🇳 MCX",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFBBF24)
                )
            }
        }

        // Quick Tab Bar
        MarketTabBar(
            selectedTab = MarketTab.INDIA,
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
                        text = "The India ${selectedMetal.displayName.lowercase()} price (MCX) is currently",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MetalTheme.colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$${String.format("%.2f", data.indiaPriceUsdPerOz)}",
                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 34.sp),
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFBBF24)
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
                                    text = "+${String.format("%.2f", data.premiumPercent)}% India Premium",
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
                                text = "MCX Domestic Rate",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textMuted
                            )
                            Text(
                                text = "₹${String.format("%,.0f", data.mcxPriceInr)} ${data.inrQuoteUnit}",
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
                                text = "USD/INR Forex",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textMuted
                            )
                            Text(
                                text = "₹${data.usdInrRate}",
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
                                text = "India vs Western Price",
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
                                    .background(indiaColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "India (MCX)",
                                style = MaterialTheme.typography.labelSmall,
                                color = indiaColor
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
                            val minVal = points.minOf { minOf(it.westernPriceUsd, it.indiaPriceUsd) } * 0.98
                            val maxVal = points.maxOf { maxOf(it.westernPriceUsd, it.indiaPriceUsd) } * 1.02
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

                                val indiaPath = Path()
                                val westernPath = Path()

                                points.forEachIndexed { i, pt ->
                                    val x = (i.toFloat() / (points.size - 1)) * w
                                    val yIndia = h - (((pt.indiaPriceUsd - minVal) / span) * h).toFloat()
                                    val yWest = h - (((pt.westernPriceUsd - minVal) / span) * h).toFloat()

                                    if (i == 0) {
                                        indiaPath.moveTo(x, yIndia)
                                        westernPath.moveTo(x, yWest)
                                    } else {
                                        indiaPath.lineTo(x, yIndia)
                                        westernPath.lineTo(x, yWest)
                                    }
                                }

                                drawPath(
                                    path = westernPath,
                                    color = metalColor,
                                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                                )

                                drawPath(
                                    path = indiaPath,
                                    color = indiaColor,
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

            // Taxes & Landed Cost Breakdown Card
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
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Taxes, Duties & Structural Cost Floor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MetalTheme.colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Indian bullion carries a structural ~18-19% tax burden over international spot, setting an import price floor nationwide.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MetalTheme.colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    data.taxBreakdown.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (item.isTotal) FontWeight.Bold else FontWeight.Medium,
                                    color = if (item.isTotal) MetalGold else MetalTheme.colors.textPrimary
                                )
                                Text(
                                    text = item.costFormula,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MetalTheme.colors.textMuted
                                )
                            }

                            Text(
                                text = "$${String.format("%.2f", item.valueUsd)} / oz",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isTotal) MetalGold else MetalTheme.colors.textPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seasonal Festivals & Rural Investment Dynamics
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
                            text = "Key Indian Market Drivers",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MetalTheme.colors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    IndiaInsightBullet(
                        title = "World's Largest Silver Consumer",
                        description = "India consumes 5,000 to 7,000 tonnes of silver annually. Surges in Indian import volumes frequently drain Western vaults and lead global price discover."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    IndiaInsightBullet(
                        title = "Diwali & Dhanteras Festival Surges",
                        description = "During Dhanteras (the festival of wealth) and Diwali (October/November), purchasing physical silver and gold coins and utensils is auspicious, causing annual premium spikes."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    IndiaInsightBullet(
                        title = "October - February Wedding Season",
                        description = "Over 10 million weddings take place in India each year, accounting for more than 50% of annual jewelry fabrication demand, centering in Gujarat and Rajasthan."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    IndiaInsightBullet(
                        title = "Rural Store of Value",
                        description = "Over 60% of India's metal purchases originate in rural districts where farming families rely on physical gold and silver as non-banking stores of intergenerational wealth."
                    )
                }
            }
        }
    }
}

@Composable
private fun IndiaInsightBullet(
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
