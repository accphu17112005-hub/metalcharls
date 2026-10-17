import Foundation

enum UnitConverter {
    static let troyOunceInGrams: Double = 31.1034768
    static let kgInTroyOunces: Double = 32.1507465686
    
    static func convertPrice(pricePerUnit: Double, from fromUnit: WeightUnit, to toUnit: WeightUnit) -> Double {
        if fromUnit == toUnit { return pricePerUnit }
        
        let pricePerOz: Double
        switch fromUnit {
        case .oz:
            pricePerOz = pricePerUnit
        case .gram:
            pricePerOz = pricePerUnit * troyOunceInGrams
        case .kg:
            pricePerOz = (pricePerUnit / 1000.0) * troyOunceInGrams
        }
        return toUnit.convertPriceFromOz(pricePerOz)
    }
    
    static func convertWeight(amount: Double, from fromUnit: WeightUnit, to toUnit: WeightUnit) -> Double {
        if fromUnit == toUnit { return amount }
        
        let grams: Double
        switch fromUnit {
        case .oz:
            grams = amount * troyOunceInGrams
        case .gram:
            grams = amount
        case .kg:
            grams = amount * 1000.0
        }
        
        switch toUnit {
        case .oz:
            return grams / troyOunceInGrams
        case .gram:
            return grams
        case .kg:
            return grams / 1000.0
        }
    }
}
