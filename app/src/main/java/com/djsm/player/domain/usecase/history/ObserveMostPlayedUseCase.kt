package com.djsm.player.domain.usecase.history

import com.djsm.player.domain.repository.HistoryAggregateItem
import com.djsm.player.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveMostPlayedUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    operator fun invoke(): Flow<List<HistoryAggregateItem>> {
        return historyRepository.observeMostPlayed()
    }
}
