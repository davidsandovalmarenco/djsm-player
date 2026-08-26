package com.djsm.player.domain.repository

import kotlinx.coroutines.flow.Flow

data class HistoryEvent(
    val eventId: String,
    val mediaStoreId: Long,
    val fingerprint: String,
    val playedAt: Long,
    val snapshotTitle: String,
    val snapshotArtist: String,
    val durationMs: Long
)

data class HistoryAggregateItem(
    val fingerprint: String,
    val lastPlayedAt: Long,
    val playCount: Int
)

interface HistoryRepository {
    suspend fun recordPlayback(
        mediaStoreId: Long,
        fingerprint: String,
        title: String,
        artist: String,
        durationMs: Long
    )
    fun observeHistory(): Flow<List<HistoryEvent>>
    fun observeRecentlyPlayed(): Flow<List<HistoryAggregateItem>>
    fun observeMostPlayed(): Flow<List<HistoryAggregateItem>>
}
