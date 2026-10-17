package org.metalscope.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.metalscope.app.data.repository.MetalRepository
import org.metalscope.app.data.repository.PreferencesRepository
import org.metalscope.app.data.repository.WatchlistRepository

class ViewModelFactory(
    private val metalRepository: MetalRepository,
    private val preferencesRepository: PreferencesRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(metalRepository, preferencesRepository, watchlistRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
