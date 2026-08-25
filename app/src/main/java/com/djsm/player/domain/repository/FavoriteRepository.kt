package com.djsm.player.domain.repository

import com.djsm.player.domain.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<Favorite>>
    suspend fun addFavorite(mediaStoreId: Long, fingerprint: String)
    suspend fun removeFavorite(mediaStoreId: Long, fingerprint: String)
}
