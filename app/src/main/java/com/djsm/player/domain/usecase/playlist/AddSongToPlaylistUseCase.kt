package com.djsm.player.domain.usecase.playlist

import com.djsm.player.domain.model.Song
import com.djsm.player.domain.repository.PlaylistRepository
import javax.inject.Inject

class AddSongToPlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(playlistId: String, song: Song): Boolean {
        // En un futuro podríamos tener soporte nativo de mensajes/Toasts si devuelve falso
        return playlistRepository.addSongToPlaylist(playlistId, song.id, song.fingerprint)
    }
}
