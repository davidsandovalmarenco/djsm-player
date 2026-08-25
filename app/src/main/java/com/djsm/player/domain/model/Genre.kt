package com.djsm.player.domain.model

data class Genre(
    val id: Long,
    val name: String,
    val songCount: Int,
    val songs: List<Song>
)
