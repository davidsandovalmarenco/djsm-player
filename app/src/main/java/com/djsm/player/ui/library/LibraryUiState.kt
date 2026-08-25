package com.djsm.player.ui.library

import com.djsm.player.domain.model.Song

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data object Empty : LibraryUiState
    data class NoSearchResults(val query: String) : LibraryUiState
    data class Success(val songs: List<Song>) : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}