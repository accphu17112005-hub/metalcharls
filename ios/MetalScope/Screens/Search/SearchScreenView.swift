import SwiftUI

struct SearchScreenView: View {
    @ObservedObject var viewModel: MainViewModel
    let onNavigateBack: () -> Void
    let onNavigateToMetal: (String) -> Void
    
    @State private var query: String = ""
    
    private let quickTags = ["XAU", "XAG", "XPT", "XPD", "Gold", "Silver", "Platinum", "Palladium"]
    
    var body: some View {
        let results = viewModel.quotes.filter { quote in
            if query.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                return true
            }
            return quote.symbol.localizedCaseInsensitiveContains(query) ||
                   quote.name.localizedCaseInsensitiveContains(query)
        }
        
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // Top Search Bar with Back Button
                HStack(spacing: 12) {
                    Button(action: onNavigateBack) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                            .frame(width: 36, height: 36)
                            .background(Color.metalElevatedDark)
                            .clipShape(Circle())
                    }
                    .buttonStyle(PlainButtonStyle())
                    
                    HStack {
                        Image(systemName: "magnifyingglass")
                            .foregroundColor(.metalTextSecondaryDark)
                        TextField("Search symbol or metal name", text: $query)
                            .font(.system(size: 15))
                            .foregroundColor(.metalTextPrimaryDark)
                        
                        if !query.isEmpty {
                            Button(action: { query = "" }) {
                                Image(systemName: "xmark.circle.fill")
                                    .foregroundColor(.metalTextMutedDark)
                            }
                        }
                    }
                    .padding(10)
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(12)
                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.metalBorderDark, lineWidth: 1))
                }
                .padding(.horizontal, 16)
                .padding(.top, 10)
                
                // Quick Suggestion Pills
                VStack(alignment: .leading, spacing: 8) {
                    Text("Quick Suggestions")
                        .font(.system(size: 12, weight: .semibold))
                        .foregroundColor(.metalTextSecondaryDark)
                        .padding(.horizontal, 16)
                    
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(quickTags, id: \.self) { tag in
                                Button(action: {
                                    query = tag
                                    HapticManager.selection()
                                }) {
                                    Text(tag)
                                        .font(.system(size: 12, weight: .bold))
                                        .foregroundColor(query.lowercased() == tag.lowercased() ? .metalBackgroundDark : .metalGold)
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 6)
                                        .background(query.lowercased() == tag.lowercased() ? Color.metalGold : Color.metalElevatedDark)
                                        .cornerRadius(8)
                                        .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color.metalGold.opacity(0.4), lineWidth: 1))
                                }
                                .buttonStyle(PlainButtonStyle())
                            }
                        }
                        .padding(.horizontal, 16)
                    }
                }
                
                // Search Results
                VStack(alignment: .leading, spacing: 10) {
                    Text("Results (\(results.count))")
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                        .padding(.horizontal, 16)
                    
                    if results.isEmpty {
                        VStack(spacing: 12) {
                            Image(systemName: "magnifyingglass")
                                .font(.system(size: 32))
                                .foregroundColor(.metalTextMutedDark)
                            Text("No metals found matching '\(query)'")
                                .font(.system(size: 14))
                                .foregroundColor(.metalTextSecondaryDark)
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 60)
                    } else {
                        VStack(spacing: 10) {
                            ForEach(results) { quote in
                                MetalPriceCardView(
                                    quote: quote,
                                    currency: viewModel.currency,
                                    unit: viewModel.unit,
                                    isFavorite: viewModel.watchlistSymbols.contains(quote.symbol),
                                    onFavoriteToggle: { viewModel.toggleWatchlist(symbol: quote.symbol) },
                                    onClick: { onNavigateToMetal(quote.symbol) }
                                )
                            }
                        }
                        .padding(.horizontal, 16)
                    }
                }
            }
            .padding(.bottom, 30)
        }
        .background(Color.metalBackgroundDark.ignoresSafeArea())
        .navigationBarHidden(true)
    }
}
