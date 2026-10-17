package org.metalscope.app.ui.screens.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.ui.components.MetalPriceCard
import org.metalscope.app.ui.viewmodel.MainUiState
import org.metalscope.app.ui.viewmodel.MainViewModel

@Composable
fun WatchlistScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMetal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteQuotes = uiState.quotes.filter { uiState.watchlistSymbols.contains(it.symbol) }

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
                text = "Watchlist (${favoriteQuotes.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MetalTheme.colors.textPrimary
            )
        }

        if (favoriteQuotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Your Watchlist is Empty",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MetalTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap the star icon on any metal card or market row to add it to your personal watchlist.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MetalTheme.colors.textSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Pinned Precious Metals",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetalTheme.colors.textSecondary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(favoriteQuotes, key = { it.symbol }) { quote ->
                    MetalPriceCard(
                        quote = quote,
                        currency = uiState.currency,
                        unit = uiState.unit,
                        isFavorite = true,
                        onFavoriteToggle = { viewModel.toggleWatchlist(quote.symbol) },
                        onClick = { onNavigateToMetal(quote.symbol) }
                    )
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}
