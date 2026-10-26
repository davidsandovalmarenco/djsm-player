package com.djsm.player.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djsm.player.domain.repository.AppSettings
import com.djsm.player.domain.repository.AppTheme
import com.djsm.player.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.observeSettings().collect { newSettings ->
                _settings.value = newSettings
            }
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun setExcludeShortAudios(exclude: Boolean) {
        viewModelScope.launch {
            settingsRepository.setMinimumDurationMs(if (exclude) 30_000L else 0L)
        }
    }

    fun setCrossfadeMs(durationMs: Long) {
        viewModelScope.launch {
            settingsRepository.setCrossfadeMs(durationMs)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        viewModelScope.launch {
            settingsRepository.setPlaybackSpeed(speed)
        }
    }

    fun setDefaultTab(tabIndex: Int) {
        viewModelScope.launch {
            settingsRepository.setDefaultTab(tabIndex)
        }
    }

    fun setRememberQueue(remember: Boolean) {
        viewModelScope.launch {
            settingsRepository.setRememberQueue(remember)
        }
    }

    fun setSleepTimerMinutes(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.setSleepTimerMinutes(minutes)
        }
    }

    fun toggleExcludedFolder(folderId: String) {
        viewModelScope.launch {
            val current = _settings.value.excludedFolders
            val updated = if (current.contains(folderId)) {
                current - folderId
            } else {
                current + folderId
            }
            settingsRepository.setExcludedFolders(updated)
        }
    }
}
