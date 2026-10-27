import SwiftUI

struct HomeScreenView: View {
    @ObservedObject var viewModel: MainViewModel
    let onNavigateToMetal: (String) -> Void
    let onNavigateToSearch: () -> Void
    let onNavigateToWatchlist: () -> Void
    var onNavigateToTab: ((MarketTab) -> Void)? = nil
    var onNavigateToSettings: (() -> Void)? = nil
    
    @State private var chartMetal: MetalType = .gold
    @State private var chartTimeRange: TimeRange = .oneDay
    @State private var showCurrencyPicker: Bool = false
    @State private var showUnitPicker: Bool = false
    
    var body: some View {
        let chartRecords = viewModel.getHistoricalData(metal: chartMetal, timeRange: chartTimeRange)
        
        ScrollView {
            VStack(spacing: 0) {
                // 1. Top Header Bar
                HeaderBarView(
                    watchlistCount: viewModel.watchlistSymbols.count,
                    isRefreshing: viewModel.isRefreshing,
                    onSearchClick: onNavigateToSearch,
                    onWatchlistClick: onNavigateToWatchlist,
                    onRefreshClick: { viewModel.refresh() },
                    onSettingsClick: onNavigateToSettings
                )
                
                // 2. Market Status Bar
                MarketStatusBadgeView(
                    isOpen: viewModel.quotes.first?.isMarketOpen ?? true,
                    sessionText: viewModel.quotes.first?.marketSession ?? "Markets Active",
                    lastUpdated: viewModel.lastUpdated,
                    currency: viewModel.currency,
                    unit: viewModel.unit,
                    onCurrencyClick: { showCurrencyPicker = true },
                    onUnitClick: { showUnitPicker = true }
                )
                
                // 3. Featured Gold Card
                if let goldQuote = viewModel.featuredGold {
                    FeaturedGoldCardView(
                        quote: goldQuote,
                        currency: viewModel.currency,
                        unit: viewModel.unit,
                        isFavorite: viewModel.watchlistSymbols.contains(goldQuote.symbol),
                        onFavoriteToggle: { viewModel.toggleWatchlist(symbol: goldQuote.symbol) },
                        onClick: { onNavigateToMetal(goldQuote.symbol) }
                    )
                    .padding(.horizontal, 16)
                    .padding(.top, 10)
                }
                
                // 4. Precious Metals Section Header
                HStack {
                    Text("Precious Metals")
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    Spacer()
                    Text("Spot Benchmarks")
                        .font(.system(size: 12))
                        .foregroundColor(.metalTextSecondaryDark)
                }
                .padding(.horizontal, 16)
                .padding(.top, 22)
                .padding(.bottom, 12)
                
                // 5. Precious Metals Cards (Silver, Platinum, Palladium)
                let nonGoldQuotes = viewModel.quotes.filter { $0.symbol != "XAU" }
                VStack(spacing: 10) {
                    ForEach(nonGoldQuotes) { quote in
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
                
                // 5.5 Global Premiums & Vault Stocks (Shanghai, COMEX, India)
                VStack(spacing: 12) {
                    HStack {
                        Text("Global Premiums & Vaults")
                            .font(.system(size: 20, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                        Spacer()
                        Text("Arbitrage & Reserves")
                            .font(.system(size: 12))
                            .foregroundColor(.metalTextSecondaryDark)
                    }
                    .padding(.top, 24)
                    
                    VStack(spacing: 10) {
                        // Shanghai Card
                        HomeHubCardView(
                            title: "Shanghai Premium (SGE)",
                            subtitle: "China Silver $60.65 vs US Spot $53.89/oz",
                            badge: "+12.54% China Premium",
                            badgeColor: .metalGreen,
                            iconEmoji: "🇨🇳",
                            detail: "13.82 CNY/g · SHFE Warrant Stock: 2,140t",
                            onClick: { onNavigateToTab?(.shanghai) }
                        )

                        // COMEX Silver Card
                        HomeHubCardView(
                            title: "COMEX Silver Inventory",
                            subtitle: "Physical Vault Reserves in NY & Depository",
                            badge: "337.5M oz Total",
                            badgeColor: Color(red: 96/255, green: 165/255, blue: 250/255),
                            iconEmoji: "🏛️",
                            detail: "Registered: 99.8M oz (29.6%) · Drawdown: -34.8%",
                            onClick: { onNavigateToTab?(.comex) }
                        )

                        // India Card
                        HomeHubCardView(
                            title: "India Premium (MCX)",
                            subtitle: "MCX Silver $63.56 vs Western $53.88/oz",
                            badge: "+17.96% India Premium",
                            badgeColor: .metalGreen,
                            iconEmoji: "🇮🇳",
                            detail: "₹88,450 / kg · 15% Duty + 3% GST Floor",
                            onClick: { onNavigateToTab?(.india) }
                        )
                    }
                }
                .padding(.horizontal, 16)

                // 6. Market Overview Chart Card
                VStack(alignment: .leading, spacing: 14) {
                    HStack {
                        Text("Market Overview")
                            .font(.system(size: 17, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                        
                        Spacer()
                        
                        // Metal Selector Pills
                        HStack(spacing: 6) {
                            ForEach(MetalType.allCases) { metal in
                                let isSelected = metal == chartMetal
                                Button(action: {
                                    chartMetal = metal
                                    HapticManager.selection()
                                }) {
                                    Text(metal.symbol)
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(isSelected ? metal.defaultColor : .metalTextSecondaryDark)
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 4)
                                        .background(isSelected ? metal.defaultColor.opacity(0.2) : Color.metalElevatedDark)
                                        .cornerRadius(8)
                                        .overlay(
                                            RoundedRectangle(cornerRadius: 8)
                                                .stroke(isSelected ? metal.defaultColor : Color.metalBorderDark, lineWidth: 1)
                                        )
                                }
                                .buttonStyle(PlainButtonStyle())
                            }
                        }
                    }
                    
                    InteractiveChartView(
                        records: chartRecords,
                        currency: viewModel.currency,
                        unit: viewModel.unit,
                        selectedTimeRange: chartTimeRange,
                        onTimeRangeSelected: { chartTimeRange = it },
                        lineColor: chartMetal.defaultColor
                    )
                }
                .padding(16)
                .background(Color.metalSurfaceDark)
                .cornerRadius(20)
                .overlay(
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(Color.metalBorderDark, lineWidth: 1)
                )
                .padding(.horizontal, 16)
                .padding(.top, 24)
                
                // 7. Quick Market Insights Card
                QuickInsightCardView(insight: viewModel.insights)
                    .padding(.horizontal, 16)
                    .padding(.top, 20)
                    .padding(.bottom, 28)
            }
        }
        .background(Color.metalBackgroundDark.ignoresSafeArea())
        .sheet(isPresented: $showCurrencyPicker) {
            CurrencyPickerSheet(selected: viewModel.currency) { newCur in
                viewModel.setCurrency(newCur)
                showCurrencyPicker = false
            }
        }
        .sheet(isPresented: $showUnitPicker) {
            UnitPickerSheet(selected: viewModel.unit) { newUnit in
                viewModel.setWeightUnit(newUnit)
                showUnitPicker = false
            }
        }
    }
}

// MARK: - HomeHubCardView
struct HomeHubCardView: View {
    let title: String
    let subtitle: String
    let badge: String
    let badgeColor: Color
    let iconEmoji: String
    let detail: String
    let onClick: () -> Void

    var body: some View {
        Button(action: {
            HapticManager.lightImpact()
            onClick()
        }) {
            HStack(spacing: 12) {
                // Icon Box
                Text(iconEmoji)
                    .font(.system(size: 20))
                    .frame(width: 44, height: 44)
                    .background(Color.metalElevatedDark)
                    .cornerRadius(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.metalBorderDark, lineWidth: 1)
                    )

                // Info
                VStack(alignment: .leading, spacing: 3) {
                    HStack(spacing: 6) {
                        Text(title)
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)

                        Text(badge)
                            .font(.system(size: 10, weight: .bold))
                            .foregroundColor(badgeColor)
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(badgeColor.opacity(0.16))
                            .clipShape(Capsule())
                    }

                    Text(subtitle)
                        .font(.system(size: 12))
                        .foregroundColor(.metalTextSecondaryDark)
                        .lineLimit(1)

                    Text(detail)
                        .font(.system(size: 11))
                        .foregroundColor(.metalTextMutedDark)
                        .lineLimit(1)
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundColor(.metalTextMutedDark)
            }
            .padding(14)
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
