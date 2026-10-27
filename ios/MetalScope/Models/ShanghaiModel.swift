import Foundation

struct ShanghaiComparisonPoint: Identifiable {
    let id = UUID()
    let timestamp: Int64
    let dateStr: String
    let shanghaiPriceUsd: Double
    let westernPriceUsd: Double
    let premiumPercent: Double
}

struct ShanghaiMarketData {
    let metalType: MetalType
    let shanghaiPriceUsdPerOz: Double
    let westernPriceUsdPerOz: Double
    let premiumUsdPerOz: Double
    let premiumPercent: Double
    let sgePriceCnyPerGram: Double
    let usdCnyRate: Double
    let shfeInventoryTonnes: Double
    let shfeInventory30dChangeTonnes: Double
    let comparisonHistory: [ShanghaiComparisonPoint]
}
