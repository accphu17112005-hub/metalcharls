package org.metalscope.app.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.ui.components.FeaturedGoldCard
import org.metalscope.app.ui.components.HeaderBar
import org.metalscope.app.ui.components.InteractiveChart
import org.metalscope.app.ui.components.MarketStatusBadge
import org.metalscope.app.ui.components.MetalPriceCard
import org.metalscope.app.ui.components.QuickInsightCard
import org.metalscope.app.ui.viewmodel.MainUiState
import org.metalscope.app.ui.viewmodel.MainViewModel

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material3.Icon
import org.metalscope.app.theme.ChangePositive
import org.metalscope.app.theme.ChangeNegative
import org.metalscope.app.ui.components.MarketTab
import org.metalscope.app.ui.components.MarketTabBar

@Composable
fun HomeScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    onNavigateToMetal: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToWatchlist: () -> Unit,
    onOpenCurrencyPicker: () -> Unit,
    onOpenUnitPicker: () -> Unit,
    onNavigateToTab: (MarketTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var chartMetal by remember { mutableStateOf(MetalType.GOLD) }
    var chartTimeRange by remember { mutableStateOf(TimeRange.ONE_DAY) }

    val chartRecords = remember(chartMetal, chartTimeRange) {
        viewModel.getHistoricalData(chartMetal, chartTimeRange)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MetalTheme.colors.background)
    ) {
        // 1. Top Header
        item {
            HeaderBar(
                watchlistCount = uiState.watchlistSymbols.size,
                isRefreshing = uiState.isRefreshing,
                onSearchClick = onNavigateToSearch,
                onWatchlistClick = onNavigateToWatchlist,
                onRefreshClick = { viewModel.refresh() }
            )
        }

        // 1.5 Quick Market Tab Switcher
        item {
            MarketTabBar(
                selectedTab = MarketTab.HOME,
                onTabSelected = onNavigateToTab
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // 2. Market Status Bar
        item {
            MarketStatusBadge(
                isOpen = uiState.quotes.firstOrNull()?.isMarketOpen ?: true,
                sessionText = uiState.quotes.firstOrNull()?.marketSession ?: "Markets Active",
                lastUpdated = uiState.lastUpdated,
                currency = uiState.currency,
                unit = uiState.unit,
                onCurrencyClick = onOpenCurrencyPicker,
                onUnitClick = onOpenUnitPicker,
                isDemo = true
            )
        }

        // 3. Featured Gold Card
        item {
            Spacer(modifier = Modifier.height(10.dp))
            uiState.featuredGold?.let { goldQuote ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    FeaturedGoldCard(
                        quote = goldQuote,
                        currency = uiState.currency,
                        unit = uiState.unit,
                        isFavorite = uiState.watchlistSymbols.contains(goldQuote.symbol),
                        onFavoriteToggle = { viewModel.toggleWatchlist(goldQuote.symbol) },
                        onClick = { onNavigateToMetal(goldQuote.symbol) }
                    )
                }
            }
        }

        // 4. Precious Metals Section Header
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Precious Metals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
                Text(
                    text = "Spot Benchmarks",
                    style = MaterialTheme.typography.labelSmall,
                    color = MetalTheme.colors.textSecondary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 5. Precious Metals Cards (Silver, Platinum, Palladium)
        item {
            val nonGoldQuotes = uiState.quotes.filter { it.symbol != "XAU" }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                nonGoldQuotes.forEach { quote ->
                    MetalPriceCard(
                        quote = quote,
                        currency = uiState.currency,
                        unit = uiState.unit,
                        isFavorite = uiState.watchlistSymbols.contains(quote.symbol),
                        onFavoriteToggle = { viewModel.toggleWatchlist(quote.symbol) },
                        onClick = { onNavigateToMetal(quote.symbol) }
                    )
                }
            }
        }

        // 5.5 Global Premiums & Vault Stocks (Shanghai, COMEX, India)
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Global Premiums & Vaults",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
                Text(
                    text = "Arbitrage & Reserves",
                    style = MaterialTheme.typography.labelSmall,
                    color = MetalTheme.colors.textSecondary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Shanghai Card
                HomeHubCard(
                    title = "Shanghai Premium (SGE)",
                    subtitle = "China Silver $60.65 vs US Spot $53.89/oz",
                    badge = "+12.54% China Premium",
                    badgePositive = true,
                    iconEmoji = "🇨🇳",
                    detail = "13.82 CNY/g · SHFE Warrant Stock: 2,140t",
                    onClick = { onNavigateToTab(MarketTab.SHANGHAI) }
                )

                // COMEX Silver Card
                HomeHubCard(
                    title = "COMEX Silver Inventory",
                    subtitle = "Physical Vault Reserves in NY & Depository",
                    badge = "337.5M oz Total",
                    badgePositive = false,
                    iconEmoji = "🏛️",
                    detail = "Registered: 99.8M oz (29.6%) · Drawdown: -34.8%",
                    onClick = { onNavigateToTab(MarketTab.COMEX) }
                )

                // India Card
                HomeHubCard(
                    title = "India Premium (MCX)",
                    subtitle = "MCX Silver $63.56 vs Western $53.88/oz",
                    badge = "+17.96% India Premium",
                    badgePositive = true,
                    iconEmoji = "🇮🇳",
                    detail = "₹88,450 / kg · 15% Duty + 3% GST Floor",
                    onClick = { onNavigateToTab(MarketTab.INDIA) }
                )
            }
        }

        // 6. Market Overview Chart Card
        item {
            Spacer(modifier = Modifier.height(24.dp))
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
                    // Metal Selector Pills for Chart
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Market Overview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MetalTheme.colors.textPrimary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MetalType.entries.forEach { metal ->
                                val isSelected = metal == chartMetal
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) metal.defaultColor.copy(alpha = 0.2f) else MetalTheme.colors.elevated)
                                        .border(
                                            1.dp,
                                            if (isSelected) metal.defaultColor else MetalTheme.colors.border,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { chartMetal = metal }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = metal.symbol,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) metal.defaultColor else MetalTheme.colors.textSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Interactive Chart
                    InteractiveChart(
                        records = chartRecords,
                        currency = uiState.currency,
                        unit = uiState.unit,
                        selectedTimeRange = chartTimeRange,
                        onTimeRangeSelected = { chartTimeRange = it },
                        lineColor = chartMetal.defaultColor
                    )
                }
            }
        }

        // 7. Quick Market Insights Card
        item {
            Spacer(modifier = Modifier.height(20.dp))
            uiState.insights?.let { insight ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    QuickInsightCard(insight = insight)
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun HomeHubCard(
    title: String,
    subtitle: String,
    badge: String,
    badgePositive: Boolean,
    iconEmoji: String,
    detail: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MetalTheme.colors.elevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = iconEmoji, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MetalTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (badgePositive) ChangePositive.copy(alpha = 0.2f) else Color(0xFF60A5FA).copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (badgePositive) ChangePositive else Color(0xFF60A5FA)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MetalTheme.colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = detail,
                        style = MaterialTheme.typography.labelSmall,
                        color = MetalTheme.colors.textMuted
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = MetalTheme.colors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

