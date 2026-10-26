package com.djsm.player.domain.repository

import kotlinx.coroutines.flow.Flow

enum class AppTheme {
    LIGHT, DARK, SYSTEM, AMOLED
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val excludedFolders: Set<String> = emptySet(),
    val minimumDurationMs: Long = 30_000L,
    val crossfadeMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val defaultTab: Int = 0,
    val rememberQueue: Boolean = true,
    val sleepTimerMinutes: Int = 0
)

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun setTheme(theme: AppTheme)
    suspend fun setExcludedFolders(folders: Set<String>)
    suspend fun setMinimumDurationMs(durationMs: Long)
    suspend fun setCrossfadeMs(durationMs: Long)
    suspend fun setPlaybackSpeed(speed: Float)
    suspend fun setDefaultTab(tabIndex: Int)
    suspend fun setRememberQueue(remember: Boolean)
    suspend fun setSleepTimerMinutes(minutes: Int)
}
