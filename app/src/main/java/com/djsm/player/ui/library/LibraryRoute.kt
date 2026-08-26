package com.djsm.player.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.djsm.player.domain.model.Song

@Composable
fun LibraryRoute(
    viewModel: LibraryViewModel,
    onSongClick: (
        selectedSong: Song,
        songs: List<Song>
    ) -> Unit,
    onAlbumClick: (Long) -> Unit,
    onArtistClick: (Long) -> Unit,
    onFolderClick: (String) -> Unit,
    onGenreClick: (Long) -> Unit,
    onPlaylistClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()

    when (val state = uiState) {

        is LibraryUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is LibraryUiState.Empty -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No se encontraron canciones en el dispositivo")
            }
        }
        
        is LibraryUiState.NoSearchResults -> {
            LibraryScreen(
                songs = emptyList(),
                albums = emptyList(),
                artists = emptyList(),
                folders = emptyList(),
                genres = emptyList(),
                playlists = emptyList(),
                favoriteSongIds = emptySet(),
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                sortOption = sortOption,
                onSortOptionChange = viewModel::updateSortOption,
                sortOrder = sortOrder,
                onSortOrderToggle = viewModel::toggleSortOrder,
                onSongClick = {},
                onToggleFavorite = { _, _ -> },
                onAlbumClick = {},
                onArtistClick = {},
                onFolderClick = {},
                onGenreClick = {},
                onPlaylistClick = {},
                onCreatePlaylist = viewModel::createPlaylist,
                onAddSongToPlaylist = viewModel::addSongToPlaylist,
                modifier = modifier,
                isNoSearchResults = true
            )
        }

        is LibraryUiState.Error -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.message
                )
                Button(
                    onClick = viewModel::loadSongs
                ) {
                    Text("Reintentar")
                }
            }
        }

        is LibraryUiState.Success -> {
            LibraryScreen(
                songs = state.songs,
                albums = state.albums,
                artists = state.artists,
                folders = state.folders,
                genres = state.genres,
                playlists = state.playlists,
                favoriteSongIds = state.favoriteSongIds,
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                sortOption = sortOption,
                onSortOptionChange = viewModel::updateSortOption,
                sortOrder = sortOrder,
                onSortOrderToggle = viewModel::toggleSortOrder,
                onSongClick = { selectedSong ->
                    onSongClick(
                        selectedSong,
                        state.songs
                    )
                },
                onToggleFavorite = viewModel::toggleFavorite,
                onAlbumClick = { album ->
                    onAlbumClick(album.id)
                },
                onArtistClick = { artist ->
                    onArtistClick(artist.id)
                },
                onFolderClick = { folder ->
                    onFolderClick(folder.id)
                },
                onGenreClick = { genre ->
                    onGenreClick(genre.id)
                },
                onPlaylistClick = { playlist ->
                    onPlaylistClick(playlist.id)
                },
                onCreatePlaylist = viewModel::createPlaylist,
                onAddSongToPlaylist = viewModel::addSongToPlaylist,
                modifier = modifier,
                isNoSearchResults = false
            )
        }
    }
}