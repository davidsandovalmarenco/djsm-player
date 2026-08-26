package com.djsm.player.data.repository

import com.djsm.player.data.local.room.HistoryDao
import com.djsm.player.data.local.room.PlaybackHistoryEntity
import com.djsm.player.domain.repository.HistoryAggregateItem
import com.djsm.player.domain.repository.HistoryEvent
import com.djsm.player.domain.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {

    override suspend fun recordPlayback(
        mediaStoreId: Long,
        fingerprint: String,
        title: String,
        artist: String,
        durationMs: Long
    ) {
        withContext(Dispatchers.IO) {
            val entity = PlaybackHistoryEntity(
                eventId = UUID.randomUUID().toString(),
                mediaStoreId = mediaStoreId,
                fingerprint = fingerprint,
                playedAt = System.currentTimeMillis(),
                snapshotTitle = title,
                snapshotArtist = artist,
                durationMs = durationMs
            )
            historyDao.insertPlaybackEvent(entity)
        }
    }

    override fun observeHistory(): Flow<List<HistoryEvent>> {
        return historyDao.observeHistory().map { entities ->
            entities.map {
                HistoryEvent(
                    eventId = it.eventId,
                    mediaStoreId = it.mediaStoreId,
                    fingerprint = it.fingerprint,
                    playedAt = it.playedAt,
                    snapshotTitle = it.snapshotTitle,
                    snapshotArtist = it.snapshotArtist,
                    durationMs = it.durationMs
                )
            }
        }
    }

    override fun observeRecentlyPlayed(): Flow<List<HistoryAggregateItem>> {
        return historyDao.observeRecentlyPlayed().map { entities ->
            entities.map {
                HistoryAggregateItem(
                    fingerprint = it.fingerprint,
                    lastPlayedAt = it.lastPlayedAt,
                    playCount = it.playCount
                )
            }
        }
    }

    override fun observeMostPlayed(): Flow<List<HistoryAggregateItem>> {
        return historyDao.observeMostPlayed().map { entities ->
            entities.map {
                HistoryAggregateItem(
                    fingerprint = it.fingerprint,
                    lastPlayedAt = it.lastPlayedAt,
                    playCount = it.playCount
                )
            }
        }
    }
}
