package org.metalscope.app.data.model

data class MarketInsight(
    val goldSilverRatio: Double,
    val goldSilverHistoricalMedian: Double = 75.0,
    val goldPlatinumRatio: Double,
    val platinumPalladiumSpreadUsd: Double,
    val weeklyBestPerformer: MetalType,
    val weeklyBestPerformancePercent: Double,
    val marketSessionSummary: String,
    val tradingHoursCommentary: String
)
