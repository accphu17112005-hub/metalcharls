import Foundation

struct MarketInsight: Codable, Equatable {
    let goldSilverRatio: Double
    var goldSilverHistoricalMedian: Double = 75.0
    let goldPlatinumRatio: Double
    let platinumPalladiumSpreadUsd: Double
    let weeklyBestPerformer: MetalType
    let weeklyBestPerformancePercent: Double
    let marketSessionSummary: String
    let tradingHoursCommentary: String
}
