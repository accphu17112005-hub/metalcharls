package org.metalscope.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.metalscope.app.data.model.Currency
import org.metalscope.app.data.model.HistoricalRecord
import org.metalscope.app.data.model.MarketInsight
import org.metalscope.app.data.model.MetalType
import org.metalscope.app.data.model.PriceQuote
import org.metalscope.app.data.model.TimeRange
import org.metalscope.app.data.model.WeightUnit
import org.metalscope.app.data.repository.MetalRepository
import org.metalscope.app.data.repository.PreferencesRepository
import org.metalscope.app.data.repository.WatchlistRepository
import org.metalscope.app.theme.ThemeMode

enum class SortOption(val displayName: String) {
    PRICE_DESC("Highest Price"),
    PRICE_ASC("Lowest Price"),
    CHANGE_DESC("Top Gainers (24h)"),
    CHANGE_ASC("Top Losers (24h)"),
    NAME("Name (A-Z)")
}

enum class MarketFilter(val displayName: String) {
    ALL("All Markets"),
    PRECIOUS("Precious Metals"),
    RATIOS("Ratios & Spreads")
}

data class MainUiState(
    val quotes: List<PriceQuote> = emptyList(),
    val filteredQuotes: List<PriceQuote> = emptyList(),
    val featuredGold: PriceQuote? = null,
    val insights: MarketInsight? = null,
    val currency: Currency = Currency.USD,
    val unit: WeightUnit = WeightUnit.OZ,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val watchlistSymbols: Set<String> = emptySet(),
    val isRefreshing: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis(),
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.PRICE_DESC,
    val filter: MarketFilter = MarketFilter.ALL,
    val isCardView: Boolean = false,
    val isHapticsEnabled: Boolean = true,
    val refreshInterval: Int = 15,
    val decimalOverride: Int? = null
)

class MainViewModel(
    private val metalRepository: MetalRepository,
    private val preferencesRepository: PreferencesRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.PRICE_DESC)
    val sortOption = _sortOption.asStateFlow()

    private val _marketFilter = MutableStateFlow(MarketFilter.ALL)
    val marketFilter = _marketFilter.asStateFlow()

    private val _isCardView = MutableStateFlow(false)
    val isCardView = _isCardView.asStateFlow()

    val uiState: StateFlow<MainUiState> = combine(
        metalRepository.quotes,
        metalRepository.insights,
        metalRepository.isRefreshing,
        metalRepository.lastUpdatedTime,
        preferencesRepository.currencyFlow,
        preferencesRepository.weightUnitFlow,
        preferencesRepository.themeModeFlow,
        preferencesRepository.hapticsEnabledFlow,
        preferencesRepository.refreshIntervalFlow,
        watchlistRepository.watchlistFlow,
        _searchQuery,
        _sortOption,
        _marketFilter,
        _isCardView
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val quotes = args[0] as List<PriceQuote>
        val insights = args[1] as MarketInsight
        val isRefreshing = args[2] as Boolean
        val lastUpdated = args[3] as Long
        val currency = args[4] as Currency
        val unit = args[5] as WeightUnit
        val themeMode = args[6] as ThemeMode
        val isHaptics = args[7] as Boolean
        val refreshInterval = args[8] as Int
        @Suppress("UNCHECKED_CAST")
        val watchlist = args[9] as Set<String>
        val search = args[10] as String
        val sort = args[11] as SortOption
        val filter = args[12] as MarketFilter
        val cardView = args[13] as Boolean

        val featuredGold = quotes.firstOrNull { it.symbol == "XAU" }

        // Filter and sort quotes
        var list = quotes.filter { quote ->
            val matchQuery = search.isBlank() ||
                    quote.symbol.contains(search, ignoreCase = true) ||
                    quote.name.contains(search, ignoreCase = true)
            matchQuery
        }

        list = when (sort) {
            SortOption.PRICE_DESC -> list.sortedByDescending { it.calculatePrice(currency, unit) }
            SortOption.PRICE_ASC -> list.sortedBy { it.calculatePrice(currency, unit) }
            SortOption.CHANGE_DESC -> list.sortedByDescending { it.changePercent24h }
            SortOption.CHANGE_ASC -> list.sortedBy { it.changePercent24h }
            SortOption.NAME -> list.sortedBy { it.name }
        }

        MainUiState(
            quotes = quotes,
            filteredQuotes = list,
            featuredGold = featuredGold,
            insights = insights,
            currency = currency,
            unit = unit,
            themeMode = themeMode,
            watchlistSymbols = watchlist,
            isRefreshing = isRefreshing,
            lastUpdated = lastUpdated,
            searchQuery = search,
            sortOption = sort,
            filter = filter,
            isCardView = cardView,
            isHapticsEnabled = isHaptics,
            refreshInterval = refreshInterval
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    fun refresh() {
        metalRepository.refresh(simulateTick = true)
    }

    fun setCurrency(currency: Currency) {
        viewModelScope.launch { preferencesRepository.setCurrency(currency) }
    }

    fun setWeightUnit(unit: WeightUnit) {
        viewModelScope.launch { preferencesRepository.setWeightUnit(unit) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferencesRepository.setThemeMode(mode) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setHapticsEnabled(enabled) }
    }

    fun setRefreshInterval(seconds: Int) {
        viewModelScope.launch {
            preferencesRepository.setRefreshInterval(seconds)
            metalRepository.startAutoRefresh(seconds)
        }
    }

    fun setDecimalOverride(decimals: Int?) {
        viewModelScope.launch { preferencesRepository.setDecimalOverride(decimals) }
    }

    fun toggleWatchlist(symbol: String) {
        viewModelScope.launch { watchlistRepository.toggleFavorite(symbol) }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOption(sortOption: SortOption) {
        _sortOption.value = sortOption
    }

    fun setMarketFilter(filter: MarketFilter) {
        _marketFilter.value = filter
    }

    fun setCardView(isCard: Boolean) {
        _isCardView.value = isCard
    }

    fun clearAllData() {
        viewModelScope.launch {
            preferencesRepository.clearAll()
            watchlistRepository.clearWatchlist()
            metalRepository.clearCache()
        }
    }

    fun getHistoricalData(metal: MetalType, timeRange: TimeRange): List<HistoricalRecord> {
        return metalRepository.getHistoricalData(metal, timeRange)
    }

    fun getShanghaiData(
        metalType: MetalType = MetalType.SILVER,
        timeRange: TimeRange = TimeRange.ONE_MONTH
    ): org.metalscope.app.data.model.ShanghaiMarketData {
        return metalRepository.getShanghaiData(metalType, timeRange)
    }

    fun getComexInventoryData(
        metalType: MetalType = MetalType.SILVER,
        timeRange: TimeRange = TimeRange.ONE_MONTH
    ): org.metalscope.app.data.model.ComexInventoryData {
        return metalRepository.getComexInventoryData(metalType, timeRange)
    }

    fun getIndiaData(
        metalType: MetalType = MetalType.SILVER,
        timeRange: TimeRange = TimeRange.ONE_MONTH
    ): org.metalscope.app.data.model.IndiaMarketData {
        return metalRepository.getIndiaData(metalType, timeRange)
    }
}
