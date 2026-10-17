import SwiftUI

enum MetalType: String, CaseIterable, Identifiable, Codable {
    case gold = "GOLD"
    case silver = "SILVER"
    case platinum = "PLATINUM"
    case palladium = "PALLADIUM"
    
    var id: String { rawValue }
    
    var symbol: String {
        switch self {
        case .gold: return "XAU"
        case .silver: return "XAG"
        case .platinum: return "XPT"
        case .palladium: return "XPD"
        }
    }
    
    var displayName: String {
        switch self {
        case .gold: return "Gold"
        case .silver: return "Silver"
        case .platinum: return "Platinum"
        case .palladium: return "Palladium"
        }
    }
    
    var chemicalSymbol: String {
        switch self {
        case .gold: return "Au"
        case .silver: return "Ag"
        case .platinum: return "Pt"
        case .palladium: return "Pd"
        }
    }
    
    var atomicNumber: Int {
        switch self {
        case .gold: return 79
        case .silver: return 47
        case .platinum: return 78
        case .palladium: return 46
        }
    }
    
    var defaultColor: Color {
        switch self {
        case .gold: return .metalGold
        case .silver: return .metalSilver
        case .platinum: return .metalPlatinum
        case .palladium: return .metalPalladium
        }
    }
    
    var description: String {
        switch self {
        case .gold:
            return "Global monetary anchor, premier inflation hedge, and trusted central bank reserve asset."
        case .silver:
            return "Dual-demand precious metal with critical photovoltaic, electronic, and monetary store value."
        case .platinum:
            return "Ultra-dense industrial and luxury catalyst with significant green hydrogen application."
        case .palladium:
            return "Rare automotive emissions catalyst and semiconductor component."
        }
    }
    
    var standardPurity: String {
        switch self {
        case .gold: return ".9999 Fine (24K)"
        case .silver: return ".999 Fine"
        case .platinum: return ".9995 Fine"
        case .palladium: return ".9995 Fine"
        }
    }
    
    static func from(symbol: String) -> MetalType {
        let clean = symbol.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        return MetalType.allCases.first { $0.symbol == clean || $0.rawValue == clean } ?? .gold
    }
}
