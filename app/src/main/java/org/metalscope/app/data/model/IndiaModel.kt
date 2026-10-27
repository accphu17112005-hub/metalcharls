package org.metalscope.app.data.model

data class IndiaComparisonPoint(
    val timestamp: Long,
    val dateStr: String,
    val indiaPriceUsd: Double,
    val westernPriceUsd: Double,
    val premiumPercent: Double
)

data class IndiaTaxBreakdownItem(
    val label: String,
    val costFormula: String,
    val valueUsd: Double,
    val isTotal: Boolean = false
)

data class IndiaMarketData(
    val metalType: MetalType,
    val indiaPriceUsdPerOz: Double,
    val westernPriceUsdPerOz: Double,
    val premiumUsdPerOz: Double,
    val premiumPercent: Double,
    val mcxPriceInr: Double,
    val inrQuoteUnit: String,
    val usdInrRate: Double,
    val importDutyPercent: Double,
    val gstPercent: Double,
    val totalTaxFloorPercent: Double,
    val taxBreakdown: List<IndiaTaxBreakdownItem>,
    val comparisonHistory: List<IndiaComparisonPoint>
)
