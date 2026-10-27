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
    
    // MARK: - Specialty Market Data Providers (Mirroring Android DemoDataProvider)
    
    static func getShanghaiData(
        metalType: MetalType = .silver,
        timeRange: TimeRange = .oneMonth
    ) -> ShanghaiMarketData {
        let isSilver = metalType == .silver
        let westernPrice = isSilver ? 53.89 : 4016.71
        let shanghaiPrice = isSilver ? 60.65 : 4085.20
        let premiumUsd = shanghaiPrice - westernPrice
        let premiumPct = (premiumUsd / westernPrice) * 100.0
        let sgeCnyPerGram = isSilver ? 13.82 : 950.20
        let usdCnyRate = 7.23
        let shfeTonnes = isSilver ? 2140.5 : 32.4
        let shfe30dChange = isSilver ? -85.2 : -2.1

        let count: Int
        let stepSeconds: Int64
        switch timeRange {
        case .oneDay:
            count = 12
            stepSeconds = 2 * 3600
        case .oneWeek:
            count = 7
            stepSeconds = 24 * 3600
        case .oneMonth:
            count = 30
            stepSeconds = 24 * 3600
        case .threeMonths:
            count = 60
            stepSeconds = 2 * 24 * 3600
        case .sixMonths:
            count = 90
            stepSeconds = 4 * 24 * 3600
        case .oneYear:
            count = 120
            stepSeconds = 7 * 24 * 3600
        case .all:
            count = 180
            stepSeconds = 30 * 24 * 3600
        }

        let now = Date().timeIntervalSince1970
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MMM dd"
        dateFormatter.timeZone = TimeZone(identifier: "UTC")

        var history: [ShanghaiComparisonPoint] = []
        var runningWest = westernPrice * 0.94
        for i in 0..<count {
            let ts = Int64(now) - (Int64(count - 1 - i) * stepSeconds)
            let randDelta = (Double.random(in: 0...1) - 0.47) * (runningWest * 0.015)
            runningWest += randDelta
            let premMult = isSilver ? (1.08 + Double.random(in: 0...1) * 0.06) : (1.01 + Double.random(in: 0...1) * 0.02)
            let runningShanghai = runningWest * premMult
            let premVal = ((runningShanghai - runningWest) / runningWest) * 100.0

            let d = Date(timeIntervalSince1970: TimeInterval(ts))
            history.append(
                ShanghaiComparisonPoint(
                    timestamp: ts * 1000,
                    dateStr: dateFormatter.string(from: d),
                    shanghaiPriceUsd: runningShanghai,
                    westernPriceUsd: runningWest,
                    premiumPercent: premVal
                )
            )
        }

        return ShanghaiMarketData(
            metalType: metalType,
            shanghaiPriceUsdPerOz: shanghaiPrice,
            westernPriceUsdPerOz: westernPrice,
            premiumUsdPerOz: premiumUsd,
            premiumPercent: premiumPct,
            sgePriceCnyPerGram: sgeCnyPerGram,
            usdCnyRate: usdCnyRate,
            shfeInventoryTonnes: shfeTonnes,
            shfeInventory30dChangeTonnes: shfe30dChange,
            comparisonHistory: history
        )
    }

    static func getComexInventoryData(
        metalType: MetalType = .silver,
        timeRange: TimeRange = .oneMonth
    ) -> ComexInventoryData {
        let isSilver = metalType == .silver
        let price = isSilver ? 53.89 : 4016.71
        let changePct = isSilver ? -0.02 : 0.15
        let totalOz = isSilver ? 337500000.0 : 17400000.0
        let registeredOz = isSilver ? 99800000.0 : 8200000.0
        let eligibleOz = isSilver ? 237700000.0 : 9200000.0
        let dailyChangeOz = isSilver ? -600000.0 : -48000.0
        let drawdownAth = isSilver ? -34.8 : -43.4
        let change30d = isSilver ? -4200000.0 : -320000.0
        let peakReg = isSilver ? 152000000.0 : 14500000.0

        let vaults: [ComexVault]
        if isSilver {
            vaults = [
                ComexVault(vaultName: "JPMorgan Chase Bank, N.A.", location: "New York, NY", totalOunces: 105400000.0, registeredOunces: 38200000.0, eligibleOunces: 67200000.0, sharePercent: 31.2),
                ComexVault(vaultName: "Brink's Inc.", location: "Springfield Gardens, NY", totalOunces: 98500000.0, registeredOunces: 28500000.0, eligibleOunces: 70000000.0, sharePercent: 29.2),
                ComexVault(vaultName: "HSBC Bank USA, N.A.", location: "New York, NY", totalOunces: 62100000.0, registeredOunces: 18100000.0, eligibleOunces: 44000000.0, sharePercent: 18.4),
                ComexVault(vaultName: "Loomis International", location: "Jamaica, NY", totalOunces: 34200000.0, registeredOunces: 9400000.0, eligibleOunces: 24800000.0, sharePercent: 10.1),
                ComexVault(vaultName: "CNT Depository LLC", location: "Bridgewater, MA", totalOunces: 22800000.0, registeredOunces: 4200000.0, eligibleOunces: 18600000.0, sharePercent: 6.8),
                ComexVault(vaultName: "Delaware Depository", location: "Wilmington, DE", totalOunces: 14500000.0, registeredOunces: 1400000.0, eligibleOunces: 13100000.0, sharePercent: 4.3)
            ]
        } else {
            vaults = [
                ComexVault(vaultName: "Brink's Inc.", location: "Springfield Gardens, NY", totalOunces: 6800000.0, registeredOunces: 3400000.0, eligibleOunces: 3400000.0, sharePercent: 39.1),
                ComexVault(vaultName: "JPMorgan Chase Bank, N.A.", location: "New York, NY", totalOunces: 5200000.0, registeredOunces: 2600000.0, eligibleOunces: 2600000.0, sharePercent: 29.9),
                ComexVault(vaultName: "HSBC Bank USA, N.A.", location: "New York, NY", totalOunces: 3100000.0, registeredOunces: 1300000.0, eligibleOunces: 1800000.0, sharePercent: 17.8),
                ComexVault(vaultName: "Loomis International", location: "Jamaica, NY", totalOunces: 1400000.0, registeredOunces: 600000.0, eligibleOunces: 800000.0, sharePercent: 8.0),
                ComexVault(vaultName: "Malca-Amit Armored", location: "Jamaica, NY", totalOunces: 900000.0, registeredOunces: 300000.0, eligibleOunces: 600000.0, sharePercent: 5.2)
            ]
        }

        let deliveries = [
            ComexDeliveryNotice(dateStr: "Oct 24, 2026", contractMonth: "DEC 26", dailyIssued: 148, dailyStopped: 148, ouncesDelivered: 740000.0, monthToDateOunces: 18450000.0),
            ComexDeliveryNotice(dateStr: "Oct 23, 2026", contractMonth: "DEC 26", dailyIssued: 92, dailyStopped: 92, ouncesDelivered: 460000.0, monthToDateOunces: 17710000.0),
            ComexDeliveryNotice(dateStr: "Oct 22, 2026", contractMonth: "DEC 26", dailyIssued: 215, dailyStopped: 215, ouncesDelivered: 1075000.0, monthToDateOunces: 17250000.0),
            ComexDeliveryNotice(dateStr: "Oct 21, 2026", contractMonth: "DEC 26", dailyIssued: 80, dailyStopped: 80, ouncesDelivered: 400000.0, monthToDateOunces: 16175000.0),
            ComexDeliveryNotice(dateStr: "Oct 20, 2026", contractMonth: "DEC 26", dailyIssued: 310, dailyStopped: 310, ouncesDelivered: 1550000.0, monthToDateOunces: 15775000.0)
        ]

        let count: Int
        let stepSeconds: Int64
        switch timeRange {
        case .oneDay:
            count = 12
            stepSeconds = 2 * 3600
        case .oneWeek:
            count = 7
            stepSeconds = 24 * 3600
        case .oneMonth:
            count = 30
            stepSeconds = 24 * 3600
        case .threeMonths:
            count = 60
            stepSeconds = 2 * 24 * 3600
        case .sixMonths:
            count = 90
            stepSeconds = 4 * 24 * 3600
        case .oneYear:
            count = 120
            stepSeconds = 7 * 24 * 3600
        case .all:
            count = 180
            stepSeconds = 30 * 24 * 3600
        }

        let now = Date().timeIntervalSince1970
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MMM dd"
        dateFormatter.timeZone = TimeZone(identifier: "UTC")

        var history: [ComexHistoricalPoint] = []
        var runningReg = registeredOz * 1.08
        var runningElig = eligibleOz * 0.98
        var runningPrice = price * 0.92

        for i in 0..<count {
            let ts = Int64(now) - (Int64(count - 1 - i) * stepSeconds)
            runningReg -= Double.random(in: 0...120000)
            runningElig += (Double.random(in: 0...1) - 0.45) * 80000
            runningPrice += (Double.random(in: 0...1) - 0.48) * (runningPrice * 0.01)

            let d = Date(timeIntervalSince1970: TimeInterval(ts))
            history.append(
                ComexHistoricalPoint(
                    timestamp: ts * 1000,
                    dateStr: dateFormatter.string(from: d),
                    registeredOunces: runningReg,
                    eligibleOunces: runningElig,
                    spotPriceUsd: runningPrice
                )
            )
        }

        return ComexInventoryData(
            metalType: metalType,
            priceUsdPerOz: price,
            changePercent24h: changePct,
            totalVaultOunces: totalOz,
            registeredOunces: registeredOz,
            eligibleOunces: eligibleOz,
            dailyVaultNetChangeOunces: dailyChangeOz,
            drawdownAthPercent: drawdownAth,
            change30dOunces: change30d,
            peakRegisteredOunces: peakReg,
            vaults: vaults,
            recentDeliveries: deliveries,
            history: history
        )
    }

    static func getIndiaData(
        metalType: MetalType = .silver,
        timeRange: TimeRange = .oneMonth
    ) -> IndiaMarketData {
        let isSilver = metalType == .silver
        let westernPrice = isSilver ? 53.88 : 4016.71
        let indiaPrice = isSilver ? 63.56 : 4650.00
        let premiumUsd = indiaPrice - westernPrice
        let premiumPct = (premiumUsd / westernPrice) * 100.0
        let mcxInr = isSilver ? 88450.0 : 84200.0
        let inrQuoteUnit = isSilver ? "₹/kg" : "₹/10g"
        let usdInrRate = 86.40
        let importDuty = 15.0
        let gst = 3.0
        let totalTaxFloor = 18.45

        let taxBreakdown: [IndiaTaxBreakdownItem]
        if isSilver {
            taxBreakdown = [
                IndiaTaxBreakdownItem(label: "Benchmark Spot Price", costFormula: "US Spot (COMEX)", valueUsd: 53.88, isTotal: false),
                IndiaTaxBreakdownItem(label: "Import Customs Duty", costFormula: "15.0% Tariff Hiked in 2026", valueUsd: 8.08, isTotal: false),
                IndiaTaxBreakdownItem(label: "GST on Duty-Inclusive", costFormula: "3.0% Bullion Goods & Services", valueUsd: 1.86, isTotal: false),
                IndiaTaxBreakdownItem(label: "Port Clearance & AIDC", costFormula: "Agriculture Infra Cess + Handling", valueUsd: 0.65, isTotal: false),
                IndiaTaxBreakdownItem(label: "Total Landed Cost Floor", costFormula: "Structural Minimum Import Basis", valueUsd: 64.47, isTotal: true)
            ]
        } else {
            taxBreakdown = [
                IndiaTaxBreakdownItem(label: "Benchmark Spot Price", costFormula: "US Spot (COMEX)", valueUsd: 4016.71, isTotal: false),
                IndiaTaxBreakdownItem(label: "Import Customs Duty", costFormula: "15.0% Tariff Hiked in 2026", valueUsd: 602.50, isTotal: false),
                IndiaTaxBreakdownItem(label: "GST on Duty-Inclusive", costFormula: "3.0% Bullion Goods & Services", valueUsd: 138.58, isTotal: false),
                IndiaTaxBreakdownItem(label: "Port Clearance & AIDC", costFormula: "Agriculture Infra Cess + Handling", valueUsd: 42.00, isTotal: false),
                IndiaTaxBreakdownItem(label: "Total Landed Cost Floor", costFormula: "Structural Minimum Import Basis", valueUsd: 4799.79, isTotal: true)
            ]
        }

        let count: Int
        let stepSeconds: Int64
        switch timeRange {
        case .oneDay:
            count = 12
            stepSeconds = 2 * 3600
        case .oneWeek:
            count = 7
            stepSeconds = 24 * 3600
        case .oneMonth:
            count = 30
            stepSeconds = 24 * 3600
        case .threeMonths:
            count = 60
            stepSeconds = 2 * 24 * 3600
        case .sixMonths:
            count = 90
            stepSeconds = 4 * 24 * 3600
        case .oneYear:
            count = 120
            stepSeconds = 7 * 24 * 3600
        case .all:
            count = 180
            stepSeconds = 30 * 24 * 3600
        }

        let now = Date().timeIntervalSince1970
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MMM dd"
        dateFormatter.timeZone = TimeZone(identifier: "UTC")

        var history: [IndiaComparisonPoint] = []
        var runningWest = westernPrice * 0.95
        for i in 0..<count {
            let ts = Int64(now) - (Int64(count - 1 - i) * stepSeconds)
            runningWest += (Double.random(in: 0...1) - 0.48) * (runningWest * 0.014)
            let premMult = isSilver ? (1.14 + Double.random(in: 0...1) * 0.06) : (1.12 + Double.random(in: 0...1) * 0.04)
            let runningIndia = runningWest * premMult
            let premVal = ((runningIndia - runningWest) / runningWest) * 100.0

            let d = Date(timeIntervalSince1970: TimeInterval(ts))
            history.append(
                IndiaComparisonPoint(
                    timestamp: ts * 1000,
                    dateStr: dateFormatter.string(from: d),
                    indiaPriceUsd: runningIndia,
                    westernPriceUsd: runningWest,
                    premiumPercent: premVal
                )
            )
        }

        return IndiaMarketData(
            metalType: metalType,
            indiaPriceUsdPerOz: indiaPrice,
            westernPriceUsdPerOz: westernPrice,
            premiumUsdPerOz: premiumUsd,
            premiumPercent: premiumPct,
            mcxPriceInr: mcxInr,
            inrQuoteUnit: inrQuoteUnit,
            usdInrRate: usdInrRate,
            importDutyPercent: importDuty,
            gstPercent: gst,
            totalTaxFloorPercent: totalTaxFloor,
            taxBreakdown: taxBreakdown,
            comparisonHistory: history
        )
    }
}

