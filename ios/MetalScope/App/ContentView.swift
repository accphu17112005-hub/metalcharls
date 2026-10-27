import SwiftUI

enum TabSelection: Hashable {
    case home
    case shanghai
    case comex
    case india
    case markets
}

enum ActiveModal: Identifiable {
    case detail(symbol: String)
    case search
    case watchlist
    case settings
    case charts
    case history
    case dataSources
    case about
    case shanghai
    case comex
    case india
    
    var id: String {
        switch self {
        case .detail(let s): return "detail_\(s)"
        case .search: return "search"
        case .watchlist: return "watchlist"
        case .settings: return "settings"
        case .charts: return "charts"
        case .history: return "history"
        case .dataSources: return "dataSources"
        case .about: return "about"
        case .shanghai: return "shanghai"
        case .comex: return "comex"
        case .india: return "india"
        }
    }
}

struct ContentView: View {
    @StateObject private var viewModel = MainViewModel()
    @State private var selectedTab: TabSelection = .home
    @State private var activeModal: ActiveModal? = nil
    
    init() {
        // Customize UITabBar appearance for modern dark theme
        let appearance = UITabBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = UIColor(Color.metalSurfaceDark)
        appearance.shadowColor = UIColor(Color.metalBorderDark)
        
        let itemAppearance = UITabBarItemAppearance()
        itemAppearance.normal.iconColor = UIColor(Color.metalTextMutedDark)
        itemAppearance.normal.titleTextAttributes = [.foregroundColor: UIColor(Color.metalTextMutedDark)]
        
        itemAppearance.selected.iconColor = UIColor(Color.metalGold)
        itemAppearance.selected.titleTextAttributes = [.foregroundColor: UIColor(Color.metalGold)]
        
        appearance.stackedLayoutAppearance = itemAppearance
        appearance.inlineLayoutAppearance = itemAppearance
        appearance.compactInlineLayoutAppearance = itemAppearance
        
        UITabBar.appearance().standardAppearance = appearance
        UITabBar.appearance().scrollEdgeAppearance = appearance
    }
    
    private func handleMarketTabNavigation(_ tab: MarketTab) {
        switch tab {
        case .home:
            activeModal = nil
            selectedTab = .home
        case .gold:
            activeModal = .detail(symbol: "XAU")
        case .silver:
            activeModal = .detail(symbol: "XAG")
        case .shanghai:
            activeModal = nil
            selectedTab = .shanghai
        case .comex:
            activeModal = nil
            selectedTab = .comex
        case .india:
            activeModal = nil
            selectedTab = .india
        }
    }
    
    var body: some View {
        TabView(selection: $selectedTab) {
            // Tab 1: Home
            HomeScreenView(
                viewModel: viewModel,
                onNavigateToMetal: { sym in activeModal = .detail(symbol: sym) },
                onNavigateToSearch: { activeModal = .search },
                onNavigateToWatchlist: { activeModal = .watchlist },
                onNavigateToTab: { tab in handleMarketTabNavigation(tab) },
                onNavigateToSettings: { activeModal = .settings }
            )
            .tabItem {
                Label("Home", systemImage: "house.fill")
            }
            .tag(TabSelection.home)
            
            // Tab 2: Shanghai (SGE)
            ShanghaiScreenView(
                viewModel: viewModel,
                onNavigateBack: { selectedTab = .home },
                onNavigateToTab: { tab in handleMarketTabNavigation(tab) }
            )
            .tabItem {
                Label("Shanghai", systemImage: "globe.asia.australia.fill")
            }
            .tag(TabSelection.shanghai)

            // Tab 3: COMEX Silver & Inventory
            ComexScreenView(
                viewModel: viewModel,
                onNavigateBack: { selectedTab = .home },
                onNavigateToTab: { tab in handleMarketTabNavigation(tab) }
            )
            .tabItem {
                Label("COMEX", systemImage: "shippingbox.fill")
            }
            .tag(TabSelection.comex)

            // Tab 4: India (MCX)
            IndiaScreenView(
                viewModel: viewModel,
                onNavigateBack: { selectedTab = .home },
                onNavigateToTab: { tab in handleMarketTabNavigation(tab) }
            )
            .tabItem {
                Label("India", systemImage: "indianrupeesign.circle.fill")
            }
            .tag(TabSelection.india)
            
            // Tab 5: Markets
            MarketsScreenView(
                viewModel: viewModel,
                onNavigateToMetal: { sym in activeModal = .detail(symbol: sym) }
            )
            .tabItem {
                Label("Markets", systemImage: "chart.bar.fill")
            }
            .tag(TabSelection.markets)
        }
        .tint(.metalGold)
        .fullScreenCover(item: $activeModal) { modal in
            switch modal {
            case .detail(let sym):
                MetalDetailScreenView(
                    symbol: sym,
                    viewModel: viewModel,
                    onNavigateBack: { activeModal = nil },
                    onNavigateToTab: { tab in handleMarketTabNavigation(tab) }
                )
            case .search:
                SearchScreenView(
                    viewModel: viewModel,
                    onNavigateBack: { activeModal = nil },
                    onNavigateToMetal: { sym in
                        activeModal = .detail(symbol: sym)
                    }
                )
            case .watchlist:
                WatchlistScreenView(
                    viewModel: viewModel,
                    onNavigateBack: { activeModal = nil },
                    onNavigateToMetal: { sym in
                        activeModal = .detail(symbol: sym)
                    }
                )
            case .settings:
                SettingsScreenView(
                    viewModel: viewModel,
                    onNavigateToDataSources: { activeModal = .dataSources },
                    onNavigateToAbout: { activeModal = .about },
                    onNavigateToCharts: { activeModal = .charts },
                    onNavigateToHistory: { activeModal = .history }
                )
                .overlay(alignment: .topTrailing) {
                    Button(action: { activeModal = nil }) {
                        Image(systemName: "xmark.circle.fill")
                            .font(.system(size: 24))
                            .foregroundColor(.metalTextSecondaryDark)
                            .padding(16)
                    }
                }
            case .charts:
                ChartsScreenView(viewModel: viewModel)
                    .overlay(alignment: .topLeading) {
                        Button(action: { activeModal = nil }) {
                            Image(systemName: "chevron.left")
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                                .padding(16)
                        }
                    }
            case .history:
                HistoryScreenView(viewModel: viewModel)
                    .overlay(alignment: .topLeading) {
                        Button(action: { activeModal = nil }) {
                            Image(systemName: "chevron.left")
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(.metalTextPrimaryDark)
                                .padding(16)
                        }
                    }
            case .shanghai:
                ShanghaiScreenView(
                    viewModel: viewModel,
                    onNavigateBack: { activeModal = nil },
                    onNavigateToTab: { tab in handleMarketTabNavigation(tab) }
                )
            case .comex:
                ComexScreenView(
                    viewModel: viewModel,
                    onNavigateBack: { activeModal = nil },
                    onNavigateToTab: { tab in handleMarketTabNavigation(tab) }
                )
            case .india:
                IndiaScreenView(
                    viewModel: viewModel,
                    onNavigateBack: { activeModal = nil },
                    onNavigateToTab: { tab in handleMarketTabNavigation(tab) }
                )
            case .dataSources:
                DataSourcesScreenView(onNavigateBack: { activeModal = nil })
            case .about:
                AboutScreenView(onNavigateBack: { activeModal = nil })
            }
        }
        .preferredColorScheme(viewModel.themeMode == .light ? .light : .dark)
    }
}
