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
import org.metalscope.app.data.model.HistoricalRecord
import org.metalscope.app.data.model.MarketInsight
import org.metalscope.app.data.model.MetalType
import org.metalscope.app.data.model.PriceQuote
import org.metalscope.app.data.model.TimeRange
import org.metalscope.app.data.source.DemoDataProvider
import org.metalscope.app.data.source.RealMarketDataSource

class MetalRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    private val _quotes = MutableStateFlow<List<PriceQuote>>(emptyList())
    val quotes: StateFlow<List<PriceQuote>> = _quotes.asStateFlow()

    private val _insights = MutableStateFlow(DemoDataProvider.getMarketInsights())
    val insights: StateFlow<MarketInsight> = _insights.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _lastUpdatedTime = MutableStateFlow(System.currentTimeMillis())
    val lastUpdatedTime: StateFlow<Long> = _lastUpdatedTime.asStateFlow()

    private val _isRealApiConnected = MutableStateFlow(false)
    val isRealApiConnected: StateFlow<Boolean> = _isRealApiConnected.asStateFlow()

    private var autoRefreshJob: Job? = null

    init {
        // Provide immediate cached values, then fetch live market data
        _quotes.value = DemoDataProvider.getQuotes(simulateTick = false)
        refresh(simulateTick = false)
        startAutoRefresh(15) // default 15s auto-refresh
    }

    fun refresh(simulateTick: Boolean = true) {
        scope.launch {
            _isRefreshing.value = true
            try {
                val liveQuotes = withContext(Dispatchers.IO) {
                    RealMarketDataSource.fetchQuotes()
                }

                if (liveQuotes != null && liveQuotes.isNotEmpty()) {
                    _quotes.value = liveQuotes
                    _insights.value = computeInsights(liveQuotes)
                    _isRealApiConnected.value = true
                } else {
                    if (_quotes.value.isEmpty()) {
                        _quotes.value = DemoDataProvider.getQuotes(simulateTick)
                        _insights.value = DemoDataProvider.getMarketInsights()
                    }
                    _isRealApiConnected.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (_quotes.value.isEmpty()) {
                    _quotes.value = DemoDataProvider.getQuotes(simulateTick)
                    _insights.value = DemoDataProvider.getMarketInsights()
                }
                _isRealApiConnected.value = false
            } finally {
                _lastUpdatedTime.value = System.currentTimeMillis()
                _isRefreshing.value = false
            }
        }
    }

    private fun computeInsights(quotes: List<PriceQuote>): MarketInsight {
        val gold = quotes.firstOrNull { it.metalType == MetalType.GOLD }?.priceUsdPerOz ?: 2658.0
        val silver = quotes.firstOrNull { it.metalType == MetalType.SILVER }?.priceUsdPerOz ?: 31.8
        val plat = quotes.firstOrNull { it.metalType == MetalType.PLATINUM }?.priceUsdPerOz ?: 988.0
        val pall = quotes.firstOrNull { it.metalType == MetalType.PALLADIUM }?.priceUsdPerOz ?: 1032.0

        val ratio = if (silver > 0) gold / silver else 83.5
        val ptRatio = if (plat > 0) gold / plat else 2.6
        val ptPdSpread = plat - pall
        val best = quotes.maxByOrNull { it.changePercent24h }?.metalType ?: MetalType.GOLD
        val bestPct = quotes.firstOrNull { it.metalType == best }?.changePercent24h ?: 0.0
        val (isOpen, sessionTitle, sessionDesc) = DemoDataProvider.getMarketSessionStatus()

        return MarketInsight(
            goldSilverRatio = ratio,
            goldSilverHistoricalMedian = 75.0,
            goldPlatinumRatio = ptRatio,
            platinumPalladiumSpreadUsd = ptPdSpread,
            weeklyBestPerformer = best,
            weeklyBestPerformancePercent = bestPct,
            marketSessionSummary = if (isOpen) "Live Market • $sessionTitle" else "Weekend Pause • Friday Close",
            tradingHoursCommentary = sessionDesc
        )
    }

    fun startAutoRefresh(intervalSeconds: Int) {
        autoRefreshJob?.cancel()
        if (intervalSeconds <= 0) return // Manual mode

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
        if (cached != null) {
            return cached
        }

        // Fetch asynchronously in background
        scope.launch {
            val records = RealMarketDataSource.fetchHistoricalData(metal, timeRange)
            if (records.isNotEmpty()) {
                _lastUpdatedTime.value = System.currentTimeMillis()
            }
        }

        return DemoDataProvider.getHistoricalData(metal, timeRange)
    }

    fun getShanghaiData(
        metalType: MetalType = MetalType.SILVER,
        timeRange: TimeRange = TimeRange.ONE_MONTH
    ): org.metalscope.app.data.model.ShanghaiMarketData {
        return DemoDataProvider.getShanghaiData(metalType, timeRange)
    }

    fun getComexInventoryData(
        metalType: MetalType = MetalType.SILVER,
        timeRange: TimeRange = TimeRange.ONE_MONTH
    ): org.metalscope.app.data.model.ComexInventoryData {
        return DemoDataProvider.getComexInventoryData(metalType, timeRange)
    }

    fun getIndiaData(
        metalType: MetalType = MetalType.SILVER,
        timeRange: TimeRange = TimeRange.ONE_MONTH
    ): org.metalscope.app.data.model.IndiaMarketData {
        return DemoDataProvider.getIndiaData(metalType, timeRange)
    }

    fun clearCache() {
        refresh(simulateTick = false)
    }
}
