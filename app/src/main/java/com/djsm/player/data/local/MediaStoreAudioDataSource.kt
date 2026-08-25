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
        val sdkInt = android.os.Build.VERSION.SDK_INT

        val genreMap = if (sdkInt < 30) buildGenreMap() else emptyMap()

        val projectionList = mutableListOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATE_ADDED
        )

        if (sdkInt >= 29) {
            projectionList.add(MediaStore.Audio.Media.RELATIVE_PATH)
            projectionList.add(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)
        } else {
            projectionList.add(MediaStore.Audio.Media.DATA)
        }

        if (sdkInt >= 30) {
            projectionList.add(MediaStore.Audio.Media.GENRE)
            projectionList.add(MediaStore.Audio.Media.GENRE_ID)
        }

        val projection = projectionList.toTypedArray()
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            val dataColumn = if (sdkInt < 29) cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA) else -1
            val relativePathColumn = if (sdkInt >= 29) cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH) else -1
            val bucketNameColumn = if (sdkInt >= 29) cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME) else -1
            
            val genreColumn = if (sdkInt >= 30) cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE) else -1
            val genreIdColumn = if (sdkInt >= 30) cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE_ID) else -1

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val folderPath: String
                val folderName: String
                if (sdkInt >= 29) {
                    val relativePath = cursor.getString(relativePathColumn) ?: ""
                    folderPath = relativePath.trimEnd('/')
                    folderName = cursor.getString(bucketNameColumn) ?: folderPath.substringAfterLast('/')
                } else {
                    val data = cursor.getString(dataColumn) ?: ""
                    val file = java.io.File(data)
                    folderPath = file.parentFile?.absolutePath ?: ""
                    folderName = file.parentFile?.name ?: "Unknown Folder"
                }
                
                val finalFolderId = folderPath.ifBlank { "unknown_folder" }
                val finalFolderName = folderName.ifBlank { "Carpeta desconocida" }
                
                val genreId: Long
                val genreName: String
                
                if (sdkInt >= 30) {
                    genreId = if (!cursor.isNull(genreIdColumn)) cursor.getLong(genreIdColumn) else -1L
                    genreName = normalizeMetadata(cursor.getString(genreColumn), "Sin género")
                } else {
                    val genrePair = genreMap[id]
                    genreId = genrePair?.first ?: -1L
                    genreName = genrePair?.second ?: "Sin género"
                }

                val song = Song(
                    id = id,
                    contentUri = contentUri.toString(),
                    title = cursor.getString(titleColumn) ?: "Unknown title",
                    artist = normalizeMetadata(
                        value = cursor.getString(artistColumn),
                        fallback = "Artista desconocido"
                    ),
                    artistId = cursor.getLong(artistIdColumn),
                    album = normalizeMetadata(
                        value = cursor.getString(albumColumn),
                        fallback = "Álbum desconocido"
                    ),
                    albumId = cursor.getLong(albumIdColumn),
                    durationMs = cursor.getLong(durationColumn),
                    trackNumber = cursor.getInt(trackColumn).takeIf { it > 0 },
                    year = cursor.getInt(yearColumn).takeIf { it > 0 },
                    mimeType = cursor.getString(mimeTypeColumn),
                    dateAddedSeconds = cursor.getLong(dateAddedColumn),
                    folderId = finalFolderId,
                    folderName = finalFolderName,
                    genreId = genreId,
                    genre = genreName
                )

                songs.add(song)
            }
        }

        return songs
    }

    private fun buildGenreMap(): Map<Long, Pair<Long, String>> {
        val map = mutableMapOf<Long, Pair<Long, String>>()
        context.contentResolver.query(
            MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME),
            null, null, null
        )?.use { genreCursor ->
            val idCol = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID)
            val nameCol = genreCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)
            while (genreCursor.moveToNext()) {
                val genreId = genreCursor.getLong(idCol)
                val genreName = normalizeMetadata(genreCursor.getString(nameCol), "Sin género")
                
                context.contentResolver.query(
                    MediaStore.Audio.Genres.Members.getContentUri("external", genreId),
                    arrayOf(MediaStore.Audio.Genres.Members.AUDIO_ID),
                    null, null, null
                )?.use { membersCursor ->
                    val audioIdCol = membersCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.AUDIO_ID)
                    while (membersCursor.moveToNext()) {
                        map[membersCursor.getLong(audioIdCol)] = genreId to genreName
                    }
                }
            }
        }
        return map
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