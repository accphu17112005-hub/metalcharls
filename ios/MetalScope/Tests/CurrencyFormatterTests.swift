import XCTest
@testable import MetalScope

final class CurrencyFormatterTests: XCTestCase {
    
    func testUsdFormatting() {
        let formatted = CurrencyFormatter.formatPrice(amount: 2658.40, currency: .usd)
        XCTAssertEqual(formatted, "$2,658.40")
    }
    
    func testPriceWithUnit() {
        let formatted = CurrencyFormatter.formatPrice(amount: 2658.40, currency: .usd, unit: .oz)
        XCTAssertEqual(formatted, "$2,658.40 / oz")
    }
    
    func testPositiveChangeFormatting() {
        let changeStr = CurrencyFormatter.formatChange(amount: 18.60, currency: .usd)
        XCTAssertEqual(changeStr, "+$18.60")
    }
    
    func testNegativeChangeFormatting() {
        let changeStr = CurrencyFormatter.formatChange(amount: -6.40, currency: .usd)
        XCTAssertEqual(changeStr, "-$6.40")
    }
    
    func testPercentFormatting() {
        let positivePct = CurrencyFormatter.formatPercent(1.53)
        XCTAssertEqual(positivePct, "+1.53%")
        
        let negativePct = CurrencyFormatter.formatPercent(-0.64)
        XCTAssertEqual(negativePct, "-0.64%")
    }
    
    func testRatioFormatting() {
        let ratioStr = CurrencyFormatter.formatRatio(83.468)
        XCTAssertEqual(ratioStr, "83.5x")
    }
}
