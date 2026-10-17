import Foundation

enum DemoDataProvider {
    
    private static var basePrices: [MetalType: Double] = [
        .gold: 2658.40,
        .silver: 31.85,
        .platinum: 988.20,
        .palladium: 1032.50
    ]
    
    private static let baseChanges: [MetalType: (change: Double, percent: Double)] = [
        .gold: (18.60, 0.70),
        .silver: (0.48, 1.53),
        .platinum: (-6.40, -0.64),
        .palladium: (14.20, 1.39)
    ]
    
    static func getMarketSessionStatus() -> (isOpen: Bool, sessionTitle: String, sessionDesc: String) {
        var calendar = Calendar(identifier: .gregorian)
        calendar.timeZone = TimeZone(identifier: "UTC") ?? .current
        let now = Date()
        let dayOfWeek = calendar.component(.weekday, from: now) // 1: Sunday, 7: Saturday
        let hour = calendar.component(.hour, from: now)
        
        let isWeekend = dayOfWeek == 7 || (dayOfWeek == 1 && hour < 22)
        
        if isWeekend {
            return (
                isOpen: false,
                sessionTitle: "Market Closed",
                sessionDesc: "Weekend trading pause. LBMA Friday close prices shown."
            )
        } else {
            let session: String
            switch hour {
            case 0...7:
                session = "Asian Session (Tokyo / Singapore)"
            case 8...12:
                session = "European Session (London LBMA Open)"
            case 13...16:
                session = "Peak Global Liquidity (London & NY COMEX Active)"
            case 17...21:
                session = "Americas Session (NY COMEX Active)"
            default:
                session = "Pacific Pre-Market Transition"
            }
            return (isOpen: true, sessionTitle: "Markets Active", sessionDesc: session)
        }
    }
    
    static func getQuotes(simulateTick: Bool = false) -> [PriceQuote] {
        let session = getMarketSessionStatus()
        let nowMs = Int64(Date().timeIntervalSince1970 * 1000.0)
        
        return MetalType.allCases.map { metal in
            var currentPrice = basePrices[metal] ?? 1000.0
            if simulateTick {
                let delta = (Double.random(in: 0...1) - 0.48) * (currentPrice * 0.001)
                currentPrice += delta
                basePrices[metal] = currentPrice
            }
            
            let baseChange = baseChanges[metal]?.change ?? 0.0
            let baseChangePct = baseChanges[metal]?.percent ?? 0.0
            
            let spreadFactor: Double
            switch metal {
            case .gold: spreadFactor = 0.0003
            case .silver: spreadFactor = 0.0006
            case .platinum: spreadFactor = 0.0015
            case .palladium: spreadFactor = 0.0025
            }
            
            let spread = currentPrice * spreadFactor
            let bid = currentPrice - (spread / 2.0)
            let ask = currentPrice + (spread / 2.0)
            let high24h = currentPrice * 1.008
            let low24h = currentPrice * 0.993
            let open24h = currentPrice - baseChange
            let sparkline = generateSparkline(metal: metal, currentPrice: currentPrice)
            
            let volume: Double
            switch metal {
            case .gold: volume = 4_285_000_000.0
            case .silver: volume = 1_260_000_000.0
            case .platinum: volume = 380_000_000.0
            case .palladium: volume = 215_000_000.0
            }
            
            return PriceQuote(
                symbol: metal.symbol,
                name: metal.displayName,
                metalType: metal,
                priceUsdPerOz: currentPrice,
                change24hUsd: baseChange,
                changePercent24h: baseChangePct,
                high24hUsd: high24h,
                low24hUsd: low24h,
                open24hUsd: open24h,
                bidUsd: bid,
                askUsd: ask,
                spreadUsd: spread,
                volume24hUsd: volume,
                timestamp: nowMs,
                source: "LBMA / COMEX Composite Feed",
                marketType: "Spot Benchmark",
                isMarketOpen: session.isOpen,
                marketSession: session.sessionTitle,
                isStale: false,
                isDemoData: true,
                sparkline: sparkline
            )
        }
    }
    
    static func generateSparkline(metal: MetalType, currentPrice: Double) -> [Double] {
        var points: [Double] = []
        let count = 14
        var p = currentPrice * 0.985
        points.append(p)
        
        for _ in 1..<(count - 1) {
            let drift = (Double.random(in: 0...1) - 0.45) * (currentPrice * 0.004)
            p += drift
            points.append(p)
        }
        points.append(currentPrice)
        return points
    }
    
