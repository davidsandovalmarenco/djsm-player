package com.djsm.player.data.repository

import com.djsm.player.data.local.MediaStoreAudioDataSource
import com.djsm.player.domain.model.Song
import com.djsm.player.domain.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class MediaStoreMusicRepository @Inject constructor(
    private val audioDataSource: MediaStoreAudioDataSource
) : MusicRepository {

    override suspend fun getSongs(): List<Song> {

        return withContext(Dispatchers.IO) {
            audioDataSource.getSongs()
        }
    }
}