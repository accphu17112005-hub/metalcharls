import Foundation

struct PriceQuote: Identifiable, Codable, Equatable {
    var id: String { symbol }
    let symbol: String
    let name: String
    let metalType: MetalType
    let priceUsdPerOz: Double
    let change24hUsd: Double
    let changePercent24h: Double
    let high24hUsd: Double
    let low24hUsd: Double
    let open24hUsd: Double
    let bidUsd: Double
    let askUsd: Double
    let spreadUsd: Double
    let volume24hUsd: Double
    let timestamp: Int64
    let source: String
    let marketType: String
    let isMarketOpen: Boolean
    let marketSession: String
    let isStale: Boolean
    let isDemoData: Boolean
    let sparkline: [Double]
    
    typealias Boolean = Bool
    
    func calculatePrice(currency: Currency, unit: WeightUnit) -> Double {
        let convertedByUnit = unit.convertPriceFromOz(priceUsdPerOz)
        return currency.convertFromUsd(amountUsd: convertedByUnit)
    }
    
    func calculateChange(currency: Currency, unit: WeightUnit) -> Double {
        let convertedByUnit = unit.convertPriceFromOz(change24hUsd)
        return currency.convertFromUsd(amountUsd: convertedByUnit)
    }
    
    func calculateHigh(currency: Currency, unit: WeightUnit) -> Double {
        let convertedByUnit = unit.convertPriceFromOz(high24hUsd)
        return currency.convertFromUsd(amountUsd: convertedByUnit)
    }
    
    func calculateLow(currency: Currency, unit: WeightUnit) -> Double {
        let convertedByUnit = unit.convertPriceFromOz(low24hUsd)
        return currency.convertFromUsd(amountUsd: convertedByUnit)
    }
    
    func calculateBid(currency: Currency, unit: WeightUnit) -> Double {
        return currency.convertFromUsd(amountUsd: unit.convertPriceFromOz(bidUsd))
    }
    
    func calculateAsk(currency: Currency, unit: WeightUnit) -> Double {
        return currency.convertFromUsd(amountUsd: unit.convertPriceFromOz(askUsd))
    }
}
