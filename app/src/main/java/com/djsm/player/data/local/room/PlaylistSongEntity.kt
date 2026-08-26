package com.djsm.player.data.local.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_songs",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["playlistId"]), // For FK performance and querying
        Index(value = ["playlistId", "fingerprint"], unique = true) // Prevent exact same song multiple times in a playlist
    ]
)
data class PlaylistSongEntity(
    @PrimaryKey
    val playlistSongId: String,
    val playlistId: String,
    val mediaStoreId: Long,
    val fingerprint: String,
    val sortIndex: Int,
    val addedAt: Long
)
