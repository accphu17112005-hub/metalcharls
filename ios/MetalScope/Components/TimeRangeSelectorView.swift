import SwiftUI

struct TimeRangeSelectorView: View {
    let selectedRange: TimeRange
    let onSelect: (TimeRange) -> Void
    var activeColor: Color = .metalGold
    
    var body: some View {
        HStack(spacing: 6) {
            ForEach(TimeRange.allCases) { range in
                let isSelected = range == selectedRange
                Button(action: {
                    onSelect(range)
                    HapticManager.selection()
                }) {
                    Text(range.label)
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(isSelected ? activeColor : .metalTextSecondaryDark)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 6)
                        .background(
                            isSelected ? activeColor.opacity(0.18) : Color.metalElevatedDark
                        )
                        .cornerRadius(8)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(isSelected ? activeColor : Color.metalBorderDark, lineWidth: 1)
                        )
                }
                .buttonStyle(PlainButtonStyle())
            }
        }
    }
}
