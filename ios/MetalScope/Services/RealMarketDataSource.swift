import Foundation

actor RealMarketDataSource {
    static let shared = RealMarketDataSource()
    
    private let userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148"
    
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
    
    private func cacheKey(metal: MetalType, timeRange: TimeRange) -> String {
        return "\(metal.symbol)_\(timeRange.label)"
    }
    
    func fetchQuotes() async -> [PriceQuote]? {
        do {
            var quotes: [PriceQuote] = []
            
            await withTaskGroup(of: PriceQuote?.self) { group in
                for metal in MetalType.allCases {
                    group.addTask {
                        await self.fetchQuoteForMetal(metal)
                    }
                }
                
                for await quote in group {
                    if let q = quote {
                        quotes.append(q)
                    }
                }
            }
            
            if quotes.count == MetalType.allCases.count {
                return quotes.sorted { $0.symbol < $1.symbol }
            } else if !quotes.isEmpty {
                let demo = DemoDataProvider.getQuotes(simulateTick: false)
                let demoMap = Dictionary(uniqueKeysWithValues: demo.map { ($0.metalType, $0) })
                return MetalType.allCases.map { m in
                    quotes.first(where: { $0.metalType == m }) ?? demoMap[m]!
                }
            } else {
                return nil
            }
        }
    }
    
    private func fetchQuoteForMetal(_ metal: MetalType) async -> PriceQuote? {
        guard let yfSymbol = yfSymbolMap[metal] else { return nil }
        let urlString = "https://query1.finance.yahoo.com/v8/finance/chart/\(yfSymbol)?interval=15m&range=1d"
        
        if let quote = await fetchFromYahooFinance(urlString: urlString, metal: metal) {
            return quote
        }
        
        // Fallback to gold-api.com
        return await fetchQuoteFromGoldApi(metal: metal)
    }
    
    private func fetchFromYahooFinance(urlString: String, metal: MetalType) async -> PriceQuote? {
        guard let url = URL(string: urlString) else { return nil }
        var request = URLRequest(url: url)
        request.setValue(userAgent, forHTTPHeaderField: "User-Agent")
        request.timeoutInterval = 6.0
        
        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
                return nil
            }
            
            guard let json = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                  let chart = json["chart"] as? [String: Any],
                  let results = chart["result"] as? [[String: Any]],
                  let res = results.first,
                  let meta = res["meta"] as? [String: Any] else {
                return nil
            }
            
            let currentPrice = (meta["regularMarketPrice"] as? NSNumber)?.doubleValue ?? 0.0
            guard currentPrice > 0 else { return nil }
            
            let prevClose = (meta["chartPreviousClose"] as? NSNumber)?.doubleValue ?? currentPrice
            let change24h = currentPrice - prevClose
            let changePct = (prevClose > 0) ? (change24h / prevClose) * 100.0 : 0.0
            
            let high24h = (meta["regularMarketDayHigh"] as? NSNumber)?.doubleValue ?? (currentPrice * 1.005)
            let low24h = (meta["regularMarketDayLow"] as? NSNumber)?.doubleValue ?? (currentPrice * 0.995)
            let open24h = prevClose
            
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
            let volume = (meta["regularMarketVolume"] as? NSNumber)?.doubleValue ?? 50000.0
            
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
            
            let finalSparkline: [Double]
            if sparkline.count >= 8 {
                let step = max(1, sparkline.count / 14)
                var sampled: [Double] = []
                for i in stride(from: 0, to: sparkline.count, by: step) {
                    sampled.append(sparkline[i])
                }
                finalSparkline = Array(sampled.suffix(14))
            } else {
                finalSparkline = DemoDataProvider.generateSparkline(metal: metal, currentPrice: currentPrice)
            }
            
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
                bidUsd: bid,
                askUsd: ask,
                spreadUsd: spread,
                volume24hUsd: volume,
                timestamp: nowMs,
                source: "Live Market (COMEX / Spot)",
                marketType: "Spot Benchmark",
                isMarketOpen: session.isOpen,
                marketSession: session.sessionTitle,
                isStale: false,
                isDemoData: false,
                sparkline: finalSparkline
            )
        } catch {
            return nil
        }
    }
    
    private func fetchQuoteFromGoldApi(metal: MetalType) async -> PriceQuote? {
        guard let sym = goldApiSymbolMap[metal],
              let url = URL(string: "https://api.gold-api.com/price/\(sym)") else { return nil }
        
        var request = URLRequest(url: url)
        request.setValue(userAgent, forHTTPHeaderField: "User-Agent")
        request.timeoutInterval = 6.0
        
        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200,
                  let obj = try JSONSerialization.jsonObject(with: data) as? [String: Any],
                  let priceNum = obj["price"] as? NSNumber,
                  priceNum.doubleValue > 0 else {
                return nil
            }
            
            let price = priceNum.doubleValue
            let spreadFactor: Double
            switch metal {
            case .gold: spreadFactor = 0.0003
            case .silver: spreadFactor = 0.0006
            case .platinum: spreadFactor = 0.0015
            case .palladium: spreadFactor = 0.0025
            }
            let spread = price * spreadFactor
            let session = DemoDataProvider.getMarketSessionStatus()
            let nowMs = Int64(Date().timeIntervalSince1970 * 1000.0)
            
            return PriceQuote(
                symbol: metal.symbol,
                name: metal.displayName,
                metalType: metal,
                priceUsdPerOz: price,
                change24hUsd: price * 0.004,
                changePercent24h: 0.40,
                high24hUsd: price * 1.006,
                low24hUsd: price * 0.994,
                open24hUsd: price * 0.996,
                bidUsd: price - (spread / 2.0),
                askUsd: price + (spread / 2.0),
                spreadUsd: spread,
                volume24hUsd: 1250000.0,
                timestamp: nowMs,
                source: "Gold-API.com Spot",
                marketType: "Physical Spot",
                isMarketOpen: session.isOpen,
                marketSession: session.sessionTitle,
                isStale: false,
                isDemoData: false,
                sparkline: DemoDataProvider.generateSparkline(metal: metal, currentPrice: price)
            )
        } catch {
            return nil
        }
    }
    
    func fetchHistoricalData(metal: MetalType, timeRange: TimeRange) async -> [HistoricalRecord] {
        let key = cacheKey(metal: metal, timeRange: timeRange)
        if let cached = historicalCache[key] {
            return cached
        }
        
        guard let yfSymbol = yfSymbolMap[metal] else {
            return DemoDataProvider.getHistoricalData(metal: metal, timeRange: timeRange)
        }
        
        let rangeParam: String
        let intervalParam: String
        switch timeRange {
        case .oneDay: (rangeParam, intervalParam) = ("1d", "15m")
        case .oneWeek: (rangeParam, intervalParam) = ("5d", "1h")
        case .oneMonth: (rangeParam, intervalParam) = ("1mo", "1d")
        case .threeMonths: (rangeParam, intervalParam) = ("3mo", "1d")
        case .sixMonths: (rangeParam, intervalParam) = ("6mo", "1d")
        case .oneYear: (rangeParam, intervalParam) = ("1y", "1wk")
        case .all: (rangeParam, intervalParam) = ("5y", "1mo")
        }
        
        let urlString = "https://query1.finance.yahoo.com/v8/finance/chart/\(yfSymbol)?range=\(rangeParam)&interval=\(intervalParam)"
        guard let url = URL(string: urlString) else {
            return DemoDataProvider.getHistoricalData(metal: metal, timeRange: timeRange)
        }
        
        var request = URLRequest(url: url)
        request.setValue(userAgent, forHTTPHeaderField: "User-Agent")
        request.timeoutInterval = 7.0
        
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
                  let quote = quoteArr.first else {
                return DemoDataProvider.getHistoricalData(metal: metal, timeRange: timeRange)
            }
            
            let opens = quote["open"] as? [Any]
            let highs = quote["high"] as? [Any]
            let lows = quote["low"] as? [Any]
            let closes = quote["close"] as? [Any]
            let volumes = quote["volume"] as? [Any]
            
            var records: [HistoricalRecord] = []
            let dateFormatter = DateFormatter()
            dateFormatter.locale = Locale(identifier: "en_US")
            switch timeRange {
            case .oneDay: dateFormatter.dateFormat = "HH:mm"
            case .oneWeek: dateFormatter.dateFormat = "EEE HH:mm"
            case .oneMonth, .threeMonths: dateFormatter.dateFormat = "MMM dd"
            default: dateFormatter.dateFormat = "MMM yyyy"
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
                    
                    let changePct = (prevClose > 0) ? ((close - prevClose) / prevClose) * 100.0 : 0.0
                    prevClose = close
                    
                    records.append(
                        HistoricalRecord(
                            timestamp: ts,
                            dateString: dateFormatter.string(from: date),
                            closeUsdPerOz: close,
                            openUsdPerOz: open,
                            highUsdPerOz: high,
                            lowUsdPerOz: low,
                            changePercent: changePct,
                            volume: vol
                        )
                    )
                }
            }
            
            if records.count >= 2 {
                historicalCache[key] = records
                return records
            }
        } catch {
            // Handled by fallback below
        }
        
        return DemoDataProvider.getHistoricalData(metal: metal, timeRange: timeRange)
    }
    
    func getCachedHistoricalData(metal: MetalType, timeRange: TimeRange) -> [HistoricalRecord]? {
        let key = cacheKey(metal: metal, timeRange: timeRange)
        return historicalCache[key]
    }
}

extension Array {
    subscript(safe index: Int) -> Element? {
        return indices.contains(index) ? self[index] : nil
    }
}
