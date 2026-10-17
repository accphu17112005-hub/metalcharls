import XCTest
@testable import MetalScope

final class MetalModelTests: XCTestCase {
    
    func testMetalTypeSymbols() {
        XCTAssertEqual(MetalType.from(symbol: "XAU"), .gold)
        XCTAssertEqual(MetalType.from(symbol: "xag"), .silver)
        XCTAssertEqual(MetalType.from(symbol: "XPT"), .platinum)
        XCTAssertEqual(MetalType.from(symbol: "XPD"), .palladium)
    }
    
    func testDemoDataProviderQuotes() {
        let quotes = DemoDataProvider.getQuotes()
        XCTAssertEqual(quotes.count, 4)
        
        let goldQuote = quotes.first { $0.symbol == "XAU" }
        XCTAssertNotNil(goldQuote)
        XCTAssertEqual(goldQuote?.name, "Gold")
        XCTAssertTrue((goldQuote?.priceUsdPerOz ?? 0) > 2000.0)
        XCTAssertFalse(goldQuote?.sparkline.isEmpty ?? true)
    }
    
    func testCurrencyConversions() {
        let quotes = DemoDataProvider.getQuotes()
        guard let goldQuote = quotes.first(where: { $0.symbol == "XAU" }) else {
            XCTFail("Gold quote should exist")
            return
        }
        
        let usdPrice = goldQuote.calculatePrice(currency: .usd, unit: .oz)
        let vndPrice = goldQuote.calculatePrice(currency: .vnd, unit: .oz)
        let expectedVnd = usdPrice * 25420.0
        XCTAssertEqual(vndPrice, expectedVnd, accuracy: 1.0)
    }
    
    func testHistoricalDataGeneration() {
        let records1M = DemoDataProvider.getHistoricalData(metal: .gold, timeRange: .oneMonth)
        XCTAssertEqual(records1M.count, TimeRange.oneMonth.pointCount)
        XCTAssertTrue(records1M.allSatisfy { $0.closeUsdPerOz > 0 })
    }
    
    func testMarketInsightsCalculation() {
        let insight = DemoDataProvider.getMarketInsights()
        XCTAssertTrue(insight.goldSilverRatio > 50.0 && insight.goldSilverRatio < 120.0)
        XCTAssertEqual(insight.weeklyBestPerformer, .silver)
    }
}
