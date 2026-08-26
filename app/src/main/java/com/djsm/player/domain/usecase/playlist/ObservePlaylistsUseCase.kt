package com.djsm.player.domain.usecase.playlist

import com.djsm.player.domain.model.Playlist
import com.djsm.player.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlaylistsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    operator fun invoke(): Flow<List<Playlist>> {
        return playlistRepository.observeAllPlaylists()
    }
}
