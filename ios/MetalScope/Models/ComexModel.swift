import Foundation

struct ComexVault: Identifiable {
    let id = UUID()
    let vaultName: String
    let location: String
    let totalOunces: Double
    let registeredOunces: Double
    let eligibleOunces: Double
    let sharePercent: Double
}

struct ComexDeliveryNotice: Identifiable {
    let id = UUID()
    let dateStr: String
    let contractMonth: String
    let contractsIssued: Int
    let contractsStopped: Int
    let netOuncesDelivered: Double
    let cumulativeMonthOunces: Double
}

struct ComexHistoricalPoint: Identifiable {
    let id = UUID()
    let timestamp: Int64
    let dateStr: String
    let registeredOunces: Double
    let eligibleOunces: Double
    let spotPriceUsd: Double
}

struct ComexInventoryData {
    let metalType: MetalType
    let priceUsdPerOz: Double
    let changePercent24h: Double
    let totalVaultOunces: Double
    let registeredOunces: Double
    let eligibleOunces: Double
    let dailyVaultNetChangeOunces: Double
    let drawdownAthPercent: Double
    let change30dOunces: Double
    let peakRegisteredOunces: Double
    let vaults: [ComexVault]
    let recentDeliveries: [ComexDeliveryNotice]
    let history: [ComexHistoricalPoint]
}
