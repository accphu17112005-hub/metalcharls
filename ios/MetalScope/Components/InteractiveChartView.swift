import SwiftUI

struct InteractiveChartView: View {
    let records: [HistoricalRecord]
    let currency: Currency
    let unit: WeightUnit
    let selectedTimeRange: TimeRange
    let onTimeRangeSelected: (TimeRange) -> Void
    var lineColor: Color = .metalGold
    
    @State private var selectedIndex: Int? = nil
    
    private var activeRecord: HistoricalRecord? {
        if let idx = selectedIndex, idx >= 0 && idx < records.count {
            return records[idx]
        }
        return records.last
    }
    
    private var firstPrice: Double {
        records.first?.calculateClose(currency: currency, unit: unit) ?? 0.0
    }
    
    private var activePrice: Double {
        activeRecord?.calculateClose(currency: currency, unit: unit) ?? 0.0
    }
    
    private var priceDiff: Double {
        activePrice - firstPrice
    }
    
    private var percentDiff: Double {
        firstPrice > 0 ? (priceDiff / firstPrice) * 100.0 : 0.0
    }
    
    private var isPositive: Bool {
        priceDiff >= 0
    }
    
    private var effectiveColor: Color {
        if selectedIndex != nil {
            return isPositive ? .changePositive : .changeNegative
        }
        return lineColor
    }
    
    var body: some View {
        VStack(spacing: 12) {
            if records.isEmpty {
                VStack {
                    Text("No historical records available")
                        .font(.subheadline)
                        .foregroundColor(.metalTextSecondaryDark)
                }
                .frame(maxWidth: .infinity)
                .frame(height: 220)
            } else {
                // Dynamic Header / Tooltip
                HStack(alignment: .bottom) {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(CurrencyFormatter.formatPrice(amount: activePrice, currency: currency))
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                        
                        if let dateStr = activeRecord?.dateString {
                            Text(dateStr)
                                .font(.system(size: 11, weight: .medium))
                                .foregroundColor(.metalTextMutedDark)
                        }
                    }
                    
                    Spacer()
                    
                    // Return Pill
                    HStack(spacing: 4) {
                        Image(systemName: isPositive ? "arrow.up.right" : "arrow.down.right")
                            .font(.system(size: 11, weight: .bold))
                        Text(CurrencyFormatter.formatPercent(percentDiff))
                            .font(.system(size: 12, weight: .bold))
                    }
                    .foregroundColor(isPositive ? .changePositive : .changeNegative)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background((isPositive ? Color.changePositive : Color.changeNegative).opacity(0.15))
                    .cornerRadius(6)
                }
                .padding(.horizontal, 4)
                
                // Canvas Chart with Touch Drag
                GeometryReader { geometry in
                    let width = geometry.size.width
                    let height = geometry.size.height
                    
                    let prices = records.map { $0.calculateClose(currency: currency, unit: unit) }
                    let minPrice = prices.min() ?? 0.0
                    let maxPrice = prices.max() ?? 1.0
                    let range = max(0.001, maxPrice - minPrice)
                    
                    let points: [CGPoint] = prices.enumerated().map { index, price in
                        let x = (CGFloat(index) / CGFloat(max(1, records.count - 1))) * width
                        let norm = CGFloat((price - minPrice) / range)
                        let y = height - (norm * (height - 30)) - 15
                        return CGPoint(x: x, y: y)
                    }
                    
                    ZStack(alignment: .topLeading) {
                        // Background Grid Lines
                        VStack(spacing: 0) {
                            ForEach(0..<4) { _ in
                                Spacer()
                                Divider().background(Color.metalBorderDark.opacity(0.4))
                            }
                        }
                        
                        // Gradient Fill Area
                        Path { path in
                            guard let first = points.first else { return }
                            path.move(to: first)
                            for i in 1..<points.count {
                                let prev = points[i - 1]
                                let cur = points[i]
                                let midX = (prev.x + cur.x) / 2
                                path.addCurve(to: cur, control1: CGPoint(x: midX, y: prev.y), control2: CGPoint(x: midX, y: cur.y))
                            }
                            path.addLine(to: CGPoint(x: width, y: height))
                            path.addLine(to: CGPoint(x: 0, y: height))
                            path.closeSubpath()
                        }
                        .fill(
                            LinearGradient(
                                gradient: Gradient(colors: [effectiveColor.opacity(0.22), Color.clear]),
                                startPoint: .top,
                                endPoint: .bottom
                            )
                        )
                        
                        // Bezier Curve Stroke
                        Path { path in
                            guard let first = points.first else { return }
                            path.move(to: first)
                            for i in 1..<points.count {
                                let prev = points[i - 1]
                                let cur = points[i]
                                let midX = (prev.x + cur.x) / 2
                                path.addCurve(to: cur, control1: CGPoint(x: midX, y: prev.y), control2: CGPoint(x: midX, y: cur.y))
                            }
                        }
                        .stroke(
                            effectiveColor,
                            style: StrokeStyle(lineWidth: 2.5, lineCap: .round, lineJoin: .round)
                        )
                        
                        // Interactive Crosshair Indicator
                        if let selectedIdx = selectedIndex, selectedIdx < points.count {
                            let pt = points[selectedIdx]
                            
                            // Vertical dashed line
                            Path { path in
                                path.move(to: CGPoint(x: pt.x, y: 0))
                                path.addLine(to: CGPoint(x: pt.x, y: height))
                            }
                            .stroke(
                                Color.white.opacity(0.7),
                                style: StrokeStyle(lineWidth: 1, dash: [4, 4])
                            )
                            
                            // Glowing indicator circle
                            Circle()
                                .fill(effectiveColor.opacity(0.3))
                                .frame(width: 16, height: 16)
                                .position(pt)
                            
                            Circle()
                                .fill(Color.white)
                                .frame(width: 8, height: 8)
                                .position(pt)
                        }
                    }
                    .contentShape(Rectangle())
                    .gesture(
                        DragGesture(minimumDistance: 0)
                            .onChanged { value in
                                let touchX = value.location.x
                                let fraction = max(0, min(1, touchX / width))
                                let index = Int(round(fraction * CGFloat(records.count - 1)))
                                if index != selectedIndex {
                                    selectedIndex = index
                                    HapticManager.selection()
                                }
                            }
                            .onEnded { _ in
                                selectedIndex = nil
                            }
                    )
                }
                .frame(height: 200)
                
                // Time Range Selector Row
                ScrollView(.horizontal, showsIndicators: false) {
                    TimeRangeSelectorView(
                        selectedRange: selectedTimeRange,
                        onSelect: onTimeRangeSelected,
                        activeColor: lineColor
                    )
                    .padding(.horizontal, 2)
                    .padding(.top, 4)
                }
            }
        }
    }
}
