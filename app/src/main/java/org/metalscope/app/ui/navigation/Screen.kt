package org.metalscope.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // 5 Core Bottom Tabs
    data object Home : Screen("tab_home")
    data object Markets : Screen("tab_markets")
    data object Charts : Screen("tab_charts")
    data object History : Screen("tab_history")
    data object Settings : Screen("tab_settings")

    // Detail & Secondary Screens
    data object MetalDetail : Screen("metal_detail/{symbol}") {
        fun createRoute(symbol: String) = "metal_detail/$symbol"
    }
    data object Watchlist : Screen("watchlist")
    data object Search : Screen("search")
    data object DataSources : Screen("data_sources")
    data object About : Screen("about")
}

data class BottomNavItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector
)

val BottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", Icons.Filled.Home),
    BottomNavItem(Screen.Markets, "Markets", Icons.Filled.BarChart),
    BottomNavItem(Screen.Charts, "Charts", Icons.Filled.ShowChart),
    BottomNavItem(Screen.History, "History", Icons.Filled.History),
    BottomNavItem(Screen.Settings, "Settings", Icons.Filled.Settings)
)
