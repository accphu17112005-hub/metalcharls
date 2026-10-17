package org.metalscope.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.metalscope.app.data.model.MarketInsight
import org.metalscope.app.theme.ChangePositive
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalSilver
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.util.CurrencyFormatter

@Composable
fun QuickInsightCard(
    insight: MarketInsight,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
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
            // Gold / Silver Ratio Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(MetalGold.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "÷",
                            fontWeight = FontWeight.Bold,
                            color = MetalGold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Gold / Silver Ratio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MetalTheme.colors.textPrimary
                        )
                        Text(
                            text = "Ounces of silver to purchase 1 oz of gold",
                            style = MaterialTheme.typography.labelSmall,
                            color = MetalTheme.colors.textSecondary
                        )
                    }
                }

                Text(
                    text = CurrencyFormatter.formatRatio(insight.goldSilverRatio),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MetalGold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Ratio progress bar vs 50-100 historical range
            val ratioFraction = ((insight.goldSilverRatio - 50.0) / 50.0).coerceIn(0.0, 1.0).toFloat()
            LinearProgressIndicator(
                progress = { ratioFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MetalGold,
                trackColor = MetalTheme.colors.elevated
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "50x (Silver Outperforming)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MetalTheme.colors.textMuted
                )
                Text(
                    text = "100x (Gold Outperforming)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MetalTheme.colors.textMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2 Stat Highlights: Gold/Platinum & Weekly Leader
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Gold / Platinum
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MetalTheme.colors.elevated, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Au / Pt Ratio",
                            style = MaterialTheme.typography.labelSmall,
                            color = MetalTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = CurrencyFormatter.formatRatio(insight.goldPlatinumRatio),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MetalTheme.colors.textPrimary
                        )
                    }
                }

                // Weekly Leader
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MetalTheme.colors.elevated, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "7D Leader",
                            style = MaterialTheme.typography.labelSmall,
                            color = MetalTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${insight.weeklyBestPerformer.displayName} (+${insight.weeklyBestPerformancePercent}%)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ChangePositive
                        )
                    }
                }
            }
        }
    }
}
