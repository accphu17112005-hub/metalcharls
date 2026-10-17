import SwiftUI

struct FeaturedGoldCardView: View {
    let quote: PriceQuote
    let currency: Currency
    let unit: WeightUnit
    let isFavorite: Bool
    let onFavoriteToggle: () -> Void
    let onClick: () -> Void
    
    var body: some View {
        let price = quote.calculatePrice(currency: currency, unit: unit)
        let change = quote.calculateChange(currency: currency, unit: unit)
        let high = quote.calculateHigh(currency: currency, unit: unit)
        let low = quote.calculateLow(currency: currency, unit: unit)
        let isPositive = quote.changePercent24h >= 0
        
        let rangeSpan = max(0.001, high - low)
        let currentPosition = max(0.0, min(1.0, (price - low) / rangeSpan))
        
        Button(action: onClick) {
            VStack(alignment: .leading, spacing: 16) {
                // Top Row: Title, Symbol & Star
                HStack(alignment: .center) {
                    HStack(spacing: 10) {
                        ZStack {
                            Circle()
                                .fill(Color.metalGold.opacity(0.2))
                                .frame(width: 38, height: 38)
                            Circle()
                                .stroke(Color.metalGold.opacity(0.5), lineWidth: 1)
                                .frame(width: 38, height: 38)
                            Text("Au")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.metalGold)
                        }
                        
                        VStack(alignment: .leading, spacing: 2) {
                            HStack(spacing: 6) {
                                Text("Gold")
                                    .font(.system(size: 20, weight: .bold))
                                    .foregroundColor(.metalTextPrimaryDark)
                                
                                Text("FEATURED")
                                    .font(.system(size: 9, weight: .bold))
                                    .foregroundColor(.metalGold)
                                    .padding(.horizontal, 6)
                                    .padding(.vertical, 2)
                                    .background(Color.metalGold.opacity(0.2))
                                    .cornerRadius(4)
                            }
                            
                            Text("XAU • \(unit.displayName)")
                                .font(.system(size: 12))
                                .foregroundColor(.metalTextSecondaryDark)
                        }
                    }
                    
                    Spacer()
                    
                    HStack(spacing: 6) {
                        Button(action: {
                            onFavoriteToggle()
                        }) {
                            Image(systemName: isFavorite ? "star.fill" : "star")
                                .font(.system(size: 18))
                                .foregroundColor(isFavorite ? .metalGold : .metalTextSecondaryDark)
                                .frame(width: 32, height: 32)
                        }
                        .buttonStyle(PlainButtonStyle())
                        
                        Image(systemName: "chevron.right")
                            .font(.system(size: 14, weight: .semibold))
                            .foregroundColor(.metalTextMutedDark)
                    }
                }
                
                // Middle Row: Price & 24h Change & Sparkline
                HStack(alignment: .bottom) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text(CurrencyFormatter.formatPrice(amount: price, currency: currency))
                            .font(.system(size: 28, weight: .black))
                            .foregroundColor(.metalTextPrimaryDark)
                        
                        HStack(spacing: 4) {
                            Image(systemName: isPositive ? "arrow.up.right" : "arrow.down.right")
                                .font(.system(size: 12, weight: .bold))
                            Text(CurrencyFormatter.formatChange(amount: change, currency: currency))
                                .font(.system(size: 13, weight: .bold))
                            Text("(\(CurrencyFormatter.formatPercent(quote.changePercent24h)))")
                                .font(.system(size: 13, weight: .bold))
                        }
                        .foregroundColor(isPositive ? .changePositive : .changeNegative)
                    }
                    
                    Spacer()
                    
                    // Sparkline
                    SparklineView(
                        data: quote.sparkline,
                        isPositive: isPositive,
                        showFill: true
                    )
                    .frame(width: 110, height: 50)
                }
                
                // Bottom: 24h Low to 24h High Progress Bar
                VStack(spacing: 6) {
                    HStack {
                        Text("24h Low: \(CurrencyFormatter.formatPrice(amount: low, currency: currency))")
                            .font(.system(size: 11))
                            .foregroundColor(.metalTextMutedDark)
                        Spacer()
                        Text("24h High: \(CurrencyFormatter.formatPrice(amount: high, currency: currency))")
                            .font(.system(size: 11))
                            .foregroundColor(.metalTextMutedDark)
                    }
                    
                    GeometryReader { barGeo in
                        ZStack(alignment: .leading) {
                            Capsule()
                                .fill(Color.metalElevatedDark)
                                .frame(height: 5)
                            Capsule()
                                .fill(Color.metalGold)
                                .frame(width: max(8, barGeo.size.width * CGFloat(currentPosition)), height: 5)
                        }
                    }
                    .frame(height: 5)
                }
            }
            .padding(20)
            .background(Color.metalSurfaceDark)
            .cornerRadius(20)
            .overlay(
                RoundedRectangle(cornerRadius: 20)
                    .stroke(
                        LinearGradient(
                            gradient: Gradient(colors: [Color.metalGold.opacity(0.6), Color.metalBorderDark]),
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        ),
                        lineWidth: 1.5
                    )
            )
        }
        .buttonStyle(PlainButtonStyle())
    }
}
