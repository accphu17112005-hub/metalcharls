package org.metalscope.app.data.model

data class PriceQuote(
    val symbol: String,
    val name: String,
    val metalType: MetalType,
    val priceUsdPerOz: Double,
    val change24hUsd: Double,
    val changePercent24h: Double,
    val high24hUsd: Double,
    val low24hUsd: Double,
    val open24hUsd: Double,
    val bidUsd: Double,
    val askUsd: Double,
    val spreadUsd: Double,
    val volume24hUsd: Double,
    val timestamp: Long,
    val source: String,
    val marketType: String,
    val isMarketOpen: Boolean,
    val marketSession: String,
    val isStale: Boolean,
    val isDemoData: Boolean,
    val sparkline: List<Double>
) {
    fun calculatePrice(currency: Currency, unit: WeightUnit): Double {
        val convertedByUnit = unit.convertPriceFromOz(priceUsdPerOz)
        return currency.convertFromUsd(convertedByUnit)
    }

    fun calculateChange(currency: Currency, unit: WeightUnit): Double {
        val convertedByUnit = unit.convertPriceFromOz(change24hUsd)
        return currency.convertFromUsd(convertedByUnit)
    }

    fun calculateHigh(currency: Currency, unit: WeightUnit): Double {
        val convertedByUnit = unit.convertPriceFromOz(high24hUsd)
        return currency.convertFromUsd(convertedByUnit)
    }

    fun calculateLow(currency: Currency, unit: WeightUnit): Double {
        val convertedByUnit = unit.convertPriceFromOz(low24hUsd)
        return currency.convertFromUsd(convertedByUnit)
    }

    fun calculateBid(currency: Currency, unit: WeightUnit): Double {
        return currency.convertFromUsd(unit.convertPriceFromOz(bidUsd))
    }

    fun calculateAsk(currency: Currency, unit: WeightUnit): Double {
        return currency.convertFromUsd(unit.convertPriceFromOz(askUsd))
    }
}
