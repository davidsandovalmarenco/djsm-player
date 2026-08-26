package com.djsm.player.data.repository

import com.djsm.player.data.local.room.PlaylistDao
import com.djsm.player.data.local.room.PlaylistEntity
import com.djsm.player.domain.model.Playlist
import com.djsm.player.domain.model.PlaylistSongReference
import com.djsm.player.domain.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override fun observeAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.observeAllPlaylists().map { entities ->
            entities.map {
                Playlist(
                    id = it.playlistId,
                    name = it.name,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        }
    }

    override fun observePlaylist(playlistId: String): Flow<Playlist?> {
        return playlistDao.observePlaylist(playlistId).map { entity ->
            entity?.let {
                Playlist(
                    id = it.playlistId,
                    name = it.name,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
        }
    }

    override suspend fun createPlaylist(name: String) {
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            playlistDao.insertPlaylist(
                PlaylistEntity(
                    playlistId = UUID.randomUUID().toString(),
                    name = name,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    override suspend fun renamePlaylist(playlistId: String, newName: String) {
        withContext(Dispatchers.IO) {
            playlistDao.renamePlaylist(playlistId, newName, System.currentTimeMillis())
        }
    }

    override suspend fun deletePlaylist(playlistId: String) {
        withContext(Dispatchers.IO) {
            playlistDao.deletePlaylist(playlistId)
        }
    }

    override fun observePlaylistSongs(playlistId: String): Flow<List<PlaylistSongReference>> {
        return playlistDao.observePlaylistSongs(playlistId).map { entities ->
            entities.map {
                PlaylistSongReference(
                    playlistSongId = it.playlistSongId,
                    playlistId = it.playlistId,
                    mediaStoreId = it.mediaStoreId,
                    fingerprint = it.fingerprint,
                    sortIndex = it.sortIndex,
                    addedAt = it.addedAt
                )
            }
        }
    }

    override suspend fun addSongToPlaylist(
        playlistId: String,
        mediaStoreId: Long,
        fingerprint: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            playlistDao.addSongToPlaylist(playlistId, mediaStoreId, fingerprint)
        }
    }

    override suspend fun removeSongFromPlaylist(playlistSongId: String) {
        withContext(Dispatchers.IO) {
            playlistDao.deletePlaylistSong(playlistSongId)
        }
    }
}
