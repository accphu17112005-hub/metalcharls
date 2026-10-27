import SwiftUI

struct MetalDetailScreenView: View {
    let symbol: String
    @ObservedObject var viewModel: MainViewModel
    let onNavigateBack: () -> Void
    var onNavigateToTab: ((MarketTab) -> Void)? = nil
    
    @State private var timeRange: TimeRange = .oneMonth
    @State private var holdingsQuantity: String = "1.0"
    @State private var showCurrencyPicker: Bool = false
    @State private var showUnitPicker: Bool = false
    
    private var quote: PriceQuote? {
        viewModel.quotes.first { $0.symbol.uppercased() == symbol.uppercased() } ?? viewModel.featuredGold
    }
    
    var body: some View {
        guard let q = quote else {
            VStack {
                Text("Metal quote not found")
                    .foregroundColor(.metalTextSecondaryDark)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.metalBackgroundDark)
        }
        
        let metal = q.metalType
        let price = q.calculatePrice(currency: viewModel.currency, unit: viewModel.unit)
        let change = q.calculateChange(currency: viewModel.currency, unit: viewModel.unit)
        let high = q.calculateHigh(currency: viewModel.currency, unit: viewModel.unit)
        let low = q.calculateLow(currency: viewModel.currency, unit: viewModel.unit)
        let bid = q.calculateBid(currency: viewModel.currency, unit: viewModel.unit)
        let ask = q.calculateAsk(currency: viewModel.currency, unit: viewModel.unit)
        let isPos = q.changePercent24h >= 0
        let isFav = viewModel.watchlistSymbols.contains(q.symbol)
        
        let records = viewModel.getHistoricalData(metal: metal, timeRange: timeRange)
        
        let parsedQty = Double(holdingsQuantity.replacingOccurrences(of: ",", with: ".")) ?? 0.0
        let totalHoldingsValue = parsedQty * price
        
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                // Top Navigation Bar
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
                    
                    HStack(spacing: 8) {
                        Button(action: { showCurrencyPicker = true }) {
                            Text("\(viewModel.currency.code)")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(.metalGold)
                                .padding(.horizontal, 10)
                                .padding(.vertical, 6)
                                .background(Color.metalElevatedDark)
                                .cornerRadius(8)
                        }
                        
                        Button(action: { showUnitPicker = true }) {
                            Text(viewModel.unit.symbol)
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                                .padding(.horizontal, 10)
                                .padding(.vertical, 6)
                                .background(Color.metalElevatedDark)
                                .cornerRadius(8)
                        }
                        
                        Button(action: {
                            viewModel.toggleWatchlist(symbol: q.symbol)
                        }) {
                            Image(systemName: isFav ? "star.fill" : "star")
                                .font(.system(size: 18))
                                .foregroundColor(isFav ? .metalGold : .metalTextSecondaryDark)
                                .frame(width: 36, height: 36)
                                .background(Color.metalElevatedDark)
                                .clipShape(Circle())
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 10)
                
                // Metal Hero Header
                HStack(alignment: .center, spacing: 14) {
                    ZStack {
                        Circle()
                            .fill(metal.defaultColor.opacity(0.18))
                            .frame(width: 52, height: 52)
                        Circle()
                            .stroke(metal.defaultColor.opacity(0.5), lineWidth: 1.5)
                            .frame(width: 52, height: 52)
                        Text(metal.chemicalSymbol)
                            .font(.system(size: 22, weight: .black))
                            .foregroundColor(metal.defaultColor)
                    }
                    
                    VStack(alignment: .leading, spacing: 2) {
                        HStack(spacing: 8) {
                            Text(q.name)
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                            Text(q.symbol)
                                .font(.system(size: 14, weight: .bold))
                                .foregroundColor(.metalGold)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(Color.metalGold.opacity(0.15))
                                .cornerRadius(4)
                        }
                        Text("Atomic #\(metal.atomicNumber) • \(metal.standardPurity)")
                            .font(.system(size: 12))
                            .foregroundColor(.metalTextSecondaryDark)
                    }
                }
                .padding(.horizontal, 16)
                
                // Price Display Row
                VStack(alignment: .leading, spacing: 4) {
                    Text(CurrencyFormatter.formatPrice(amount: price, currency: viewModel.currency, unit: viewModel.unit))
                        .font(.system(size: 32, weight: .black))
                        .foregroundColor(.metalTextPrimaryDark)
                    
                    HStack(spacing: 6) {
                        Image(systemName: isPos ? "arrow.up.right" : "arrow.down.right")
                            .font(.system(size: 13, weight: .bold))
                        Text(CurrencyFormatter.formatChange(amount: change, currency: viewModel.currency))
                            .font(.system(size: 14, weight: .bold))
                        Text("(\(CurrencyFormatter.formatPercent(q.changePercent24h)))")
                            .font(.system(size: 14, weight: .bold))
                        Text("• 24h")
                            .font(.system(size: 13))
                            .foregroundColor(.metalTextMutedDark)
                    }
                    .foregroundColor(isPos ? .changePositive : .changeNegative)
                }
                .padding(.horizontal, 16)
                
                // Interactive Chart Card
                VStack(alignment: .leading, spacing: 12) {
                    InteractiveChartView(
                        records: records,
                        currency: viewModel.currency,
                        unit: viewModel.unit,
                        selectedTimeRange: timeRange,
                        onTimeRangeSelected: { timeRange = $0 },
                        lineColor: metal.defaultColor
                    )
                }
                .padding(16)
                .background(Color.metalSurfaceDark)
                .cornerRadius(20)
                .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.metalBorderDark, lineWidth: 1))
                .padding(.horizontal, 16)
                
