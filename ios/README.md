# MetalScope for iOS

> **Precious Metals. Clear Insights.**

Native iOS edition of **MetalScope**, built with **Swift 5.9** and **SwiftUI** following **Modern Clean MVVM Architecture**. It achieves 100% feature and visual parity with the Android edition.

---

## 1. Architecture & Technologies

- **UI Framework**: SwiftUI (iOS 17+)
- **Architecture**: MVVM + Clean Architecture + Combine + Async/Await
- **Networking**: URLSession with async/await, Yahoo Finance v8 Charts endpoint + api.gold-api.com fallback
- **Storage**: `UserDefaults` backing `PreferencesRepository` & `WatchlistRepository`
- **Charts**: Custom SwiftUI Canvas bezier interpolation graphics & interactive scrubbing crosshair
- **Haptics**: `UIImpactFeedbackGenerator` & `UISelectionFeedbackGenerator`
- **Sharing**: Native `UIActivityViewController` CSV Export & Share

---

## 2. Project Structure

```
ios/
├── MetalScope.xcodeproj/       # Xcode project file
├── Package.swift               # Swift Package Manager manifest
└── MetalScope/
    ├── App/
    │   ├── MetalScopeApp.swift # Application entrypoint
    │   └── ContentView.swift   # Root 5-Tab Bar & Modal routing
    ├── Theme/
    │   ├── ColorTokens.swift   # Dark/Light gold & surface tokens
    │   └── ThemeManager.swift  # Dynamic theme controller
    ├── Models/
    │   ├── MetalType.swift     # XAU, XAG, XPT, XPD enums & metadata
    │   ├── Currency.swift      # USD, EUR, GBP, JPY, AUD, CAD, VND
    │   ├── WeightUnit.swift    # Troy Ounce, Gram, Kilogram
    │   ├── TimeRange.swift     # 1D, 1W, 1M, 3M, 6M, 1Y, ALL
    │   ├── PriceQuote.swift    # Bid, Ask, Spread, Sparklines
    │   ├── HistoricalRecord.swift # Price bars
    │   └── MarketInsight.swift # Macro ratios
    ├── Utilities/
    │   ├── CurrencyFormatter.swift
    │   ├── UnitConverter.swift
    │   └── HapticManager.swift
    ├── Services/
    │   ├── RealMarketDataSource.swift # Live market network feed
    │   └── DemoDataProvider.swift     # Fallback & simulation feed
    ├── Repositories/
    │   ├── MetalRepository.swift
    │   ├── PreferencesRepository.swift
    │   └── WatchlistRepository.swift
    ├── ViewModels/
    │   └── MainViewModel.swift
    ├── Components/
    │   ├── HeaderBarView.swift
    │   ├── MarketStatusBadgeView.swift
    │   ├── FeaturedGoldCardView.swift
    │   ├── MetalPriceCardView.swift
    │   ├── SparklineView.swift
    │   ├── InteractiveChartView.swift
    │   ├── ComparisonChartView.swift
    │   ├── QuickInsightCardView.swift
    │   ├── TimeRangeSelectorView.swift
    │   ├── CurrencyPickerSheet.swift
    │   └── UnitPickerSheet.swift
    ├── Screens/
    │   ├── Home/HomeScreenView.swift
    │   ├── Markets/MarketsScreenView.swift
    │   ├── Charts/ChartsScreenView.swift
    │   ├── History/HistoryScreenView.swift
    │   ├── Detail/MetalDetailScreenView.swift
    │   ├── Watchlist/WatchlistScreenView.swift
    │   ├── Search/SearchScreenView.swift
    │   └── Settings/
    │       ├── SettingsScreenView.swift
    │       ├── DataSourcesScreenView.swift
    │       └── AboutScreenView.swift
    ├── Resources/
    │   ├── Info.plist
    │   └── Assets.xcassets/
    └── Tests/
        ├── CurrencyFormatterTests.swift
        ├── UnitConverterTests.swift
        └── MetalModelTests.swift
```

---

## 3. How to Build & Run in Xcode

1. Open `MetalScope.xcodeproj` in Xcode (version 15.0 or later):
   ```bash
   open ios/MetalScope.xcodeproj
   ```
2. Select an iOS Simulator (e.g., iPhone 15 Pro, iOS 17+) or a connected physical iOS device.
3. Press **Cmd + R** (or click the **Run** button).
4. Run Unit Tests: Press **Cmd + U** to run all unit tests in `MetalScopeTests`.
