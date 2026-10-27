package org.metalscope.app.data.model

data class ComexVault(
    val vaultName: String,
    val location: String,
    val totalOunces: Double,
    val registeredOunces: Double,
    val eligibleOunces: Double,
    val sharePercent: Double
)

data class ComexDeliveryNotice(
    val dateStr: String,
    val contractMonth: String,
    val contractsIssued: Int,
    val contractsStopped: Int,
    val netOuncesDelivered: Double,
    val cumulativeMonthOunces: Double
)

data class ComexHistoricalPoint(
    val timestamp: Long,
    val dateStr: String,
    val registeredOunces: Double,
    val eligibleOunces: Double,
    val spotPriceUsd: Double
)

data class ComexInventoryData(
    val metalType: MetalType,
    val priceUsdPerOz: Double,
    val changePercent24h: Double,
    val totalVaultOunces: Double,
    val registeredOunces: Double,
    val eligibleOunces: Double,
    val dailyVaultNetChangeOunces: Double,
    val drawdownAthPercent: Double,
    val change30dOunces: Double,
    val peakRegisteredOunces: Double,
    val vaults: List<ComexVault>,
    val recentDeliveries: List<ComexDeliveryNotice>,
    val history: List<ComexHistoricalPoint>
)
