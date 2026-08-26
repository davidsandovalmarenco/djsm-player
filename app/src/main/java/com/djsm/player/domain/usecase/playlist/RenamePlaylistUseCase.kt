package com.djsm.player.domain.usecase.playlist

import com.djsm.player.domain.repository.PlaylistRepository
import javax.inject.Inject

class RenamePlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: String, newName: String) {
        if (newName.isNotBlank()) {
            playlistRepository.renamePlaylist(playlistId, newName.trim())
        }
    }
}
