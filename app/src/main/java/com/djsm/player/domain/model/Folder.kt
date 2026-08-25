package com.djsm.player.domain.model

data class Folder(
    val id: String,
    val name: String,
    val songCount: Int,
    val songs: List<Song>
)
