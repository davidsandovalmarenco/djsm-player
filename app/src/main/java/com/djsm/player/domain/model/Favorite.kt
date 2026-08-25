package com.djsm.player.domain.model

data class Favorite(
    val favoriteId: String,
    val mediaStoreId: Long,
    val fingerprint: String,
    val addedAt: Long
)
