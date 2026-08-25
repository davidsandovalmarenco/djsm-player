package com.djsm.player.data.local.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    indices = [
        Index(value = ["mediaStoreId"]),
        Index(value = ["fingerprint"])
    ]
)
data class FavoriteEntity(
    @PrimaryKey
    val favoriteId: String,
    val mediaStoreId: Long,
    val fingerprint: String,
    val addedAt: Long
)
