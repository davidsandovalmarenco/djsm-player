package com.djsm.player.domain.usecase.favorite

import com.djsm.player.domain.model.Favorite
import com.djsm.player.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<Favorite>> {
        return favoriteRepository.observeFavorites()
    }
}
