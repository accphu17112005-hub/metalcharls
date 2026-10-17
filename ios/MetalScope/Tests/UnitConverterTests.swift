import XCTest
@testable import MetalScope

final class UnitConverterTests: XCTestCase {
    
    func testTroyOunceToGramsConversion() {
        let gramsInOz = UnitConverter.convertWeight(amount: 1.0, from: .oz, to: .gram)
        XCTAssertEqual(gramsInOz, 31.1034768, accuracy: 0.0001)
    }
    
    func testKilogramToTroyOuncesConversion() {
        let ozInKg = UnitConverter.convertWeight(amount: 1.0, from: .kg, to: .oz)
        XCTAssertEqual(ozInKg, 32.1507466, accuracy: 0.001)
    }
    
    func testGoldPricePerGramCalculation() {
        let goldPricePerOz = 2658.40
        let pricePerGram = WeightUnit.gram.convertPriceFromOz(goldPricePerOz)
        let expected = goldPricePerOz / 31.1034768
        XCTAssertEqual(pricePerGram, expected, accuracy: 0.01)
    }
    
    func testGoldPricePerKgCalculation() {
        let goldPricePerOz = 2658.40
        let pricePerKg = WeightUnit.kg.convertPriceFromOz(goldPricePerOz)
        let expected = (goldPricePerOz / 31.1034768) * 1000.0
        XCTAssertEqual(pricePerKg, expected, accuracy: 0.01)
    }
    
    func testIdentityConversion() {
        let price = 1500.0
        let converted = UnitConverter.convertPrice(pricePerUnit: price, from: .oz, to: .oz)
        XCTAssertEqual(converted, price, accuracy: 0.0001)
    }
}
