package com.djsm.player.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_queue")
data class QueueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mediaStoreId: Long,
    val fingerprint: String,
    val sortIndex: Int
)
