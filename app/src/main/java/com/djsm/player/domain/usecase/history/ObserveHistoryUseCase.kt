package com.djsm.player.domain.usecase.history

import com.djsm.player.domain.repository.HistoryEvent
import com.djsm.player.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    operator fun invoke(): Flow<List<HistoryEvent>> {
        return historyRepository.observeHistory()
    }
}
