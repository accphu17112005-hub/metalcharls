package org.metalscope.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import org.metalscope.app.theme.MetalScopeTheme
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.ui.navigation.MetalScopeNavigation
import org.metalscope.app.ui.viewmodel.MainViewModel
import org.metalscope.app.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val app = application as MetalScopeApp
        ViewModelFactory(
            metalRepository = app.metalRepository,
            preferencesRepository = app.preferencesRepository,
            watchlistRepository = app.watchlistRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val navController = rememberNavController()

            MetalScopeTheme(themeMode = uiState.themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MetalTheme.colors.background
                ) {
                    MetalScopeNavigation(
                        navController = navController,
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
