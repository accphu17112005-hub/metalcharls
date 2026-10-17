package org.metalscope.app.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.metalscope.app.data.model.Currency
import org.metalscope.app.data.model.PriceQuote
import org.metalscope.app.data.model.WeightUnit
import org.metalscope.app.theme.ChangeNegative
import org.metalscope.app.theme.ChangePositive
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalGoldDark
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.util.CurrencyFormatter

@Composable
fun FeaturedGoldCard(
    quote: PriceQuote,
    currency: Currency,
    unit: WeightUnit,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val price = quote.calculatePrice(currency, unit)
    val change = quote.calculateChange(currency, unit)
    val high = quote.calculateHigh(currency, unit)
    val low = quote.calculateLow(currency, unit)
    val isPositive = quote.changePercent24h >= 0

    val rangeSpan = (high - low).coerceAtLeast(0.001)
    val currentPosition = ((price - low) / rangeSpan).coerceIn(0.0, 1.0).toFloat()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MetalTheme.colors.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(MetalGold.copy(alpha = 0.6f), MetalTheme.colors.border)
            ),
            width = 1.5.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Top Row: Title, Symbol & Favorite Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MetalGold.copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, MetalGold.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Au",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MetalGold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Gold",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MetalTheme.colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(MetalGold.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "FEATURED",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = MetalGold
                                )
                            }
                        }
                        Text(
                            text = "XAU • ${unit.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MetalTheme.colors.textSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Favorite gold",
                            tint = if (isFavorite) MetalGold else MetalTheme.colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Details",
                        tint = MetalTheme.colors.textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Middle Row: Price & 24h Change & Sparkline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = CurrencyFormatter.formatPrice(price, currency),
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp),
                        fontWeight = FontWeight.Black,
                        color = MetalTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.Default.NorthEast else Icons.Default.SouthEast,
                            contentDescription = null,
                            tint = if (isPositive) ChangePositive else ChangeNegative,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = CurrencyFormatter.formatChange(change, currency),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) ChangePositive else ChangeNegative
                        )
                        Text(
                            text = "(${CurrencyFormatter.formatPercent(quote.changePercent24h)})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) ChangePositive else ChangeNegative
                        )
                    }
                }

                // Sparkline
                Box(
                    modifier = Modifier
                        .size(width = 110.dp, height = 50.dp)
                        .padding(end = 4.dp)
                ) {
                    SparklineView(
                        data = quote.sparkline,
                        isPositive = isPositive,
                        showFill = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Range Progress: 24h Low to 24h High
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "24h Low: ${CurrencyFormatter.formatPrice(low, currency)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetalTheme.colors.textMuted
                    )
                    Text(
                        text = "24h High: ${CurrencyFormatter.formatPrice(high, currency)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetalTheme.colors.textMuted
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { currentPosition },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MetalGold,
                    trackColor = MetalTheme.colors.elevated
                )
            }
        }
    }
}
