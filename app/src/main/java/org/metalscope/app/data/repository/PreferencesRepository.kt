package org.metalscope.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.metalscope.app.data.model.Currency
import org.metalscope.app.data.model.WeightUnit
import org.metalscope.app.theme.ThemeMode

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "metalscope_prefs")

class PreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val CURRENCY = stringPreferencesKey("pref_currency")
        val WEIGHT_UNIT = stringPreferencesKey("pref_weight_unit")
        val THEME_MODE = stringPreferencesKey("pref_theme_mode")
        val HAPTICS_ENABLED = booleanPreferencesKey("pref_haptics")
        val REFRESH_INTERVAL = intPreferencesKey("pref_refresh_interval")
        val DEMO_MODE = booleanPreferencesKey("pref_demo_mode")
        val API_KEY = stringPreferencesKey("pref_api_key")
        val DECIMAL_OVERRIDE = intPreferencesKey("pref_decimal_override")
    }

    val currencyFlow: Flow<Currency> = context.dataStore.data.map { preferences ->
        val code = preferences[PreferencesKeys.CURRENCY] ?: Currency.USD.code
        Currency.fromCode(code)
    }

    val weightUnitFlow: Flow<WeightUnit> = context.dataStore.data.map { preferences ->
        val code = preferences[PreferencesKeys.WEIGHT_UNIT] ?: WeightUnit.OZ.code
        WeightUnit.fromCode(code)
    }

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.DARK.name
        try {
            ThemeMode.valueOf(name)
        } catch (e: Exception) {
            ThemeMode.DARK
        }
    }

    val hapticsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAPTICS_ENABLED] ?: true
    }

    val refreshIntervalFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.REFRESH_INTERVAL] ?: 15 // Default 15s
    }

    val demoModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEMO_MODE] ?: true
    }

    val apiKeyFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.API_KEY] ?: ""
    }

    val decimalOverrideFlow: Flow<Int?> = context.dataStore.data.map { preferences ->
        val stored = preferences[PreferencesKeys.DECIMAL_OVERRIDE] ?: -1
        if (stored == -1) null else stored
    }

    suspend fun setCurrency(currency: Currency) {
        context.dataStore.edit { it[PreferencesKeys.CURRENCY] = currency.code }
    }

    suspend fun setWeightUnit(unit: WeightUnit) {
        context.dataStore.edit { it[PreferencesKeys.WEIGHT_UNIT] = unit.code }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = mode.name }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun setRefreshInterval(seconds: Int) {
        context.dataStore.edit { it[PreferencesKeys.REFRESH_INTERVAL] = seconds }
    }

    suspend fun setDemoMode(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DEMO_MODE] = enabled }
    }

    suspend fun setApiKey(apiKey: String) {
        context.dataStore.edit { it[PreferencesKeys.API_KEY] = apiKey }
    }

    suspend fun setDecimalOverride(decimals: Int?) {
        context.dataStore.edit { it[PreferencesKeys.DECIMAL_OVERRIDE] = decimals ?: -1 }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
