import SwiftUI

struct QuickInsightCardView: View {
    let insight: MarketInsight
    
    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            // Gold / Silver Ratio Section
            HStack(alignment: .center) {
                HStack(spacing: 8) {
                    ZStack {
                        Circle()
                            .fill(Color.metalGold.opacity(0.15))
                            .frame(width: 30, height: 30)
                        Text("÷")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.metalGold)
                    }
                    
                    VStack(alignment: .leading, spacing: 1) {
                        Text("Gold / Silver Ratio")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                        Text("Ounces of silver to purchase 1 oz of gold")
                            .font(.system(size: 11))
                            .foregroundColor(.metalTextSecondaryDark)
                    }
                }
                
                Spacer()
                
                Text(CurrencyFormatter.formatRatio(insight.goldSilverRatio))
                    .font(.system(size: 20, weight: .black))
                    .foregroundColor(.metalGold)
            }
            
            // Ratio Progress Bar
            let ratioFraction = max(0.0, min(1.0, (insight.goldSilverRatio - 50.0) / 50.0))
            VStack(spacing: 4) {
                GeometryReader { barGeo in
                    ZStack(alignment: .leading) {
                        Capsule()
                            .fill(Color.metalElevatedDark)
                            .frame(height: 6)
                        Capsule()
                            .fill(Color.metalGold)
                            .frame(width: max(8, barGeo.size.width * CGFloat(ratioFraction)), height: 6)
                    }
                }
                .frame(height: 6)
                
                HStack {
                    Text("50x (Silver Outperforming)")
                        .font(.system(size: 10))
                        .foregroundColor(.metalTextMutedDark)
                    Spacer()
                    Text("100x (Gold Outperforming)")
                        .font(.system(size: 10))
                        .foregroundColor(.metalTextMutedDark)
                }
            }
            
            // 2 Stat Highlights: Gold/Platinum & Weekly Leader
            HStack(spacing: 10) {
                // Au / Pt Ratio
                VStack(alignment: .leading, spacing: 3) {
                    Text("Au / Pt Ratio")
                        .font(.system(size: 11))
                        .foregroundColor(.metalTextSecondaryDark)
                    Text(CurrencyFormatter.formatRatio(insight.goldPlatinumRatio))
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(10)
                .background(Color.metalElevatedDark)
                .cornerRadius(10)
                
                // Weekly Leader
                VStack(alignment: .leading, spacing: 3) {
                    Text("7D Leader")
                        .font(.system(size: 11))
                        .foregroundColor(.metalTextSecondaryDark)
                    Text("\(insight.weeklyBestPerformer.displayName) (+\(String(format: "%.2f", insight.weeklyBestPerformancePercent))%)")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.changePositive)
                        .lineLimit(1)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(10)
                .background(Color.metalElevatedDark)
                .cornerRadius(10)
            }
        }
        .padding(16)
        .background(Color.metalSurfaceDark)
        .cornerRadius(16)
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(Color.metalBorderDark, lineWidth: 1)
        )
    }
}
