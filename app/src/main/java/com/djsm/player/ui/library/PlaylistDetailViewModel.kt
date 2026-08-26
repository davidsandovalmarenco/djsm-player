package com.djsm.player.ui.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djsm.player.domain.model.Playlist
import com.djsm.player.domain.model.Song
import com.djsm.player.domain.usecase.favorite.ObserveFavoritesUseCase
import com.djsm.player.domain.usecase.favorite.ToggleFavoriteUseCase
import com.djsm.player.domain.usecase.playlist.ObservePlaylistDetailsUseCase
import com.djsm.player.domain.usecase.playlist.PlaylistSongItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi

data class PlaylistDetailUiState(
    val isLoading: Boolean = true,
    val playlist: Playlist? = null,
    val songs: List<PlaylistSongItem> = emptyList(),
    val favoriteSongIds: Set<Long> = emptySet(),
    val error: String? = null
)

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val observePlaylistDetailsUseCase: ObservePlaylistDetailsUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val playlistIdFlow = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PlaylistDetailUiState> = playlistIdFlow
        .filterNotNull()
        .flatMapLatest { id ->
            combine(
                observePlaylistDetailsUseCase(id),
                observeFavoritesUseCase()
            ) { details, favorites ->
                if (details == null) {
                    PlaylistDetailUiState(isLoading = false, error = "Playlist no encontrada")
                } else {
                    val resolvedFavoriteIds = mutableSetOf<Long>()
                    // Simplificación para UI de PlaylistDetail
                    for (fav in favorites) {
                        // Buscamos si la canción de favoritos está en la playlist
                        val matchingSong = details.songs.find { it.song.id == fav.mediaStoreId && it.song.fingerprint == fav.fingerprint }
                            ?: details.songs.find { it.song.fingerprint == fav.fingerprint }
                        
                        if (matchingSong != null) {
                            resolvedFavoriteIds.add(matchingSong.song.id)
                        }
                    }
                    
                    PlaylistDetailUiState(
                        isLoading = false,
                        playlist = details.playlist,
                        songs = details.songs,
                        favoriteSongIds = resolvedFavoriteIds
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlaylistDetailUiState()
        )

    fun setPlaylistId(id: String) {
        if (playlistIdFlow.value != id) {
            playlistIdFlow.value = id
        }
    }

    fun toggleFavorite(song: Song, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            toggleFavoriteUseCase(song.id, song.fingerprint, isCurrentlyFavorite)
        }
    }
}
