import SwiftUI

enum TabSelection: Hashable {
    case home
    case markets
    case charts
    case history
    case settings
}

enum ActiveModal: Identifiable {
    case detail(symbol: String)
    case search
    case watchlist
    case dataSources
    case about
    
    var id: String {
        switch self {
        case .detail(let s): return "detail_\(s)"
        case .search: return "search"
        case .watchlist: return "watchlist"
        case .dataSources: return "dataSources"
        case .about: return "about"
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
    
    var body: some View {
        TabView(selection: $selectedTab) {
            // Tab 1: Home
            HomeScreenView(
                viewModel: viewModel,
                onNavigateToMetal: { sym in activeModal = .detail(symbol: sym) },
                onNavigateToSearch: { activeModal = .search },
                onNavigateToWatchlist: { activeModal = .watchlist }
            )
            .tabItem {
                Label("Home", systemImage: "house.fill")
            }
            .tag(TabSelection.home)
            
            // Tab 2: Markets
            MarketsScreenView(
                viewModel: viewModel,
                onNavigateToMetal: { sym in activeModal = .detail(symbol: sym) }
            )
            .tabItem {
                Label("Markets", systemImage: "chart.bar.fill")
            }
            .tag(TabSelection.markets)
            
            // Tab 3: Charts
            ChartsScreenView(viewModel: viewModel)
                .tabItem {
                    Label("Charts", systemImage: "waveform.path.ecg")
                }
                .tag(TabSelection.charts)
            
            // Tab 4: History
            HistoryScreenView(viewModel: viewModel)
                .tabItem {
                    Label("History", systemImage: "clock.arrow.circlepath")
                }
                .tag(TabSelection.history)
            
            // Tab 5: Settings
            SettingsScreenView(
                viewModel: viewModel,
                onNavigateToDataSources: { activeModal = .dataSources },
                onNavigateToAbout: { activeModal = .about }
            )
            .tabItem {
                Label("Settings", systemImage: "gearshape.fill")
            }
            .tag(TabSelection.settings)
        }
        .tint(.metalGold)
        .fullScreenCover(item: $activeModal) { modal in
            switch modal {
            case .detail(let sym):
                MetalDetailScreenView(
                    symbol: sym,
                    viewModel: viewModel,
                    onNavigateBack: { activeModal = nil }
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
            case .dataSources:
                DataSourcesScreenView(onNavigateBack: { activeModal = nil })
            case .about:
                AboutScreenView(onNavigateBack: { activeModal = nil })
            }
        }
        .preferredColorScheme(viewModel.themeMode == .light ? .light : .dark)
    }
}
