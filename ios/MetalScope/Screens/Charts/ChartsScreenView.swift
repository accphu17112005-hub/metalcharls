import SwiftUI

enum ChartStudioMode: String, CaseIterable, Identifiable {
    case single = "SINGLE"
    case comparison = "COMPARISON"
    case goldSilverRatio = "RATIO"
    
    var id: String { rawValue }
    
    var displayName: String {
        switch self {
        case .single: return "Single Metal"
        case .comparison: return "Multi-Metal %"
        case .goldSilverRatio: return "Au/Ag Ratio"
        }
    }
}

struct ChartsScreenView: View {
    @ObservedObject var viewModel: MainViewModel
    
    @State private var mode: ChartStudioMode = .single
    @State private var selectedMetal: MetalType = .gold
    @State private var timeRange: TimeRange = .oneMonth
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 18) {
                // Title Bar
                VStack(alignment: .leading, spacing: 2) {
                    Text("Charts Studio")
                        .font(.system(size: 26, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    Text("Interactive technical analysis & ratio indicators")
                        .font(.system(size: 12))
                        .foregroundColor(.metalTextSecondaryDark)
                }
                .padding(.horizontal, 16)
                .padding(.top, 10)
                
                // Studio Mode Switcher
                HStack(spacing: 8) {
                    ForEach(ChartStudioMode.allCases) { m in
                        let isSelected = m == mode
                        Button(action: {
                            mode = m
                            HapticManager.selection()
                        }) {
                            Text(m.displayName)
                                .font(.system(size: 13, weight: .bold))
                                .foregroundColor(isSelected ? .metalBackgroundDark : .metalTextSecondaryDark)
                                .padding(.horizontal, 14)
                                .padding(.vertical, 8)
                                .frame(maxWidth: .infinity)
                                .background(isSelected ? Color.metalGold : Color.metalElevatedDark)
                                .cornerRadius(10)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 10)
                                        .stroke(isSelected ? Color.metalGold : Color.metalBorderDark, lineWidth: 1)
                                )
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                .padding(.horizontal, 16)
                
                // Studio Content
                switch mode {
                case .single:
                    renderSingleMetalStudio()
                case .comparison:
                    renderComparisonStudio()
                case .goldSilverRatio:
                    renderRatioStudio()
                }
            }
            .padding(.bottom, 30)
        }
        .background(Color.metalBackgroundDark.ignoresSafeArea())
    }
    
    // MARK: - Single Metal Mode
    @ViewBuilder
    private func renderSingleMetalStudio() -> some View {
        let records = viewModel.getHistoricalData(metal: selectedMetal, timeRange: timeRange)
        
        let prices = records.map { $0.calculateClose(currency: viewModel.currency, unit: viewModel.unit) }
        let high = prices.max() ?? 0.0
        let low = prices.min() ?? 0.0
        let initial = prices.first ?? 0.0
        let last = prices.last ?? 0.0
        let totalReturnPct = initial > 0 ? ((last - initial) / initial) * 100.0 : 0.0
        
        VStack(spacing: 16) {
            // Metal Switcher Pills
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(MetalType.allCases) { metal in
                        let isSelected = metal == selectedMetal
                        Button(action: {
                            selectedMetal = metal
                            HapticManager.selection()
                        }) {
                            HStack(spacing: 6) {
                                Circle()
                                    .fill(metal.defaultColor)
                                    .frame(width: 8, height: 8)
                                Text(metal.displayName)
                                    .font(.system(size: 13, weight: .bold))
                                    .foregroundColor(isSelected ? metal.defaultColor : .metalTextSecondaryDark)
                            }
                            .padding(.horizontal, 12)
                            .padding(.vertical, 7)
                            .background(isSelected ? metal.defaultColor.opacity(0.18) : Color.metalElevatedDark)
                            .cornerRadius(10)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(isSelected ? metal.defaultColor : Color.metalBorderDark, lineWidth: 1)
                            )
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                .padding(.horizontal, 16)
            }
            
            // Interactive Chart Card
            VStack(alignment: .leading, spacing: 14) {
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("\(selectedMetal.displayName) (\(selectedMetal.symbol))")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                        Text("Touch and drag on chart to inspect prices")
                            .font(.system(size: 11))
                            .foregroundColor(.metalTextMutedDark)
                    }
                    Spacer()
                }
                
                InteractiveChartView(
                    records: records,
                    currency: viewModel.currency,
                    unit: viewModel.unit,
                    selectedTimeRange: timeRange,
                    onTimeRangeSelected: { timeRange = $0 },
                    lineColor: selectedMetal.defaultColor
                )
            }
            .padding(16)
            .background(Color.metalSurfaceDark)
            .cornerRadius(20)
            .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.metalBorderDark, lineWidth: 1))
            .padding(.horizontal, 16)
            
            // Period Stats Summary Cards
            HStack(spacing: 10) {
                StatCard(
                    title: "Period High",
                    value: CurrencyFormatter.formatPrice(amount: high, currency: viewModel.currency),
                    valueColor: .metalTextPrimaryDark
                )
                StatCard(
                    title: "Period Low",
                    value: CurrencyFormatter.formatPrice(amount: low, currency: viewModel.currency),
                    valueColor: .metalTextPrimaryDark
                )
                StatCard(
                    title: "Total Return",
                    value: CurrencyFormatter.formatPercent(totalReturnPct),
                    valueColor: totalReturnPct >= 0 ? .changePositive : .changeNegative
                )
            }
            .padding(.horizontal, 16)
        }
    }
    
    // MARK: - Multi-Metal Comparison Mode
    @ViewBuilder
    private func renderComparisonStudio() -> some View {
        let allRecords = Dictionary(uniqueKeysWithValues: MetalType.allCases.map {
            ($0, viewModel.getHistoricalData(metal: $0, timeRange: timeRange))
        })
        
        VStack(spacing: 16) {
            VStack(alignment: .leading, spacing: 14) {
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Normalized % Performance")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                        Text("Baseline 0.0% at start of timeframe")
                            .font(.system(size: 11))
                            .foregroundColor(.metalTextMutedDark)
                    }
                    Spacer()
                }
                
                ComparisonChartView(metalData: allRecords)
                
                ScrollView(.horizontal, showsIndicators: false) {
                    TimeRangeSelectorView(
                        selectedRange: timeRange,
                        onSelect: { timeRange = $0 }
                    )
                    .padding(.top, 4)
                }
            }
            .padding(16)
            .background(Color.metalSurfaceDark)
            .cornerRadius(20)
            .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.metalBorderDark, lineWidth: 1))
            .padding(.horizontal, 16)
        }
    }
    
    // MARK: - Gold/Silver Ratio Mode
    @ViewBuilder
    private func renderRatioStudio() -> some View {
        VStack(spacing: 16) {
            QuickInsightCardView(insight: viewModel.insights)
                .padding(.horizontal, 16)
            
            let goldRecords = viewModel.getHistoricalData(metal: .gold, timeRange: timeRange)
            let silverRecords = viewModel.getHistoricalData(metal: .silver, timeRange: timeRange)
            
            // Build synthetic ratio records
            let ratioRecords: [HistoricalRecord] = zip(goldRecords, silverRecords).map { g, s in
                let r = s.closeUsdPerOz > 0 ? g.closeUsdPerOz / s.closeUsdPerOz : 80.0
                return HistoricalRecord(
                    timestamp: g.timestamp,
                    dateString: g.dateString,
                    closeUsdPerOz: r,
                    openUsdPerOz: r,
                    highUsdPerOz: r,
                    lowUsdPerOz: r,
                    changePercent: 0.0,
                    volume: 0.0
                )
            }
            
            VStack(alignment: .leading, spacing: 14) {
                Text("Historical Au/Ag Ratio Indicator")
                    .font(.system(size: 17, weight: .bold))
                    .foregroundColor(.metalTextPrimaryDark)
                
                InteractiveChartView(
                    records: ratioRecords,
                    currency: .usd,
                    unit: .oz,
                    selectedTimeRange: timeRange,
                    onTimeRangeSelected: { timeRange = $0 },
                    lineColor: .metalGold
                )
            }
            .padding(16)
            .background(Color.metalSurfaceDark)
            .cornerRadius(20)
            .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.metalBorderDark, lineWidth: 1))
            .padding(.horizontal, 16)
        }
    }
}

struct StatCard: View {
    let title: String
    let value: String
    let valueColor: Color
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.system(size: 11))
                .foregroundColor(.metalTextSecondaryDark)
            Text(value)
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(valueColor)
                .lineLimit(1)
                .minimumScaleFactor(0.8)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(12)
        .background(Color.metalElevatedDark)
        .cornerRadius(12)
    }
}
