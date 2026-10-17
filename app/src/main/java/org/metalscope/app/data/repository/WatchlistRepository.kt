package org.metalscope.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchlistRepository(private val context: Context) {

    private val watchlistKey = stringSetPreferencesKey("pref_watchlist_symbols")

    val watchlistFlow: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[watchlistKey] ?: setOf("XAU", "XAG") // Gold and Silver default favorites
    }

    suspend fun toggleFavorite(symbol: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[watchlistKey] ?: setOf("XAU", "XAG")
            val updated = if (current.contains(symbol)) {
                current - symbol
            } else {
                current + symbol
            }
            preferences[watchlistKey] = updated
        }
    }

    suspend fun isFavorite(symbol: String, favorites: Set<String>): Boolean {
        return favorites.contains(symbol)
    }

    suspend fun clearWatchlist() {
        context.dataStore.edit { preferences ->
            preferences[watchlistKey] = emptySet()
        }
    }
}
