package com.djsm.player.domain.repository

data class QueueItem(
    val mediaStoreId: Long,
    val fingerprint: String
)

data class PlaybackState(
    val currentMediaId: String?,
    val currentPositionMs: Long,
    val repeatMode: Int,
    val shuffleModeEnabled: Boolean
)

interface QueueRepository {
    suspend fun getQueue(): List<QueueItem>
    suspend fun saveQueue(queue: List<QueueItem>)
    suspend fun getPlaybackState(): PlaybackState?
    suspend fun savePlaybackState(state: PlaybackState)
}
