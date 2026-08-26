package com.djsm.player.playback

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.djsm.player.domain.model.Song
import android.os.Bundle

fun Song.toMediaItem(): MediaItem {

    val extras = Bundle().apply {
        putLong("albumId", albumId)
        putLong("mediaStoreId", id)
        putString("fingerprint", fingerprint)
        putLong("durationMs", durationMs)
    }

    return MediaItem.Builder()
        .setMediaId(id.toString())
        .setUri(contentUri)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .setExtras(extras)
                .build()
        )
        .build()
}