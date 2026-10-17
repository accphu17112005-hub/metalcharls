package org.metalscope.app.data.model

data class HistoricalRecord(
    val timestamp: Long,
    val dateString: String,
    val closeUsdPerOz: Double,
    val openUsdPerOz: Double,
    val highUsdPerOz: Double,
    val lowUsdPerOz: Double,
    val changePercent: Double,
    val volume: Double
) {
    fun calculateClose(currency: Currency, unit: WeightUnit): Double {
        return currency.convertFromUsd(unit.convertPriceFromOz(closeUsdPerOz))
    }

    fun calculateOpen(currency: Currency, unit: WeightUnit): Double {
        return currency.convertFromUsd(unit.convertPriceFromOz(openUsdPerOz))
    }

    fun calculateHigh(currency: Currency, unit: WeightUnit): Double {
        return currency.convertFromUsd(unit.convertPriceFromOz(highUsdPerOz))
    }

    fun calculateLow(currency: Currency, unit: WeightUnit): Double {
        return currency.convertFromUsd(unit.convertPriceFromOz(lowUsdPerOz))
    }
}

data class ChartPoint(
    val timestamp: Long,
    val value: Double,
    val label: String
)
