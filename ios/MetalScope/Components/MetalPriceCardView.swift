import SwiftUI

struct MetalPriceCardView: View {
    let quote: PriceQuote
    let currency: Currency
    let unit: WeightUnit
    let isFavorite: Bool
    let onFavoriteToggle: () -> Void
    let onClick: () -> Void
    
    var body: some View {
        let price = quote.calculatePrice(currency: currency, unit: unit)
        let isPositive = quote.changePercent24h >= 0
        let metalColor = quote.metalType.defaultColor
        
        Button(action: onClick) {
            VStack(alignment: .leading, spacing: 12) {
                // Header: Symbol avatar, Name, Symbol, and Star
                HStack(alignment: .center) {
                    HStack(spacing: 8) {
                        ZStack {
                            Circle()
                                .fill(metalColor.opacity(0.15))
                                .frame(width: 32, height: 32)
                            Circle()
                                .stroke(metalColor.opacity(0.4), lineWidth: 1)
                                .frame(width: 32, height: 32)
                            Text(quote.metalType.chemicalSymbol)
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(metalColor)
                        }
                        
                        VStack(alignment: .leading, spacing: 1) {
                            Text(quote.name)
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                            Text("\(quote.symbol) • \(unit.symbol)")
                                .font(.system(size: 11))
                                .foregroundColor(.metalTextSecondaryDark)
                        }
                    }
                    
                    Spacer()
                    
                    Button(action: onFavoriteToggle) {
                        Image(systemName: isFavorite ? "star.fill" : "star")
                            .font(.system(size: 16))
                            .foregroundColor(isFavorite ? .metalGold : .metalTextMutedDark)
                            .frame(width: 28, height: 28)
                    }
                    .buttonStyle(PlainButtonStyle())
                }
                
                // Body: Price and Sparkline
                HStack(alignment: .bottom) {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(CurrencyFormatter.formatPrice(amount: price, currency: currency))
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                        
                        HStack(spacing: 3) {
                            Image(systemName: isPositive ? "arrow.up.right" : "arrow.down.right")
                                .font(.system(size: 10, weight: .bold))
                            Text(CurrencyFormatter.formatPercent(quote.changePercent24h))
                                .font(.system(size: 12, weight: .semibold))
                        }
                        .foregroundColor(isPositive ? .changePositive : .changeNegative)
                    }
                    
                    Spacer()
                    
                    SparklineView(
                        data: quote.sparkline,
                        isPositive: isPositive,
                        showFill: true
                    )
                    .frame(width: 80, height: 36)
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
        .buttonStyle(PlainButtonStyle())
    }
}
