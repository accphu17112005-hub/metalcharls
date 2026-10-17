import Foundation
import Combine

class MetalRepository: ObservableObject {
    @Published var quotes: [PriceQuote] = []
    @Published var insights: MarketInsight
    @Published var isRefreshing: Bool = false
    @Published var lastUpdatedTime: Int64 = Int64(Date().timeIntervalSince1970 * 1000.0)
    @Published var isRealApiConnected: Bool = false
    
    private var autoRefreshTimer: Timer?
    
    init() {
        self.insights = DemoDataProvider.getMarketInsights()
        self.quotes = DemoDataProvider.getQuotes(simulateTick: false)
        
        Task { @MainActor in
            await self.refresh(simulateTick: false)
            self.startAutoRefresh(intervalSeconds: 15)
        }
    }
    
    @MainActor
    func refresh(simulateTick: Bool = true) async {
        guard !isRefreshing else { return }
        isRefreshing = true
        
        let liveQuotes = await RealMarketDataSource.shared.fetchQuotes()
        if let live = liveQuotes, !live.isEmpty {
            self.quotes = live
            self.insights = computeInsights(quotes: live)
            self.isRealApiConnected = true
        } else {
            if self.quotes.isEmpty {
                self.quotes = DemoDataProvider.getQuotes(simulateTick: simulateTick)
                self.insights = DemoDataProvider.getMarketInsights()
            }
            self.isRealApiConnected = false
        }
        
        self.lastUpdatedTime = Int64(Date().timeIntervalSince1970 * 1000.0)
        self.isRefreshing = false
    }
    
    private func computeInsights(quotes: [PriceQuote]) -> MarketInsight {
        let gold = quotes.first(where: { $0.metalType == .gold })?.priceUsdPerOz ?? 2658.0
        let silver = quotes.first(where: { $0.metalType == .silver })?.priceUsdPerOz ?? 31.8
        let plat = quotes.first(where: { $0.metalType == .platinum })?.priceUsdPerOz ?? 988.0
        let pall = quotes.first(where: { $0.metalType == .palladium })?.priceUsdPerOz ?? 1032.0
        
        let ratio = (silver > 0) ? gold / silver : 83.5
        let ptRatio = (plat > 0) ? gold / plat : 2.6
        let ptPdSpread = plat - pall
        let best = quotes.max(by: { $0.changePercent24h < $1.changePercent24h })?.metalType ?? .gold
        let bestPct = quotes.first(where: { $0.metalType == best })?.changePercent24h ?? 0.0
        let session = DemoDataProvider.getMarketSessionStatus()
        
        return MarketInsight(
            goldSilverRatio: ratio,
            goldSilverHistoricalMedian: 75.0,
            goldPlatinumRatio: ptRatio,
            platinumPalladiumSpreadUsd: ptPdSpread,
            weeklyBestPerformer: best,
            weeklyBestPerformancePercent: bestPct,
            marketSessionSummary: session.isOpen ? "Live Market • \(session.sessionTitle)" : "Weekend Pause • Friday Close",
            tradingHoursCommentary: session.sessionDesc
        )
    }
    
    func startAutoRefresh(intervalSeconds: Int) {
        autoRefreshTimer?.invalidate()
        guard intervalSeconds > 0 else { return }
        
        autoRefreshTimer = Timer.scheduledTimer(withTimeInterval: TimeInterval(intervalSeconds), repeats: true) { [weak self] _ in
            Task { @MainActor in
                await self?.refresh(simulateTick: false)
            }
        }
    }
    
    func getHistoricalData(metal: MetalType, timeRange: TimeRange) async -> [HistoricalRecord] {
        return await RealMarketDataSource.shared.fetchHistoricalData(metal: metal, timeRange: timeRange)
    }
    
    func getCachedHistoricalData(metal: MetalType, timeRange: TimeRange) async -> [HistoricalRecord]? {
        return await RealMarketDataSource.shared.getCachedHistoricalData(metal: metal, timeRange: timeRange)
    }
    
    func clearCache() {
        Task { @MainActor in
            await self.refresh(simulateTick: false)
        }
    }
    
    deinit {
        autoRefreshTimer?.invalidate()
    }
}
