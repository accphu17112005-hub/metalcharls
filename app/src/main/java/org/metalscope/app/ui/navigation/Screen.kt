package org.metalscope.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // 5 Core Bottom Tabs
    data object Home : Screen("tab_home")
    data object Shanghai : Screen("tab_shanghai")
    data object ComexSilver : Screen("tab_comex")
    data object India : Screen("tab_india")
    data object Markets : Screen("tab_markets")

    // Charts & History & Settings Tabs
    data object Charts : Screen("tab_charts")
    data object History : Screen("tab_history")
    data object Settings : Screen("tab_settings")

    // Dedicated Direct Web Route Aliases
    data object MetalsXau : Screen("metals/xau")
    data object MetalsXag : Screen("metals/xag")
    data object ShanghaiDirect : Screen("shanghai")
    data object ComexSilverDirect : Screen("comex/silver")
    data object IndiaDirect : Screen("india")

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
    BottomNavItem(Screen.Shanghai, "Shanghai", Icons.Filled.Language),
    BottomNavItem(Screen.ComexSilver, "COMEX", Icons.Filled.Inventory2),
    BottomNavItem(Screen.India, "India", Icons.Filled.MonetizationOn),
    BottomNavItem(Screen.Markets, "Markets", Icons.Filled.BarChart)
)
