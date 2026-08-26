package com.djsm.player.domain.usecase.playlist

import com.djsm.player.domain.repository.PlaylistRepository
import javax.inject.Inject

class RemoveSongFromPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistSongId: String) {
        playlistRepository.removeSongFromPlaylist(playlistSongId)
    }
}
