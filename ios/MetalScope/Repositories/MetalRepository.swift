import Foundation
import Combine

class MetalRepository: ObservableObject {
    @Published var quotes: [PriceQuote] = []
    @Published var insights: MarketInsight?
    @Published var isRefreshing: Bool = false
    @Published var lastUpdatedTime: Int64 = Int64(Date().timeIntervalSince1970 * 1000.0)
    @Published var isRealApiConnected: Bool = false
    @Published var networkError: Bool = false

    private var autoRefreshTimer: Timer?

    init() {
        // No demo data on init — start with empty state
        Task { @MainActor in
            await self.refresh()
            self.startAutoRefresh(intervalSeconds: 15)
        }
    }

    @MainActor
    func refresh() async {
        guard !isRefreshing else { return }
        isRefreshing = true
        networkError = false

        let liveQuotes = await RealMarketDataSource.shared.fetchQuotes()
        if let live = liveQuotes, !live.isEmpty {
            self.quotes = live
            self.insights = computeInsights(quotes: live)
            self.isRealApiConnected = true
            self.networkError = false
        } else {
            // Real API failed — do NOT fall back to demo data
            self.isRealApiConnected = false
            if self.quotes.isEmpty {
                self.networkError = true
            }
        }

        self.lastUpdatedTime = Int64(Date().timeIntervalSince1970 * 1000.0)
        self.isRefreshing = false
    }

    private func computeInsights(quotes: [PriceQuote]) -> MarketInsight {
        let gold = quotes.first(where: { $0.metalType == .gold })?.priceUsdPerOz ?? 0.0
        let silver = quotes.first(where: { $0.metalType == .silver })?.priceUsdPerOz ?? 0.0
        let plat = quotes.first(where: { $0.metalType == .platinum })?.priceUsdPerOz ?? 0.0
        let pall = quotes.first(where: { $0.metalType == .palladium })?.priceUsdPerOz ?? 0.0

        let ratio = silver > 0 ? gold / silver : 0.0
        let ptRatio = plat > 0 ? gold / plat : 0.0
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
            marketSessionSummary: session.isOpen ? "Live Market • \(session.sessionTitle)" : "Market Closed",
            tradingHoursCommentary: session.sessionDesc
        )
    }

    func startAutoRefresh(intervalSeconds: Int) {
        autoRefreshTimer?.invalidate()
        guard intervalSeconds > 0 else { return }

        autoRefreshTimer = Timer.scheduledTimer(withTimeInterval: TimeInterval(intervalSeconds), repeats: true) { [weak self] _ in
            Task { @MainActor in
                await self?.refresh()
            }
        }
    }

    func getHistoricalData(metal: MetalType, timeRange: TimeRange) async -> [HistoricalRecord] {
        return await RealMarketDataSource.shared.fetchHistoricalData(metal: metal, timeRange: timeRange)
    }

    func getCachedHistoricalData(metal: MetalType, timeRange: TimeRange) async -> [HistoricalRecord]? {
        return await RealMarketDataSource.shared.getCachedHistoricalData(metal: metal, timeRange: timeRange)
    }

    // MARK: - Specialty Screens — all return nil on failure (no fake fallback)

    func getShanghaiData(
        metal: MetalType = .silver,
        timeRange: TimeRange = .oneMonth
    ) async -> ShanghaiMarketData? {
        return await RealMarketDataSource.shared.fetchShanghaiData(metal: metal, timeRange: timeRange)
    }

    func getComexInventoryData(
        metal: MetalType = .silver,
        timeRange: TimeRange = .oneMonth
    ) async -> ComexInventoryData? {
        return await RealMarketDataSource.shared.fetchComexInventoryData(metal: metal, timeRange: timeRange)
    }

    func getIndiaData(
        metal: MetalType = .silver,
        timeRange: TimeRange = .oneMonth
    ) async -> IndiaMarketData? {
        return await RealMarketDataSource.shared.fetchIndiaData(metal: metal, timeRange: timeRange)
    }

    func clearCache() {
        Task { @MainActor in
            await self.refresh()
        }
    }

    deinit {
        autoRefreshTimer?.invalidate()
    }
}
