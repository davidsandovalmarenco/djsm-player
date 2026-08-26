package com.djsm.player.domain.repository

import kotlinx.coroutines.flow.Flow

enum class AppTheme {
    LIGHT, DARK, SYSTEM, AMOLED
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val excludedFolders: Set<String> = emptySet(),
    val minimumDurationMs: Long = 30_000L
)

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun setTheme(theme: AppTheme)
    suspend fun setExcludedFolders(folders: Set<String>)
    suspend fun setMinimumDurationMs(durationMs: Long)
}
