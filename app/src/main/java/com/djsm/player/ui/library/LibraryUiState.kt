package com.djsm.player.ui.library

import com.djsm.player.domain.model.Song

data class LibraryUiState(
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)