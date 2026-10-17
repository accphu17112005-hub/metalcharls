package org.metalscope.app.data.model

enum class TimeRange(
    val label: String,
    val description: String,
    val pointCount: Int
) {
    ONE_DAY(label = "1D", description = "24 Hours (Intraday)", pointCount = 24),
    ONE_WEEK(label = "1W", description = "7 Days", pointCount = 28),
    ONE_MONTH(label = "1M", description = "30 Days", pointCount = 30),
    THREE_MONTHS(label = "3M", description = "90 Days (Quarterly)", pointCount = 36),
    SIX_MONTHS(label = "6M", description = "180 Days", pointCount = 40),
    ONE_YEAR(label = "1Y", description = "365 Days (Annual)", pointCount = 52),
    ALL(label = "ALL", description = "5-Year Macro History", pointCount = 60);

    companion object {
        fun fromLabel(label: String): TimeRange {
            return entries.firstOrNull { it.label.equals(label, ignoreCase = true) } ?: ONE_DAY
        }
    }
}
