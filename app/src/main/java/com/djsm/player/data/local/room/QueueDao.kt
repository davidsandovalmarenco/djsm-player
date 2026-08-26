package com.djsm.player.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface QueueDao {

    @Query("SELECT * FROM playback_queue ORDER BY sortIndex ASC")
    fun getQueue(): List<QueueEntity>

    @Query("DELETE FROM playback_queue")
    fun clearQueue()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQueue(queue: List<QueueEntity>)

    @Transaction
    fun replaceQueue(queue: List<QueueEntity>) {
        clearQueue()
        insertQueue(queue)
    }

    @Query("SELECT * FROM playback_state WHERE id = 1")
    fun getPlaybackState(): PlaybackStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePlaybackState(state: PlaybackStateEntity)
}
