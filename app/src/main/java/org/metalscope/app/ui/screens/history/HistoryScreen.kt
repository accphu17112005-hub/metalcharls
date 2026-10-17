package org.metalscope.app.ui.screens.history

import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.metalscope.app.data.model.HistoricalRecord
import org.metalscope.app.data.model.MetalType
import org.metalscope.app.data.model.TimeRange
import org.metalscope.app.theme.ChangeNegative
import org.metalscope.app.theme.ChangePositive
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.ui.screens.charts.StatItem
import org.metalscope.app.ui.viewmodel.MainUiState
import org.metalscope.app.ui.viewmodel.MainViewModel
import org.metalscope.app.util.CurrencyFormatter

@Composable
fun HistoryScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedMetal by remember { mutableStateOf(MetalType.GOLD) }
    var timeRange by remember { mutableStateOf(TimeRange.ONE_MONTH) }

    val records = remember(selectedMetal, timeRange) {
        viewModel.getHistoricalData(selectedMetal, timeRange)
    }

    val prices = records.map { it.calculateClose(uiState.currency, uiState.unit) }
    val high = prices.maxOrNull() ?: 0.0
    val low = prices.minOrNull() ?: 0.0
    val avg = if (prices.isNotEmpty()) prices.average() else 0.0
    val initial = prices.firstOrNull() ?: 0.0
    val last = prices.lastOrNull() ?: 0.0
    val totalReturnPct = if (initial > 0) ((last - initial) / initial) * 100.0 else 0.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MetalTheme.colors.background)
    ) {
        // Header Bar with CSV Share action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
                Text(
                    text = "Historical price quotes & benchmark archive",
                    style = MaterialTheme.typography.labelSmall,
                    color = MetalTheme.colors.textSecondary
                )
            }

            IconButton(
                onClick = {
                    val csv = buildString {
                        append("Date,Close(${uiState.currency.code}/${uiState.unit.symbol}),Open,High,Low,Change%\n")
                        records.reversed().forEach { r ->
                            append("${r.dateString},${r.calculateClose(uiState.currency, uiState.unit)},${r.calculateOpen(uiState.currency, uiState.unit)},${r.calculateHigh(uiState.currency, uiState.unit)},${r.calculateLow(uiState.currency, uiState.unit)},${r.changePercent}%\n")
                        }
                    }
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "MetalScope Historical Data (${selectedMetal.displayName}):\n\n$csv")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Export Historical Data"))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Export data",
                    tint = MetalGold
                )
            }
        }

        // Metal Selector Row
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

        Spacer(modifier = Modifier.height(10.dp))

        // Time Range Chips Row
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            org.metalscope.app.ui.components.TimeRangeSelector(
                selectedTimeRange = timeRange,
                onSelect = { timeRange = it }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Summary Statistics Card
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
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
                    label = "Average",
                    value = CurrencyFormatter.formatPrice(avg, uiState.currency)
                )
                StatItem(
                    label = "Total Return",
                    value = CurrencyFormatter.formatPercent(totalReturnPct),
                    valueColor = if (totalReturnPct >= 0) ChangePositive else ChangeNegative
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Historical Records List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(records.reversed(), key = { it.timestamp }) { record ->
                HistoricalRecordCard(
                    record = record,
                    currency = uiState.currency,
                    unit = uiState.unit
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun HistoricalRecordCard(
    record: HistoricalRecord,
    currency: org.metalscope.app.data.model.Currency,
    unit: org.metalscope.app.data.model.WeightUnit,
    modifier: Modifier = Modifier
) {
    val close = record.calculateClose(currency, unit)
    val high = record.calculateHigh(currency, unit)
    val low = record.calculateLow(currency, unit)
    val isPositive = record.changePercent >= 0

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MetalTheme.colors.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MetalTheme.colors.border),
            width = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = record.dateString,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "H: ${CurrencyFormatter.formatPrice(high, currency)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetalTheme.colors.textMuted
                    )
                    Text(
                        text = "L: ${CurrencyFormatter.formatPrice(low, currency)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetalTheme.colors.textMuted
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.formatPrice(close, currency),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isPositive) Icons.Default.NorthEast else Icons.Default.SouthEast,
                        contentDescription = null,
                        tint = if (isPositive) ChangePositive else ChangeNegative,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = CurrencyFormatter.formatPercent(record.changePercent),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isPositive) ChangePositive else ChangeNegative
                    )
                }
            }
        }
    }
}
