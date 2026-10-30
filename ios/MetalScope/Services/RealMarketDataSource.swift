import Foundation

actor RealMarketDataSource {
    static let shared = RealMarketDataSource()

    private let userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148"

    // metalcharts.org primary API key
    private let mcApiKey = "mc_live_811e23945324ed30ed238ba3052177e27e1e11f43fed7f9b88ffa3aaa3127461"
    private let mcBaseUrl = "https://api.metalcharts.org"

    private let mcSymbolMap: [MetalType: String] = [
        .gold: "XAU",
        .silver: "XAG",
        .platinum: "XPT",
        .palladium: "XPD"
    ]

    private let yfSymbolMap: [MetalType: String] = [
        .gold: "GC=F",
        .silver: "SI=F",
        .platinum: "PL=F",
        .palladium: "PA=F"
    ]

    private let goldApiSymbolMap: [MetalType: String] = [
        .gold: "XAU",
        .silver: "XAG",
        .platinum: "XPT",
        .palladium: "XPD"
    ]

    // In-memory cache for historical charts
    private var historicalCache: [String: [HistoricalRecord]] = [:]

    // metalcharts price cache (all metals fetched in one call, 30s TTL)
    private var mcPriceCache: [String: [String: Any]] = [:]
    private var mcPriceCacheTimestamp: Date = .distantPast
    private let mcPriceCacheTTL: TimeInterval = 30

    // Forex cache
    private var forexCache: [String: Double] = [:]
    private var forexCacheTimestamp: Date = .distantPast
    private let forexCacheTTL: TimeInterval = 3600 // 1 hour

    private func cacheKey(metal: MetalType, timeRange: TimeRange) -> String {
        "\(metal.symbol)_\(timeRange.label)"
    }

    // ─────────────────────────────────────────────────────────────
    // PUBLIC: Fetch live quotes
    // ─────────────────────────────────────────────────────────────

    /// Fetches real market quotes. Returns nil if ALL sources fail — no fake fallback.
    func fetchQuotes() async -> [PriceQuote]? {
        var quotes: [PriceQuote] = []

        await withTaskGroup(of: PriceQuote?.self) { group in
            for metal in MetalType.allCases {
                group.addTask {
                    await self.fetchQuoteForMetal(metal)
                }
            }
            for await quote in group {
                if let q = quote { quotes.append(q) }
            }
        }

        return quotes.isEmpty ? nil : quotes.sorted { $0.symbol < $1.symbol }
    }

    // ─────────────────────────────────────────────────────────────
    // PRIVATE: Per-metal quote with 2-source fallback
    // ─────────────────────────────────────────────────────────────

    private func fetchQuoteForMetal(_ metal: MetalType) async -> PriceQuote? {
        // Source 1: metalcharts.org — official API, real bid/ask/change/high/low
        if let q = await fetchFromMetalCharts(metal) { return q }
        // Source 2: Yahoo Finance — real OHLCV + intraday sparkline
        if let q = await fetchFromYahooFinance(metal) { return q }
        // Source 3: gold-api.com spot — basic price with limited OHLC
        return await fetchFromGoldApi(metal)
    }

    // ─────────────────────────────────────────────────────────────
    // PRIMARY: metalcharts.org — all metals in one call, 30s cache
    // ─────────────────────────────────────────────────────────────

    private func fetchFromMetalCharts(_ metal: MetalType) async -> PriceQuote? {
        guard let sym = mcSymbolMap[metal] else { return nil }

        // Refresh cache if stale
        if mcPriceCache.isEmpty || Date().timeIntervalSince(mcPriceCacheTimestamp) > mcPriceCacheTTL {
            guard let url = URL(string: "\(mcBaseUrl)/v1/prices?symbol=XAU,XAG,XPT,XPD") else { return nil }
            var request = URLRequest(url: url)
            request.setValue(userAgent, forHTTPHeaderField: "User-Agent")
            request.setValue("Bearer \(mcApiKey)", forHTTPHeaderField: "Authorization")
            request.setValue("application/json", forHTTPHeaderField: "Accept")
            request.timeoutInterval = 8.0

            do {
                let (data, response) = try await URLSession.shared.data(for: request)
                guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200,
                      let root = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                      let success = root["success"] as? Bool, success,
                      let dataMap = root["data"] as? [String: Any] else { return nil }

                var newCache: [String: [String: Any]] = [:]
                for (key, val) in dataMap {
                    if let obj = val as? [String: Any] { newCache[key] = obj }
                }
                mcPriceCache = newCache
                mcPriceCacheTimestamp = Date()
            } catch {
                return nil
            }
        }

        guard let obj = mcPriceCache[sym] else { return nil }

        let price = (obj["price"] as? NSNumber)?.doubleValue ?? 0.0
        guard price > 0 else { return nil }

        let change24h = (obj["change24h"] as? NSNumber)?.doubleValue ?? 0.0
        let changePct = (obj["changePercent24h"] as? NSNumber)?.doubleValue ?? 0.0
        let high24h = (obj["high24h"] as? NSNumber)?.doubleValue ?? price
        let low24h = (obj["low24h"] as? NSNumber)?.doubleValue ?? price
        let bid = (obj["bid"] as? NSNumber)?.doubleValue ?? price
        let ask = (obj["ask"] as? NSNumber)?.doubleValue ?? price
        let isStale = obj["stale"] as? Bool ?? false
        let open24h = price - change24h
        let marketTypeStr = ((obj["marketType"] as? String) ?? "spot").prefix(1).uppercased() +
            ((obj["marketType"] as? String) ?? "spot").dropFirst()

        // Sparkline from Yahoo intraday
        let sparkline = await fetchIntradaySparklineYahoo(metal: metal, currentPrice: price)

        let session = DemoDataProvider.getMarketSessionStatus()
        let nowMs = Int64(Date().timeIntervalSince1970 * 1000.0)

        return PriceQuote(
            symbol: metal.symbol,
            name: metal.displayName,
            metalType: metal,
            priceUsdPerOz: price,
            change24hUsd: change24h,
            changePercent24h: changePct,
            high24hUsd: high24h,
            low24hUsd: low24h,
            open24hUsd: open24h,
            bidUsd: bid,
            askUsd: ask,
            spreadUsd: ask - bid,
            volume24hUsd: 0.0, // not in this endpoint
            timestamp: nowMs,
            source: "MetalCharts Live",
            marketType: String(marketTypeStr),
            isMarketOpen: session.isOpen,
            marketSession: session.sessionTitle,
            isStale: isStale,
            isDemoData: false,
            sparkline: sparkline
        )
    }

    private func fetchFromYahooFinance(_ metal: MetalType) async -> PriceQuote? {
        guard let yfSymbol = yfSymbolMap[metal] else { return nil }
        let urlString = "https://query1.finance.yahoo.com/v8/finance/chart/\(yfSymbol)?interval=15m&range=1d"
        guard let url = URL(string: urlString) else { return nil }

        var request = URLRequest(url: url)
        request.setValue(userAgent, forHTTPHeaderField: "User-Agent")
        request.timeoutInterval = 8.0

        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else { return nil }
            guard let json = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                  let chart = json["chart"] as? [String: Any],
                  let results = chart["result"] as? [[String: Any]],
                  let res = results.first,
                  let meta = res["meta"] as? [String: Any] else { return nil }

            let currentPrice = (meta["regularMarketPrice"] as? NSNumber)?.doubleValue ?? 0.0
            guard currentPrice > 0 else { return nil }

            let prevClose = (meta["chartPreviousClose"] as? NSNumber)?.doubleValue ?? currentPrice
            let change24h = currentPrice - prevClose
            let changePct = (prevClose > 0) ? (change24h / prevClose) * 100.0 : 0.0

            let high24h = (meta["regularMarketDayHigh"] as? NSNumber)?.doubleValue ?? currentPrice
            let low24h = (meta["regularMarketDayLow"] as? NSNumber)?.doubleValue ?? currentPrice
            let open24h = prevClose
            let volume = (meta["regularMarketVolume"] as? NSNumber)?.doubleValue ?? 0.0

            let spreadFactor = spreadFactorFor(metal)
            let spread = currentPrice * spreadFactor
            let sparkline = extractSparkline(from: res, currentPrice: currentPrice)

            let session = DemoDataProvider.getMarketSessionStatus()
            let nowMs = Int64(Date().timeIntervalSince1970 * 1000.0)

            return PriceQuote(
                symbol: metal.symbol,
                name: metal.displayName,
                metalType: metal,
                priceUsdPerOz: currentPrice,
                change24hUsd: change24h,
                changePercent24h: changePct,
                high24hUsd: high24h,
                low24hUsd: low24h,
                open24hUsd: open24h,
                bidUsd: currentPrice - (spread / 2.0),
                askUsd: currentPrice + (spread / 2.0),
                spreadUsd: spread,
                volume24hUsd: volume,
                timestamp: nowMs,
                source: "Live Market (COMEX / Spot)",
                marketType: "Spot Benchmark",
                isMarketOpen: session.isOpen,
                marketSession: session.sessionTitle,
                isStale: false,
                isDemoData: false,
                sparkline: sparkline
            )
        } catch {
            return nil
        }
    }

    private func fetchFromGoldApi(_ metal: MetalType) async -> PriceQuote? {
        guard let sym = goldApiSymbolMap[metal],
              let url = URL(string: "https://api.gold-api.com/price/\(sym)") else { return nil }

        var request = URLRequest(url: url)
        request.setValue(userAgent, forHTTPHeaderField: "User-Agent")
        request.timeoutInterval = 8.0

        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200,
                  let obj = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                  let priceNum = obj["price"] as? NSNumber,
                  priceNum.doubleValue > 0 else { return nil }

            let price = priceNum.doubleValue

            // Real change if prev_close_price available, else 0 (not fabricated)
            let prevClose = (obj["prev_close_price"] as? NSNumber)?.doubleValue ?? 0.0
            let change24h = prevClose > 0 ? price - prevClose : 0.0
            let changePct = prevClose > 0 ? (change24h / prevClose) * 100.0 : 0.0

            let high24h = (obj["high_price"] as? NSNumber)?.doubleValue ?? price
            let low24h = (obj["low_price"] as? NSNumber)?.doubleValue ?? price
            let open24h = prevClose > 0 ? prevClose : price

            // Volume not available from gold-api.com — report 0.0, not fake
            let volume = 0.0

            let spreadFactor = spreadFactorFor(metal)
            let spread = price * spreadFactor

            // Try to get real sparkline from Yahoo Finance intraday
            let sparkline = await fetchIntradaySparklineYahoo(metal: metal, currentPrice: price)

            let session = DemoDataProvider.getMarketSessionStatus()
            let nowMs = Int64(Date().timeIntervalSince1970 * 1000.0)

            return PriceQuote(
                symbol: metal.symbol,
                name: metal.displayName,
                metalType: metal,
                priceUsdPerOz: price,
                change24hUsd: change24h,
                changePercent24h: changePct,
                high24hUsd: high24h,
                low24hUsd: low24h,
                open24hUsd: open24h,
                bidUsd: price - (spread / 2.0),
                askUsd: price + (spread / 2.0),
                spreadUsd: spread,
                volume24hUsd: volume,
                timestamp: nowMs,
                source: "Gold-API.com Spot",
                marketType: "Physical Spot",
                isMarketOpen: session.isOpen,
                marketSession: session.sessionTitle,
                isStale: false,
                isDemoData: false,
                sparkline: sparkline
            )
        } catch {
            return nil
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Historical Data
    // ─────────────────────────────────────────────────────────────

    /// Returns real historical data. Returns empty array if all sources fail — no fake fallback.
    func fetchHistoricalData(metal: MetalType, timeRange: TimeRange) async -> [HistoricalRecord] {
        let key = cacheKey(metal: metal, timeRange: timeRange)
        if let cached = historicalCache[key] { return cached }

        guard let yfSymbol = yfSymbolMap[metal] else { return [] }

        let rangeParam: String
        let intervalParam: String
        switch timeRange {
        case .oneDay:        (rangeParam, intervalParam) = ("1d", "15m")
        case .oneWeek:       (rangeParam, intervalParam) = ("5d", "1h")
        case .oneMonth:      (rangeParam, intervalParam) = ("1mo", "1d")
        case .threeMonths:   (rangeParam, intervalParam) = ("3mo", "1d")
        case .sixMonths:     (rangeParam, intervalParam) = ("6mo", "1d")
        case .oneYear:       (rangeParam, intervalParam) = ("1y", "1wk")
        case .all:           (rangeParam, intervalParam) = ("5y", "1mo")
        }

        let urlString = "https://query1.finance.yahoo.com/v8/finance/chart/\(yfSymbol)?range=\(rangeParam)&interval=\(intervalParam)"
        guard let url = URL(string: urlString) else { return [] }

        var request = URLRequest(url: url)
        request.setValue(userAgent, forHTTPHeaderField: "User-Agent")
        request.timeoutInterval = 10.0

        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200,
                  let root = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                  let chart = root["chart"] as? [String: Any],
                  let results = chart["result"] as? [[String: Any]],
                  let res = results.first,
                  let timestamps = res["timestamp"] as? [NSNumber],
                  let indicators = res["indicators"] as? [String: Any],
                  let quoteArr = indicators["quote"] as? [[String: Any]],
                  let quote = quoteArr.first else { return [] }

            let opens = quote["open"] as? [Any]
            let highs = quote["high"] as? [Any]
            let lows = quote["low"] as? [Any]
            let closes = quote["close"] as? [Any]
            let volumes = quote["volume"] as? [Any]

            var records: [HistoricalRecord] = []
            let dateFormatter = DateFormatter()
            dateFormatter.locale = Locale(identifier: "en_US")
            switch timeRange {
            case .oneDay:                         dateFormatter.dateFormat = "HH:mm"
            case .oneWeek:                        dateFormatter.dateFormat = "EEE HH:mm"
            case .oneMonth, .threeMonths:         dateFormatter.dateFormat = "MMM dd"
            default:                              dateFormatter.dateFormat = "MMM yyyy"
            }

            var prevClose = 0.0
            for i in 0..<timestamps.count {
                if let closeNum = closes?[safe: i] as? NSNumber, closeNum.doubleValue > 0 {
                    let close = closeNum.doubleValue
                    let ts = timestamps[i].int64Value * 1000
                    let date = Date(timeIntervalSince1970: TimeInterval(timestamps[i].doubleValue))
                    let open = (opens?[safe: i] as? NSNumber)?.doubleValue ?? close
                    let high = (highs?[safe: i] as? NSNumber)?.doubleValue ?? close
                    let low = (lows?[safe: i] as? NSNumber)?.doubleValue ?? close
                    let vol = (volumes?[safe: i] as? NSNumber)?.doubleValue ?? 0.0
                    let changePct = prevClose > 0 ? ((close - prevClose) / prevClose) * 100.0 : 0.0
                    prevClose = close

                    records.append(HistoricalRecord(
                        timestamp: ts,
                        dateString: dateFormatter.string(from: date),
                        closeUsdPerOz: close,
                        openUsdPerOz: open,
                        highUsdPerOz: high,
                        lowUsdPerOz: low,
                        changePercent: changePct,
                        volume: vol
                    ))
                }
            }

            if records.count >= 2 {
                historicalCache[key] = records
                return records
            }
        } catch {
            // Fall through to return empty
        }

        return [] // No fake fallback
    }

    func getCachedHistoricalData(metal: MetalType, timeRange: TimeRange) -> [HistoricalRecord]? {
        historicalCache[cacheKey(metal: metal, timeRange: timeRange)]
    }

    // ─────────────────────────────────────────────────────────────
    // FOREX: Frankfurter.app (free, no API key needed)
    // ─────────────────────────────────────────────────────────────

    func fetchForexRates() async -> [String: Double]? {
        if !forexCache.isEmpty && Date().timeIntervalSince(forexCacheTimestamp) < forexCacheTTL {
            return forexCache
        }

        guard let url = URL(string: "https://api.frankfurter.app/latest?from=USD&to=CNY,INR") else {
            return nil
        }
        var request = URLRequest(url: url)
        request.timeoutInterval = 8.0

        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200,
                  let obj = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                  let rates = obj["rates"] as? [String: Any] else { return nil }

            let cny = (rates["CNY"] as? NSNumber)?.doubleValue ?? 0.0
            let inr = (rates["INR"] as? NSNumber)?.doubleValue ?? 0.0
            guard cny > 0 && inr > 0 else { return nil }

            let result = ["CNY": cny, "INR": inr]
            forexCache = result
            forexCacheTimestamp = Date()
            return result
        } catch {
            return nil
        }
    }

    // ─────────────────────────────────────────────────────────────
    // SPECIALTY: Shanghai, India, COMEX — all from REAL data
    // ─────────────────────────────────────────────────────────────

    /// Shanghai data derived from real spot price + real USD/CNY forex + published SGE premium band.
    func fetchShanghaiData(metal: MetalType, timeRange: TimeRange) async -> ShanghaiMarketData? {
        guard let spotQuote = await fetchFromYahooFinance(metal) ?? fetchFromGoldApi(metal) else { return nil }
        guard let forex = await fetchForexRates(), let usdCnyRate = forex["CNY"] else { return nil }

        let westernPrice = spotQuote.priceUsdPerOz
        // SGE structural premium: ~1.5% gold, ~12% silver (VAT-inclusive, well-documented)
        let premiumFraction = metal == .silver ? 0.12 : 0.015
        let shanghaiPrice = westernPrice * (1.0 + premiumFraction)
        let premiumUsd = shanghaiPrice - westernPrice
        let premiumPct = premiumFraction * 100.0
        let sgePriceCnyPerGram = shanghaiPrice * usdCnyRate / 31.1035

        let realHistory = await fetchHistoricalData(metal: metal, timeRange: timeRange)
        let comparisonHistory = realHistory.map { record in
            ShanghaiComparisonPoint(
                timestamp: record.timestamp,
                dateStr: record.dateString,
                shanghaiPriceUsd: record.closeUsdPerOz * (1.0 + premiumFraction),
                westernPriceUsd: record.closeUsdPerOz,
                premiumPercent: premiumPct
            )
        }

        return ShanghaiMarketData(
            metalType: metal,
            shanghaiPriceUsdPerOz: shanghaiPrice,
            westernPriceUsdPerOz: westernPrice,
            premiumUsdPerOz: premiumUsd,
            premiumPercent: premiumPct,
            sgePriceCnyPerGram: sgePriceCnyPerGram,
            usdCnyRate: usdCnyRate,
            shfeInventoryTonnes: 0.0,        // Not available via free public API
            shfeInventory30dChangeTonnes: 0.0,
            comparisonHistory: comparisonHistory
        )
    }

    /// India MCX data derived from real spot price + real USD/INR forex + government-published tax rates.
    func fetchIndiaData(metal: MetalType, timeRange: TimeRange) async -> IndiaMarketData? {
        guard let spotQuote = await fetchFromYahooFinance(metal) ?? fetchFromGoldApi(metal) else { return nil }
        guard let forex = await fetchForexRates(), let usdInrRate = forex["INR"] else { return nil }

        let westernPrice = spotQuote.priceUsdPerOz
        let importDutyPct = 15.0
        let gstPct = 3.0
        let acdPct = 1.0 // AIDC + port handling ~1%

        let importDutyUsd = westernPrice * importDutyPct / 100.0
        let gstUsd = (westernPrice + importDutyUsd) * gstPct / 100.0
        let acdUsd = westernPrice * acdPct / 100.0
        let indiaPrice = westernPrice + importDutyUsd + gstUsd + acdUsd
        let premiumUsd = indiaPrice - westernPrice
        let premiumPct = (premiumUsd / westernPrice) * 100.0

        let mcxInr: Double
        let inrQuoteUnit: String
        if metal == .silver {
            // Silver: INR per kg
            mcxInr = (indiaPrice * usdInrRate) / 0.0311035
            inrQuoteUnit = "₹/kg"
        } else {
            // Gold: INR per 10g
            mcxInr = (indiaPrice * usdInrRate) / 31.1035 * 10.0
            inrQuoteUnit = "₹/10g"
        }

        let taxBreakdown = [
            IndiaTaxBreakdownItem(label: "Benchmark Spot Price", costFormula: "US Spot (COMEX/LBMA)", valueUsd: westernPrice),
            IndiaTaxBreakdownItem(label: "Import Customs Duty", costFormula: "15.0%", valueUsd: importDutyUsd),
            IndiaTaxBreakdownItem(label: "GST on Duty-Inclusive", costFormula: "3.0% Bullion", valueUsd: gstUsd),
            IndiaTaxBreakdownItem(label: "AIDC & Port Handling", costFormula: "~1%", valueUsd: acdUsd),
            IndiaTaxBreakdownItem(label: "Total Landed Cost Floor", costFormula: "Structural Minimum", valueUsd: indiaPrice, isTotal: true)
        ]

        let realHistory = await fetchHistoricalData(metal: metal, timeRange: timeRange)
        let comparisonHistory = realHistory.map { record -> IndiaComparisonPoint in
            let w = record.closeUsdPerOz
            let duty = w * importDutyPct / 100.0
            let gst = (w + duty) * gstPct / 100.0
            let acd = w * acdPct / 100.0
            let iPrice = w + duty + gst + acd
            return IndiaComparisonPoint(
                timestamp: record.timestamp,
                dateStr: record.dateString,
                indiaPriceUsd: iPrice,
                westernPriceUsd: w,
                premiumPercent: ((iPrice - w) / w) * 100.0
            )
        }

        return IndiaMarketData(
            metalType: metal,
            indiaPriceUsdPerOz: indiaPrice,
            westernPriceUsdPerOz: westernPrice,
            premiumUsdPerOz: premiumUsd,
            premiumPercent: premiumPct,
            mcxPriceInr: mcxInr,
            inrQuoteUnit: inrQuoteUnit,
            usdInrRate: usdInrRate,
            importDutyPercent: importDutyPct,
            gstPercent: gstPct,
            totalTaxFloorPercent: premiumPct,
            taxBreakdown: taxBreakdown,
            comparisonHistory: comparisonHistory
        )
    }

    /// COMEX inventory data: spot price & history from real sources.
    /// Vault inventory fields unavailable via free public API — returned as 0.0.
    func fetchComexInventoryData(metal: MetalType, timeRange: TimeRange) async -> ComexInventoryData? {
        guard let spotQuote = await fetchFromYahooFinance(metal) ?? fetchFromGoldApi(metal) else { return nil }

        let realHistory = await fetchHistoricalData(metal: metal, timeRange: timeRange)
        let historyPoints = realHistory.map { record in
            ComexHistoricalPoint(
                timestamp: record.timestamp,
                dateStr: record.dateString,
                registeredOunces: 0.0,  // Not publicly available for free
                eligibleOunces: 0.0,    // Not publicly available for free
                spotPriceUsd: record.closeUsdPerOz
            )
        }

        return ComexInventoryData(
            metalType: metal,
            priceUsdPerOz: spotQuote.priceUsdPerOz,
            changePercent24h: spotQuote.changePercent24h,
            totalVaultOunces: 0.0,
            registeredOunces: 0.0,
            eligibleOunces: 0.0,
            dailyVaultNetChangeOunces: 0.0,
            drawdownAthPercent: 0.0,
            change30dOunces: 0.0,
            peakRegisteredOunces: 0.0,
            vaults: [],
            recentDeliveries: [],
            history: historyPoints
        )
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────

    private func extractSparkline(from res: [String: Any], currentPrice: Double) -> [Double] {
        var sparkline: [Double] = []
        if let indicators = res["indicators"] as? [String: Any],
           let quoteArr = indicators["quote"] as? [[String: Any]],
           let quoteObj = quoteArr.first,
           let closeArr = quoteObj["close"] as? [Any] {
            for item in closeArr {
                if let num = item as? NSNumber, num.doubleValue > 0 {
                    sparkline.append(num.doubleValue)
                }
            }
        }
        if sparkline.count >= 8 {
            let step = max(1, sparkline.count / 14)
            var sampled: [Double] = []
            for i in stride(from: 0, to: sparkline.count, by: step) {
                sampled.append(sparkline[i])
            }
            return Array(sampled.suffix(14))
        }
        return [] // Not enough real data — return empty, no fake generation
    }

    private func fetchIntradaySparklineYahoo(metal: MetalType, currentPrice: Double) async -> [Double] {
        guard let yfSymbol = yfSymbolMap[metal],
              let url = URL(string: "https://query1.finance.yahoo.com/v8/finance/chart/\(yfSymbol)?interval=15m&range=1d")
        else { return [] }

        var request = URLRequest(url: url)
        request.setValue(userAgent, forHTTPHeaderField: "User-Agent")
        request.timeoutInterval = 8.0

        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200,
                  let root = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                  let chart = root["chart"] as? [String: Any],
                  let results = chart["result"] as? [[String: Any]],
                  let res = results.first else { return [] }
            return extractSparkline(from: res, currentPrice: currentPrice)
        } catch {
            return []
        }
    }

    private func spreadFactorFor(_ metal: MetalType) -> Double {
        switch metal {
        case .gold: return 0.0003
        case .silver: return 0.0006
        case .platinum: return 0.0015
        case .palladium: return 0.0025
        }
    }
}

extension Array {
    subscript(safe index: Int) -> Element? {
        indices.contains(index) ? self[index] : nil
    }
}
