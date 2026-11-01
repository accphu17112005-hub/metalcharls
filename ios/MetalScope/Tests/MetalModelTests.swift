import XCTest
@testable import MetalScope

final class MetalModelTests: XCTestCase {
    
    func testMetalTypeSymbols() {
        XCTAssertEqual(MetalType.from(symbol: "XAU"), .gold)
        XCTAssertEqual(MetalType.from(symbol: "xag"), .silver)
        XCTAssertEqual(MetalType.from(symbol: "XPT"), .platinum)
        XCTAssertEqual(MetalType.from(symbol: "XPD"), .palladium)
    }
    
    func testPriceQuoteCalculations() {
        let goldQuote = PriceQuote(
            symbol: "XAU",
            name: "Gold",
            metalType: .gold,
            priceUsdPerOz: 3000.0,
            change24hUsd: 15.0,
            changePercent24h: 0.5,
            high24hUsd: 3010.0,
            low24hUsd: 2990.0,
            open24hUsd: 2985.0,
            bidUsd: 2999.0,
            askUsd: 3001.0,
            spreadUsd: 2.0,
            volume24hUsd: 10000.0,
            timestamp: Int64(Date().timeIntervalSince1970 * 1000.0),
            source: "MetalCharts Live",
            marketType: "Spot",
            isMarketOpen: true,
            marketSession: "European Session",
            isStale: false,
            isDemoData: false,
            sparkline: [2990.0, 2995.0, 3000.0]
        )
        
        let usdPrice = goldQuote.calculatePrice(currency: .usd, unit: .oz)
        let vndPrice = goldQuote.calculatePrice(currency: .vnd, unit: .oz)
        let expectedVnd = usdPrice * 25420.0
        XCTAssertEqual(vndPrice, expectedVnd, accuracy: 1.0)
        
        let gramPrice = goldQuote.calculatePrice(currency: .usd, unit: .gram)
        let expectedGram = usdPrice / 31.1034768
        XCTAssertEqual(gramPrice, expectedGram, accuracy: 0.01)
    }
    
    func testMarketSessionStatus() {
        let session = MarketSessionUtils.getMarketSessionStatus()
        XCTAssertFalse(session.sessionTitle.isEmpty)
        XCTAssertFalse(session.sessionDesc.isEmpty)
    }
}
