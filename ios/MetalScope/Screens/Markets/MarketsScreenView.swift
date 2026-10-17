import SwiftUI

struct MarketsScreenView: View {
    @ObservedObject var viewModel: MainViewModel
    let onNavigateToMetal: (String) -> Void
    
    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                // Title Bar
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Markets")
                            .font(.system(size: 26, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                        Text("Live precious metals benchmarks & ratios")
                            .font(.system(size: 12))
                            .foregroundColor(.metalTextSecondaryDark)
                    }
                    Spacer()
                    
                    // Card vs List toggle button
                    Button(action: {
                        viewModel.setCardView(!viewModel.isCardView)
                        HapticManager.selection()
                    }) {
                        Image(systemName: viewModel.isCardView ? "list.bullet" : "square.grid.2x2")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.metalGold)
                            .padding(8)
                            .background(Color.metalElevatedDark)
                            .cornerRadius(8)
                            .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color.metalBorderDark, lineWidth: 1))
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 10)
                
                // Search Bar
                HStack {
                    Image(systemName: "magnifyingglass")
                        .foregroundColor(.metalTextSecondaryDark)
                    TextField("Search symbol (XAU, XAG...) or name", text: $viewModel.searchQuery)
                        .font(.system(size: 14))
                        .foregroundColor(.metalTextPrimaryDark)
                    
                    if !viewModel.searchQuery.isEmpty {
                        Button(action: { viewModel.searchQuery = "" }) {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundColor(.metalTextMutedDark)
                        }
                    }
                }
                .padding(10)
                .background(Color.metalSurfaceDark)
                .cornerRadius(12)
                .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.metalBorderDark, lineWidth: 1))
                .padding(.horizontal, 16)
                
                // Filter & Sort Controls
                HStack {
                    // Filter Chips
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(MarketFilter.allCases) { filter in
                                let isSelected = filter == viewModel.marketFilter
                                Button(action: {
                                    viewModel.marketFilter = filter
                                    HapticManager.selection()
                                }) {
                                    Text(filter.displayName)
                                        .font(.system(size: 12, weight: .semibold))
                                        .foregroundColor(isSelected ? .metalBackgroundDark : .metalTextSecondaryDark)
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 6)
                                        .background(isSelected ? Color.metalGold : Color.metalElevatedDark)
                                        .cornerRadius(8)
                                        .overlay(
                                            RoundedRectangle(cornerRadius: 8)
                                                .stroke(isSelected ? Color.metalGold : Color.metalBorderDark, lineWidth: 1)
                                        )
                                }
                                .buttonStyle(PlainButtonStyle())
                            }
                        }
                    }
                    
                    Spacer()
                    
                    // Sort Menu
                    Menu {
                        ForEach(SortOption.allCases) { option in
                            Button(action: {
                                viewModel.sortOption = option
                                HapticManager.selection()
                            }) {
                                HStack {
                                    Text(option.displayName)
                                    if option == viewModel.sortOption {
                                        Image(systemName: "checkmark")
                                    }
                                }
                            }
                        }
                    } label: {
                        HStack(spacing: 4) {
                            Image(systemName: "arrow.up.arrow.down")
                                .font(.system(size: 11, weight: .bold))
                            Text(viewModel.sortOption.displayName)
                                .font(.system(size: 11, weight: .semibold))
                        }
                        .foregroundColor(.metalGold)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 6)
                        .background(Color.metalElevatedDark)
                        .cornerRadius(8)
                        .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color.metalBorderDark, lineWidth: 1))
                    }
                }
                .padding(.horizontal, 16)
                
                // List Content
                if viewModel.marketFilter == .ratios {
                    // Dedicated Macro Ratios View
                    QuickInsightCardView(insight: viewModel.insights)
                        .padding(.horizontal, 16)
                } else {
                    if viewModel.filteredQuotes.isEmpty {
                        VStack(spacing: 12) {
                            Image(systemName: "magnifyingglass")
                                .font(.system(size: 36))
                                .foregroundColor(.metalTextMutedDark)
                            Text("No metals match '\(viewModel.searchQuery)'")
                                .font(.system(size: 15, weight: .medium))
                                .foregroundColor(.metalTextSecondaryDark)
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 60)
                    } else if viewModel.isCardView {
                        // Rich Card View
                        VStack(spacing: 12) {
                            ForEach(viewModel.filteredQuotes) { quote in
                                MetalPriceCardView(
                                    quote: quote,
                                    currency: viewModel.currency,
                                    unit: viewModel.unit,
                                    isFavorite: viewModel.watchlistSymbols.contains(quote.symbol),
                                    onFavoriteToggle: { viewModel.toggleWatchlist(symbol: quote.symbol) },
                                    onClick: { onNavigateToMetal(quote.symbol) }
                                )
                            }
                        }
                        .padding(.horizontal, 16)
                    } else {
                        // Compact List View
                        VStack(spacing: 1) {
                            ForEach(viewModel.filteredQuotes) { quote in
                                CompactMetalRowView(
                                    quote: quote,
                                    currency: viewModel.currency,
                                    unit: viewModel.unit,
                                    isFavorite: viewModel.watchlistSymbols.contains(quote.symbol),
                                    onFavoriteToggle: { viewModel.toggleWatchlist(symbol: quote.symbol) },
                                    onClick: { onNavigateToMetal(quote.symbol) }
                                )
                                if quote.symbol != viewModel.filteredQuotes.last?.symbol {
                                    Divider().background(Color.metalBorderDark.opacity(0.4))
                                }
                            }
                        }
                        .background(Color.metalSurfaceDark)
                        .cornerRadius(16)
                        .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.metalBorderDark, lineWidth: 1))
                        .padding(.horizontal, 16)
                    }
                }
            }
            .padding(.bottom, 24)
        }
        .background(Color.metalBackgroundDark.ignoresSafeArea())
    }
}

struct CompactMetalRowView: View {
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
            HStack(spacing: 12) {
                // Chemical Circle
                ZStack {
                    Circle()
                        .fill(metalColor.opacity(0.15))
                        .frame(width: 34, height: 34)
                    Text(quote.metalType.chemicalSymbol)
                        .font(.system(size: 13, weight: .bold))
                        .foregroundColor(metalColor)
                }
                
                // Name & Symbol
                VStack(alignment: .leading, spacing: 2) {
                    Text(quote.name)
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    Text("\(quote.symbol) • \(unit.symbol)")
                        .font(.system(size: 11))
                        .foregroundColor(.metalTextSecondaryDark)
                }
                
                Spacer()
                
                // Sparkline
                SparklineView(
                    data: quote.sparkline,
                    isPositive: isPositive,
                    showFill: false
                )
                .frame(width: 60, height: 26)
                
                Spacer()
                
                // Price & Change
                VStack(alignment: .trailing, spacing: 2) {
                    Text(CurrencyFormatter.formatPrice(amount: price, currency: currency))
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    
                    Text(CurrencyFormatter.formatPercent(quote.changePercent24h))
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(isPositive ? .changePositive : .changeNegative)
                }
                
                // Star
                Button(action: onFavoriteToggle) {
                    Image(systemName: isFavorite ? "star.fill" : "star")
                        .font(.system(size: 15))
                        .foregroundColor(isFavorite ? .metalGold : .metalTextMutedDark)
                        .frame(width: 28, height: 28)
                }
                .buttonStyle(PlainButtonStyle())
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 12)
        }
        .buttonStyle(PlainButtonStyle())
    }
}
