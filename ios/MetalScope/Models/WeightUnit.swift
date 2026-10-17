import Foundation

enum WeightUnit: String, CaseIterable, Identifiable, Codable {
    case oz = "OZ"
    case gram = "G"
    case kg = "KG"
    
    var id: String { rawValue }
    
    var code: String { rawValue }
    
    var displayName: String {
        switch self {
        case .oz: return "Troy Ounce"
        case .gram: return "Gram"
        case .kg: return "Kilogram"
        }
    }
    
    var symbol: String {
        switch self {
        case .oz: return "oz"
        case .gram: return "g"
        case .kg: return "kg"
        }
    }
    
    var gramsFactor: Double {
        switch self {
        case .oz: return Self.troyOunceToGrams
        case .gram: return 1.0
        case .kg: return 1000.0
        }
    }
    
    static let troyOunceToGrams: Double = 31.1034768
    static let kgToTroyOunces: Double = 32.1507465686
    
    func convertPriceFromOz(_ pricePerOz: Double) -> Double {
        switch self {
        case .oz:
            return pricePerOz
        case .gram:
            return pricePerOz / Self.troyOunceToGrams
        case .kg:
            return (pricePerOz / Self.troyOunceToGrams) * 1000.0
        }
    }
    
    static func from(code: String) -> WeightUnit {
        let clean = code.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        return WeightUnit.allCases.first { $0.code == clean } ?? .oz
    }
}
