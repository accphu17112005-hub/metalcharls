package org.metalscope.app.util

import java.util.Calendar
import java.util.TimeZone

object MarketSessionUtils {
    data class MarketSessionStatus(
        val isOpen: Boolean,
        val sessionTitle: String,
        val sessionDesc: String
    )

    fun getMarketSessionStatus(): MarketSessionStatus {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val isWeekend = dayOfWeek == Calendar.SATURDAY || (dayOfWeek == Calendar.SUNDAY && hour < 22)

        return if (isWeekend) {
            MarketSessionStatus(
                isOpen = false,
                sessionTitle = "Market Closed",
                sessionDesc = "Weekend trading pause. LBMA Friday close prices shown."
            )
        } else {
            val session = when (hour) {
                in 0..7 -> "Asian Session (Tokyo / Singapore)"
                in 8..12 -> "European Session (London LBMA Open)"
                in 13..16 -> "Peak Global Liquidity (London & NY COMEX Active)"
                in 17..21 -> "Americas Session (NY COMEX Active)"
                else -> "Pacific Pre-Market Transition"
            }
            MarketSessionStatus(
                isOpen = true,
                sessionTitle = "Markets Active",
                sessionDesc = session
            )
        }
    }
}
