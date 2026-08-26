package com.djsm.player.domain.usecase.playlist

import com.djsm.player.domain.repository.PlaylistRepository
import javax.inject.Inject

class DeletePlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: String) {
        playlistRepository.deletePlaylist(playlistId)
    }
}
