package com.djsm.player.domain.usecase.playlist

import com.djsm.player.domain.model.Playlist
import com.djsm.player.domain.model.Song
import com.djsm.player.domain.repository.MusicRepository
import com.djsm.player.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class PlaylistDetails(
    val playlist: Playlist,
    val songs: List<PlaylistSongItem>
)

data class PlaylistSongItem(
    val playlistSongId: String,
    val song: Song,
    val sortIndex: Int
)

class ObservePlaylistDetailsUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val musicRepository: MusicRepository
) {
    operator fun invoke(playlistId: String): Flow<PlaylistDetails?> {
        return combine(
            playlistRepository.observePlaylist(playlistId),
            playlistRepository.observePlaylistSongs(playlistId),
            musicRepository.observeSongs()
        ) { playlist, songRefs, allSongs ->
            if (playlist == null) return@combine null

            val songMapById = allSongs.associateBy { it.id }
            val songMapByFingerprint = allSongs.associateBy { it.fingerprint }

            val resolvedSongs = songRefs.mapNotNull { ref ->
                var resolvedSong = songMapById[ref.mediaStoreId]
                if (resolvedSong == null || resolvedSong.fingerprint != ref.fingerprint) {
                    resolvedSong = songMapByFingerprint[ref.fingerprint]
                }
                
                if (resolvedSong != null) {
                    PlaylistSongItem(
                        playlistSongId = ref.playlistSongId,
                        song = resolvedSong,
                        sortIndex = ref.sortIndex
                    )
                } else {
                    null // Orphaned
                }
            }
            
            PlaylistDetails(
                playlist = playlist,
                songs = resolvedSongs.sortedBy { it.sortIndex }
            )
        }
    }
}
