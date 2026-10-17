import Foundation
import SwiftUI
import Combine

enum SortOption: String, CaseIterable, Identifiable {
    case priceDesc = "PRICE_DESC"
    case priceAsc = "PRICE_ASC"
    case changeDesc = "CHANGE_DESC"
    case changeAsc = "CHANGE_ASC"
    case name = "NAME"
    
    var id: String { rawValue }
    
    var displayName: String {
        switch self {
        case .priceDesc: return "Highest Price"
        case .priceAsc: return "Lowest Price"
        case .changeDesc: return "Top Gainers (24h)"
        case .changeAsc: return "Top Losers (24h)"
        case .name: return "Name (A-Z)"
        }
    }
}

enum MarketFilter: String, CaseIterable, Identifiable {
    case all = "ALL"
    case precious = "PRECIOUS"
    case ratios = "RATIOS"
    
    var id: String { rawValue }
    
    var displayName: String {
        switch self {
        case .all: return "All Markets"
        case .precious: return "Precious Metals"
        case .ratios: return "Ratios & Spreads"
        }
    }
}

@MainActor
class MainViewModel: ObservableObject {
    
    // Dependencies
    let metalRepository: MetalRepository
    let preferencesRepository: PreferencesRepository
    let watchlistRepository: WatchlistRepository
    
    // Search & Filter UI state
    @Published var searchQuery: String = ""
    @Published var sortOption: SortOption = .priceDesc
    @Published var marketFilter: MarketFilter = .all
    @Published var isCardView: Bool = false
    
    // Local historical cache for views that request synchronously
    @Published var historicalDataStore: [String: [HistoricalRecord]] = [:]
    
    private var cancellables = Set<AnyCancellable>()
    
    init(
        metalRepository: MetalRepository = MetalRepository(),
        preferencesRepository: PreferencesRepository = PreferencesRepository(),
        watchlistRepository: WatchlistRepository = WatchlistRepository()
    ) {
        self.metalRepository = metalRepository
        self.preferencesRepository = preferencesRepository
        self.watchlistRepository = watchlistRepository
        self.isCardView = preferencesRepository.isCardView
        
        // Listen to changes in preferences and repository to trigger re-renders
        metalRepository.$quotes
            .sink { [weak self] _ in self?.objectWillChange.send() }
            .store(in: &cancellables)
            
        metalRepository.$insights
            .sink { [weak self] _ in self?.objectWillChange.send() }
            .store(in: &cancellables)
            
        metalRepository.$isRefreshing
            .sink { [weak self] _ in self?.objectWillChange.send() }
            .store(in: &cancellables)
            
        preferencesRepository.$currency
            .sink { [weak self] _ in self?.objectWillChange.send() }
            .store(in: &cancellables)
            
        preferencesRepository.$weightUnit
            .sink { [weak self] _ in self?.objectWillChange.send() }
            .store(in: &cancellables)
            
        watchlistRepository.$watchlistSymbols
            .sink { [weak self] _ in self?.objectWillChange.send() }
            .store(in: &cancellables)
    }
    
    // MARK: - Computed State
    
    var quotes: [PriceQuote] {
        metalRepository.quotes
    }
    
    var featuredGold: PriceQuote? {
        quotes.first { $0.symbol == "XAU" }
    }
    
    var insights: MarketInsight {
        metalRepository.insights
    }
    
    var currency: Currency {
        preferencesRepository.currency
    }
    
    var unit: WeightUnit {
        preferencesRepository.weightUnit
    }
    
    var themeMode: AppThemeMode {
        preferencesRepository.themeMode
    }
    
    var isHapticsEnabled: Bool {
        preferencesRepository.isHapticsEnabled
    }
    
    var refreshInterval: Int {
        preferencesRepository.refreshInterval
    }
    
    var isRefreshing: Bool {
        metalRepository.isRefreshing
    }
    
    var lastUpdated: Int64 {
        metalRepository.lastUpdatedTime
    }
    
    var watchlistSymbols: Set<String> {
        watchlistRepository.watchlistSymbols
    }
    
    var filteredQuotes: [PriceQuote] {
        var list = quotes.filter { quote in
            if searchQuery.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                return true
            }
            return quote.symbol.localizedCaseInsensitiveContains(searchQuery) ||
                   quote.name.localizedCaseInsensitiveContains(searchQuery)
        }
        
        switch sortOption {
        case .priceDesc:
            list.sort { $0.calculatePrice(currency: currency, unit: unit) > $1.calculatePrice(currency: currency, unit: unit) }
        case .priceAsc:
            list.sort { $0.calculatePrice(currency: currency, unit: unit) < $1.calculatePrice(currency: currency, unit: unit) }
        case .changeDesc:
            list.sort { $0.changePercent24h > $1.changePercent24h }
        case .changeAsc:
            list.sort { $0.changePercent24h < $1.changePercent24h }
        case .name:
            list.sort { $0.name < $1.name }
        }
        
        return list
    }
    
    // MARK: - Actions
    
    func refresh() {
        if isHapticsEnabled {
            HapticManager.lightImpact()
        }
        Task {
            await metalRepository.refresh(simulateTick: true)
        }
    }
    
    func setCurrency(_ cur: Currency) {
        preferencesRepository.currency = cur
        if isHapticsEnabled {
            HapticManager.selection()
        }
    }
    
    func setWeightUnit(_ u: WeightUnit) {
        preferencesRepository.weightUnit = u
        if isHapticsEnabled {
            HapticManager.selection()
        }
    }
    
    func setThemeMode(_ mode: AppThemeMode) {
        preferencesRepository.themeMode = mode
        if isHapticsEnabled {
            HapticManager.selection()
        }
    }
    
    func setHapticsEnabled(_ enabled: Bool) {
        preferencesRepository.isHapticsEnabled = enabled
    }
    
    func setRefreshInterval(_ seconds: Int) {
        preferencesRepository.refreshInterval = seconds
        metalRepository.startAutoRefresh(intervalSeconds: seconds)
    }
    
    func toggleWatchlist(symbol: String) {
        watchlistRepository.toggleFavorite(symbol: symbol)
        if isHapticsEnabled {
            HapticManager.mediumImpact()
        }
    }
    
    func setCardView(_ cardView: Bool) {
        self.isCardView = cardView
        preferencesRepository.isCardView = cardView
    }
    
    func clearAllData() {
        preferencesRepository.clearAll()
        watchlistRepository.clearWatchlist()
        metalRepository.clearCache()
        if isHapticsEnabled {
            HapticManager.notificationSuccess()
        }
    }
    
    func getHistoricalData(metal: MetalType, timeRange: TimeRange) -> [HistoricalRecord] {
        let key = "\(metal.symbol)_\(timeRange.label)"
        if let existing = historicalDataStore[key] {
            return existing
        }
        
        // Provide demo data immediately
        let fallback = DemoDataProvider.getHistoricalData(metal: metal, timeRange: timeRange)
        historicalDataStore[key] = fallback
        
        // Fetch asynchronously from repository
        Task {
            let fresh = await metalRepository.getHistoricalData(metal: metal, timeRange: timeRange)
            if !fresh.isEmpty {
                self.historicalDataStore[key] = fresh
            }
        }
        
        return fallback
    }
}
