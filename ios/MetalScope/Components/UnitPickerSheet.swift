import SwiftUI

struct UnitPickerSheet: View {
    let selected: WeightUnit
    let onSelect: (WeightUnit) -> Void
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationStack {
            List {
                ForEach(WeightUnit.allCases) { unit in
                    Button(action: {
                        onSelect(unit)
                        dismiss()
                    }) {
                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                HStack(spacing: 8) {
                                    Text(unit.displayName)
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(.metalTextPrimaryDark)
                                    Text("(\(unit.symbol))")
                                        .font(.system(size: 15, weight: .semibold))
                                        .foregroundColor(.metalGold)
                                }
                                Text("1 oz t = \(String(format: "%.4f", unit.gramsFactor)) g")
                                    .font(.system(size: 12))
                                    .foregroundColor(.metalTextSecondaryDark)
                            }
                            
                            Spacer()
                            
                            if unit == selected {
                                Image(systemName: "checkmark")
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundColor(.metalGold)
                            }
                        }
                        .padding(.vertical, 4)
                    }
                    .listRowBackground(Color.metalSurfaceDark)
                }
            }
            .scrollContentBackground(.hidden)
            .background(Color.metalBackgroundDark)
            .navigationTitle("Select Weight Unit")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Close") { dismiss() }
                        .foregroundColor(.metalGold)
                }
            }
        }
    }
}
