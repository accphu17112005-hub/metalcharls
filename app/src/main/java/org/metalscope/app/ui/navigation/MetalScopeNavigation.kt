package org.metalscope.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import org.metalscope.app.data.model.Currency
import org.metalscope.app.data.model.WeightUnit
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.ui.components.MarketTab
import org.metalscope.app.ui.screens.charts.ChartsScreen
import org.metalscope.app.ui.screens.comex.ComexSilverScreen
import org.metalscope.app.ui.screens.detail.MetalDetailScreen
import org.metalscope.app.ui.screens.history.HistoryScreen
import org.metalscope.app.ui.screens.home.HomeScreen
import org.metalscope.app.ui.screens.india.IndiaScreen
import org.metalscope.app.ui.screens.markets.MarketsScreen
import org.metalscope.app.ui.screens.search.SearchScreen
import org.metalscope.app.ui.screens.settings.AboutScreen
import org.metalscope.app.ui.screens.settings.DataSourcesScreen
import org.metalscope.app.ui.screens.settings.SettingsScreen
import org.metalscope.app.ui.screens.shanghai.ShanghaiScreen
import org.metalscope.app.ui.screens.watchlist.WatchlistScreen
import org.metalscope.app.ui.viewmodel.MainUiState
import org.metalscope.app.ui.viewmodel.MainViewModel

