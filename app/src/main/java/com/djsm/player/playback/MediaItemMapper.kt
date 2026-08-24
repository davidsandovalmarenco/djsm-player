package com.djsm.player.playback

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.djsm.player.domain.model.Song

fun Song.toMediaItem(): MediaItem {

    return MediaItem.Builder()
        .setMediaId(id.toString())
        .setUri(contentUri)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .build()
        )
        .build()
}