import SwiftUI

struct DataSourcesScreenView: View {
    let onNavigateBack: () -> Void
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                // Top Bar
                HStack {
                    Button(action: onNavigateBack) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                            .frame(width: 36, height: 36)
                            .background(Color.metalElevatedDark)
                            .clipShape(Circle())
                    }
                    .buttonStyle(PlainButtonStyle())
                    
                    Spacer()
                    
                    Text("Data Sources & Methodology")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    
                    Spacer()
                    
                    Color.clear.frame(width: 36, height: 36)
                }
                .padding(.horizontal, 16)
                .padding(.top, 10)
                
                // Content Cards
                VStack(spacing: 14) {
                    MethodologyCard(
                        title: "1. LBMA Auction Fixings",
                        subtitle: "London Bullion Market Association (LBMA)",
                        content: "Gold and Silver global auction fixings take place twice daily (LBMA Gold AM/PM at 10:30 and 15:00 UTC) providing the benchmark reference price for institutional settlements worldwide."
                    )
                    
                    MethodologyCard(
                        title: "2. COMEX Futures Exchange",
                        subtitle: "CME Group (New York / Chicago)",
                        content: "Active front-month futures contracts (GC for Gold, SI for Silver, PL for Platinum, PA for Palladium) provide high-frequency intraday volume, bid/ask depth, and liquidity."
                    )
                    
                    MethodologyCard(
                        title: "3. Spot Conversion Formula",
                        subtitle: "Exact Physical & Forex Calculation",
                        content: "Quotes are computed from fine troy ounce benchmarks ($1\\text{ oz t} = 31.1034768\\text{ g}$). Real-time currency conversions reflect institutional interbank forex midpoint rates."
                    )
                    
                    MethodologyCard(
                        title: "4. Fail-Safe Dual Feed",
                        subtitle: "Yahoo Finance & Gold-API Fallback",
                        content: "MetalScope utilizes redundant endpoints with automatic in-memory caching to guarantee seamless charting and offline accessibility even under intermittent network conditions."
                    )
                }
                .padding(.horizontal, 16)
            }
            .padding(.bottom, 30)
        }
        .background(Color.metalBackgroundDark.ignoresSafeArea())
        .navigationBarHidden(true)
    }
}

struct MethodologyCard: View {
    let title: String
    let subtitle: String
    let content: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title)
                .font(.system(size: 16, weight: .bold))
                .foregroundColor(.metalGold)
            Text(subtitle)
                .font(.system(size: 12, weight: .semibold))
                .foregroundColor(.metalTextSecondaryDark)
            Text(content)
                .font(.system(size: 13))
                .lineSpacing(4)
                .foregroundColor(.metalTextPrimaryDark.opacity(0.9))
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.metalSurfaceDark)
        .cornerRadius(16)
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.metalBorderDark, lineWidth: 1))
    }
}
