package com.djsm.player.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    abstract fun updatePlaylist(playlist: PlaylistEntity)

    @Query("UPDATE playlists SET name = :newName, updatedAt = :updatedAt WHERE playlistId = :playlistId")
    abstract fun renamePlaylist(playlistId: String, newName: String, updatedAt: Long)

    @Query("DELETE FROM playlists WHERE playlistId = :playlistId")
    abstract fun deletePlaylist(playlistId: String)

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    abstract fun observeAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE playlistId = :playlistId")
    abstract fun observePlaylist(playlistId: String): Flow<PlaylistEntity?>

    @Query("SELECT IFNULL(MAX(sortIndex), -1) FROM playlist_songs WHERE playlistId = :playlistId")
    abstract fun getMaxSortIndex(playlistId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertPlaylistSong(song: PlaylistSongEntity): Long

    @Transaction
    open fun addSongToPlaylist(playlistId: String, mediaStoreId: Long, fingerprint: String): Boolean {
        val maxIndex = getMaxSortIndex(playlistId)
        val songEntity = PlaylistSongEntity(
            playlistSongId = UUID.randomUUID().toString(),
            playlistId = playlistId,
            mediaStoreId = mediaStoreId,
            fingerprint = fingerprint,
            sortIndex = maxIndex + 1,
            addedAt = System.currentTimeMillis()
        )
        // returns -1 if ignored due to constraint
        val result = insertPlaylistSong(songEntity)
        return result != -1L
    }

    @Query("DELETE FROM playlist_songs WHERE playlistSongId = :playlistSongId")
    abstract fun deletePlaylistSong(playlistSongId: String)

    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY sortIndex ASC")
    abstract fun observePlaylistSongs(playlistId: String): Flow<List<PlaylistSongEntity>>
}
