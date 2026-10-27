package org.metalscope.app.data.source

import org.metalscope.app.data.model.ComexDeliveryNotice
import org.metalscope.app.data.model.ComexHistoricalPoint
import org.metalscope.app.data.model.ComexInventoryData
import org.metalscope.app.data.model.ComexVault
import org.metalscope.app.data.model.HistoricalRecord
import org.metalscope.app.data.model.IndiaComparisonPoint
import org.metalscope.app.data.model.IndiaMarketData
import org.metalscope.app.data.model.IndiaTaxBreakdownItem
import org.metalscope.app.data.model.MarketInsight
import org.metalscope.app.data.model.MetalType
import org.metalscope.app.data.model.PriceQuote
import org.metalscope.app.data.model.ShanghaiComparisonPoint
import org.metalscope.app.data.model.ShanghaiMarketData
import org.metalscope.app.data.model.TimeRange
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

object DemoDataProvider {

    private val basePrices = mutableMapOf(
        MetalType.GOLD to 4016.70,
        MetalType.SILVER to 53.88,
        MetalType.PLATINUM to 1024.50,
        MetalType.PALLADIUM to 1048.20
    )

    private val baseChanges = mapOf(
        MetalType.GOLD to Pair(18.60, 0.46),
        MetalType.SILVER to Pair(-0.04, -0.07),
        MetalType.PLATINUM to Pair(6.40, 0.63),
        MetalType.PALLADIUM to Pair(-12.30, -1.16)
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

    fun getShanghaiData(
        metalType: MetalType = MetalType.SILVER,
        timeRange: TimeRange = TimeRange.ONE_MONTH
    ): ShanghaiMarketData {
        val isSilver = metalType == MetalType.SILVER
        val westernPrice = if (isSilver) 53.89 else 4016.71
        val shanghaiPrice = if (isSilver) 60.65 else 4085.20
        val premiumUsd = shanghaiPrice - westernPrice
        val premiumPct = (premiumUsd / westernPrice) * 100.0
        val sgeCnyPerGram = if (isSilver) 13.82 else 950.20
        val usdCnyRate = 7.23
        val shfeTonnes = if (isSilver) 2140.5 else 32.4
        val shfe30dChange = if (isSilver) -85.2 else -2.1

        val count = when (timeRange) {
            TimeRange.ONE_DAY -> 12
            TimeRange.ONE_WEEK -> 7
            TimeRange.ONE_MONTH -> 30
            TimeRange.THREE_MONTHS -> 60
            TimeRange.SIX_MONTHS -> 90
            TimeRange.ONE_YEAR -> 120
            TimeRange.ALL -> 180
        }

        val stepMs = when (timeRange) {
            TimeRange.ONE_DAY -> 2 * 3600000L
            TimeRange.ONE_WEEK -> 24 * 3600000L
            TimeRange.ONE_MONTH -> 24 * 3600000L
            TimeRange.THREE_MONTHS -> 2 * 24 * 3600000L
            TimeRange.SIX_MONTHS -> 4 * 24 * 3600000L
            TimeRange.ONE_YEAR -> 7 * 24 * 3600000L
            TimeRange.ALL -> 30 * 24 * 3600000L
        }
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MMM dd", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val random = Random(42 + metalType.ordinal)
        val history = mutableListOf<ShanghaiComparisonPoint>()

        var runningWest = westernPrice * 0.94
        for (i in 0 until count) {
            val ts = now - ((count - 1 - i) * stepMs)
            runningWest += (random.nextDouble() - 0.47) * (runningWest * 0.015)
            val premMult = if (isSilver) (1.08 + random.nextDouble() * 0.06) else (1.01 + random.nextDouble() * 0.02)
            val runningShanghai = runningWest * premMult
            val premVal = ((runningShanghai - runningWest) / runningWest) * 100.0

            history.add(
                ShanghaiComparisonPoint(
                    timestamp = ts,
                    dateStr = dateFormat.format(Date(ts)),
                    shanghaiPriceUsd = runningShanghai,
                    westernPriceUsd = runningWest,
                    premiumPercent = premVal
                )
            )
        }

        return ShanghaiMarketData(
            metalType = metalType,
            shanghaiPriceUsdPerOz = shanghaiPrice,
            westernPriceUsdPerOz = westernPrice,
            premiumUsdPerOz = premiumUsd,
            premiumPercent = premiumPct,
            sgePriceCnyPerGram = sgeCnyPerGram,
            usdCnyRate = usdCnyRate,
            shfeInventoryTonnes = shfeTonnes,
            shfeInventory30dChangeTonnes = shfe30dChange,
            comparisonHistory = history
        )
    }

    fun getComexInventoryData(
        metalType: MetalType = MetalType.SILVER,
        timeRange: TimeRange = TimeRange.ONE_MONTH
    ): ComexInventoryData {
        val isSilver = metalType == MetalType.SILVER
        val price = if (isSilver) 53.89 else 4016.71
        val changePct = if (isSilver) -0.02 else 0.15
        val totalOz = if (isSilver) 337500000.0 else 17400000.0
        val registeredOz = if (isSilver) 99800000.0 else 8200000.0
        val eligibleOz = if (isSilver) 237700000.0 else 9200000.0
        val dailyChangeOz = if (isSilver) -600000.0 else -48000.0
        val drawdownAth = if (isSilver) -34.8 else -43.4
        val change30d = if (isSilver) -4200000.0 else -320000.0
        val peakReg = if (isSilver) 152000000.0 else 14500000.0

        val vaults = if (isSilver) {
            listOf(
                ComexVault("JPMorgan Chase Bank, N.A.", "New York, NY", 105400000.0, 38200000.0, 67200000.0, 31.2),
                ComexVault("Brink's Inc.", "Springfield Gardens, NY", 98500000.0, 28500000.0, 70000000.0, 29.2),
                ComexVault("HSBC Bank USA, N.A.", "New York, NY", 62100000.0, 18100000.0, 44000000.0, 18.4),
                ComexVault("Loomis International", "Jamaica, NY", 34200000.0, 9400000.0, 24800000.0, 10.1),
                ComexVault("CNT Depository LLC", "Bridgewater, MA", 22800000.0, 4200000.0, 18600000.0, 6.8),
                ComexVault("Delaware Depository", "Wilmington, DE", 14500000.0, 1400000.0, 13100000.0, 4.3)
            )
        } else {
            listOf(
                ComexVault("Brink's Inc.", "Springfield Gardens, NY", 6800000.0, 3400000.0, 3400000.0, 39.1),
                ComexVault("JPMorgan Chase Bank, N.A.", "New York, NY", 5200000.0, 2600000.0, 2600000.0, 29.9),
                ComexVault("HSBC Bank USA, N.A.", "New York, NY", 3100000.0, 1300000.0, 1800000.0, 17.8),
                ComexVault("Loomis International", "Jamaica, NY", 1400000.0, 600000.0, 800000.0, 8.0),
                ComexVault("Malca-Amit Armored", "Jamaica, NY", 900000.0, 300000.0, 600000.0, 5.2)
            )
        }

        val deliveries = listOf(
            ComexDeliveryNotice("Oct 24, 2026", "DEC 26", 148, 148, 740000.0, 18450000.0),
            ComexDeliveryNotice("Oct 23, 2026", "DEC 26", 92, 92, 460000.0, 17710000.0),
            ComexDeliveryNotice("Oct 22, 2026", "DEC 26", 215, 215, 1075000.0, 17250000.0),
            ComexDeliveryNotice("Oct 21, 2026", "DEC 26", 80, 80, 400000.0, 16175000.0),
            ComexDeliveryNotice("Oct 20, 2026", "DEC 26", 310, 310, 1550000.0, 15775000.0)
        )

        val count = when (timeRange) {
            TimeRange.ONE_DAY -> 12
            TimeRange.ONE_WEEK -> 7
            TimeRange.ONE_MONTH -> 30
            TimeRange.THREE_MONTHS -> 60
            TimeRange.SIX_MONTHS -> 90
            TimeRange.ONE_YEAR -> 120
            TimeRange.ALL -> 180
        }
        val stepMs = when (timeRange) {
            TimeRange.ONE_DAY -> 2 * 3600000L
            TimeRange.ONE_WEEK -> 24 * 3600000L
            TimeRange.ONE_MONTH -> 24 * 3600000L
            TimeRange.THREE_MONTHS -> 2 * 24 * 3600000L
            TimeRange.SIX_MONTHS -> 4 * 24 * 3600000L
            TimeRange.ONE_YEAR -> 7 * 24 * 3600000L
            TimeRange.ALL -> 30 * 24 * 3600000L
        }
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MMM dd", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val random = Random(99 + metalType.ordinal)
        val history = mutableListOf<ComexHistoricalPoint>()

        var runningReg = registeredOz * 1.08
        var runningElig = eligibleOz * 0.98
        var runningPrice = price * 0.92

        for (i in 0 until count) {
            val ts = now - ((count - 1 - i) * stepMs)
            runningReg -= (random.nextDouble() * 120000)
            runningElig += ((random.nextDouble() - 0.45) * 80000)
            runningPrice += ((random.nextDouble() - 0.48) * (runningPrice * 0.01))

            history.add(
                ComexHistoricalPoint(
                    timestamp = ts,
                    dateStr = dateFormat.format(Date(ts)),
                    registeredOunces = runningReg,
                    eligibleOunces = runningElig,
                    spotPriceUsd = runningPrice
                )
            )
        }

        return ComexInventoryData(
            metalType = metalType,
            priceUsdPerOz = price,
            changePercent24h = changePct,
            totalVaultOunces = totalOz,
            registeredOunces = registeredOz,
            eligibleOunces = eligibleOz,
            dailyVaultNetChangeOunces = dailyChangeOz,
            drawdownAthPercent = drawdownAth,
            change30dOunces = change30d,
            peakRegisteredOunces = peakReg,
            vaults = vaults,
            recentDeliveries = deliveries,
            history = history
        )
    }

    fun getIndiaData(
        metalType: MetalType = MetalType.SILVER,
        timeRange: TimeRange = TimeRange.ONE_MONTH
    ): IndiaMarketData {
        val isSilver = metalType == MetalType.SILVER
        val westernPrice = if (isSilver) 53.88 else 4016.71
        val indiaPrice = if (isSilver) 63.56 else 4650.00
        val premiumUsd = indiaPrice - westernPrice
        val premiumPct = (premiumUsd / westernPrice) * 100.0
        val mcxInr = if (isSilver) 88450.0 else 84200.0
        val inrQuoteUnit = if (isSilver) "₹/kg" else "₹/10g"
        val usdInrRate = 86.40
        val importDuty = 15.0
        val gst = 3.0
        val totalTaxFloor = 18.45

        val taxBreakdown = if (isSilver) {
            listOf(
                IndiaTaxBreakdownItem("Benchmark Spot Price", "US Spot (COMEX)", 53.88),
                IndiaTaxBreakdownItem("Import Customs Duty", "15.0% Tariff Hiked in 2026", 8.08),
                IndiaTaxBreakdownItem("GST on Duty-Inclusive", "3.0% Bullion Goods & Services", 1.86),
                IndiaTaxBreakdownItem("Port Clearance & AIDC", "Agriculture Infra Cess + Handling", 0.65),
                IndiaTaxBreakdownItem("Total Landed Cost Floor", "Structural Minimum Import Basis", 64.47, isTotal = true)
            )
        } else {
            listOf(
                IndiaTaxBreakdownItem("Benchmark Spot Price", "US Spot (COMEX)", 4016.71),
                IndiaTaxBreakdownItem("Import Customs Duty", "15.0% Tariff Hiked in 2026", 602.50),
                IndiaTaxBreakdownItem("GST on Duty-Inclusive", "3.0% Bullion Goods & Services", 138.58),
                IndiaTaxBreakdownItem("Port Clearance & AIDC", "Agriculture Infra Cess + Handling", 42.00),
                IndiaTaxBreakdownItem("Total Landed Cost Floor", "Structural Minimum Import Basis", 4799.79, isTotal = true)
            )
        }

        val count = when (timeRange) {
            TimeRange.ONE_DAY -> 12
            TimeRange.ONE_WEEK -> 7
            TimeRange.ONE_MONTH -> 30
            TimeRange.THREE_MONTHS -> 60
            TimeRange.SIX_MONTHS -> 90
            TimeRange.ONE_YEAR -> 120
            TimeRange.ALL -> 180
        }
        val stepMs = when (timeRange) {
            TimeRange.ONE_DAY -> 2 * 3600000L
            TimeRange.ONE_WEEK -> 24 * 3600000L
            TimeRange.ONE_MONTH -> 24 * 3600000L
            TimeRange.THREE_MONTHS -> 2 * 24 * 3600000L
            TimeRange.SIX_MONTHS -> 4 * 24 * 3600000L
            TimeRange.ONE_YEAR -> 7 * 24 * 3600000L
            TimeRange.ALL -> 30 * 24 * 3600000L
        }
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MMM dd", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        val random = Random(77 + metalType.ordinal)
        val history = mutableListOf<IndiaComparisonPoint>()

        var runningWest = westernPrice * 0.95
        for (i in 0 until count) {
            val ts = now - ((count - 1 - i) * stepMs)
            runningWest += (random.nextDouble() - 0.48) * (runningWest * 0.014)
            val premMult = if (isSilver) (1.14 + random.nextDouble() * 0.06) else (1.12 + random.nextDouble() * 0.04)
            val runningIndia = runningWest * premMult
            val premVal = ((runningIndia - runningWest) / runningWest) * 100.0

            history.add(
                IndiaComparisonPoint(
                    timestamp = ts,
                    dateStr = dateFormat.format(Date(ts)),
                    indiaPriceUsd = runningIndia,
                    westernPriceUsd = runningWest,
                    premiumPercent = premVal
                )
            )
        }

        return IndiaMarketData(
            metalType = metalType,
            indiaPriceUsdPerOz = indiaPrice,
            westernPriceUsdPerOz = westernPrice,
            premiumUsdPerOz = premiumUsd,
            premiumPercent = premiumPct,
            mcxPriceInr = mcxInr,
            inrQuoteUnit = inrQuoteUnit,
            usdInrRate = usdInrRate,
            importDutyPercent = importDuty,
            gstPercent = gst,
            totalTaxFloorPercent = totalTaxFloor,
            taxBreakdown = taxBreakdown,
            comparisonHistory = history
        )
    }
}
