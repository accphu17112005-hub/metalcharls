import SwiftUI

struct HeaderBarView: View {
    var title: String = "MetalScope"
    var tagline: String = "Precious Metals. Clear Insights."
    var watchlistCount: Int = 0
    var isRefreshing: Bool = false
    let onSearchClick: () -> Void
    let onWatchlistClick: () -> Void
    let onRefreshClick: () -> Void
    
    @State private var rotationAngle: Double = 0
    
    var body: some View {
        HStack {
            // Brand Logo & Title
            HStack(spacing: 10) {
                ZStack {
                    RoundedRectangle(cornerRadius: 10)
                        .fill(Color.metalGold.opacity(0.15))
                        .frame(width: 38, height: 38)
                    Text("Au")
                        .font(.system(size: 16, weight: .black))
                        .foregroundColor(.metalGold)
                }
                
                VStack(alignment: .leading, spacing: 1) {
                    Text(title)
                        .font(.system(size: 20, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    Text(tagline)
                        .font(.system(size: 11))
                        .foregroundColor(.metalTextSecondaryDark)
                }
            }
            
            Spacer()
            
            // Actions
            HStack(spacing: 6) {
                // Search
                Button(action: onSearchClick) {
                    Image(systemName: "magnifyingglass")
                        .font(.system(size: 17, weight: .semibold))
                        .foregroundColor(.metalTextPrimaryDark)
                        .frame(width: 36, height: 36)
                }
                .buttonStyle(PlainButtonStyle())
                
                // Watchlist with Badge
                Button(action: onWatchlistClick) {
                    ZStack(alignment: .topTrailing) {
                        Image(systemName: watchlistCount > 0 ? "star.fill" : "star")
                            .font(.system(size: 17, weight: .semibold))
                            .foregroundColor(watchlistCount > 0 ? .metalGold : .metalTextPrimaryDark)
                            .frame(width: 36, height: 36)
                        
                        if watchlistCount > 0 {
                            Text("\(watchlistCount)")
                                .font(.system(size: 9, weight: .bold))
                                .foregroundColor(.metalBackgroundDark)
                                .padding(4)
                                .background(Color.metalGold)
                                .clipShape(Circle())
                                .offset(x: 2, y: -2)
                        }
                    }
                }
                .buttonStyle(PlainButtonStyle())
                
                // Refresh
                Button(action: {
                    onRefreshClick()
                    if isRefreshing {
                        withAnimation(.linear(duration: 1.0).repeatForever(autoreverses: false)) {
                            rotationAngle += 360
                        }
                    }
                }) {
                    Image(systemName: "arrow.clockwise")
                        .font(.system(size: 17, weight: .semibold))
                        .foregroundColor(.metalTextPrimaryDark)
                        .rotationEffect(.degrees(isRefreshing ? rotationAngle : 0))
                        .frame(width: 36, height: 36)
                }
                .buttonStyle(PlainButtonStyle())
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
        .onChange(of: isRefreshing) { newValue in
            if newValue {
                withAnimation(.linear(duration: 1.0).repeatForever(autoreverses: false)) {
                    rotationAngle += 360
                }
            } else {
                rotationAngle = 0
            }
        }
    }
}
