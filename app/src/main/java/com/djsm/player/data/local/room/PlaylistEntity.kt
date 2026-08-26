package com.djsm.player.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey
    val playlistId: String,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
)
