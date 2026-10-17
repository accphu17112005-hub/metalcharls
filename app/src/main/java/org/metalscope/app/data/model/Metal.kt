package org.metalscope.app.data.model

import androidx.compose.ui.graphics.Color
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalPalladium
import org.metalscope.app.theme.MetalPlatinum
import org.metalscope.app.theme.MetalSilver

enum class MetalType(
    val symbol: String,
    val displayName: String,
    val chemicalSymbol: String,
    val atomicNumber: Int,
    val defaultColor: Color,
    val description: String,
    val standardPurity: String
) {
    GOLD(
        symbol = "XAU",
        displayName = "Gold",
        chemicalSymbol = "Au",
        atomicNumber = 79,
        defaultColor = MetalGold,
        description = "Global monetary anchor, premier inflation hedge, and trusted central bank reserve asset.",
        standardPurity = ".9999 Fine (24K)"
    ),
    SILVER(
        symbol = "XAG",
        displayName = "Silver",
        chemicalSymbol = "Ag",
        atomicNumber = 47,
        defaultColor = MetalSilver,
        description = "Dual-demand precious metal with critical photovoltaic, electronic, and monetary store value.",
        standardPurity = ".999 Fine"
    ),
    PLATINUM(
        symbol = "XPT",
        displayName = "Platinum",
        chemicalSymbol = "Pt",
        atomicNumber = 78,
        defaultColor = MetalPlatinum,
        description = "Ultra-dense industrial and luxury catalyst with significant green hydrogen application.",
        standardPurity = ".9995 Fine"
    ),
    PALLADIUM(
        symbol = "XPD",
        displayName = "Palladium",
        chemicalSymbol = "Pd",
        atomicNumber = 46,
        defaultColor = MetalPalladium,
        description = "Rare automotive emissions catalyst and semiconductor component.",
        standardPurity = ".9995 Fine"
    );

    companion object {
        fun fromSymbol(symbol: String): MetalType {
            return entries.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) } ?: GOLD
        }
    }
}
