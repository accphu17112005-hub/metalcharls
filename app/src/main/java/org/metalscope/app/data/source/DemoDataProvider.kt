package org.metalscope.app.data.source

import org.metalscope.app.data.model.HistoricalRecord
import org.metalscope.app.data.model.MarketInsight
import org.metalscope.app.data.model.MetalType
import org.metalscope.app.data.model.PriceQuote
import org.metalscope.app.data.model.TimeRange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

object DemoDataProvider {

    private val basePrices = mutableMapOf(
        MetalType.GOLD to 2658.40,
        MetalType.SILVER to 31.85,
        MetalType.PLATINUM to 988.20,
        MetalType.PALLADIUM to 1032.50
    )

    private val baseChanges = mapOf(
        MetalType.GOLD to Pair(18.60, 0.70),
        MetalType.SILVER to Pair(0.48, 1.53),
        MetalType.PLATINUM to Pair(-6.40, -0.64),
        MetalType.PALLADIUM to Pair(14.20, 1.39)
    )

    fun getMarketSessionStatus(): Triple<Boolean, String, String> {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val isWeekend = dayOfWeek == Calendar.SATURDAY || (dayOfWeek == Calendar.SUNDAY && hour < 22)

        return if (isWeekend) {
            Triple(
                false,
                "Market Closed",
                "Weekend trading pause. LBMA Friday close prices shown."
            )
        } else {
            val session = when (hour) {
                in 0..7 -> "Asian Session (Tokyo / Singapore)"
                in 8..12 -> "European Session (London LBMA Open)"
                in 13..16 -> "Peak Global Liquidity (London & NY COMEX Active)"
                in 17..21 -> "Americas Session (NY COMEX Active)"
                else -> "Pacific Pre-Market Transition"
            }
            Triple(true, "Markets Active", session)
        }
    }

    fun getQuotes(simulateTick: Boolean = false): List<PriceQuote> {
        val (isOpen, sessionTitle, _) = getMarketSessionStatus()
        val now = System.currentTimeMillis()

        return MetalType.entries.map { metal ->
            var currentPrice = basePrices[metal] ?: 1000.0
            if (simulateTick) {
                val tickDelta = (Random.nextDouble() - 0.48) * (currentPrice * 0.001)
                currentPrice += tickDelta
                basePrices[metal] = currentPrice
            }

            val (baseChange, baseChangePct) = baseChanges[metal] ?: Pair(0.0, 0.0)
            val spreadFactor = when (metal) {
                MetalType.GOLD -> 0.0003
                MetalType.SILVER -> 0.0006
                MetalType.PLATINUM -> 0.0015
                MetalType.PALLADIUM -> 0.0025
            }
            val spread = currentPrice * spreadFactor
            val bid = currentPrice - (spread / 2.0)
            val ask = currentPrice + (spread / 2.0)

            val high24h = currentPrice * 1.008
            val low24h = currentPrice * 0.993
            val open24h = currentPrice - baseChange

            // Generate 12-point sparkline for cards
            val sparkline = generateSparkline(metal, currentPrice)

            PriceQuote(
                symbol = metal.symbol,
                name = metal.displayName,
                metalType = metal,
                priceUsdPerOz = currentPrice,
                change24hUsd = baseChange,
                changePercent24h = baseChangePct,
                high24hUsd = high24h,
                low24hUsd = low24h,
                open24hUsd = open24h,
                bidUsd = bid,
                askUsd = ask,
                spreadUsd = spread,
                volume24hUsd = when (metal) {
                    MetalType.GOLD -> 4285000000.0
                    MetalType.SILVER -> 1260000000.0
                    MetalType.PLATINUM -> 380000000.0
                    MetalType.PALLADIUM -> 215000000.0
                },
                timestamp = now,
                source = "LBMA / COMEX Composite Feed",
                marketType = "Spot Benchmark",
                isMarketOpen = isOpen,
                marketSession = sessionTitle,
                isStale = false,
                isDemoData = true,
                sparkline = sparkline
            )
        }
    }

    fun generateSparkline(metal: MetalType, currentPrice: Double): List<Double> {
        val points = mutableListOf<Double>()
        val count = 14
        val random = Random(metal.name.hashCode())
        var p = currentPrice * 0.985
        points.add(p)
        for (i in 1 until count - 1) {
            val drift = (random.nextDouble() - 0.45) * (currentPrice * 0.004)
            p += drift
            points.add(p)
        }
        points.add(currentPrice)
        return points
    }

