package org.metalscope.app.ui.screens.detail

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.metalscope.app.data.model.Currency
import org.metalscope.app.data.model.MetalType
import org.metalscope.app.data.model.PriceQuote
import org.metalscope.app.data.model.TimeRange
import org.metalscope.app.data.model.WeightUnit
import org.metalscope.app.theme.ChangeNegative
import org.metalscope.app.theme.ChangePositive
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.ui.components.InteractiveChart
import org.metalscope.app.ui.components.MarketStatusBadge
import org.metalscope.app.ui.viewmodel.MainUiState
import org.metalscope.app.ui.viewmodel.MainViewModel
import org.metalscope.app.util.CurrencyFormatter

@Composable
fun MetalDetailScreen(
    symbol: String,
    uiState: MainUiState,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onOpenCurrencyPicker: () -> Unit,
    onOpenUnitPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quote = uiState.quotes.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) }
        ?: uiState.featuredGold

    if (quote == null) {
        Box(
            modifier = modifier.fillMaxSize().background(MetalTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Metal not found", color = MetalTheme.colors.textSecondary)
        }
        return
    }

    var timeRange by remember { mutableStateOf(TimeRange.ONE_DAY) }
    var calcAmount by remember { mutableStateOf("1.0") }

    val records = remember(quote.metalType, timeRange) {
        viewModel.getHistoricalData(quote.metalType, timeRange)
    }

    val price = quote.calculatePrice(uiState.currency, uiState.unit)
    val change = quote.calculateChange(uiState.currency, uiState.unit)
    val high = quote.calculateHigh(uiState.currency, uiState.unit)
    val low = quote.calculateLow(uiState.currency, uiState.unit)
    val isPositive = quote.changePercent24h >= 0
    val isFavorite = uiState.watchlistSymbols.contains(quote.symbol)
    val color = quote.metalType.defaultColor

    val rangeSpan = (high - low).coerceAtLeast(0.001)
    val currentPosition = ((price - low) / rangeSpan).coerceIn(0.0, 1.0).toFloat()

    // Calculated investment value
    val amountDouble = calcAmount.toDoubleOrNull() ?: 1.0
    val totalEstimatedValue = price * amountDouble

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MetalTheme.colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(color.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = quote.metalType.chemicalSymbol,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${quote.name} (${quote.symbol})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
            }

            IconButton(onClick = { viewModel.toggleWatchlist(quote.symbol) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Watchlist toggle",
                    tint = if (isFavorite) MetalGold else MetalTheme.colors.textSecondary
                )
            }
        }

        // Quick Currency / Unit Bar
        MarketStatusBadge(
            isOpen = quote.isMarketOpen,
            sessionText = quote.marketSession,
            lastUpdated = quote.timestamp,
            currency = uiState.currency,
            unit = uiState.unit,
            onCurrencyClick = onOpenCurrencyPicker,
            onUnitClick = onOpenUnitPicker,
            isDemo = quote.isDemoData
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Hero Price & Change
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = CurrencyFormatter.formatPrice(price, uiState.currency),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp),
                fontWeight = FontWeight.Black,
                color = MetalTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.NorthEast else Icons.Default.SouthEast,
                    contentDescription = null,
                    tint = if (isPositive) ChangePositive else ChangeNegative,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = CurrencyFormatter.formatChange(change, uiState.currency),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) ChangePositive else ChangeNegative
                )
                Text(
                    text = "(${CurrencyFormatter.formatPercent(quote.changePercent24h)})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) ChangePositive else ChangeNegative
                )
                Text(
                    text = "24H",
                    style = MaterialTheme.typography.labelSmall,
                    color = MetalTheme.colors.textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Financial Chart
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
                InteractiveChart(
                    records = records,
                    currency = uiState.currency,
                    unit = uiState.unit,
                    selectedTimeRange = timeRange,
                    onTimeRangeSelected = { timeRange = it },
                    lineColor = color
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Key Market Statistics Card
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
                    text = "Key Market Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 24h Range Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "24h Low: ${CurrencyFormatter.formatPrice(low, uiState.currency)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MetalTheme.colors.textMuted
                        )
                        Text(
                            text = "24h High: ${CurrencyFormatter.formatPrice(high, uiState.currency)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MetalTheme.colors.textMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { currentPosition },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = color,
                        trackColor = MetalTheme.colors.elevated
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailStatColumn("Bid", CurrencyFormatter.formatPrice(quote.calculateBid(uiState.currency, uiState.unit), uiState.currency))
                    DetailStatColumn("Ask", CurrencyFormatter.formatPrice(quote.calculateAsk(uiState.currency, uiState.unit), uiState.currency))
                    DetailStatColumn("Spread", CurrencyFormatter.formatPrice(uiState.currency.convertFromUsd(quote.spreadUsd), uiState.currency))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailStatColumn("Standard Purity", quote.metalType.standardPurity)
                    DetailStatColumn("Atomic Number", "${quote.metalType.atomicNumber} (${quote.metalType.chemicalSymbol})")
                    DetailStatColumn("Benchmark", quote.marketType)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Metal Value Calculator
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
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = MetalGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Holdings Value Calculator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MetalTheme.colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = calcAmount,
                        onValueChange = { calcAmount = it },
                        label = { Text("Quantity (${uiState.unit.symbol})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MetalTheme.colors.elevated,
                            unfocusedContainerColor = MetalTheme.colors.elevated,
                            focusedBorderColor = MetalGold,
                            unfocusedBorderColor = MetalTheme.colors.border,
                            focusedTextColor = MetalTheme.colors.textPrimary,
                            unfocusedTextColor = MetalTheme.colors.textPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Column(
                        modifier = Modifier.weight(1.2f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Estimated Value",
                            style = MaterialTheme.typography.labelSmall,
                            color = MetalTheme.colors.textSecondary
                        )
                        Text(
                            text = CurrencyFormatter.formatPrice(totalEstimatedValue, uiState.currency),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MetalGold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Metal Description & Fundamentals
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
                    text = "About ${quote.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = quote.metalType.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MetalTheme.colors.textSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Source: ${quote.source}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetalTheme.colors.textMuted
                    )
                    Text(
                        text = "Demo Feed • Verified",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetalGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun DetailStatColumn(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MetalTheme.colors.textMuted
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MetalTheme.colors.textPrimary
        )
    }
}
