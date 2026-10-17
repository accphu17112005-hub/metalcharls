package org.metalscope.app.ui.screens.markets

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
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.metalscope.app.data.model.PriceQuote
import org.metalscope.app.theme.ChangeNegative
import org.metalscope.app.theme.ChangePositive
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.ui.components.MetalPriceCard
import org.metalscope.app.ui.components.SparklineView
import org.metalscope.app.ui.viewmodel.MainUiState
import org.metalscope.app.ui.viewmodel.MainViewModel
import org.metalscope.app.ui.viewmodel.MarketFilter
import org.metalscope.app.ui.viewmodel.SortOption
import org.metalscope.app.util.CurrencyFormatter

@Composable
fun MarketsScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    onNavigateToMetal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var sortMenuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MetalTheme.colors.background)
    ) {
        // Title Bar & View Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Markets",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
                Text(
                    text = "${uiState.filteredQuotes.size} active metal contracts",
                    style = MaterialTheme.typography.labelSmall,
                    color = MetalTheme.colors.textSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Sort Menu Button
                Box {
                    IconButton(onClick = { sortMenuOpen = true }) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort markets",
                            tint = MetalGold
                        )
                    }

                    DropdownMenu(
                        expanded = sortMenuOpen,
                        onDismissRequest = { sortMenuOpen = false },
                        modifier = Modifier.background(MetalTheme.colors.surface)
                    ) {
                        SortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option.displayName,
                                        color = if (uiState.sortOption == option) MetalGold else MetalTheme.colors.textPrimary,
                                        fontWeight = if (uiState.sortOption == option) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    viewModel.setSortOption(option)
                                    sortMenuOpen = false
                                }
                            )
                        }
                    }
                }

                // Grid / List Toggle
                IconButton(onClick = { viewModel.setCardView(!uiState.isCardView) }) {
                    Icon(
                        imageVector = if (uiState.isCardView) Icons.Default.ViewList else Icons.Default.GridView,
                        contentDescription = "Toggle View",
                        tint = MetalTheme.colors.textSecondary
                    )
                }
            }
        }

        // Search Input Field
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text(
                        text = "Search by symbol (XAU) or name...",
                        color = MetalTheme.colors.textMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MetalTheme.colors.textMuted
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MetalTheme.colors.surface,
                    unfocusedContainerColor = MetalTheme.colors.surface,
                    focusedBorderColor = MetalGold,
                    unfocusedBorderColor = MetalTheme.colors.border,
                    focusedTextColor = MetalTheme.colors.textPrimary,
                    unfocusedTextColor = MetalTheme.colors.textPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MarketFilter.entries.forEach { filter ->
                val isSelected = uiState.filter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MetalGold else MetalTheme.colors.elevated)
                        .clickable { viewModel.setMarketFilter(filter) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF0B1020) else MetalTheme.colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Market List / Cards
        if (uiState.filteredQuotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No metals matching \"${uiState.searchQuery}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MetalTheme.colors.textSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (uiState.isCardView) {
                    items(uiState.filteredQuotes, key = { it.symbol }) { quote ->
                        MetalPriceCard(
                            quote = quote,
                            currency = uiState.currency,
                            unit = uiState.unit,
                            isFavorite = uiState.watchlistSymbols.contains(quote.symbol),
                            onFavoriteToggle = { viewModel.toggleWatchlist(quote.symbol) },
                            onClick = { onNavigateToMetal(quote.symbol) }
                        )
                    }
                } else {
                    items(uiState.filteredQuotes, key = { it.symbol }) { quote ->
                        MarketRowItem(
                            quote = quote,
                            currency = uiState.currency,
                            unit = uiState.unit,
                            isFavorite = uiState.watchlistSymbols.contains(quote.symbol),
                            onFavoriteToggle = { viewModel.toggleWatchlist(quote.symbol) },
                            onClick = { onNavigateToMetal(quote.symbol) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun MarketRowItem(
    quote: PriceQuote,
    currency: org.metalscope.app.data.model.Currency,
    unit: org.metalscope.app.data.model.WeightUnit,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val price = quote.calculatePrice(currency, unit)
    val isPositive = quote.changePercent24h >= 0
    val color = quote.metalType.defaultColor

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MetalTheme.colors.surface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MetalTheme.colors.border),
            width = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Icon + Symbol + Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1.3f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(color.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, color.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = quote.metalType.chemicalSymbol,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = quote.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MetalTheme.colors.textPrimary
                    )
                    Text(
                        text = "${quote.symbol} • ${unit.symbol}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetalTheme.colors.textSecondary
                    )
                }
            }

            // Center: Mini Sparkline
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(28.dp)
                    .padding(horizontal = 4.dp)
            ) {
                SparklineView(
                    data = quote.sparkline,
                    isPositive = isPositive,
                    showFill = false,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Right: Price, % Change, Favorite
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(1.2f)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyFormatter.formatPrice(price, currency),
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
                            text = CurrencyFormatter.formatPercent(quote.changePercent24h),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) ChangePositive else ChangeNegative
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Watchlist toggle",
                        tint = if (isFavorite) MetalGold else MetalTheme.colors.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
