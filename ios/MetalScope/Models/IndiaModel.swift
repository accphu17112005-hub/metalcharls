import Foundation

struct IndiaComparisonPoint: Identifiable {
    let id = UUID()
    let timestamp: Int64
    let dateStr: String
    let indiaPriceUsd: Double
    let westernPriceUsd: Double
    let premiumPercent: Double
}

struct IndiaTaxBreakdownItem: Identifiable {
    let id = UUID()
    let label: String
    let costFormula: String
    let valueUsd: Double
    let isTotal: Bool
}

struct IndiaMarketData {
    let metalType: MetalType
    let indiaPriceUsdPerOz: Double
    let westernPriceUsdPerOz: Double
    let premiumUsdPerOz: Double
    let premiumPercent: Double
    let mcxPriceInr: Double
    let inrQuoteUnit: String
    let usdInrRate: Double
    let importDutyPercent: Double
    let gstPercent: Double
    let totalTaxFloorPercent: Double
    let taxBreakdown: [IndiaTaxBreakdownItem]
    let comparisonHistory: [IndiaComparisonPoint]
}
