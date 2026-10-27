import SwiftUI

// MARK: - ComexScreenView
struct ComexScreenView: View {
    @ObservedObject var viewModel: MainViewModel
    let onNavigateBack: () -> Void
    let onNavigateToTab: (MarketTab) -> Void

    @State private var selectedMetal: MetalType = .silver
    @State private var selectedTimeRange: TimeRange = .oneMonth

    var data: ComexInventoryData {
        viewModel.getComexInventoryData(metal: selectedMetal, timeRange: selectedTimeRange)
    }

    private let registeredColor = Color(red: 239/255, green: 68/255, blue: 68/255)
    private let eligibleColor   = Color(red:  59/255, green: 130/255, blue: 246/255)

    private var metalColor: Color { selectedMetal == .silver ? .metalSilver : .metalGold }
    private var registeredRatio: Double {
        guard data.totalVaultOunces > 0 else { return 0.5 }
        return min(max(data.registeredOunces / data.totalVaultOunces, 0), 1)
    }

    var body: some View {
        VStack(spacing: 0) {
            // App Bar
            HStack {
                Button(action: onNavigateBack) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 17, weight: .semibold))
                        .foregroundColor(.metalTextPrimaryDark)
                        .padding(8)
                }

                Spacer()

                VStack(spacing: 2) {
                    Text("COMEX Inventory & Vaults")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    Text("Registered vs Eligible Vault Stocks")
                        .font(.system(size: 11))
                        .foregroundColor(.metalTextSecondaryDark)
                }

                Spacer()

