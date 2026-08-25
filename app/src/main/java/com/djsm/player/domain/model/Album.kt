package com.djsm.player.domain.model

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val songCount: Int,
    val durationMs: Long,
    val songs: List<Song>
)
