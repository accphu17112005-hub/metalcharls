import Foundation

enum Currency: String, CaseIterable, Identifiable, Codable {
    case usd = "USD"
    case eur = "EUR"
    case gbp = "GBP"
    case jpy = "JPY"
    case aud = "AUD"
    case cad = "CAD"
    case vnd = "VND"
    
    var id: String { rawValue }
    
    var code: String { rawValue }
    
    var symbol: String {
        switch self {
        case .usd: return "$"
        case .eur: return "€"
        case .gbp: return "£"
        case .jpy: return "¥"
        case .aud: return "A$"
        case .cad: return "C$"
        case .vnd: return "₫"
        }
    }
    
    var displayName: String {
        switch self {
        case .usd: return "US Dollar"
        case .eur: return "Euro"
        case .gbp: return "British Pound"
        case .jpy: return "Japanese Yen"
        case .aud: return "Australian Dollar"
        case .cad: return "Canadian Dollar"
        case .vnd: return "Vietnamese Dong"
        }
    }
    
    var rateFromUsd: Double {
        switch self {
        case .usd: return 1.0
        case .eur: return 0.925
        case .gbp: return 0.788
        case .jpy: return 153.2
        case .aud: return 1.545
        case .cad: return 1.385
        case .vnd: return 25420.0
        }
    }
    
    var standardDecimals: Int {
        switch self {
        case .jpy, .vnd: return 0
        default: return 2
        }
    }
    
    func convertFromUsd(amountUsd: Double) -> Double {
        return amountUsd * rateFromUsd
    }
    
    static func from(code: String) -> Currency {
        let clean = code.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        return Currency.allCases.first { $0.code == clean } ?? .usd
    }
}
