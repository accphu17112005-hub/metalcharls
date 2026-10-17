import Foundation

enum CurrencyFormatter {
    
    static func formatPrice(
        amount: Double,
        currency: Currency,
        unit: WeightUnit? = nil,
        decimalOverride: Int? = nil
    ) -> String {
        let decimals: Int
        if let override = decimalOverride {
            decimals = override
        } else if currency == .vnd || currency == .jpy {
            decimals = 0
        } else if amount < 1.0 {
            decimals = 4
        } else {
            decimals = currency.standardDecimals
        }
        
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.minimumFractionDigits = decimals
        formatter.maximumFractionDigits = decimals
        formatter.locale = Locale(identifier: "en_US")
        
        let formattedNumber = formatter.string(from: NSNumber(value: amount)) ?? String(format: "%.\(decimals)f", amount)
        
        let priceWithSymbol: String
        switch currency {
        case .usd, .gbp, .jpy, .aud, .cad:
            priceWithSymbol = "\(currency.symbol)\(formattedNumber)"
        case .eur:
            priceWithSymbol = "\(formattedNumber) \(currency.symbol)"
        case .vnd:
            priceWithSymbol = "\(formattedNumber) \(currency.symbol)"
        }
        
        if let unit = unit {
            return "\(priceWithSymbol) / \(unit.symbol)"
        } else {
            return priceWithSymbol
        }
    }
    
    static func formatChange(
        amount: Double,
        currency: Currency,
        includeSign: Bool = true
    ) -> String {
        let decimals = (currency == .vnd || currency == .jpy) ? 0 : 2
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.minimumFractionDigits = decimals
        formatter.maximumFractionDigits = decimals
        formatter.locale = Locale(identifier: "en_US")
        
        let absVal = abs(amount)
        let formattedNumber = formatter.string(from: NSNumber(value: absVal)) ?? String(format: "%.\(decimals)f", absVal)
        
        let sign: String
        if amount > 0 && includeSign {
            sign = "+"
        } else if amount < 0 {
            sign = "-"
        } else {
            sign = ""
        }
        
        switch currency {
        case .usd, .gbp, .jpy, .aud, .cad:
            return "\(sign)\(currency.symbol)\(formattedNumber)"
        case .eur, .vnd:
            return "\(sign)\(formattedNumber) \(currency.symbol)"
        }
    }
    
    static func formatPercent(_ percent: Double, includeSign: Bool = true) -> String {
        let sign: String
        if percent > 0 && includeSign {
            sign = "+"
        } else if percent < 0 {
            sign = "-"
        } else {
            sign = ""
        }
        return String(format: "%@%.2f%%", sign, abs(percent))
    }
    
    static func formatRatio(_ ratio: Double) -> String {
        return String(format: "%.1fx", ratio)
    }
}