    fun getHistoricalData(metal: MetalType, timeRange: TimeRange): List<HistoricalRecord> {
        val currentPrice = basePrices[metal] ?: 1000.0
        val records = mutableListOf<HistoricalRecord>()
        val pointCount = timeRange.pointCount
        val now = System.currentTimeMillis()

        val intervalMillis = when (timeRange) {
            TimeRange.ONE_DAY -> 3600_000L // 1 hour
            TimeRange.ONE_WEEK -> 6 * 3600_000L // 6 hours
            TimeRange.ONE_MONTH -> 24 * 3600_000L // 1 day
            TimeRange.THREE_MONTHS -> 2 * 24 * 3600_000L // 2 days
            TimeRange.SIX_MONTHS -> 4 * 24 * 3600_000L // 4 days
            TimeRange.ONE_YEAR -> 7 * 24 * 3600_000L // 1 week
            TimeRange.ALL -> 30 * 24 * 3600_000L // 1 month
        }

        val dateFormat = when (timeRange) {
            TimeRange.ONE_DAY -> SimpleDateFormat("HH:mm", Locale.US)
            TimeRange.ONE_WEEK -> SimpleDateFormat("EEE, d MMM", Locale.US)
            TimeRange.ONE_MONTH, TimeRange.THREE_MONTHS -> SimpleDateFormat("MMM d", Locale.US)
            TimeRange.SIX_MONTHS, TimeRange.ONE_YEAR -> SimpleDateFormat("MMM yyyy", Locale.US)
            TimeRange.ALL -> SimpleDateFormat("yyyy", Locale.US)
        }

        // Base trend multiplier depending on timeframe
        val trendMultiplier = when (timeRange) {
            TimeRange.ONE_DAY -> 0.006
            TimeRange.ONE_WEEK -> 0.02
            TimeRange.ONE_MONTH -> 0.05
            TimeRange.THREE_MONTHS -> 0.09
            TimeRange.SIX_MONTHS -> 0.16
            TimeRange.ONE_YEAR -> 0.28
            TimeRange.ALL -> 0.55
        }

        val random = Random(metal.name.hashCode() + timeRange.ordinal * 97)
        val initialPrice = currentPrice * (1.0 - trendMultiplier + (random.nextDouble() * 0.04))
        var runningPrice = initialPrice

        for (i in 0 until pointCount) {
            val ts = now - ((pointCount - 1 - i) * intervalMillis)
            val stepFraction = i.toDouble() / (pointCount - 1).coerceAtLeast(1)
            // Gradual curve towards currentPrice with volatility noise
            val targetPrice = initialPrice + (currentPrice - initialPrice) * stepFraction
            val noise = (random.nextDouble() - 0.49) * (currentPrice * 0.015)
            runningPrice = if (i == pointCount - 1) currentPrice else (targetPrice + noise)

            val open = runningPrice * (1.0 - (random.nextDouble() - 0.5) * 0.005)
            val high = maxOf(open, runningPrice) * (1.0 + random.nextDouble() * 0.004)
            val low = minOf(open, runningPrice) * (1.0 - random.nextDouble() * 0.004)
            val chgPct = if (open > 0) ((runningPrice - open) / open) * 100.0 else 0.0

            records.add(
                HistoricalRecord(
                    timestamp = ts,
                    dateString = dateFormat.format(Date(ts)),
                    closeUsdPerOz = runningPrice,
                    openUsdPerOz = open,
                    highUsdPerOz = high,
                    lowUsdPerOz = low,
                    changePercent = chgPct,
                    volume = (Random.nextDouble() * 100000) + 50000
                )
            )
        }

        return records
    }

    fun getMarketInsights(): MarketInsight {
        val goldPrice = basePrices[MetalType.GOLD] ?: 2658.40
        val silverPrice = basePrices[MetalType.SILVER] ?: 31.85
        val platinumPrice = basePrices[MetalType.PLATINUM] ?: 988.20
        val palladiumPrice = basePrices[MetalType.PALLADIUM] ?: 1032.50

        val goldSilverRatio = goldPrice / silverPrice
        val goldPlatinumRatio = goldPrice / platinumPrice
        val ptPdSpread = platinumPrice - palladiumPrice

        val (_, sessionTitle, sessionDesc) = getMarketSessionStatus()

        return MarketInsight(
            goldSilverRatio = goldSilverRatio,
            goldSilverHistoricalMedian = 75.0,
            goldPlatinumRatio = goldPlatinumRatio,
            platinumPalladiumSpreadUsd = ptPdSpread,
            weeklyBestPerformer = MetalType.SILVER,
            weeklyBestPerformancePercent = 3.42,
            marketSessionSummary = sessionTitle,
            tradingHoursCommentary = sessionDesc
        )
    }
}
