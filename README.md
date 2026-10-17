# MetalScope
> **Precious Metals. Clear Insights.**

A premier, professional mobile precious metals market dashboard built with **Kotlin** and **Jetpack Compose** following **Modern Android Architecture (MVVM & Clean Architecture)**.

---

## 1. Overview & Capabilities

MetalScope delivers institutional-grade market data, interactive financial charts, and macro performance ratios for gold, silver, platinum, and palladium:

- **Gold (XAU)** — 24K Spot Benchmark & Global Reserve Anchor
- **Silver (XAG)** — Dual-Demand Industrial & Monetary Store
- **Platinum (XPT)** — Green Hydrogen & Catalytic Dense Metal
- **Palladium (XPD)** — Automotive & Semiconductor Rare Catalyst

---

## 2. Visual Design System

The application features a tailored **dark-first** financial dashboard with optional light theme support:

| Token | Hex Value | Role |
| :--- | :--- | :--- |
| **Background** | `#0B1020` | Canvas base & system bar color |
| **Main Surface** | `#121A2B` | Cards & container background |
| **Elevated Surface** | `#192338` | Interactive chips, pills & inputs |
| **Border / Outline** | `#283449` | Restrained card borders & dividers |
| **Text Primary** | `#F4F7FC` | High-contrast numbers & titles |
| **Text Secondary** | `#96A3B8` | Subtitles, labels, and metadata |
| **Gold Accent** | `#D9B45B` | Primary brand accent & Gold (Au) |
| **Silver Accent** | `#B8C4D6` | Silver (Ag) charts & icons |
| **Positive Change** | `#22C55E` | 24h gain indicator & positive ticks |
| **Negative Change** | `#EF4444` | 24h loss indicator & negative ticks |

---

## 3. Core Features & Navigation

### 5 Main Bottom Navigation Tabs:
1. **Home Dashboard**:
   - Header with MetalScope logo, search shortcut, and watchlist counter.
   - Market session indicator (London LBMA, NY COMEX, Asian session, Weekend status) with live timestamps.
   - **Featured Gold Card (XAU/USD)**: High-resolution live price, 24h range slider bar, and mini sparkline.
   - **Precious Metals Grid**: Silver, Platinum, Palladium spot quotes with instant unit & currency conversion.
   - **Market Overview Chart**: Tab switcher across metals with timeframes (1D, 1W, 1M, 3M, 6M, 1Y, ALL) and interactive touch scrub crosshair.
   - **Quick Insights**: Real-time Gold/Silver ratio with 50-100x historical gauge, Au/Pt spread, and weekly leader.

2. **Markets Screen**:
   - Real-time search by symbol (XAU, XAG, XPT, XPD) or metal name.
   - Sorting options: Highest Price, Lowest Price, 24h Gainers, 24h Losers, Alphabetical.
   - Filtering: All, Precious Metals, Ratios.
   - View mode switcher: Compact list rows vs rich detailed cards.
   - Favorite star toggle on every row.

3. **Charts Studio**:
   - High-performance Canvas financial charts with smooth bezier curve interpolation and gradient fills.
   - Interactive crosshair scrub: dragging across the chart displays exact price, date/time, and % change pill tooltip.
   - Three analytical charting modes:
     - **Single Metal**: Individual high-resolution historical chart.
     - **Multi-Metal %**: Normalized performance comparison (baseline 0.0%) across all 4 metals on a single chart with color-coded toggle chips.
     - **Gold/Silver Ratio**: Dedicated macro ratio indicator.
   - Period summary metrics: Period High, Period Low, and Total Return %.

4. **History Explorer**:
   - Historical records table and cards with Date, Close, Open, High, Low, and Change %.
   - Preset timeframes: 1D, 1W, 1M, 3M, 6M, 1Y, ALL.
   - Summary cards: Period High, Period Low, Period Average, Total Return.
   - **CSV Export & Share**: One-tap share of historical quotes via Android system share sheet.