                // Market Stats Grid (2 Columns)
                VStack(alignment: .leading, spacing: 12) {
                    Text("Market Execution Metrics")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                        .padding(.horizontal, 16)
                    
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
                        StatTile(label: "Bid Price", value: CurrencyFormatter.formatPrice(amount: bid, currency: viewModel.currency))
                        StatTile(label: "Ask Price", value: CurrencyFormatter.formatPrice(amount: ask, currency: viewModel.currency))
                        StatTile(label: "Spread", value: CurrencyFormatter.formatPrice(amount: q.spreadUsd, currency: viewModel.currency, decimalOverride: 4))
                        StatTile(label: "24h Volume (Est.)", value: CurrencyFormatter.formatPrice(amount: q.volume24hUsd, currency: viewModel.currency, decimalOverride: 0))
                        StatTile(label: "24h Low", value: CurrencyFormatter.formatPrice(amount: low, currency: viewModel.currency))
                        StatTile(label: "24h High", value: CurrencyFormatter.formatPrice(amount: high, currency: viewModel.currency))
                    }
                    .padding(.horizontal, 16)
                }
                
                // COMEX Futures & Basis Card
                VStack(alignment: .leading, spacing: 12) {
                    HStack {
                        Text("COMEX Futures & Basis")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)

                        Spacer()

                        Button(action: {
                            HapticManager.selection()
                            onNavigateToTab?(.comex)
                        }) {
                            HStack(spacing: 4) {
                                Text("View Vaults")
                                    .font(.system(size: 11, weight: .bold))
                                Image(systemName: "arrow.up.forward")
                                    .font(.system(size: 10, weight: .bold))
                            }
                            .foregroundColor(Color(red: 96/255, green: 165/255, blue: 250/255))
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(Color(red: 59/255, green: 130/255, blue: 246/255).opacity(0.18))
                            .cornerRadius(6)
                        }
                        .buttonStyle(PlainButtonStyle())
                    }

                    HStack {
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Active Month")
                                .font(.system(size: 11))
                                .foregroundColor(.metalTextMutedDark)
                            Text("DEC 2026")
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                        }

                        Spacer()

                        VStack(alignment: .leading, spacing: 2) {
                            Text("Futures Price")
                                .font(.system(size: 11))
                                .foregroundColor(.metalTextMutedDark)
                            Text(CurrencyFormatter.formatPrice(amount: price * 1.0045, currency: viewModel.currency))
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                        }

                        Spacer()

                        VStack(alignment: .leading, spacing: 2) {
                            Text("Basis Spread")
                                .font(.system(size: 11))
                                .foregroundColor(.metalTextMutedDark)
                            Text("+0.45%")
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.metalGreen)
                        }

                        Spacer()

                        VStack(alignment: .trailing, spacing: 2) {
                            Text("Exchange")
                                .font(.system(size: 11))
                                .foregroundColor(.metalTextMutedDark)
                            Text("CME / COMEX")
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                        }
                    }
                }
                .padding(16)
                .background(Color.metalSurfaceDark)
                .cornerRadius(18)
                .overlay(RoundedRectangle(cornerRadius: 18).stroke(Color.metalBorderDark, lineWidth: 1))
                .padding(.horizontal, 16)
                
                // Holdings Calculator Card
                VStack(alignment: .leading, spacing: 14) {
                    HStack {
                        Image(systemName: "plus.forwardslash.minus")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.metalGold)
                        Text("Holdings Value Calculator")
                            .font(.system(size: 17, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                    }
                    
                    Text("Calculate market value for your physical bullion or contracts in \(viewModel.unit.displayName).")
                        .font(.system(size: 12))
                        .foregroundColor(.metalTextSecondaryDark)
                    
                    HStack(spacing: 12) {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Quantity (\(viewModel.unit.symbol))")
                                .font(.system(size: 11, weight: .semibold))
                                .foregroundColor(.metalTextSecondaryDark)
                            
                            TextField("1.0", text: $holdingsQuantity)
                                .keyboardType(.decimalPad)
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                                .padding(10)
                                .background(Color.metalElevatedDark)
                                .cornerRadius(10)
                                .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.metalBorderDark, lineWidth: 1))
                        }
                        
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Total Value")
                                .font(.system(size: 11, weight: .semibold))
                                .foregroundColor(.metalTextSecondaryDark)
                            
                            Text(CurrencyFormatter.formatPrice(amount: totalHoldingsValue, currency: viewModel.currency))
                                .font(.system(size: 18, weight: .black))
                                .foregroundColor(.metalGold)
                                .padding(10)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .background(Color.metalElevatedDark)
                                .cornerRadius(10)
                                .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.metalGold.opacity(0.3), lineWidth: 1))
                        }
                    }
                }
                .padding(16)
                .background(Color.metalSurfaceDark)
                .cornerRadius(20)
                .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.metalBorderDark, lineWidth: 1))
                .padding(.horizontal, 16)
                
                // Fundamental Profile
                VStack(alignment: .leading, spacing: 10) {
                    Text("Fundamental Profile")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    
                    Text(metal.description)
                        .font(.system(size: 13))
                        .lineSpacing(4)
                        .foregroundColor(.metalTextSecondaryDark)
                    
                    HStack(spacing: 20) {
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Benchmark Purity")
                                .font(.system(size: 11))
                                .foregroundColor(.metalTextMutedDark)
                            Text(metal.standardPurity)
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                        }
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Market Standard")
                                .font(.system(size: 11))
                                .foregroundColor(.metalTextMutedDark)
                            Text(q.marketType)
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                        }
                    }
                    .padding(.top, 4)
                }
                .padding(16)
                .background(Color.metalSurfaceDark)
                .cornerRadius(20)
                .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.metalBorderDark, lineWidth: 1))
                .padding(.horizontal, 16)
                .padding(.bottom, 24)
            }
        }
        .background(Color.metalBackgroundDark.ignoresSafeArea())
        .navigationBarHidden(true)
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

struct StatTile: View {
    let label: String
    let value: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(label)
                .font(.system(size: 11))
                .foregroundColor(.metalTextSecondaryDark)
            Text(value)
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(.metalTextPrimaryDark)
                .lineLimit(1)
                .minimumScaleFactor(0.8)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(12)
        .background(Color.metalSurfaceDark)
        .cornerRadius(12)
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.metalBorderDark, lineWidth: 1))
    }
}