    static func getHistoricalData(metal: MetalType, timeRange: TimeRange) -> [HistoricalRecord] {
        let currentPrice = basePrices[metal] ?? 1000.0
        var records: [HistoricalRecord] = []
        let pointCount = timeRange.pointCount
        let now = Date()
        let nowMs = Int64(now.timeIntervalSince1970 * 1000.0)
        
        let intervalSeconds: TimeInterval
        switch timeRange {
        case .oneDay: intervalSeconds = 3600 // 1 hour
        case .oneWeek: intervalSeconds = 6 * 3600
        case .oneMonth: intervalSeconds = 24 * 3600
        case .threeMonths: intervalSeconds = 2 * 24 * 3600
        case .sixMonths: intervalSeconds = 4 * 24 * 3600
        case .oneYear: intervalSeconds = 7 * 24 * 3600
        case .all: intervalSeconds = 30 * 24 * 3600
        }
        
        let dateFormatter = DateFormatter()
        dateFormatter.locale = Locale(identifier: "en_US")
        switch timeRange {
        case .oneDay: dateFormatter.dateFormat = "HH:mm"
        case .oneWeek: dateFormatter.dateFormat = "EEE, d MMM"
        case .oneMonth, .threeMonths: dateFormatter.dateFormat = "MMM d"
        case .sixMonths, .oneYear: dateFormatter.dateFormat = "MMM yyyy"
        case .all: dateFormatter.dateFormat = "yyyy"
        }
        
        let trendMultiplier: Double
        switch timeRange {
        case .oneDay: trendMultiplier = 0.006
        case .oneWeek: trendMultiplier = 0.02
        case .oneMonth: trendMultiplier = 0.05
        case .threeMonths: trendMultiplier = 0.09
        case .sixMonths: trendMultiplier = 0.16
        case .oneYear: trendMultiplier = 0.28
        case .all: trendMultiplier = 0.55
        }
        
        let initialPrice = currentPrice * (1.0 - trendMultiplier + Double.random(in: 0...0.04))
        var runningPrice = initialPrice
        
        for i in 0..<pointCount {
            let offsetSec = Double(pointCount - 1 - i) * intervalSeconds
            let date = now.addingTimeInterval(-offsetSec)
            let ts = Int64(date.timeIntervalSince1970 * 1000.0)
            
            let stepFraction = Double(i) / Double(max(1, pointCount - 1))
            let targetPrice = initialPrice + (currentPrice - initialPrice) * stepFraction
            let noise = (Double.random(in: 0...1) - 0.49) * (currentPrice * 0.015)
            runningPrice = (i == pointCount - 1) ? currentPrice : (targetPrice + noise)
            
            let open = runningPrice * (1.0 - (Double.random(in: 0...1) - 0.5) * 0.005)
            let high = max(open, runningPrice) * (1.0 + Double.random(in: 0...0.004))
            let low = min(open, runningPrice) * (1.0 - Double.random(in: 0...0.004))
            let chgPct = (open > 0) ? ((runningPrice - open) / open) * 100.0 : 0.0
            
            records.append(
                HistoricalRecord(
                    timestamp: ts,
                    dateString: dateFormatter.string(from: date),
                    closeUsdPerOz: runningPrice,
                    openUsdPerOz: open,
                    highUsdPerOz: high,
                    lowUsdPerOz: low,
                    changePercent: chgPct,
                    volume: (Double.random(in: 0...1) * 100000) + 50000
                )
            )
        }
        
        return records
    }
    
    static func getMarketInsights() -> MarketInsight {
        let gold = basePrices[.gold] ?? 2658.40
        let silver = basePrices[.silver] ?? 31.85
        let platinum = basePrices[.platinum] ?? 988.20
        let palladium = basePrices[.palladium] ?? 1032.50
        
        let ratio = (silver > 0) ? gold / silver : 83.5
        let ptRatio = (platinum > 0) ? gold / platinum : 2.6
        let spread = platinum - palladium
        let session = getMarketSessionStatus()
        
        return MarketInsight(
            goldSilverRatio: ratio,
            goldSilverHistoricalMedian: 75.0,
            goldPlatinumRatio: ptRatio,
            platinumPalladiumSpreadUsd: spread,
            weeklyBestPerformer: .silver,
            weeklyBestPerformancePercent: 3.42,
            marketSessionSummary: session.sessionTitle,
            tradingHoursCommentary: session.sessionDesc
        )
    }
}
