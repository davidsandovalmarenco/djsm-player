package com.djsm.player.data.local

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.LruCache
import android.util.Size
import com.kyant.taglib.TagLib

class ArtworkLoader(
    private val context: Context
) {

    fun loadArtwork(
        contentUri: String,
        albumId: Long?
    ): Bitmap? {

        val cacheKey = createCacheKey(
            contentUri = contentUri,
            albumId = albumId
        )

        memoryCache.get(cacheKey)?.let { bitmap ->
            return bitmap
        }

        if (missingArtworkCache.contains(cacheKey)) {
            return null
        }

        val uri = Uri.parse(contentUri)

        val artwork =
            loadWithTagLib(uri)
                ?: loadWithMediaMetadataRetriever(uri)
                ?: loadWithMediaStore(uri)
                ?: loadWithAlbumMediaStore(albumId)

        if (artwork != null) {

            memoryCache.put(
                cacheKey,
                artwork
            )

        } else {

            missingArtworkCache.add(
                cacheKey
            )
        }

        return artwork
    }

    private fun loadWithTagLib(
        uri: Uri
    ): Bitmap? {

        return try {

            context.contentResolver
                .openFileDescriptor(uri, "r")
                ?.use { parcelFileDescriptor ->

                    val duplicatedFd = parcelFileDescriptor
                        .dup()
                        .detachFd()

                    val pictures = TagLib.getPictures(
                        duplicatedFd
                    )

                    val picture =
                        pictures.firstOrNull { picture ->
                            picture.pictureType.equals(
                                "Front Cover",
                                ignoreCase = true
                            )
                        }
                            ?: pictures.firstOrNull()

                    val artworkBytes =
                        picture
                            ?.data
                            ?.takeIf { it.isNotEmpty() }

                    artworkBytes?.let { bytes ->

                        BitmapFactory.decodeByteArray(
                            bytes,
                            0,
                            bytes.size
                        )
                    }
                }

        } catch (_: Exception) {

            null
        }
    }

    private fun loadWithMediaMetadataRetriever(
        uri: Uri
    ): Bitmap? {

        val retriever = MediaMetadataRetriever()

        return try {

            retriever.setDataSource(
                context,
                uri
            )

            retriever.embeddedPicture?.let { bytes ->

                BitmapFactory.decodeByteArray(
                    bytes,
                    0,
                    bytes.size
                )
            }

        } catch (_: Exception) {

            null

        } finally {

            retriever.release()
        }
    }

    private fun loadWithMediaStore(
        uri: Uri
    ): Bitmap? {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return null
        }

        return try {

            context.contentResolver.loadThumbnail(
                uri,
                Size(1000, 1000),
                null
            )

        } catch (_: Exception) {

            null
        }
    }

    private fun loadWithAlbumMediaStore(
        albumId: Long?
    ): Bitmap? {

        if (
            albumId == null ||
            albumId <= 0L ||
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
        ) {
            return null
        }

        return try {

            val albumUri = ContentUris.withAppendedId(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                albumId
            )

            context.contentResolver.loadThumbnail(
                albumUri,
                Size(1000, 1000),
                null
            )

        } catch (_: Exception) {

            null
        }
    }

    private fun createCacheKey(
        contentUri: String,
        albumId: Long?
    ): String {
        return "$contentUri:${albumId ?: 0L}"
    }

    companion object {

        private val memoryCache = object : LruCache<String, Bitmap>(
            calculateCacheSize()
        ) {

            override fun sizeOf(
                key: String,
                value: Bitmap
            ): Int {

                return value.allocationByteCount / 1024
            }
        }

        private val missingArtworkCache =
            mutableSetOf<String>()

        private fun calculateCacheSize(): Int {

            val maxMemoryKb =
                Runtime.getRuntime()
                    .maxMemory()
                    .div(1024)
                    .toInt()

            return maxMemoryKb / 16
        }
    }
}