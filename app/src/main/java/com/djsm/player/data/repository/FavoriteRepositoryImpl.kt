package com.djsm.player.data.repository

import com.djsm.player.data.local.room.FavoriteDao
import com.djsm.player.data.local.room.FavoriteEntity
import com.djsm.player.domain.model.Favorite
import com.djsm.player.domain.repository.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<Favorite>> {
        return favoriteDao.observeAll().map { entities ->
            entities.map {
                Favorite(
                    favoriteId = it.favoriteId,
                    mediaStoreId = it.mediaStoreId,
                    fingerprint = it.fingerprint,
                    addedAt = it.addedAt
                )
            }
        }
    }

    override suspend fun addFavorite(mediaStoreId: Long, fingerprint: String) {
        withContext(Dispatchers.IO) {
            val entity = FavoriteEntity(
                favoriteId = UUID.randomUUID().toString(),
                mediaStoreId = mediaStoreId,
                fingerprint = fingerprint,
                addedAt = System.currentTimeMillis()
            )
            favoriteDao.insert(entity)
        }
    }

    override suspend fun removeFavorite(mediaStoreId: Long, fingerprint: String) {
        withContext(Dispatchers.IO) {
            favoriteDao.delete(mediaStoreId, fingerprint)
        }
    }
}
