package com.djsm.player.domain.usecase.playlist

import com.djsm.player.domain.repository.PlaylistRepository
import javax.inject.Inject

class CreatePlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(name: String) {
        if (name.isNotBlank()) {
            playlistRepository.createPlaylist(name.trim())
        }
    }
}
