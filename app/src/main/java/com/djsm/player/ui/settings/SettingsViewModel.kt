package com.djsm.player.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djsm.player.domain.repository.AppSettings
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

    fun setExcludeShortAudios(exclude: Boolean) {
        viewModelScope.launch {
            settingsRepository.setMinimumDurationMs(if (exclude) 30_000L else 0L)
        }
    }
}
