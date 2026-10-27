import SwiftUI

// MARK: - ShanghaiScreenView
struct ShanghaiScreenView: View {
    @ObservedObject var viewModel: MainViewModel
    let onNavigateBack: () -> Void
    let onNavigateToTab: (MarketTab) -> Void

    @State private var selectedMetal: MetalType = .silver
    @State private var selectedTimeRange: TimeRange = .oneMonth
    @State private var showPremiumOverlay: Bool = true

    var data: ShanghaiMarketData {
        viewModel.getShanghaiData(metal: selectedMetal, timeRange: selectedTimeRange)
    }

    private let chinaColor = Color(red: 239/255, green: 68/255, blue: 68/255)
    private var metalColor: Color { selectedMetal == .silver ? .metalSilver : .metalGold }

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
                    Text("Shanghai Premium (SGE)")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    Text("China Gold Exchange vs COMEX")
                        .font(.system(size: 11))
                        .foregroundColor(.metalTextSecondaryDark)
                }

                Spacer()

                Text("🇨🇳 SGE")
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(Color(red: 248/255, green: 113/255, blue: 113/255))
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .background(Color(red: 239/255, green: 68/255, blue: 68/255).opacity(0.2))
                    .clipShape(Capsule())
                    .padding(.trailing, 8)
            }
            .padding(.top, 4)

            // Quick Tab Bar
            MarketTabBarView(selectedTab: .shanghai, onTabSelected: onNavigateToTab)

            // Scrollable Content
            ScrollView {
                VStack(spacing: 16) {
                    // Metal Switcher (Silver / Gold)
                    HStack(spacing: 8) {
                        ForEach([MetalType.silver, MetalType.gold], id: \.symbol) { metal in
                            let isSel = metal == selectedMetal
                            let pillColor: Color = metal == .silver ? .metalSilver : .metalGold
                            Button(action: {
                                HapticManager.selection()
                                selectedMetal = metal
                            }) {
                                Text("\(metal.displayName) (\(metal.symbol))")
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

                    // Hero Summary Card
                    VStack(alignment: .leading, spacing: 10) {
                        Text("The Shanghai \(selectedMetal.displayName.lowercased()) price is currently")
                            .font(.system(size: 13))
                            .foregroundColor(.metalTextSecondaryDark)

                        HStack(alignment: .bottom, spacing: 6) {
                            Text(String(format: "$%.2f", data.shanghaiPriceUsdPerOz))
                                .font(.system(size: 34, weight: .black))
                                .foregroundColor(Color(red: 248/255, green: 113/255, blue: 113/255))

                            Text("/ oz")
                                .font(.system(size: 15, weight: .medium))
                                .foregroundColor(.metalTextSecondaryDark)
                                .padding(.bottom, 4)
                        }

                        HStack(spacing: 8) {
                            HStack(spacing: 4) {
                                Image(systemName: "arrow.up.right")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(.metalGreen)
                                Text(String(format: "+%.2f%% China Premium", data.premiumPercent))
                                    .font(.system(size: 12, weight: .bold))
                                    .foregroundColor(.metalGreen)
                            }
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(Color.metalGreen.opacity(0.16))
                            .cornerRadius(8)
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(Color.metalGreen.opacity(0.5), lineWidth: 1)
                            )

                            Text(String(format: "vs US Spot $%.2f/oz", data.westernPriceUsdPerOz))
                                .font(.system(size: 12))
                                .foregroundColor(.metalTextSecondaryDark)
                        }

                        Divider()
                            .background(Color.metalBorderDark)
                            .padding(.vertical, 4)

                        // Secondary Metrics Pill Grid
                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text("SGE Domestic Rate")
                                    .font(.system(size: 11))
                                    .foregroundColor(.metalTextMutedDark)
                                Text(String(format: "%.2f CNY/g", data.sgePriceCnyPerGram))
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.metalTextPrimaryDark)
                            }

                            Spacer()

                            VStack(alignment: .leading, spacing: 2) {
                                Text("Premium Spread")
                                    .font(.system(size: 11))
                                    .foregroundColor(.metalTextMutedDark)
                                Text(String(format: "+$%.2f / oz", data.premiumUsdPerOz))
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.metalGreen)
                            }

                            Spacer()

                            VStack(alignment: .trailing, spacing: 2) {
                                Text("USD/CNY Forex")
                                    .font(.system(size: 11))
                                    .foregroundColor(.metalTextMutedDark)
                                Text(String(format: "¥%.2f", data.usdCnyRate))
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.metalTextPrimaryDark)
                            }
                        }
                    }
                    .padding(18)
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(20)
                    .overlay(
                        RoundedRectangle(cornerRadius: 20)
                            .stroke(Color.metalBorderDark, lineWidth: 1)
                    )
                    .padding(.horizontal, 16)

                    // Price Comparison Chart Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Shanghai vs Western Price")
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(.metalTextPrimaryDark)
                                Text("Comparison in USD/oz")
                                    .font(.system(size: 11))
                                    .foregroundColor(.metalTextSecondaryDark)
                            }

                            Spacer()

                            HStack(spacing: 6) {
                                Text("Premium %")
                                    .font(.system(size: 11, weight: .medium))
                                    .foregroundColor(showPremiumOverlay ? .metalGreen : .metalTextMutedDark)
                                Toggle("", isOn: $showPremiumOverlay)
                                    .labelsHidden()
                                    .toggleStyle(SwitchToggleStyle(tint: .metalGreen))
                                    .scaleEffect(0.8)
                            }
                        }

                        // Legend
                        HStack(spacing: 16) {
                            HStack(spacing: 6) {
                                Circle()
                                    .fill(chinaColor)
                                    .frame(width: 8, height: 8)
                                Text("Shanghai (SGE)")
                                    .font(.system(size: 11, weight: .medium))
                                    .foregroundColor(chinaColor)
                            }

                            HStack(spacing: 6) {
                                Circle()
                                    .fill(metalColor)
                                    .frame(width: 8, height: 8)
                                Text("Western Spot (COMEX)")
                                    .font(.system(size: 11, weight: .medium))
                                    .foregroundColor(metalColor)
                            }
                        }

                        // Canvas Chart
                        let points = data.comparisonHistory
                        if !points.isEmpty {
                            let minVal = (points.map { min($0.westernPriceUsd, $0.shanghaiPriceUsd) }.min() ?? 0) * 0.98
                            let maxVal = (points.map { max($0.westernPriceUsd, $0.shanghaiPriceUsd) }.max() ?? 100) * 1.02
                            let span = max(maxVal - minVal, 0.01)

                            GeometryReader { geo in
                                let w = geo.size.width
                                let h = geo.size.height

                                ZStack {
                                    // Grid lines
                                    ForEach(1...3, id: \.self) { step in
                                        let y = h * CGFloat(step) / 4.0
                                        Path { path in
                                            path.move(to: CGPoint(x: 0, y: y))
                                            path.addLine(to: CGPoint(x: w, y: y))
                                        }
                                        .stroke(Color.metalBorderDark.opacity(0.4), lineWidth: 1)
                                    }

                                    // Western Line
                                    Path { path in
                                        for (i, pt) in points.enumerated() {
                                            let x = (CGFloat(i) / CGFloat(max(points.count - 1, 1))) * w
                                            let y = h - CGFloat((pt.westernPriceUsd - minVal) / span) * h
                                            if i == 0 { path.move(to: CGPoint(x: x, y: y)) }
                                            else { path.addLine(to: CGPoint(x: x, y: y)) }
                                        }
                                    }
                                    .stroke(metalColor, style: StrokeStyle(lineWidth: 2.2, lineCap: .round, lineJoin: .round))

                                    // Shanghai Line
                                    Path { path in
                                        for (i, pt) in points.enumerated() {
                                            let x = (CGFloat(i) / CGFloat(max(points.count - 1, 1))) * w
                                            let y = h - CGFloat((pt.shanghaiPriceUsd - minVal) / span) * h
                                            if i == 0 { path.move(to: CGPoint(x: x, y: y)) }
                                            else { path.addLine(to: CGPoint(x: x, y: y)) }
                                        }
                                    }
                                    .stroke(chinaColor, style: StrokeStyle(lineWidth: 2.5, lineCap: .round, lineJoin: .round))
                                }
                            }
                            .frame(height: 180)
                        }

                        // Time Range Selector
                        TimeRangeSelectorView(
                            selectedRange: selectedTimeRange,
                            onSelectRange: { newRange in
                                HapticManager.selection()
                                selectedTimeRange = newRange
                            }
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

                    // SHFE Inventory vs Price Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack(spacing: 8) {
                            Image(systemName: "waveform.path.ecg")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundColor(Color(red: 96/255, green: 165/255, blue: 250/255))
                            Text("SHFE Inventory & Warrant Stocks")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                        }

                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Current Warehouse Stock")
                                    .font(.system(size: 11))
                                    .foregroundColor(.metalTextMutedDark)
                                Text(String(format: "%.1f tonnes", data.shfeInventoryTonnes))
                                    .font(.system(size: 18, weight: .bold))
                                    .foregroundColor(.metalTextPrimaryDark)
                            }

                            Spacer()

                            VStack(alignment: .trailing, spacing: 2) {
                                Text("30-Day Change")
                                    .font(.system(size: 11))
                                    .foregroundColor(.metalTextMutedDark)
                                Text(String(format: "%.1f tonnes", data.shfeInventory30dChangeTonnes))
                                    .font(.system(size: 18, weight: .bold))
                                    .foregroundColor(data.shfeInventory30dChangeTonnes < 0 ? Color(red: 248/255, green: 113/255, blue: 113/255) : .metalGreen)
                            }
                        }

                        Text("Warehouse warrant inventory on the Shanghai Futures Exchange (SHFE) has drained as physical industrial consumption outpaces registered domestic refiners.")
                            .font(.system(size: 12))
                            .foregroundColor(.metalTextSecondaryDark)
                            .lineSpacing(2)
                    }
                    .padding(16)
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(18)
                    .overlay(
                        RoundedRectangle(cornerRadius: 18)
                            .stroke(Color.metalBorderDark, lineWidth: 1)
                    )
                    .padding(.horizontal, 16)

                    // Structural Dynamics Cards
                    VStack(alignment: .leading, spacing: 14) {
                        HStack(spacing: 8) {
                            Image(systemName: "info.circle.fill")
                                .font(.system(size: 15))
                                .foregroundColor(.metalGold)
                            Text("Why Shanghai Trades at a Premium")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                        }

                        ShanghaiInsightBulletView(
                            title: "13% Value-Added Tax (VAT)",
                            description: "China imposes a 13% VAT on domestic silver transactions. Importers cannot easily reclaim VAT on re-exports, establishing a structural pricing floor."
                        )

                        ShanghaiInsightBulletView(
                            title: "PBOC Import Quotas",
                            description: "The People's Bank of China allocates strict import licenses to commercial banks. When domestic physical demand spikes, quotas restrict supply and drive up premiums."
                        )

                        ShanghaiInsightBulletView(
                            title: "Physical Delivery vs Paper Arbitrage",
                            description: "Shanghai Gold Exchange contracts require 100% physical delivery in certified bars. COMEX is largely paper cash-settled, preventing cross-border price equalisation."
                        )

                        ShanghaiInsightBulletView(
                            title: "Solar Photovoltaic Dominance",
                            description: "China manufactures over 80% of global solar panels, consuming over 190 million ounces of silver paste annually, creating intense local spot bidding."
                        )
                    }
                    .padding(16)
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(18)
                    .overlay(
                        RoundedRectangle(cornerRadius: 18)
                            .stroke(Color.metalBorderDark, lineWidth: 1)
                    )
                    .padding(.horizontal, 16)
                    .padding(.bottom, 24)
                }
            }
        }
        .background(Color.metalBackgroundDark.ignoresSafeArea())
    }
}

// MARK: - Bullet View Helper
private struct ShanghaiInsightBulletView: View {
    let title: String
    let description: String

    var body: some View {
        VStack(alignment: .leading, spacing: 3) {
            Text(title)
                .font(.system(size: 13, weight: .bold))
                .foregroundColor(.metalTextPrimaryDark)
            Text(description)
                .font(.system(size: 12))
                .foregroundColor(.metalTextSecondaryDark)
                .lineSpacing(2)
        }
    }
}
