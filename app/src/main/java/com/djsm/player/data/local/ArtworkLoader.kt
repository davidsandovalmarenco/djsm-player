package com.djsm.player.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Size
import com.kyant.taglib.TagLib
import android.content.ContentUris
import android.provider.MediaStore

class ArtworkLoader(
    private val context: Context
) {

    fun loadArtwork(
        contentUri: String,
        albumId: Long?
    ): Bitmap?{

        val uri = Uri.parse(contentUri)

        return loadWithTagLib(uri)
            ?: loadWithMediaMetadataRetriever(uri)
            ?: loadWithMediaStore(uri)
            ?: loadWithAlbumMediaStore(albumId)
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

    private fun loadWithTagLib(
        uri: Uri
    ): Bitmap? {

        return try {

            context.contentResolver
                .openFileDescriptor(uri, "r")
                ?.use { parcelFileDescriptor ->

                    val pictures = TagLib.getPictures(
                        parcelFileDescriptor
                            .dup()
                            .detachFd()
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

            val artworkBytes =
                retriever.embeddedPicture

            artworkBytes?.let { bytes ->

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
}