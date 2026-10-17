import SwiftUI

struct MarketStatusBadgeView: View {
    let isOpen: Bool
    let sessionText: String
    let lastUpdated: Int64
    let currency: Currency
    let unit: WeightUnit
    let onCurrencyClick: () -> Void
    let onUnitClick: () -> Void
    
    private var timeString: String {
        let date = Date(timeIntervalSince1970: TimeInterval(lastUpdated) / 1000.0)
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm:ss"
        formatter.locale = Locale(identifier: "en_US")
        return formatter.string(from: date)
    }
    
    var body: some View {
        HStack {
            // Status Dot + Session + Time
            HStack(spacing: 6) {
                Circle()
                    .fill(isOpen ? Color.changePositive : Color.changeNegative)
                    .frame(width: 8, height: 8)
                
                Text(sessionText)
                    .font(.system(size: 11, weight: .medium))
                    .foregroundColor(.metalTextPrimaryDark)
                    .lineLimit(1)
                
                Text("• \(timeString)")
                    .font(.system(size: 11))
                    .foregroundColor(.metalTextMutedDark)
            }
            .padding(.horizontal, 10)
            .padding(.vertical, 6)
            .background(Color.metalSurfaceDark)
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(Color.metalBorderDark, lineWidth: 1)
            )
            
            Spacer()
            
            // Currency & Unit Switchers
            HStack(spacing: 6) {
                Button(action: onCurrencyClick) {
                    Text("\(currency.code) (\(currency.symbol))")
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(.metalGold)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 5)
                        .background(Color.metalElevatedDark)
                        .cornerRadius(8)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.metalBorderDark, lineWidth: 1)
                        )
                }
                .buttonStyle(PlainButtonStyle())
                
                Button(action: onUnitClick) {
                    Text(unit.symbol)
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(.metalTextPrimaryDark)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 5)
                        .background(Color.metalElevatedDark)
                        .cornerRadius(8)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.metalBorderDark, lineWidth: 1)
                        )
                }
                .buttonStyle(PlainButtonStyle())
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 6)
    }
}