                Text("🏛️ COMEX")
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(Color(red: 96/255, green: 165/255, blue: 250/255))
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .background(Color(red: 59/255, green: 130/255, blue: 246/255).opacity(0.2))
                    .clipShape(Capsule())
                    .padding(.trailing, 8)
            }
            .padding(.top, 4)

            // Quick Tab Bar
            MarketTabBarView(selectedTab: .comex, onTabSelected: onNavigateToTab)

            // Scrollable Content
            ScrollView {
                VStack(spacing: 16) {
                    // Metal Switcher
                    HStack(spacing: 8) {
                        ForEach([MetalType.silver, MetalType.gold], id: \.symbol) { metal in
                            let isSel = metal == selectedMetal
                            let pillColor: Color = metal == .silver ? .metalSilver : .metalGold
                            Button(action: { selectedMetal = metal }) {
                                Text("\(metal.displayName) Inventory")
                                    .font(.system(size: 13, weight: isSel ? .bold : .medium))
                                    .foregroundColor(isSel ? pillColor : .metalTextSecondaryDark)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 10)
                                    .background(isSel ? pillColor.opacity(0.18) : Color.metalElevatedDark)
                                    .cornerRadius(12)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 12)
                                            .stroke(isSel ? pillColor : Color.metalBorderDark, lineWidth: isSel ? 1.5 : 1)
                                    )
                            }
                            .buttonStyle(PlainButtonStyle())
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.top, 10)

                    // Hero Inventory Overview Card
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Total \(selectedMetal.displayName) in COMEX Approved Vaults")
                            .font(.system(size: 13))
                            .foregroundColor(.metalTextSecondaryDark)

                        HStack(alignment: .lastTextBaseline, spacing: 6) {
                            Text(String(format: "%.1fM", data.totalVaultOunces / 1_000_000))
                                .font(.system(size: 36, weight: .black))
                                .foregroundColor(.metalTextPrimaryDark)
                            Text("troy ounces")
                                .font(.system(size: 15, weight: .medium))
                                .foregroundColor(.metalTextSecondaryDark)
                        }

                        HStack(spacing: 8) {
                            HStack(spacing: 4) {
                                Image(systemName: "arrow.down.right")
                                    .font(.system(size: 12))
                                    .foregroundColor(registeredColor)
                                Text(String(format: "%.1fk oz Net Outflow (24h)", data.dailyVaultNetChangeOunces / 1000))
                                    .font(.system(size: 12, weight: .bold))
                                    .foregroundColor(registeredColor)
                            }
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(registeredColor.opacity(0.18))
                            .cornerRadius(8)
                            .overlay(RoundedRectangle(cornerRadius: 8).stroke(registeredColor, lineWidth: 1))

                            Text("Spot: $\(String(format: "%.2f", data.priceUsdPerOz))")
                                .font(.system(size: 12))
                                .foregroundColor(.metalTextSecondaryDark)
                        }

                        // Stacked Inventory Bar
                        VStack(spacing: 6) {
                            HStack {
                                HStack(spacing: 4) {
                                    Circle().fill(registeredColor).frame(width: 8, height: 8)
                                    Text(String(format: "Registered: %.1fM oz (%.1f%%)", data.registeredOunces / 1_000_000, registeredRatio * 100))
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(registeredColor)
                                }
                                Spacer()
                                HStack(spacing: 4) {
                                    Circle().fill(eligibleColor).frame(width: 8, height: 8)
                                    Text(String(format: "Eligible: %.1fM oz (%.1f%%)", data.eligibleOunces / 1_000_000, (1 - registeredRatio) * 100))
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(eligibleColor)
                                }
                            }

                            GeometryReader { geo in
                                HStack(spacing: 0) {
                                    Rectangle()
                                        .fill(registeredColor)
                                        .frame(width: geo.size.width * registeredRatio)
                                    Rectangle()
                                        .fill(eligibleColor)
                                }
                                .clipShape(RoundedRectangle(cornerRadius: 6))
                            }
                            .frame(height: 12)
                        }
                    }
                    .padding(18)
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(20)
                    .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.metalBorderDark, lineWidth: 1))
                    .padding(.horizontal, 16)

                    // Registered Drawdown Tracker
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            HStack(spacing: 8) {
                                Image(systemName: "chart.line.downtrend.xyaxis")
                                    .foregroundColor(registeredColor)
                                Text("Registered Drawdown Tracker")
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(.metalTextPrimaryDark)
                            }
                            Spacer()
                            Text("\(String(format: "%.0f", data.drawdownAthPercent))% ATH")
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(Color(red: 248/255, green: 113/255, blue: 113/255))
                                .padding(.horizontal, 8)
                                .padding(.vertical, 2)
                                .background(registeredColor.opacity(0.18))
                                .cornerRadius(6)
                        }

                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Peak Registered")
                                    .font(.system(size: 11))
                                    .foregroundColor(.metalTextMutedDark)
                                Text(String(format: "%.1fM oz", data.peakRegisteredOunces / 1_000_000))
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(.metalTextPrimaryDark)
                            }
                            Spacer()
                            VStack(alignment: .center, spacing: 2) {
                                Text("Current Registered")
                                    .font(.system(size: 11))
                                    .foregroundColor(.metalTextMutedDark)
                                Text(String(format: "%.1fM oz", data.registeredOunces / 1_000_000))
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(Color(red: 248/255, green: 113/255, blue: 113/255))
                            }
                            Spacer()
                            VStack(alignment: .trailing, spacing: 2) {
                                Text("30-Day Change")
                                    .font(.system(size: 11))
                                    .foregroundColor(.metalTextMutedDark)
                                Text(String(format: "%.1fM oz", data.change30dOunces / 1_000_000))
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(data.change30dOunces < 0 ? registeredColor : .metalChangePositive)
                            }
                        }
                    }
                    .padding(16)
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(18)
                    .overlay(RoundedRectangle(cornerRadius: 18).stroke(Color.metalBorderDark, lineWidth: 1))
                    .padding(.horizontal, 16)

                    // Historical Vault Drain Chart
                    VStack(alignment: .leading, spacing: 14) {
                        VStack(alignment: .leading, spacing: 2) {
                            Text("COMEX Vault Drain History")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                            Text("Tracking deliverable registered ounces over time")
                                .font(.system(size: 11))
                                .foregroundColor(.metalTextSecondaryDark)
                        }

                        ComexLineChartView(points: data.history, lineColor: registeredColor)
                            .frame(height: 180)

                        TimeRangeSelectorView(selectedRange: selectedTimeRange, onSelect: { selectedTimeRange = $0 })
                    }
                    .padding(16)
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(20)
                    .overlay(RoundedRectangle(cornerRadius: 20).stroke(Color.metalBorderDark, lineWidth: 1))
                    .padding(.horizontal, 16)

                    // Vault Depositories
                    VStack(alignment: .leading, spacing: 12) {
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Approved COMEX Vault Depositories")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                            Text("Physical holdings across approved vault operators")
                                .font(.system(size: 11))
                                .foregroundColor(.metalTextSecondaryDark)
                        }

                        ForEach(data.vaults) { vault in
                            VStack(spacing: 4) {
                                HStack {
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(vault.vaultName)
                                            .font(.system(size: 14, weight: .bold))
                                            .foregroundColor(.metalTextPrimaryDark)
                                        Text(vault.location)
                                            .font(.system(size: 11))
                                            .foregroundColor(.metalTextMutedDark)
                                    }
                                    Spacer()
                                    VStack(alignment: .trailing, spacing: 2) {
                                        Text(String(format: "%.1fM oz", vault.totalOunces / 1_000_000))
                                            .font(.system(size: 14, weight: .bold))
                                            .foregroundColor(.metalTextPrimaryDark)
                                        Text(String(format: "%.1f%% share", vault.sharePercent))
                                            .font(.system(size: 11))
                                            .foregroundColor(.metalTextSecondaryDark)
                                    }
                                }

                                GeometryReader { geo in
                                    ZStack(alignment: .leading) {
                                        RoundedRectangle(cornerRadius: 2).fill(Color.metalElevatedDark)
                                        RoundedRectangle(cornerRadius: 2)
                                            .fill(Color(red: 96/255, green: 165/255, blue: 250/255))
                                            .frame(width: geo.size.width * CGFloat(vault.sharePercent / 100))
                                    }
                                }
                                .frame(height: 4)
                            }
                            .padding(.vertical, 6)
                        }
                    }
                    .padding(16)
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(18)
                    .overlay(RoundedRectangle(cornerRadius: 18).stroke(Color.metalBorderDark, lineWidth: 1))
                    .padding(.horizontal, 16)

                    // Daily Delivery Notices
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Daily Delivery Notices & History")
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)

                        ForEach(data.recentDeliveries) { delivery in
                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(delivery.dateStr)
                                        .font(.system(size: 14, weight: .bold))
                                        .foregroundColor(.metalTextPrimaryDark)
                                    Text("Month: \(delivery.contractMonth) · \(delivery.contractsIssued) contracts")
                                        .font(.system(size: 11))
                                        .foregroundColor(.metalTextSecondaryDark)
                                }
                                Spacer()
                                VStack(alignment: .trailing, spacing: 2) {
                                    Text(String(format: "%,.0f oz", delivery.netOuncesDelivered))
                                        .font(.system(size: 14, weight: .bold))
                                        .foregroundColor(.metalChangePositive)
                                    Text(String(format: "Total: %.1fM oz", delivery.cumulativeMonthOunces / 1_000_000))
                                        .font(.system(size: 11))
                                        .foregroundColor(.metalTextMutedDark)
                                }
                            }
                            .padding(.vertical, 6)
                        }
                    }
                    .padding(16)
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(18)
                    .overlay(RoundedRectangle(cornerRadius: 18).stroke(Color.metalBorderDark, lineWidth: 1))
                    .padding(.horizontal, 16)

                    // Educational Info Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack(spacing: 8) {
                            Image(systemName: "info.circle.fill")
                                .foregroundColor(.metalGold)
                            Text("Understanding COMEX Vault Categories")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                        }

                        ComexInsightBullet(
                            title: "Registered vs. Eligible Silver",
                            description: "Eligible silver meets COMEX fineness (.999) and brand standards but is simply stored for private owners. Registered silver is backed by warehouse warrants and is available for delivery against futures contracts."
                        )

                        ComexInsightBullet(
                            title: "Why Registered Drawdowns Matter",
                            description: "When registered inventory drops below 100M ounces while paper open interest remains high, the paper-to-physical ratio stretches. This makes short positions vulnerable to physical delivery demands and short squeezes."
                        )
                    }
                    .padding(16)
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(18)
                    .overlay(RoundedRectangle(cornerRadius: 18).stroke(Color.metalBorderDark, lineWidth: 1))
                    .padding(.horizontal, 16)
                    .padding(.bottom, 24)
                }
            }
        }
        .background(Color.metalBackgroundDark.ignoresSafeArea())
    }
}

