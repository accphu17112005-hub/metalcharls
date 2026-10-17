import Foundation

class WatchlistRepository: ObservableObject {
    private let defaults = UserDefaults.standard
    private let key = "pref_watchlist_symbols"
    
    @Published var watchlistSymbols: Set<String> {
        didSet {
            defaults.set(Array(watchlistSymbols), forKey: key)
        }
    }
    
    init() {
        if let stored = defaults.stringArray(forKey: key) {
            self.watchlistSymbols = Set(stored)
        } else {
            self.watchlistSymbols = ["XAU", "XAG"] // Default Gold and Silver
        }
    }
    
    func toggleFavorite(symbol: String) {
        if watchlistSymbols.contains(symbol) {
            watchlistSymbols.remove(symbol)
        } else {
            watchlistSymbols.insert(symbol)
        }
    }
    
    func isFavorite(symbol: String) -> Bool {
        return watchlistSymbols.contains(symbol)
    }
    
    func clearWatchlist() {
        watchlistSymbols.removeAll()
    }
}
