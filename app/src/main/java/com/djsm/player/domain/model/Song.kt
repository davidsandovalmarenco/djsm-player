package com.djsm.player.domain.model

data class Song(
    val id: Long,
    val contentUri: String,
    val title: String,
    val artist: String,
    val artistId: Long,
    val album: String,
    val albumId: Long,
    val durationMs: Long,
    val trackNumber: Int?,
    val year: Int?,
    val mimeType: String?,
    val dateAddedSeconds: Long,
    val folderId: String,
    val folderName: String,
    val genreId: Long,
    val genre: String
)