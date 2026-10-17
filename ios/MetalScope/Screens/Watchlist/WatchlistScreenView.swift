import SwiftUI

struct WatchlistScreenView: View {
    @ObservedObject var viewModel: MainViewModel
    let onNavigateBack: () -> Void
    let onNavigateToMetal: (String) -> Void
    
    var body: some View {
        let favoriteQuotes = viewModel.quotes.filter { viewModel.watchlistSymbols.contains($0.symbol) }
        
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // Top Bar
                HStack {
                    Button(action: onNavigateBack) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                            .frame(width: 36, height: 36)
                            .background(Color.metalElevatedDark)
                            .clipShape(Circle())
                    }
                    .buttonStyle(PlainButtonStyle())
                    
                    Spacer()
                    
                    Text("Watchlist (\(favoriteQuotes.count))")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    
                    Spacer()
                    
                    Color.clear.frame(width: 36, height: 36)
                }
                .padding(.horizontal, 16)
                .padding(.top, 10)
                
                if favoriteQuotes.isEmpty {
                    VStack(spacing: 16) {
                        Image(systemName: "star")
                            .font(.system(size: 48))
                            .foregroundColor(.metalGold.opacity(0.6))
                        
                        Text("No Favorite Metals")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(.metalTextPrimaryDark)
                        
                        Text("Tap the star icon on any precious metal card in Home or Markets to pin it to your watchlist.")
                            .font(.system(size: 13))
                            .foregroundColor(.metalTextSecondaryDark)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 32)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 80)
                } else {
                    VStack(spacing: 12) {
                        ForEach(favoriteQuotes) { quote in
                            MetalPriceCardView(
                                quote: quote,
                                currency: viewModel.currency,
                                unit: viewModel.unit,
                                isFavorite: true,
                                onFavoriteToggle: { viewModel.toggleWatchlist(symbol: quote.symbol) },
                                onClick: { onNavigateToMetal(quote.symbol) }
                            )
                        }
                    }
                    .padding(.horizontal, 16)
                }
            }
            .padding(.bottom, 30)
        }
        .background(Color.metalBackgroundDark.ignoresSafeArea())
        .navigationBarHidden(true)
    }
}
