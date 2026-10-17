package org.metalscope.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.metalscope.app.data.model.Currency
import org.metalscope.app.data.model.WeightUnit
import org.metalscope.app.theme.ChangeNegative
import org.metalscope.app.theme.ChangePositive
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MarketStatusBadge(
    isOpen: Boolean,
    sessionText: String,
    lastUpdated: Long,
    currency: Currency,
    unit: WeightUnit,
    onCurrencyClick: () -> Unit,
    onUnitClick: () -> Unit,
    isDemo: Boolean = true,
    modifier: Modifier = Modifier
) {
    val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)
    val timeString = timeFormatter.format(Date(lastUpdated))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Market Status Dot + Label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(MetalTheme.colors.surface, RoundedCornerShape(12.dp))
                .border(1.dp, MetalTheme.colors.border, RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (isOpen) ChangePositive else ChangeNegative, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = sessionText,
                style = MaterialTheme.typography.labelSmall,
                color = MetalTheme.colors.textPrimary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "• $timeString",
                style = MaterialTheme.typography.labelSmall,
                color = MetalTheme.colors.textMuted
            )
        }

        // Quick Currency & Unit Switchers
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // Currency Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MetalTheme.colors.elevated)
                    .border(1.dp, MetalTheme.colors.border, RoundedCornerShape(8.dp))
                    .clickable { onCurrencyClick() }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "${currency.code} (${currency.symbol})",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MetalGold
                )
            }

            // Unit Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MetalTheme.colors.elevated)
                    .border(1.dp, MetalTheme.colors.border, RoundedCornerShape(8.dp))
                    .clickable { onUnitClick() }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = unit.symbol,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MetalTheme.colors.textPrimary
                )
            }
        }
    }
}
