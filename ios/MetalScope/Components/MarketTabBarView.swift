import SwiftUI

// MARK: - MarketTab Enum
enum MarketTab: String, CaseIterable, Identifiable {
    case home = "HOME"
    case gold = "GOLD"
    case silver = "SILVER"
    case shanghai = "SHANGHAI"
    case comex = "COMEX"
    case india = "INDIA"

    var id: String { rawValue }

    var title: String {
        switch self {
        case .home: return "Home"
        case .gold: return "Gold (XAU)"
        case .silver: return "Silver (XAG)"
        case .shanghai: return "Shanghai"
        case .comex: return "COMEX Silver"
        case .india: return "India"
        }
    }

    var iconEmoji: String {
        switch self {
        case .home: return "🏠"
        case .gold: return "🟡"
        case .silver: return "⚪"
        case .shanghai: return "🇨🇳"
        case .comex: return "🏛️"
        case .india: return "🇮🇳"
        }
    }

    var badge: String? {
        switch self {
        case .home: return nil
        case .gold: return "$4,016"
        case .silver: return "$53.88"
        case .shanghai: return "+12.5%"
        case .comex: return "337.5M oz"
        case .india: return "+18.0%"
        }
    }

    var badgeColor: Color {
        switch self {
        case .home: return .clear
        case .gold: return .metalGold
        case .silver: return .metalSilver
        case .shanghai: return Color(red: 239/255, green: 68/255, blue: 68/255)
        case .comex: return Color(red: 96/255, green: 165/255, blue: 250/255)
        case .india: return Color(red: 245/255, green: 158/255, blue: 11/255)
        }
    }
}

// MARK: - MarketTabBarView
struct MarketTabBarView: View {
    let selectedTab: MarketTab
    let onTabSelected: (MarketTab) -> Void

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(MarketTab.allCases) { tab in
                    let isSelected = tab == selectedTab
                    Button(action: {
                        HapticManager.selection()
                        onTabSelected(tab)
                    }) {
                        HStack(spacing: 6) {
                            Text(tab.iconEmoji)
                                .font(.system(size: 13))

                            Text(tab.title)
                                .font(.system(size: 12, weight: isSelected ? .bold : .medium))
                                .foregroundColor(isSelected ? .metalGold : .metalTextSecondaryDark)

                            if let badge = tab.badge {
                                Text(badge)
                                    .font(.system(size: 10, weight: .bold))
                                    .foregroundColor(tab.badgeColor)
                                    .padding(.horizontal, 5)
                                    .padding(.vertical, 2)
                                    .background(tab.badgeColor.opacity(0.16))
                                    .clipShape(Capsule())
                            }
                        }
                        .padding(.horizontal, 12)
                        .padding(.vertical, 7)
                        .background(isSelected ? Color.metalGold.opacity(0.18) : Color.metalElevatedDark)
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
            .padding(.vertical, 6)
        }
    }
}
