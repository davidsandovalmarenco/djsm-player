package com.djsm.player.data.local

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.provider.MediaStore
import com.djsm.player.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MediaStoreAudioDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    fun observeSongs(): Flow<List<Song>> = callbackFlow {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                trySend(Unit)
            }
        }
        
        context.contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )
        
        trySend(Unit)
        
        awaitClose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }
        .conflate()
        .map { getSongs() }
        .flowOn(Dispatchers.IO)

    fun getSongs(): List<Song> {
        val songs = mutableListOf<Song>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->

            val idColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)

            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)

            val artistColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            val albumColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

            val albumIdColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            val trackColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)

            val yearColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

            val mimeTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)

            val dateAddedColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {

                val id = cursor.getLong(idColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val song = Song(
                    id = id,
                    contentUri = contentUri.toString(),
                    title = cursor.getString(titleColumn) ?: "Unknown title",
                    artist = normalizeMetadata(
                        value = cursor.getString(artistColumn),
                        fallback = "Artista desconocido"
                    ),
                    album = normalizeMetadata(
                        value = cursor.getString(albumColumn),
                        fallback = "Álbum desconocido"
                    ),
                    albumId = cursor.getLong(albumIdColumn),
                    durationMs = cursor.getLong(durationColumn),
                    trackNumber = cursor.getInt(trackColumn).takeIf { it > 0 },
                    year = cursor.getInt(yearColumn).takeIf { it > 0 },
                    mimeType = cursor.getString(mimeTypeColumn),
                    dateAddedSeconds = cursor.getLong(dateAddedColumn)
                )

                songs.add(song)
            }
        }

        return songs
    }
    private fun normalizeMetadata(
        value: String?,
        fallback: String
    ): String {
        return if (
            value.isNullOrBlank() ||
            value.equals("<unknown>", ignoreCase = true)
        ) {
            fallback
        } else {
            value
        }
    }
}