package org.metalscope.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import org.metalscope.app.theme.ChangePositive
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalSilver
import org.metalscope.app.theme.MetalTheme

enum class MarketTab(
    val title: String,
    val iconEmoji: String,
    val route: String,
    val badge: String? = null,
    val badgeColor: Color? = null
) {
    HOME("Home", "🏠", "tab_home"),
    GOLD("Gold (XAU)", "🟡", "metal_detail/XAU", "$4,016", MetalGold),
    SILVER("Silver (XAG)", "⚪", "metal_detail/XAG", "$53.88", MetalSilver),
    SHANGHAI("Shanghai", "🇨🇳", "tab_shanghai", "+12.5%", ChangePositive),
    COMEX("COMEX Silver", "🏛️", "tab_comex", "337.5M oz", Color(0xFF60A5FA)),
    INDIA("India", "🇮🇳", "tab_india", "+18.0%", ChangePositive)
}

@Composable
fun MarketTabBar(
    selectedTab: MarketTab,
    onTabSelected: (MarketTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MarketTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            val activeColor = when (tab) {
                MarketTab.GOLD -> MetalGold
                MarketTab.SILVER -> MetalSilver
                MarketTab.SHANGHAI -> Color(0xFFF87171)
                MarketTab.COMEX -> Color(0xFF60A5FA)
                MarketTab.INDIA -> Color(0xFFFBBF24)
                MarketTab.HOME -> MetalGold
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) activeColor.copy(alpha = 0.18f) else MetalTheme.colors.elevated
                    )
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) activeColor else MetalTheme.colors.border,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = tab.iconEmoji,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MetalTheme.colors.textPrimary else MetalTheme.colors.textSecondary
                    )

                    tab.badge?.let { badgeText ->
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    (tab.badgeColor ?: MetalGold).copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = tab.badgeColor ?: MetalGold
                            )
                        }
                    }
                }
            }
        }
    }
}