// MARK: - Comex Line Chart
private struct ComexLineChartView: View {
    let points: [ComexHistoricalPoint]
    let lineColor: Color

    var body: some View {
        Canvas { context, size in
            guard points.count > 1 else { return }
            let minVal = (points.map { $0.registeredOunces }.min() ?? 0) * 0.95
            let maxVal = (points.map { $0.registeredOunces }.max() ?? 1) * 1.05
            let span = max(maxVal - minVal, 0.01)
            let w = size.width
            let h = size.height

            // Grid lines
            for step in 1...3 {
                let y = h * CGFloat(step) / 4
                var path = Path()
                path.move(to: CGPoint(x: 0, y: y))
                path.addLine(to: CGPoint(x: w, y: y))
                context.stroke(path, with: .color(.white.opacity(0.08)), lineWidth: 1)
            }

            // Line path
            var linePath = Path()
            for (i, pt) in points.enumerated() {
                let x = CGFloat(i) / CGFloat(points.count - 1) * w
                let y = h - CGFloat((pt.registeredOunces - minVal) / span) * h
                if i == 0 { linePath.move(to: CGPoint(x: x, y: y)) }
                else { linePath.addLine(to: CGPoint(x: x, y: y)) }
            }
            context.stroke(linePath, with: .color(lineColor), style: StrokeStyle(lineWidth: 2.5, lineCap: .round, lineJoin: .round))
        }
    }
}

// MARK: - Insight Bullet
private struct ComexInsightBullet: View {
    let title: String
    let description: String

    var body: some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(title)
                .font(.system(size: 13, weight: .bold))
                .foregroundColor(.metalTextPrimaryDark)
            Text(description)
                .font(.system(size: 12))
                .foregroundColor(.metalTextSecondaryDark)
                .lineSpacing(3)
        }
    }
}
