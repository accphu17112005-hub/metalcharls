import SwiftUI

// MARK: - IndiaScreenView
struct IndiaScreenView: View {
    @ObservedObject var viewModel: MainViewModel
    let onNavigateBack: () -> Void
    let onNavigateToTab: (MarketTab) -> Void

    @State private var selectedMetal: MetalType = .silver
    @State private var selectedTimeRange: TimeRange = .oneMonth
    @State private var showPremiumOverlay: Bool = true

    var data: IndiaMarketData {
        viewModel.getIndiaData(metal: selectedMetal, timeRange: selectedTimeRange)
    }

    private let indiaColor = Color(red: 245/255, green: 158/255, blue: 11/255) // Saffron / Gold
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
                    Text("India MCX Premium")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    Text("Domestic Landed Cost vs US Spot")
                        .font(.system(size: 11))
                        .foregroundColor(.metalTextSecondaryDark)
                }

                Spacer()

                Text("🇮🇳 MCX")
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(Color(red: 251/255, green: 191/255, blue: 36/255))
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .background(Color(red: 245/255, green: 158/255, blue: 11/255).opacity(0.2))
                    .clipShape(Capsule())
                    .padding(.trailing, 8)
            }
            .padding(.top, 4)

            // Quick Tab Bar
            MarketTabBarView(selectedTab: .india, onTabSelected: onNavigateToTab)

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
                        Text("The India \(selectedMetal.displayName.lowercased()) price (MCX) is currently")
                            .font(.system(size: 13))
                            .foregroundColor(.metalTextSecondaryDark)

                        HStack(alignment: .bottom, spacing: 6) {
                            Text(String(format: "$%.2f", data.indiaPriceUsdPerOz))
                                .font(.system(size: 34, weight: .black))
                                .foregroundColor(Color(red: 251/255, green: 191/255, blue: 36/255))

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
                                Text(String(format: "+%.2f%% India Premium", data.premiumPercent))
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
                                Text("MCX Domestic Rate")
                                    .font(.system(size: 11))
                                    .foregroundColor(.metalTextMutedDark)
                                Text(String(format: "₹%.0f %@", data.mcxPriceInr, data.inrQuoteUnit))
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
                                Text("USD/INR Forex")
                                    .font(.system(size: 11))
                                    .foregroundColor(.metalTextMutedDark)
                                Text(String(format: "₹%.2f", data.usdInrRate))
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
                                Text("India vs Western Price")
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
                                    .fill(indiaColor)
                                    .frame(width: 8, height: 8)
                                Text("India (MCX)")
                                    .font(.system(size: 11, weight: .medium))
                                    .foregroundColor(indiaColor)
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
                            let minVal = (points.map { min($0.westernPriceUsd, $0.indiaPriceUsd) }.min() ?? 0) * 0.98
                            let maxVal = (points.map { max($0.westernPriceUsd, $0.indiaPriceUsd) }.max() ?? 100) * 1.02
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

                                    // India Line
                                    Path { path in
                                        for (i, pt) in points.enumerated() {
                                            let x = (CGFloat(i) / CGFloat(max(points.count - 1, 1))) * w
                                            let y = h - CGFloat((pt.indiaPriceUsd - minVal) / span) * h
                                            if i == 0 { path.move(to: CGPoint(x: x, y: y)) }
                                            else { path.addLine(to: CGPoint(x: x, y: y)) }
                                        }
                                    }
                                    .stroke(indiaColor, style: StrokeStyle(lineWidth: 2.5, lineCap: .round, lineJoin: .round))
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

                    // Taxes & Landed Cost Breakdown Card
                    VStack(alignment: .leading, spacing: 12) {
                        HStack(spacing: 8) {
                            Image(systemName: "newspaper.fill")
                                .font(.system(size: 15))
                                .foregroundColor(Color(red: 251/255, green: 191/255, blue: 36/255))
                            Text("Taxes, Duties & Structural Cost Floor")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                        }

                        Text("Indian bullion carries a structural ~18-19% tax burden over international spot, setting an import price floor nationwide.")
                            .font(.system(size: 12))
                            .foregroundColor(.metalTextSecondaryDark)
                            .lineSpacing(2)

                        VStack(spacing: 8) {
                            ForEach(data.taxBreakdown) { item in
                                HStack {
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(item.label)
                                            .font(.system(size: 13, weight: item.isTotal ? .bold : .medium))
                                            .foregroundColor(item.isTotal ? .metalGold : .metalTextPrimaryDark)
                                        Text(item.costFormula)
                                            .font(.system(size: 11))
                                            .foregroundColor(.metalTextMutedDark)
                                    }

                                    Spacer()

                                    Text(String(format: "$%.2f / oz", item.valueUsd))
                                        .font(.system(size: 13, weight: .bold))
                                        .foregroundColor(item.isTotal ? .metalGold : .metalTextPrimaryDark)
                                }
                                .padding(.vertical, 2)

                                if !item.isTotal {
                                    Divider().background(Color.metalBorderDark.opacity(0.6))
                                }
                            }
                        }
                    }
                    .padding(16)
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(18)
                    .overlay(
                        RoundedRectangle(cornerRadius: 18)
                            .stroke(Color.metalBorderDark, lineWidth: 1)
                    )
                    .padding(.horizontal, 16)

                    // Festive & Rural Dynamics Cards
                    VStack(alignment: .leading, spacing: 14) {
                        HStack(spacing: 8) {
                            Image(systemName: "info.circle.fill")
                                .font(.system(size: 15))
                                .foregroundColor(.metalGold)
                            Text("Key Indian Market Drivers")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                        }

                        IndiaInsightBulletView(
                            title: "World's Largest Silver Consumer",
                            description: "India consumes 5,000 to 7,000 tonnes of silver annually. Surges in Indian import volumes frequently drain Western vaults and lead global price discovery."
                        )

                        IndiaInsightBulletView(
                            title: "Diwali & Dhanteras Festival Surges",
                            description: "During Dhanteras (the festival of wealth) and Diwali (October/November), purchasing physical silver and gold coins and utensils is auspicious, causing annual premium spikes."
                        )

                        IndiaInsightBulletView(
                            title: "October - February Wedding Season",
                            description: "Over 10 million weddings take place in India each year, accounting for more than 50% of annual jewelry fabrication demand, centering in Gujarat and Rajasthan."
                        )

                        IndiaInsightBulletView(
                            title: "Rural Store of Value",
                            description: "Over 60% of India's metal purchases originate in rural districts where farming families rely on physical gold and silver as non-banking stores of intergenerational wealth."
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
private struct IndiaInsightBulletView: View {
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
