import SwiftUI

struct AboutScreenView: View {
    let onNavigateBack: () -> Void
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
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
                    
                    Text("About MetalScope")
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    
                    Spacer()
                    
                    Color.clear.frame(width: 36, height: 36)
                }
                .padding(.horizontal, 16)
                .padding(.top, 10)
                
                // Brand Hero
                VStack(spacing: 12) {
                    ZStack {
                        RoundedRectangle(cornerRadius: 18)
                            .fill(Color.metalGold.opacity(0.18))
                            .frame(width: 72, height: 72)
                        RoundedRectangle(cornerRadius: 18)
                            .stroke(Color.metalGold.opacity(0.5), lineWidth: 2)
                            .frame(width: 72, height: 72)
                        Text("Au")
                            .font(.system(size: 32, weight: .black))
                            .foregroundColor(.metalGold)
                    }
                    
                    Text("MetalScope")
                        .font(.system(size: 26, weight: .black))
                        .foregroundColor(.metalTextPrimaryDark)
                    
                    Text("Precious Metals. Clear Insights.")
                        .font(.system(size: 13, weight: .semibold))
                        .foregroundColor(.metalGold)
                    
                    Text("Version 1.0.0 (Native iOS)")
                        .font(.system(size: 12))
                        .foregroundColor(.metalTextSecondaryDark)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 16)
                
                // Summary & Risk Disclaimer
                VStack(alignment: .leading, spacing: 14) {
                    Text("Institutional Precision in Your Pocket")
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    
                    Text("MetalScope delivers institutional-grade market data, interactive financial charts, and macro performance ratios for gold, silver, platinum, and palladium. Designed for bullion investors, traders, and physical stackers.")
                        .font(.system(size: 13))
                        .lineSpacing(4)
                        .foregroundColor(.metalTextSecondaryDark)
                    
                    Divider().background(Color.metalBorderDark)
                    
                    Text("Financial Risk Disclaimer")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.changeNegative)
                    
                    Text("Precious metals spot prices and physical bullion products carry substantial risk and price volatility. Data provided within MetalScope is for analytical, informational, and educational purposes only. Past performance is not indicative of future market returns.")
                        .font(.system(size: 12))
                        .lineSpacing(4)
                        .foregroundColor(.metalTextSecondaryDark)
                }
                .padding(16)
                .background(Color.metalSurfaceDark)
                .cornerRadius(16)
                .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.metalBorderDark, lineWidth: 1))
                .padding(.horizontal, 16)
            }
            .padding(.bottom, 30)
        }
        .background(Color.metalBackgroundDark.ignoresSafeArea())
        .navigationBarHidden(true)
    }
}
