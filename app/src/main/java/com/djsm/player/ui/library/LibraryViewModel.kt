package com.djsm.player.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djsm.player.domain.usecase.GetSongsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            LibraryUiState(
                isLoading = true
            )
        )

    val uiState: StateFlow<LibraryUiState> =
        _uiState.asStateFlow()

    init {
        loadSongs()
    }

    fun loadSongs() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

            try {

                val songs =
                    getSongsUseCase()

                _uiState.value =
                    LibraryUiState(
                        songs = songs,
                        isLoading = false,
                        errorMessage = null
                    )

            } catch (exception: Exception) {

                _uiState.value =
                    LibraryUiState(
                        songs = emptyList(),
                        isLoading = false,
                        errorMessage =
                            exception.message
                                ?: "No se pudo cargar la biblioteca"
                    )
            }
        }
    }
}