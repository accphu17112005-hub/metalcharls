package org.metalscope.app.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.metalscope.app.data.model.ComexInventoryData
import org.metalscope.app.data.model.HistoricalRecord
import org.metalscope.app.data.model.IndiaMarketData
import org.metalscope.app.data.model.MarketInsight
import org.metalscope.app.data.model.MetalType
import org.metalscope.app.data.model.PriceQuote
import org.metalscope.app.data.model.ShanghaiMarketData
import org.metalscope.app.data.model.TimeRange
import org.metalscope.app.data.source.RealMarketDataSource
import org.metalscope.app.util.MarketSessionUtils

class MetalRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    private val _quotes = MutableStateFlow<List<PriceQuote>>(emptyList())
    val quotes: StateFlow<List<PriceQuote>> = _quotes.asStateFlow()

    private val _insights = MutableStateFlow<MarketInsight?>(null)
    val insights: StateFlow<MarketInsight?> = _insights.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _lastUpdatedTime = MutableStateFlow(System.currentTimeMillis())
    val lastUpdatedTime: StateFlow<Long> = _lastUpdatedTime.asStateFlow()

    private val _isRealApiConnected = MutableStateFlow(false)
    val isRealApiConnected: StateFlow<Boolean> = _isRealApiConnected.asStateFlow()

    private val _networkError = MutableStateFlow(false)
    val networkError: StateFlow<Boolean> = _networkError.asStateFlow()

    private var autoRefreshJob: Job? = null

    init {
        // Start with empty state — no demo/fake data shown on init
        refresh(simulateTick = false)
        startAutoRefresh(15)
    }

    fun refresh(simulateTick: Boolean = false) {
        scope.launch {
            _isRefreshing.value = true
            _networkError.value = false
            try {
                val liveQuotes = withContext(Dispatchers.IO) {
                    RealMarketDataSource.fetchQuotes()
                }

                if (!liveQuotes.isNullOrEmpty()) {
                    _quotes.value = liveQuotes
                    _insights.value = computeInsights(liveQuotes)
                    _isRealApiConnected.value = true
                    _networkError.value = false
                } else {
                    // Real API failed — do NOT fall back to demo data
                    _isRealApiConnected.value = false
                    _networkError.value = _quotes.value.isEmpty()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isRealApiConnected.value = false
                _networkError.value = _quotes.value.isEmpty()
            } finally {
                _lastUpdatedTime.value = System.currentTimeMillis()
                _isRefreshing.value = false
            }
        }
    }

    private fun computeInsights(quotes: List<PriceQuote>): MarketInsight {
        val gold = quotes.firstOrNull { it.metalType == MetalType.GOLD }?.priceUsdPerOz ?: 0.0
        val silver = quotes.firstOrNull { it.metalType == MetalType.SILVER }?.priceUsdPerOz ?: 0.0
        val plat = quotes.firstOrNull { it.metalType == MetalType.PLATINUM }?.priceUsdPerOz ?: 0.0
        val pall = quotes.firstOrNull { it.metalType == MetalType.PALLADIUM }?.priceUsdPerOz ?: 0.0

        val ratio = if (silver > 0) gold / silver else 0.0
        val ptRatio = if (plat > 0) gold / plat else 0.0
        val ptPdSpread = plat - pall
        val best = quotes.maxByOrNull { it.changePercent24h }?.metalType ?: MetalType.GOLD
        val bestPct = quotes.firstOrNull { it.metalType == best }?.changePercent24h ?: 0.0
        val (isOpen, sessionTitle, sessionDesc) = MarketSessionUtils.getMarketSessionStatus()

        return MarketInsight(
            goldSilverRatio = ratio,
            goldSilverHistoricalMedian = 75.0,
            goldPlatinumRatio = ptRatio,
            platinumPalladiumSpreadUsd = ptPdSpread,
            weeklyBestPerformer = best,
            weeklyBestPerformancePercent = bestPct,
            marketSessionSummary = if (isOpen) "Live Market • $sessionTitle" else "Market Closed",
            tradingHoursCommentary = sessionDesc
        )
    }

    fun startAutoRefresh(intervalSeconds: Int) {
        autoRefreshJob?.cancel()
        if (intervalSeconds <= 0) return

        autoRefreshJob = scope.launch {
            while (isActive) {
                delay(intervalSeconds * 1000L)
                refresh(simulateTick = false)
            }
        }
    }

    fun getQuote(symbol: String): PriceQuote? {
        return _quotes.value.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) }
    }

    fun getHistoricalData(metal: MetalType, timeRange: TimeRange): List<HistoricalRecord> {
        val cached = RealMarketDataSource.getCachedHistoricalData(metal, timeRange)
        if (cached != null && cached.isNotEmpty()) return cached

        scope.launch(Dispatchers.IO) {
            val records = RealMarketDataSource.fetchHistoricalData(metal, timeRange)
            if (records.isNotEmpty()) {
                _lastUpdatedTime.value = System.currentTimeMillis()
            }
        }
        return cached ?: emptyList()
    }

    fun getCachedHistoricalData(metal: MetalType, timeRange: TimeRange): List<HistoricalRecord>? {
        return RealMarketDataSource.getCachedHistoricalData(metal, timeRange)
    }

    fun getShanghaiData(
        metalType: MetalType = MetalType.SILVER,
        timeRange: TimeRange = TimeRange.ONE_MONTH
    ): ShanghaiMarketData? {
        val cached = RealMarketDataSource.getCachedShanghaiData(metalType, timeRange)
        if (cached != null) return cached

        scope.launch(Dispatchers.IO) {
            val data = RealMarketDataSource.fetchShanghaiData(metalType, timeRange)
            if (data != null) {
                _lastUpdatedTime.value = System.currentTimeMillis()
            }
        }
        return null
    }

    fun getComexInventoryData(
        metalType: MetalType = MetalType.SILVER,
        timeRange: TimeRange = TimeRange.ONE_MONTH
    ): ComexInventoryData? {
        val cached = RealMarketDataSource.getCachedComexData(metalType, timeRange)
        if (cached != null) return cached

        scope.launch(Dispatchers.IO) {
            val data = RealMarketDataSource.fetchComexInventoryData(metalType, timeRange)
            if (data != null) {
                _lastUpdatedTime.value = System.currentTimeMillis()
            }
        }
        return null
    }

    fun getIndiaData(
        metalType: MetalType = MetalType.SILVER,
        timeRange: TimeRange = TimeRange.ONE_MONTH
    ): IndiaMarketData? {
        val cached = RealMarketDataSource.getCachedIndiaData(metalType, timeRange)
        if (cached != null) return cached

        scope.launch(Dispatchers.IO) {
            val data = RealMarketDataSource.fetchIndiaData(metalType, timeRange)
            if (data != null) {
                _lastUpdatedTime.value = System.currentTimeMillis()
            }
        }
        return null
    }

    fun clearCache() {
        refresh(simulateTick = false)
    }
}
