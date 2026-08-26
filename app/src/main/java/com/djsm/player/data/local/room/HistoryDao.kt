package com.djsm.player.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class HistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertPlaybackEvent(event: PlaybackHistoryEntity): Long

    @Query("SELECT * FROM playback_history ORDER BY playedAt DESC")
    abstract fun observeHistory(): Flow<List<PlaybackHistoryEntity>>

    @Query("SELECT fingerprint, MAX(playedAt) as lastPlayedAt, COUNT(eventId) as playCount FROM playback_history GROUP BY fingerprint ORDER BY lastPlayedAt DESC")
    abstract fun observeRecentlyPlayed(): Flow<List<HistoryAggregate>>

    @Query("SELECT fingerprint, COUNT(eventId) as playCount, MAX(playedAt) as lastPlayedAt FROM playback_history GROUP BY fingerprint ORDER BY playCount DESC, lastPlayedAt DESC")
    abstract fun observeMostPlayed(): Flow<List<HistoryAggregate>>

    @Query("SELECT snapshotTitle, snapshotArtist FROM playback_history WHERE fingerprint = :fingerprint ORDER BY playedAt DESC LIMIT 1")
    abstract fun getLatestSnapshot(fingerprint: String): SnapshotInfo?
}

data class HistoryAggregate(
    val fingerprint: String,
    val lastPlayedAt: Long,
    val playCount: Int
)

data class SnapshotInfo(
    val snapshotTitle: String,
    val snapshotArtist: String
)
