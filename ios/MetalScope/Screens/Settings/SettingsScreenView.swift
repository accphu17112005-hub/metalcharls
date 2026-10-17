import SwiftUI

struct SettingsScreenView: View {
    @ObservedObject var viewModel: MainViewModel
    let onNavigateToDataSources: () -> Void
    let onNavigateToAbout: () -> Void
    
    @State private var showCurrencyPicker: Bool = false
    @State private var showUnitPicker: Bool = false
    @State private var showResetAlert: Bool = false
    
    private let refreshOptions = [
        (10, "10s (Fast)"),
        (15, "15s (Default)"),
        (30, "30s"),
        (60, "60s (Battery Saver)"),
        (0, "Manual Refresh Only")
    ]
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 22) {
                // Header
                VStack(alignment: .leading, spacing: 2) {
                    Text("Settings")
                        .font(.system(size: 26, weight: .bold))
                        .foregroundColor(.metalTextPrimaryDark)
                    Text("Currency, weight units, refresh rate & theme")
                        .font(.system(size: 12))
                        .foregroundColor(.metalTextSecondaryDark)
                }
                .padding(.horizontal, 16)
                .padding(.top, 10)
                
                // Section 1: Market Data & Conversions
                VStack(alignment: .leading, spacing: 10) {
                    Text("MARKET DATA & CONVERSIONS")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.metalGold)
                        .padding(.horizontal, 16)
                    
                    VStack(spacing: 1) {
                        // Currency Row
                        Button(action: { showCurrencyPicker = true }) {
                            SettingsRow(
                                icon: "dollarsign.circle",
                                title: "Display Currency",
                                subtitle: "\(viewModel.currency.displayName) (\(viewModel.currency.symbol))",
                                value: viewModel.currency.code
                            )
                        }
                        
                        Divider().background(Color.metalBorderDark.opacity(0.4))
                        
                        // Weight Unit Row
                        Button(action: { showUnitPicker = true }) {
                            SettingsRow(
                                icon: "scalemass",
                                title: "Weight Unit",
                                subtitle: viewModel.unit.displayName,
                                value: viewModel.unit.symbol
                            )
                        }
                    }
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(16)
                    .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.metalBorderDark, lineWidth: 1))
                    .padding(.horizontal, 16)
                }
                
                // Section 2: Appearance & Refresh
                VStack(alignment: .leading, spacing: 10) {
                    Text("DISPLAY & REFRESH")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.metalGold)
                        .padding(.horizontal, 16)
                    
                    VStack(spacing: 1) {
                        // Theme Mode
                        HStack {
                            Label("Theme Mode", systemImage: "moon.fill")
                                .font(.system(size: 15, weight: .semibold))
                                .foregroundColor(.metalTextPrimaryDark)
                            Spacer()
                            Picker("Theme Mode", selection: Binding(
                                get: { viewModel.themeMode },
                                set: { viewModel.setThemeMode($0) }
                            )) {
                                ForEach(AppThemeMode.allCases) { mode in
                                    Text(mode.displayName).tag(mode)
                                }
                            }
                            .pickerStyle(MenuPickerStyle())
                            .tint(.metalGold)
                        }
                        .padding(14)
                        
                        Divider().background(Color.metalBorderDark.opacity(0.4))
                        
                        // Auto-refresh interval
                        HStack {
                            Label("Auto-Refresh", systemImage: "clock.arrow.2.circlepath")
                                .font(.system(size: 15, weight: .semibold))
                                .foregroundColor(.metalTextPrimaryDark)
                            Spacer()
                            Picker("Refresh", selection: Binding(
                                get: { viewModel.refreshInterval },
                                set: { viewModel.setRefreshInterval($0) }
                            )) {
                                ForEach(refreshOptions, id: \.0) { item in
                                    Text(item.1).tag(item.0)
                                }
                            }
                            .pickerStyle(MenuPickerStyle())
                            .tint(.metalGold)
                        }
                        .padding(14)
                        
                        Divider().background(Color.metalBorderDark.opacity(0.4))
                        
                        // Haptic Feedback Toggle
                        Toggle(isOn: Binding(
                            get: { viewModel.isHapticsEnabled },
                            set: { viewModel.setHapticsEnabled($0) }
                        )) {
                            Label("Haptic Feedback", systemImage: "waveform")
                                .font(.system(size: 15, weight: .semibold))
                                .foregroundColor(.metalTextPrimaryDark)
                        }
                        .tint(.metalGold)
                        .padding(14)
                    }
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(16)
                    .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.metalBorderDark, lineWidth: 1))
                    .padding(.horizontal, 16)
                }
                
                // Section 3: Information & Resources
                VStack(alignment: .leading, spacing: 10) {
                    Text("METHODOLOGY & LEGAL")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.metalGold)
                        .padding(.horizontal, 16)
                    
                    VStack(spacing: 1) {
                        Button(action: onNavigateToDataSources) {
                            SettingsNavRow(icon: "chart.line.uptrend.xyaxis", title: "Data Sources & Methodology")
                        }
                        
                        Divider().background(Color.metalBorderDark.opacity(0.4))
                        
                        Button(action: onNavigateToAbout) {
                            SettingsNavRow(icon: "info.circle", title: "About MetalScope & Risk Disclaimer")
                        }
                    }
                    .background(Color.metalSurfaceDark)
                    .cornerRadius(16)
                    .overlay(RoundedRectangle(cornerRadius: 16).stroke(Color.metalBorderDark, lineWidth: 1))
                    .padding(.horizontal, 16)
                }
                
                // Section 4: Data Management
                VStack(spacing: 10) {
                    Button(action: { showResetAlert = true }) {
                        HStack {
                            Image(systemName: "trash")
                                .font(.system(size: 14, weight: .bold))
                            Text("Reset All Data & Clear Cache")
                                .font(.system(size: 14, weight: .bold))
                        }
                        .foregroundColor(.changeNegative)
                        .frame(maxWidth: .infinity)
                        .padding(14)
                        .background(Color.changeNegative.opacity(0.12))
                        .cornerRadius(12)
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.changeNegative.opacity(0.3), lineWidth: 1))
                    }
                    .padding(.horizontal, 16)
                }
                .padding(.top, 10)
            }
            .padding(.bottom, 30)
        }
        .background(Color.metalBackgroundDark.ignoresSafeArea())
        .sheet(isPresented: $showCurrencyPicker) {
            CurrencyPickerSheet(selected: viewModel.currency) { newCur in
                viewModel.setCurrency(newCur)
                showCurrencyPicker = false
            }
        }
        .sheet(isPresented: $showUnitPicker) {
            UnitPickerSheet(selected: viewModel.unit) { newUnit in
                viewModel.setWeightUnit(newUnit)
                showUnitPicker = false
            }
        }
        .alert("Reset MetalScope?", isPresented: $showResetAlert) {
            Button("Cancel", role: .cancel) {}
            Button("Reset Everything", role: .destructive) {
                viewModel.clearAllData()
            }
        } message: {
            Text("This will restore default currency (USD), weight unit (oz t), watchlist favorites, and clear cached historical charts.")
        }
    }
}

struct SettingsRow: View {
    let icon: String
    let title: String
    let subtitle: String
    let value: String
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .font(.system(size: 18))
                .foregroundColor(.metalGold)
                .frame(width: 24)
            
            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundColor(.metalTextPrimaryDark)
                Text(subtitle)
                    .font(.system(size: 12))
                    .foregroundColor(.metalTextSecondaryDark)
            }
            
            Spacer()
            
            Text(value)
                .font(.system(size: 14, weight: .bold))
                .foregroundColor(.metalGold)
            
            Image(systemName: "chevron.right")
                .font(.system(size: 12, weight: .semibold))
                .foregroundColor(.metalTextMutedDark)
        }
        .padding(14)
    }
}

struct SettingsNavRow: View {
    let icon: String
    let title: String
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .font(.system(size: 18))
                .foregroundColor(.metalGold)
                .frame(width: 24)
            
            Text(title)
                .font(.system(size: 15, weight: .semibold))
                .foregroundColor(.metalTextPrimaryDark)
            
            Spacer()
            
            Image(systemName: "chevron.right")
                .font(.system(size: 12, weight: .semibold))
                .foregroundColor(.metalTextMutedDark)
        }
        .padding(14)
    }
}
