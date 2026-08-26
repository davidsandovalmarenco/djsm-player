package com.djsm.player.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_state")
data class PlaybackStateEntity(
    @PrimaryKey
    val id: Int = 1,
    val currentMediaId: String?,
    val currentPositionMs: Long,
    val repeatMode: Int,
    val shuffleModeEnabled: Boolean
)
