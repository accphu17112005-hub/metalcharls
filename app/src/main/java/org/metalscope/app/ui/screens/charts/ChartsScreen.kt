package org.metalscope.app.ui.screens.charts

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.metalscope.app.data.model.MetalType
import org.metalscope.app.data.model.TimeRange
import org.metalscope.app.theme.ChangeNegative
import org.metalscope.app.theme.ChangePositive
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.ui.components.ComparisonChart
import org.metalscope.app.ui.components.InteractiveChart
import org.metalscope.app.ui.viewmodel.MainUiState
import org.metalscope.app.ui.viewmodel.MainViewModel
import org.metalscope.app.util.CurrencyFormatter

enum class ChartStudioMode(val displayName: String) {
    SINGLE("Single Metal"),
    COMPARISON("Multi-Metal %"),
    GOLD_SILVER_RATIO("Au/Ag Ratio")
}

@Composable
fun ChartsScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(ChartStudioMode.SINGLE) }
    var selectedMetal by remember { mutableStateOf(MetalType.GOLD) }
    var timeRange by remember { mutableStateOf(TimeRange.ONE_MONTH) }

    val singleRecords = remember(selectedMetal, timeRange) {
        viewModel.getHistoricalData(selectedMetal, timeRange)
    }

    val allMetalRecords = remember(timeRange) {
        MetalType.entries.associateWith { metal ->
            viewModel.getHistoricalData(metal, timeRange)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MetalTheme.colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Title Bar
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Chart Studio",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MetalTheme.colors.textPrimary
            )
            Text(
                text = "Interactive financial charts & multi-metal performance analysis",
                style = MaterialTheme.typography.labelSmall,
                color = MetalTheme.colors.textSecondary
            )
        }

        // Mode Selector: Single / Multi-Metal % / Au/Ag Ratio
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ChartStudioMode.entries.forEach { studioMode ->
                val isSelected = studioMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) MetalGold else MetalTheme.colors.elevated)
                        .border(
                            1.dp,
                            if (isSelected) MetalGold else MetalTheme.colors.border,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { mode = studioMode }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = studioMode.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF0B1020) else MetalTheme.colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Metal Selector (when Single mode)
        if (mode == ChartStudioMode.SINGLE) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetalType.entries.forEach { metal ->
                    val isSelected = metal == selectedMetal
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) metal.defaultColor.copy(alpha = 0.2f) else MetalTheme.colors.surface)
                            .border(
                                1.dp,
                                if (isSelected) metal.defaultColor else MetalTheme.colors.border,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedMetal = metal }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = metal.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) metal.defaultColor else MetalTheme.colors.textSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Main Chart Card
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
                when (mode) {
                    ChartStudioMode.SINGLE -> {
                        InteractiveChart(
                            records = singleRecords,
                            currency = uiState.currency,
                            unit = uiState.unit,
                            selectedTimeRange = timeRange,
                            onTimeRangeSelected = { timeRange = it },
                            lineColor = selectedMetal.defaultColor
                        )
                    }
                    ChartStudioMode.COMPARISON -> {
                        ComparisonChart(metalData = allMetalRecords)
                        Spacer(modifier = Modifier.height(14.dp))
                        org.metalscope.app.ui.components.TimeRangeSelector(
                            selectedTimeRange = timeRange,
                            onSelect = { timeRange = it }
                        )
                    }
                    ChartStudioMode.GOLD_SILVER_RATIO -> {
                        // Ratio calculated from gold and silver records
                        val goldRecords = allMetalRecords[MetalType.GOLD] ?: emptyList()
                        val silverRecords = allMetalRecords[MetalType.SILVER] ?: emptyList()
                        val ratioRecords = goldRecords.zip(silverRecords).map { (g, s) ->
                            val r = if (s.closeUsdPerOz > 0) g.closeUsdPerOz / s.closeUsdPerOz else 80.0
                            g.copy(
                                closeUsdPerOz = r,
                                openUsdPerOz = r,
                                highUsdPerOz = r,
                                lowUsdPerOz = r
                            )
                        }

                        Column {
                            Text(
                                text = "Gold to Silver Spot Ratio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MetalGold
                            )
                            Text(
                                text = "Historical ratio indicator (Historical Median ~75x)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MetalTheme.colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            InteractiveChart(
                                records = ratioRecords,
                                currency = org.metalscope.app.data.model.Currency.USD, // Ratio is dimensionless
                                unit = org.metalscope.app.data.model.WeightUnit.OZ,
                                selectedTimeRange = timeRange,
                                onTimeRangeSelected = { timeRange = it },
                                lineColor = MetalGold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Range Statistics Summary Card
        if (singleRecords.isNotEmpty() && mode == ChartStudioMode.SINGLE) {
            val prices = singleRecords.map { it.calculateClose(uiState.currency, uiState.unit) }
            val first = prices.first()
            val last = prices.last()
            val high = prices.maxOrNull() ?: last
            val low = prices.minOrNull() ?: first
            val diff = last - first
            val pct = if (first > 0) (diff / first) * 100.0 else 0.0

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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Period Statistics (${timeRange.label})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MetalTheme.colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            label = "Period High",
                            value = CurrencyFormatter.formatPrice(high, uiState.currency)
                        )
                        StatItem(
                            label = "Period Low",
                            value = CurrencyFormatter.formatPrice(low, uiState.currency)
                        )
                        StatItem(
                            label = "Total Return",
                            value = CurrencyFormatter.formatPercent(pct),
                            valueColor = if (pct >= 0) ChangePositive else ChangeNegative
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    valueColor: Color = MetalTheme.colors.textPrimary
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MetalTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
