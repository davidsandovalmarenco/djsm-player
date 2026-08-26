package com.djsm.player.data.repository

import com.djsm.player.data.local.room.PlaybackStateEntity
import com.djsm.player.data.local.room.QueueDao
import com.djsm.player.data.local.room.QueueEntity
import com.djsm.player.domain.repository.PlaybackState
import com.djsm.player.domain.repository.QueueItem
import com.djsm.player.domain.repository.QueueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QueueRepositoryImpl @Inject constructor(
    private val queueDao: QueueDao
) : QueueRepository {

    override suspend fun getQueue(): List<QueueItem> = withContext(Dispatchers.IO) {
        queueDao.getQueue().map {
            QueueItem(it.mediaStoreId, it.fingerprint)
        }
    }

    override suspend fun saveQueue(queue: List<QueueItem>) = withContext(Dispatchers.IO) {
        val entities = queue.mapIndexed { index, item ->
            QueueEntity(
                mediaStoreId = item.mediaStoreId,
                fingerprint = item.fingerprint,
                sortIndex = index
            )
        }
        queueDao.replaceQueue(entities)
    }

    override suspend fun getPlaybackState(): PlaybackState? = withContext(Dispatchers.IO) {
        queueDao.getPlaybackState()?.let {
            PlaybackState(
                currentMediaId = it.currentMediaId,
                currentPositionMs = it.currentPositionMs,
                repeatMode = it.repeatMode,
                shuffleModeEnabled = it.shuffleModeEnabled
            )
        }
    }

    override suspend fun savePlaybackState(state: PlaybackState) = withContext(Dispatchers.IO) {
        queueDao.savePlaybackState(
            PlaybackStateEntity(
                id = 1,
                currentMediaId = state.currentMediaId,
                currentPositionMs = state.currentPositionMs,
                repeatMode = state.repeatMode,
                shuffleModeEnabled = state.shuffleModeEnabled
            )
        )
    }
}
