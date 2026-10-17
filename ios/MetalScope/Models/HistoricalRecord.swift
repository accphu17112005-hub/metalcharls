import Foundation

struct HistoricalRecord: Identifiable, Codable, Equatable {
    var id: String { "\(timestamp)_\(closeUsdPerOz)" }
    let timestamp: Int64
    let dateString: String
    let closeUsdPerOz: Double
    let openUsdPerOz: Double
    let highUsdPerOz: Double
    let lowUsdPerOz: Double
    let changePercent: Double
    let volume: Double
    
    var date: Date {
        Date(timeIntervalSince1970: TimeInterval(timestamp) / 1000.0)
    }
    
    func calculateClose(currency: Currency, unit: WeightUnit) -> Double {
        return currency.convertFromUsd(amountUsd: unit.convertPriceFromOz(closeUsdPerOz))
    }
    
    func calculateOpen(currency: Currency, unit: WeightUnit) -> Double {
        return currency.convertFromUsd(amountUsd: unit.convertPriceFromOz(openUsdPerOz))
    }
    
    func calculateHigh(currency: Currency, unit: WeightUnit) -> Double {
        return currency.convertFromUsd(amountUsd: unit.convertPriceFromOz(highUsdPerOz))
    }
    
    func calculateLow(currency: Currency, unit: WeightUnit) -> Double {
        return currency.convertFromUsd(amountUsd: unit.convertPriceFromOz(lowUsdPerOz))
    }
}

struct ChartPoint: Identifiable, Equatable {
    var id: Int64 { timestamp }
    let timestamp: Int64
    let value: Double
    let label: String
}
