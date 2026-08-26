package com.djsm.player.domain.usecase.history

import com.djsm.player.domain.repository.HistoryRepository
import javax.inject.Inject

class RecordPlaybackUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(
        mediaStoreId: Long,
        fingerprint: String,
        title: String,
        artist: String,
        durationMs: Long
    ) {
        historyRepository.recordPlayback(mediaStoreId, fingerprint, title, artist, durationMs)
    }
}