5. **Settings & Preferences**:
   - **Currencies**: Instant conversion to USD ($), EUR (€), GBP (£), JPY (¥), AUD (A$), CAD (C$), and VND (₫).
   - **Weight Units**: Troy Ounce (oz t), Gram (g), and Kilogram (kg) using exact physical conversion constants ($1\text{ oz t} = 31.1034768\text{ g}$).
   - **Theme**: Dark, Light, or System default.
   - **Auto-Refresh**: 10s, 30s, 60s, or Manual mode.
   - **Haptic Feedback**: Configurable vibration on chart scrub.
   - **Data Sources & Methodology**: Explaining LBMA auction fixings, COMEX futures, and spot calculations.
   - **About & Legal**: Version information and prominent financial risk disclaimer.

### Detail & Additional Screens:
- **Metal Detail Screen**: In-depth stats (Bid, Ask, Spread, Volume, Standard Purity, Range Bar), **Holdings Value Calculator** (input quantity in selected unit $\rightarrow$ calculates total portfolio market value), and fundamental background.
- **Dedicated Watchlist Screen**: Filtered view of all favorited metals.
- **Fast Fullscreen Search Screen**: Real-time search with symbol pills.

---

## 4. Architecture & Technology Stack

```
org.metalscope.app
├── MetalScopeApp.kt          # Application singleton & repository container
├── MainActivity.kt           # Edge-to-edge Compose host
├── theme/                    # Color, Type, Theme tokens
├── data/
│   ├── model/               # MetalType, PriceQuote, WeightUnit, Currency, TimeRange
│   ├── source/              # DemoDataProvider & API adapter
│   └── repository/          # MetalRepository, PreferencesRepository, WatchlistRepository
├── ui/
│   ├── navigation/          # Screen routes, BottomNavItems, MetalScopeNavigation
│   ├── components/          # Canvas charts, sparklines, cards, status badges
│   ├── screens/             # Home, Markets, Detail, Charts, History, Watchlist, Search, Settings
│   └── viewmodel/           # MainViewModel, ViewModelFactory, MainUiState
└── util/                    # CurrencyFormatter, UnitConverter
```

- **UI**: Jetpack Compose + Material 3 + Custom Canvas Graphics
- **Architecture**: MVVM + Clean Architecture + StateFlow
- **Storage**: AndroidX Jetpack Preferences DataStore
- **Async & Reactive**: Kotlin Coroutines + Flow

---

## 5. Build & Run Instructions

### Prerequisites
- JDK 21 installed (`/usr/lib/jvm/java-21-openjdk-amd64`)
- Android SDK installed (`~/Android/Sdk`) with API 34 or 35 platforms and build-tools
- Gradle 8.14+ (bundled with `gradlew`)

### 1. Build Debug APK
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew assembleDebug
```
The generated APK will be at:
`app/build/outputs/apk/debug/app-debug.apk`

### 2. Run Unit Tests
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew test
```

### 3. Install on Connected Device or Emulator
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew installDebug
```
Or start the app directly via `adb`:
```bash
adb shell am start -n org.metalscope.app/.MainActivity
```

### 4. Opening in Android Studio
1. Open Android Studio.
2. Select **Open** and choose this project directory (`/home/huydo/Downloads/metalcharts`).
3. Android Studio will automatically recognize the `settings.gradle.kts` and sync Gradle.
4. Click the green **Run** button to launch on your device or emulator.

---

## 6. iOS Native Edition (Swift & SwiftUI)

The complete native iOS implementation is located in the [`ios/`](file:///home/huydo/Downloads/metalcharts/ios/) directory:
- **100% Feature Parity**: Replicates all 5 core navigation tabs (Home, Markets, Charts Studio, History Explorer, Settings) and secondary screens (Detail, Watchlist, Search, About, Data Sources).
- **Technology Stack**: Swift 5.9, SwiftUI, Combine, URLSession async/await, and custom Canvas interactive charts with crosshair scrub.
- **Xcode Ready**: Can be opened immediately with Xcode via `ios/MetalScope.xcodeproj` or as a Swift Package with `ios/Package.swift`.
- Detailed instructions: [`ios/README.md`](file:///home/huydo/Downloads/metalcharts/ios/README.md).

---

## 7. License & Disclaimer

Precious metals spot prices and physical bullion products carry substantial risk and volatility. MetalScope is designed for analytical and informational purposes. Past performance is not indicative of future results.
