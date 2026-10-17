import SwiftUI

struct HistoryScreenView: View {
    @ObservedObject var viewModel: MainViewModel
    
    @State private var selectedMetal: MetalType = .gold
    @State private var timeRange: TimeRange = .oneMonth
    @State private var showShareSheet: Bool = false
    @State private var csvURL: URL? = nil
    
    var body: some View {
        let records = viewModel.getHistoricalData(metal: selectedMetal, timeRange: timeRange)
        let prices = records.map { $0.calculateClose(currency: viewModel.currency, unit: viewModel.unit) }
        let high = prices.max() ?? 0.0
        let low = prices.min() ?? 0.0
        let avg = prices.isEmpty ? 0.0 : prices.reduce(0, +) / Double(prices.count)
        let initial = prices.first ?? 0.0
        let last = prices.last ?? 0.0
        let totalReturnPct = initial > 0 ? ((last - initial) / initial) * 100.0 : 0.0
        
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // Header with Share Button
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("History Explorer")
                            .font(.system(size: 26, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                        Text("Historical price records, analytics & CSV export")
                            .font(.system(size: 12))
                            .foregroundColor(.metalTextSecondaryDark)
                    }
                    Spacer()
                    
                    // Share CSV Button
                    Button(action: {
                        exportCSV(records: records)
                    }) {
                        HStack(spacing: 5) {
                            Image(systemName: "square.and.arrow.up")
                                .font(.system(size: 13, weight: .bold))
                            Text("Export CSV")
                                .font(.system(size: 12, weight: .bold))
                        }
                        .foregroundColor(.metalGold)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 8)
                        .background(Color.metalGold.opacity(0.15))
                        .cornerRadius(10)
                        .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.metalGold.opacity(0.4), lineWidth: 1))
                    }
                    .buttonStyle(PlainButtonStyle())
                }
                .padding(.horizontal, 16)
                .padding(.top, 10)
                
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
                
                // Time Range Selector
                ScrollView(.horizontal, showsIndicators: false) {
                    TimeRangeSelectorView(
                        selectedRange: timeRange,
                        onSelect: { timeRange = $0 },
                        activeColor: selectedMetal.defaultColor
                    )
                    .padding(.horizontal, 16)
                }
                
                // Period Metrics Summary
                VStack(spacing: 10) {
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
                    }
                    HStack(spacing: 10) {
                        StatCard(
                            title: "Period Average",
                            value: CurrencyFormatter.formatPrice(amount: avg, currency: viewModel.currency),
                            valueColor: .metalTextPrimaryDark
                        )
                        StatCard(
                            title: "Total Return",
                            value: CurrencyFormatter.formatPercent(totalReturnPct),
                            valueColor: totalReturnPct >= 0 ? .changePositive : .changeNegative
                        )
                    }
                }
                .padding(.horizontal, 16)
                
                // Historical Records List
                VStack(alignment: .leading, spacing: 10) {
                    Text("Daily Quotes (\(records.count) records)")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                        .padding(.horizontal, 16)
                    
                    VStack(spacing: 8) {
                        ForEach(records.reversed()) { record in
                            let close = record.calculateClose(currency: viewModel.currency, unit: viewModel.unit)
                            let open = record.calculateOpen(currency: viewModel.currency, unit: viewModel.unit)
                            let h = record.calculateHigh(currency: viewModel.currency, unit: viewModel.unit)
                            let l = record.calculateLow(currency: viewModel.currency, unit: viewModel.unit)
                            let isPos = record.changePercent >= 0
                            
                            VStack(spacing: 8) {
                                HStack {
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(record.dateString)
                                            .font(.system(size: 14, weight: .bold))
                                            .foregroundColor(.metalTextPrimaryDark)
                                        Text("Close: \(CurrencyFormatter.formatPrice(amount: close, currency: viewModel.currency))")
                                            .font(.system(size: 13, weight: .semibold))
                                            .foregroundColor(.metalGold)
                                    }
                                    
                                    Spacer()
                                    
                                    HStack(spacing: 4) {
                                        Image(systemName: isPos ? "arrow.up.right" : "arrow.down.right")
                                            .font(.system(size: 11, weight: .bold))
                                        Text(CurrencyFormatter.formatPercent(record.changePercent))
                                            .font(.system(size: 12, weight: .bold))
                                    }
                                    .foregroundColor(isPos ? .changePositive : .changeNegative)
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 4)
                                    .background((isPos ? Color.changePositive : Color.changeNegative).opacity(0.15))
                                    .cornerRadius(6)
                                }
                                
                                HStack {
                                    Text("Open: \(CurrencyFormatter.formatPrice(amount: open, currency: viewModel.currency))")
                                    Spacer()
                                    Text("High: \(CurrencyFormatter.formatPrice(amount: h, currency: viewModel.currency))")
                                    Spacer()
                                    Text("Low: \(CurrencyFormatter.formatPrice(amount: l, currency: viewModel.currency))")
                                }
                                .font(.system(size: 11))
                                .foregroundColor(.metalTextSecondaryDark)
                            }
                            .padding(14)
                            .background(Color.metalSurfaceDark)
                            .cornerRadius(12)
                            .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.metalBorderDark, lineWidth: 1))
                            .padding(.horizontal, 16)
                        }
                    }
                }
            }
            .padding(.bottom, 30)
        }
        .background(Color.metalBackgroundDark.ignoresSafeArea())
        .sheet(isPresented: $showShareSheet) {
            if let url = csvURL {
                ShareActivityView(activityItems: [url])
            }
        }
    }
    
    private func exportCSV(records: [HistoricalRecord]) {
        HapticManager.mediumImpact()
        var csv = "Timestamp,Date,Close_\(viewModel.currency.code),Open_\(viewModel.currency.code),High_\(viewModel.currency.code),Low_\(viewModel.currency.code),ChangePercent\n"
        
        for r in records {
            let c = r.calculateClose(currency: viewModel.currency, unit: viewModel.unit)
            let o = r.calculateOpen(currency: viewModel.currency, unit: viewModel.unit)
            let h = r.calculateHigh(currency: viewModel.currency, unit: viewModel.unit)
            let l = r.calculateLow(currency: viewModel.currency, unit: viewModel.unit)
            csv += "\(r.timestamp),\"\(r.dateString)\",\(c),\(o),\(h),\(l),\(r.changePercent)\n"
        }
        
        let filename = "\(selectedMetal.symbol)_Historical_\(timeRange.label).csv"
        let tempURL = FileManager.default.temporaryDirectory.appendingPathComponent(filename)
        
        do {
            try csv.write(to: tempURL, atomically: true, encoding: .utf8)
            self.csvURL = tempURL
            self.showShareSheet = true
        } catch {
            print("Failed to write CSV: \(error)")
        }
    }
}

struct ShareActivityView: UIViewControllerRepresentable {
    let activityItems: [Any]
    
    func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: activityItems, applicationActivities: nil)
    }
    
    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}
