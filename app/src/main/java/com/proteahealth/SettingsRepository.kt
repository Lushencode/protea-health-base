package com.proteahealth

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val SOUND_ALERTS = booleanPreferencesKey("sound_alerts")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val DATA_SAVER = booleanPreferencesKey("data_saver")
    }

    val fontScale: Flow<Float> = context.dataStore.data.map { prefs -> prefs[FONT_SCALE] ?: 1.0f }
    val soundAlerts: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[SOUND_ALERTS] ?: true }
    val darkMode: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[DARK_MODE] ?: false }
    val dataSaver: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[DATA_SAVER] ?: true }

    suspend fun saveFontScale(value: Float) {
        context.dataStore.edit { it[FONT_SCALE] = value }
    }

    suspend fun saveSoundAlerts(enabled: Boolean) {
        context.dataStore.edit { it[SOUND_ALERTS] = enabled }
    }

    suspend fun saveDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled }
    }

    suspend fun saveDataSaver(enabled: Boolean) {
        context.dataStore.edit { it[DATA_SAVER] = enabled }
    }
}