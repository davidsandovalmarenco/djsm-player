package com.djsm.player.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.djsm.player.domain.repository.AppSettings
import com.djsm.player.domain.repository.AppTheme
import com.djsm.player.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val EXCLUDED_FOLDERS = stringSetPreferencesKey("excluded_folders")
        val MINIMUM_DURATION_MS = longPreferencesKey("minimum_duration_ms")
    }

    override fun observeSettings(): Flow<AppSettings> {
        return dataStore.data.map { prefs ->
            val themeStr = prefs[Keys.THEME] ?: AppTheme.SYSTEM.name
            val theme = try {
                AppTheme.valueOf(themeStr)
            } catch (e: Exception) {
                AppTheme.SYSTEM
            }
            
            AppSettings(
                theme = theme,
                excludedFolders = prefs[Keys.EXCLUDED_FOLDERS] ?: emptySet(),
                minimumDurationMs = prefs[Keys.MINIMUM_DURATION_MS] ?: 30_000L
            )
        }
    }

    override suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME] = theme.name
        }
    }

    override suspend fun setExcludedFolders(folders: Set<String>) {
        dataStore.edit { prefs ->
            prefs[Keys.EXCLUDED_FOLDERS] = folders
        }
    }

    override suspend fun setMinimumDurationMs(durationMs: Long) {
        dataStore.edit { prefs ->
            prefs[Keys.MINIMUM_DURATION_MS] = durationMs
        }
    }
}
