package com.djsm.player.data.local.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playback_history",
    indices = [
        Index(value = ["fingerprint"]),
        Index(value = ["playedAt"])
    ]
)
data class PlaybackHistoryEntity(
    @PrimaryKey
    val eventId: String,
    val mediaStoreId: Long,
    val fingerprint: String,
    val playedAt: Long,
    val snapshotTitle: String,
    val snapshotArtist: String,
    val durationMs: Long
)
