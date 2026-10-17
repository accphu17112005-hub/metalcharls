import SwiftUI

struct CurrencyPickerSheet: View {
    let selected: Currency
    let onSelect: (Currency) -> Void
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationStack {
            List {
                ForEach(Currency.allCases) { currency in
                    Button(action: {
                        onSelect(currency)
                        dismiss()
                    }) {
                        HStack {
                            VStack(alignment: .leading, spacing: 2) {
                                HStack(spacing: 8) {
                                    Text(currency.code)
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(.metalTextPrimaryDark)
                                    Text("(\(currency.symbol))")
                                        .font(.system(size: 15, weight: .semibold))
                                        .foregroundColor(.metalGold)
                                }
                                Text(currency.displayName)
                                    .font(.system(size: 13))
                                    .foregroundColor(.metalTextSecondaryDark)
                            }
                            
                            Spacer()
                            
                            if currency == selected {
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
            .navigationTitle("Select Currency")
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
