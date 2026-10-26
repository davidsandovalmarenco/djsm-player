package com.djsm.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme
import com.djsm.player.domain.repository.AppTheme
import com.djsm.player.domain.repository.SettingsRepository
import com.djsm.player.ui.DJSMPlayerApp
import com.djsm.player.core.designsystem.theme.DJSMPlayerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val settings by settingsRepository.observeSettings()
                .collectAsState(initial = com.djsm.player.domain.repository.AppSettings())

            val systemDark = isSystemInDarkTheme()

            val isDark = when (settings.theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.AMOLED -> true
                AppTheme.SYSTEM -> systemDark
            }

            val isAmoled = settings.theme == AppTheme.AMOLED ||
                    (settings.theme == AppTheme.SYSTEM && systemDark)

            DJSMPlayerTheme(
                darkTheme = isDark,
                dynamicColor = false,
                amoled = isAmoled
            ) {
                DJSMPlayerApp()
            }
        }
    }
}