@Composable
fun MetalScopeNavigation(
    navController: NavHostController,
    uiState: MainUiState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showUnitDialog by remember { mutableStateOf(false) }

    val isTopLevelDestination = BottomNavItems.any { it.screen.route == currentRoute }

    val handleTabNavigation: (MarketTab) -> Unit = { tab ->
        when (tab) {
            MarketTab.HOME -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            MarketTab.GOLD -> {
                navController.navigate(Screen.MetalDetail.createRoute("XAU"))
            }
            MarketTab.SILVER -> {
                navController.navigate(Screen.MetalDetail.createRoute("XAG"))
            }
            MarketTab.SHANGHAI -> {
                navController.navigate(Screen.Shanghai.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            MarketTab.COMEX -> {
                navController.navigate(Screen.ComexSilver.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            MarketTab.INDIA -> {
                navController.navigate(Screen.India.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    Scaffold(
        modifier = modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = MetalTheme.colors.background,
        bottomBar = {
            if (isTopLevelDestination) {
                NavigationBar(
                    containerColor = MetalTheme.colors.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = MetalTheme.colors.border,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                ) {
                    BottomNavItems.forEach { item ->
                        val selected = currentRoute == item.screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.screen.route) {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MetalGold,
                                selectedTextColor = MetalGold,
                                unselectedIconColor = MetalTheme.colors.textSecondary,
                                unselectedTextColor = MetalTheme.colors.textSecondary,
                                indicatorColor = MetalTheme.colors.elevated
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Tab 1: Home
            composable(Screen.Home.route) {
                HomeScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateToMetal = { symbol ->
                        navController.navigate(Screen.MetalDetail.createRoute(symbol))
                    },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToWatchlist = { navController.navigate(Screen.Watchlist.route) },
                    onOpenCurrencyPicker = { showCurrencyDialog = true },
                    onOpenUnitPicker = { showUnitDialog = true },
                    onNavigateToTab = handleTabNavigation
                )
            }

            // Tab 2: Shanghai Premium (SGE)
            composable(Screen.Shanghai.route) {
                ShanghaiScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTab = handleTabNavigation
                )
            }
            composable(Screen.ShanghaiDirect.route) {
                ShanghaiScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTab = handleTabNavigation
                )
            }

            // Tab 3: COMEX Silver & Inventory
            composable(Screen.ComexSilver.route) {
                ComexSilverScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTab = handleTabNavigation
                )
            }
            composable(Screen.ComexSilverDirect.route) {
                ComexSilverScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTab = handleTabNavigation
                )
            }

            // Tab 4: India Premium (MCX)
            composable(Screen.India.route) {
                IndiaScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTab = handleTabNavigation
                )
            }
            composable(Screen.IndiaDirect.route) {
                IndiaScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTab = handleTabNavigation
                )
            }

            // Tab 5: Markets
            composable(Screen.Markets.route) {
                MarketsScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateToMetal = { symbol ->
                        navController.navigate(Screen.MetalDetail.createRoute(symbol))
                    },
                    onNavigateToTab = handleTabNavigation
                )
            }

            // Direct route for https://metalcharts.org/metals/xau
            composable(Screen.MetalsXau.route) {
                MetalDetailScreen(
                    symbol = "XAU",
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenCurrencyPicker = { showCurrencyDialog = true },
                    onOpenUnitPicker = { showUnitDialog = true },
                    onNavigateToTab = handleTabNavigation
                )
            }

            // Direct route for https://metalcharts.org/metals/xag
            composable(Screen.MetalsXag.route) {
                MetalDetailScreen(
                    symbol = "XAG",
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenCurrencyPicker = { showCurrencyDialog = true },
                    onOpenUnitPicker = { showUnitDialog = true },
                    onNavigateToTab = handleTabNavigation
                )
            }

            // Charts Screen
            composable(Screen.Charts.route) {
                ChartsScreen(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }

            // History Screen
            composable(Screen.History.route) {
                HistoryScreen(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }

            // Settings Screen
            composable(Screen.Settings.route) {
                SettingsScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateToDataSources = { navController.navigate(Screen.DataSources.route) },
                    onNavigateToAbout = { navController.navigate(Screen.About.route) },
                    onOpenCurrencyDialog = { showCurrencyDialog = true },
                    onOpenUnitDialog = { showUnitDialog = true }
                )
            }

            // Detail Screen (for XAU, XAG, XPT, XPD, etc.)
            composable(
                route = Screen.MetalDetail.route,
                arguments = listOf(navArgument("symbol") { type = NavType.StringType })
            ) { backStackEntry ->
                val symbol = backStackEntry.arguments?.getString("symbol") ?: "XAU"
                MetalDetailScreen(
                    symbol = symbol,
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenCurrencyPicker = { showCurrencyDialog = true },
                    onOpenUnitPicker = { showUnitDialog = true },
                    onNavigateToTab = handleTabNavigation
                )
            }

            // Watchlist Screen
            composable(Screen.Watchlist.route) {
                WatchlistScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMetal = { symbol ->
                        navController.navigate(Screen.MetalDetail.createRoute(symbol))
                    }
                )
            }

            // Search Screen
            composable(Screen.Search.route) {
                SearchScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMetal = { symbol ->
                        navController.navigate(Screen.MetalDetail.createRoute(symbol))
                    }
                )
            }

            // Data Sources Screen
            composable(Screen.DataSources.route) {
                DataSourcesScreen(onNavigateBack = { navController.popBackStack() })
            }

            // About Screen
            composable(Screen.About.route) {
                AboutScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }

    // Currency Selector Modal Dialog
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = {
                Text(
                    text = "Select Primary Currency",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Currency.entries.forEach { cur ->
                        val isSelected = cur == uiState.currency
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MetalGold.copy(alpha = 0.15f) else MetalTheme.colors.elevated)
                                .border(
                                    1.dp,
                                    if (isSelected) MetalGold else MetalTheme.colors.border,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    viewModel.setCurrency(cur)
                                    showCurrencyDialog = false
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${cur.code} (${cur.symbol})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MetalGold else MetalTheme.colors.textPrimary
                                )
                                Text(
                                    text = cur.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MetalTheme.colors.textSecondary
                                )
                            }
                            if (isSelected) {
                                Text(
                                    text = "SELECTED",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MetalGold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text(text = "Close", color = MetalGold)
                }
            },
            containerColor = MetalTheme.colors.surface
        )
    }

    // Weight Unit Selector Modal Dialog
    if (showUnitDialog) {
        AlertDialog(
            onDismissRequest = { showUnitDialog = false },
            title = {
                Text(
                    text = "Select Price Weight Unit",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WeightUnit.entries.forEach { u ->
                        val isSelected = u == uiState.unit
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MetalGold.copy(alpha = 0.15f) else MetalTheme.colors.elevated)
                                .border(
                                    1.dp,
                                    if (isSelected) MetalGold else MetalTheme.colors.border,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    viewModel.setWeightUnit(u)
                                    showUnitDialog = false
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${u.displayName} (${u.symbol})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MetalGold else MetalTheme.colors.textPrimary
                                )
                                Text(
                                    text = when (u) {
                                        WeightUnit.OZ -> "1 Troy Ounce = 31.1035 g (Benchmark standard)"
                                        WeightUnit.GRAM -> "1 Gram = 0.03215 oz"
                                        WeightUnit.KG -> "1 Kilogram = 32.1507 oz"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MetalTheme.colors.textSecondary
                                )
                            }
                            if (isSelected) {
                                Text(
                                    text = "SELECTED",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MetalGold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showUnitDialog = false }) {
                    Text(text = "Close", color = MetalGold)
                }
            },
            containerColor = MetalTheme.colors.surface
        )
    }
}
