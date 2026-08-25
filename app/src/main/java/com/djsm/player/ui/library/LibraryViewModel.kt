package com.djsm.player.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djsm.player.domain.usecase.GetSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    private var collectionJob: Job? = null

    init {
        loadSongs()
    }

    fun loadSongs() {
        collectionJob?.cancel()

        collectionJob = viewModelScope.launch {
            _uiState.value = LibraryUiState.Loading

            getSongsUseCase.observeSongs()
                .catch { exception ->
                    _uiState.value = LibraryUiState.Error(
                        message = exception.message ?: "No se pudo cargar la biblioteca"
                    )
                }
                .collect { songs ->
                    if (songs.isEmpty()) {
                        _uiState.value = LibraryUiState.Empty
                    } else {
                        _uiState.value = LibraryUiState.Success(songs)
                    }
                }
        }
    }
}