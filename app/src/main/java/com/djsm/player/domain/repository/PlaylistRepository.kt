package com.djsm.player.domain.repository

import com.djsm.player.domain.model.Playlist
import com.djsm.player.domain.model.PlaylistSongReference
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun observeAllPlaylists(): Flow<List<Playlist>>
    fun observePlaylist(playlistId: String): Flow<Playlist?>
    suspend fun createPlaylist(name: String)
    suspend fun renamePlaylist(playlistId: String, newName: String)
    suspend fun deletePlaylist(playlistId: String)
    
    fun observePlaylistSongs(playlistId: String): Flow<List<PlaylistSongReference>>
    suspend fun addSongToPlaylist(playlistId: String, mediaStoreId: Long, fingerprint: String): Boolean
    suspend fun removeSongFromPlaylist(playlistSongId: String)
}
