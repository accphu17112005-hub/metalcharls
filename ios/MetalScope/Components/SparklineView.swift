import SwiftUI

struct SparklineView: View {
    let data: [Double]
    var isPositive: Bool = true
    var showFill: Bool = true
    
    var body: some View {
        GeometryReader { geometry in
            let width = geometry.size.width
            let height = geometry.size.height
            
            if data.count >= 2 && width > 0 && height > 4 {
                let strokeColor: Color = isPositive ? .changePositive : .changeNegative
                let fillColor: Color = strokeColor.opacity(0.15)
                
                let minVal = data.min() ?? 0.0
                let maxVal = data.max() ?? 1.0
                let range = max(0.0001, maxVal - minVal)
                
                let points: [CGPoint] = data.enumerated().map { index, value in
                    let x = (CGFloat(index) / CGFloat(data.count - 1)) * width
                    let normalized = CGFloat((value - minVal) / range)
                    let y = height - (normalized * (height - 4)) - 2
                    return CGPoint(x: x, y: y)
                }
                
                ZStack {
                    if showFill {
                        // Area Fill
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
                                gradient: Gradient(colors: [fillColor, Color.clear]),
                                startPoint: .top,
                                endPoint: .bottom
                            )
                        )
                    }
                    
                    // Stroke Line
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
                        strokeColor,
                        style: StrokeStyle(lineWidth: 2, lineCap: .round, lineJoin: .round)
                    )
                }
            }
        }
    }
}
