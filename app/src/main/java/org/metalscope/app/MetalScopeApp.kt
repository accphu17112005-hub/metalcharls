package org.metalscope.app

import android.app.Application
import org.metalscope.app.data.repository.MetalRepository
import org.metalscope.app.data.repository.PreferencesRepository
import org.metalscope.app.data.repository.WatchlistRepository

class MetalScopeApp : Application() {

    lateinit var metalRepository: MetalRepository
        private set

    lateinit var preferencesRepository: PreferencesRepository
        private set

    lateinit var watchlistRepository: WatchlistRepository
        private set

    override fun onCreate() {
        super.onCreate()
        preferencesRepository = PreferencesRepository(this)
        watchlistRepository = WatchlistRepository(this)
        metalRepository = MetalRepository()
    }
}
