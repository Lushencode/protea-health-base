package com.proteahealth.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "pharmacist_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val fontScale: Flow<Float> = context.dataStore.data.map { prefs -> prefs[FONT_SCALE] ?: 1.0f }
    val darkMode: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[DARK_MODE] ?: false }

    suspend fun saveFontScale(value: Float) {
        context.dataStore.edit { it[FONT_SCALE] = value }
    }

    suspend fun saveDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled }
    }
}