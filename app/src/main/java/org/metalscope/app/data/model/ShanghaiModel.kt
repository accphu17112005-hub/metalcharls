package org.metalscope.app.data.model

data class ShanghaiComparisonPoint(
    val timestamp: Long,
    val dateStr: String,
    val shanghaiPriceUsd: Double,
    val westernPriceUsd: Double,
    val premiumPercent: Double
)

data class ShanghaiMarketData(
    val metalType: MetalType,
    val shanghaiPriceUsdPerOz: Double,
    val westernPriceUsdPerOz: Double,
    val premiumUsdPerOz: Double,
    val premiumPercent: Double,
    val sgePriceCnyPerGram: Double,
    val usdCnyRate: Double,
    val shfeInventoryTonnes: Double,
    val shfeInventory30dChangeTonnes: Double,
    val comparisonHistory: List<ShanghaiComparisonPoint>
)
