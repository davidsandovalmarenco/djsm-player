package com.djsm.player.domain.usecase.favorite

import com.djsm.player.domain.repository.FavoriteRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(mediaStoreId: Long, fingerprint: String, isCurrentlyFavorite: Boolean) {
        if (isCurrentlyFavorite) {
            favoriteRepository.removeFavorite(mediaStoreId, fingerprint)
        } else {
            favoriteRepository.addFavorite(mediaStoreId, fingerprint)
        }
    }
}
