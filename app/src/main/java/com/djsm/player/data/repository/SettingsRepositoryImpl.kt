package com.djsm.player.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
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
        val CROSSFADE_MS = longPreferencesKey("crossfade_ms")
        val PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
        val DEFAULT_TAB = intPreferencesKey("default_tab")
        val REMEMBER_QUEUE = booleanPreferencesKey("remember_queue")
        val SLEEP_TIMER_MINUTES = intPreferencesKey("sleep_timer_minutes")
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
                minimumDurationMs = prefs[Keys.MINIMUM_DURATION_MS] ?: 30_000L,
                crossfadeMs = prefs[Keys.CROSSFADE_MS] ?: 0L,
                playbackSpeed = prefs[Keys.PLAYBACK_SPEED] ?: 1.0f,
                defaultTab = prefs[Keys.DEFAULT_TAB] ?: 0,
                rememberQueue = prefs[Keys.REMEMBER_QUEUE] ?: true,
                sleepTimerMinutes = prefs[Keys.SLEEP_TIMER_MINUTES] ?: 0
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

    override suspend fun setCrossfadeMs(durationMs: Long) {
        dataStore.edit { prefs ->
            prefs[Keys.CROSSFADE_MS] = durationMs
        }
    }

    override suspend fun setPlaybackSpeed(speed: Float) {
        dataStore.edit { prefs ->
            prefs[Keys.PLAYBACK_SPEED] = speed
        }
    }

    override suspend fun setDefaultTab(tabIndex: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_TAB] = tabIndex
        }
    }

    override suspend fun setRememberQueue(remember: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.REMEMBER_QUEUE] = remember
        }
    }

    override suspend fun setSleepTimerMinutes(minutes: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.SLEEP_TIMER_MINUTES] = minutes
        }
    }
}
