package com.djsm.player.ui.library

import com.djsm.player.domain.model.Album
import com.djsm.player.domain.model.Artist
import com.djsm.player.domain.model.Folder
import com.djsm.player.domain.model.Genre
import com.djsm.player.domain.model.Song

sealed interface LibraryUiState {
    data object Loading : LibraryUiState
    data object Empty : LibraryUiState
    data class NoSearchResults(val query: String) : LibraryUiState
    data class Success(
        val songs: List<Song>,
        val albums: List<Album>,
        val artists: List<Artist>,
        val folders: List<Folder>,
        val genres: List<Genre>,
        val playlists: List<com.djsm.player.domain.model.Playlist>,
        val favoriteSongIds: Set<Long>
    ) : LibraryUiState
    data class Error(val message: String) : LibraryUiState
}