import SwiftUI

struct ComparisonChartView: View {
    let metalData: [MetalType: [HistoricalRecord]]
    
    @State private var enabledMetals: Set<MetalType> = Set(MetalType.allCases)
    
    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            // Legend / Filter Chips
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(MetalType.allCases) { metal in
                        let isSelected = enabledMetals.contains(metal)
                        Button(action: {
                            if isSelected {
                                if enabledMetals.count > 1 {
                                    enabledMetals.remove(metal)
                                }
                            } else {
                                enabledMetals.insert(metal)
                            }
                            HapticManager.selection()
                        }) {
                            HStack(spacing: 6) {
                                Circle()
                                    .fill(metal.defaultColor)
                                    .frame(width: 8, height: 8)
                                Text(metal.displayName)
                                    .font(.system(size: 12, weight: .semibold))
                                    .foregroundColor(isSelected ? .metalTextPrimaryDark : .metalTextMutedDark)
                            }
                            .padding(.horizontal, 10)
                            .padding(.vertical, 6)
                            .background(isSelected ? metal.defaultColor.opacity(0.18) : Color.metalElevatedDark)
                            .cornerRadius(8)
                            .overlay(
                                RoundedRectangle(cornerRadius: 8)
                                    .stroke(isSelected ? metal.defaultColor : Color.metalBorderDark, lineWidth: 1)
                            )
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                .padding(.horizontal, 2)
            }
            
            // Multi-series Normalized Performance Chart
            GeometryReader { geometry in
                let width = geometry.size.width
                let height = geometry.size.height
                
                // Calculate percentage changes normalized to 0.0%
                let seriesData: [(metal: MetalType, points: [Double])] = enabledMetals.compactMap { metal in
                    guard let records = metalData[metal], records.count >= 2,
                          let first = records.first?.closeUsdPerOz, first > 0 else { return nil }
                    let normalized = records.map { (($0.closeUsdPerOz - first) / first) * 100.0 }
                    return (metal, normalized)
                }
                
                let allPoints = seriesData.flatMap { $0.points }
                let minPct = min(-2.0, allPoints.min() ?? -5.0)
                let maxPct = max(2.0, allPoints.max() ?? 5.0)
                let range = max(1.0, maxPct - minPct)
                
                let zeroY = height - (CGFloat((0.0 - minPct) / range) * (height - 30)) - 15
                
                ZStack(alignment: .topLeading) {
                    // Zero baseline (dotted)
                    Path { path in
                        path.move(to: CGPoint(x: 0, y: zeroY))
                        path.addLine(to: CGPoint(x: width, y: zeroY))
                    }
                    .stroke(Color.metalBorderDark, style: StrokeStyle(lineWidth: 1, dash: [4, 4]))
                    
                    Text("0.0%")
                        .font(.system(size: 9, weight: .bold))
                        .foregroundColor(.metalTextMutedDark)
                        .position(x: 20, y: max(10, zeroY - 8))
                    
                    // Series lines
                    ForEach(seriesData, id: \.metal) { item in
                        let pts: [CGPoint] = item.points.enumerated().map { index, pct in
                            let x = (CGFloat(index) / CGFloat(max(1, item.points.count - 1))) * width
                            let norm = CGFloat((pct - minPct) / range)
                            let y = height - (norm * (height - 30)) - 15
                            return CGPoint(x: x, y: y)
                        }
                        
                        Path { path in
                            guard let first = pts.first else { return }
                            path.move(to: first)
                            for i in 1..<pts.count {
                                let prev = pts[i - 1]
                                let cur = pts[i]
                                let midX = (prev.x + cur.x) / 2
                                path.addCurve(to: cur, control1: CGPoint(x: midX, y: prev.y), control2: CGPoint(x: midX, y: cur.y))
                            }
                        }
                        .stroke(item.metal.defaultColor, style: StrokeStyle(lineWidth: 2, lineCap: .round, lineJoin: .round))
                    }
                }
            }
            .frame(height: 220)
        }
    }
}
