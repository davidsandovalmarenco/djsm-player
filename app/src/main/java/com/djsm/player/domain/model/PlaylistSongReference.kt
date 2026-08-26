package com.djsm.player.domain.model

data class PlaylistSongReference(
    val playlistSongId: String,
    val playlistId: String,
    val mediaStoreId: Long,
    val fingerprint: String,
    val sortIndex: Int,
    val addedAt: Long
)
