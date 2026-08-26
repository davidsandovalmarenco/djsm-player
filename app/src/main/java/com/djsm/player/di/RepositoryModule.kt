package com.djsm.player.di

import com.djsm.player.data.repository.FavoriteRepositoryImpl
import com.djsm.player.data.repository.HistoryRepositoryImpl
import com.djsm.player.data.repository.MediaStoreMusicRepository
import com.djsm.player.data.repository.PlaylistRepositoryImpl
import com.djsm.player.domain.repository.FavoriteRepository
import com.djsm.player.domain.repository.HistoryRepository
import com.djsm.player.domain.repository.MusicRepository
import com.djsm.player.domain.repository.PlaylistRepository
import com.djsm.player.domain.repository.QueueRepository
import com.djsm.player.data.repository.QueueRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        implementation: MediaStoreMusicRepository
    ): MusicRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        implementation: FavoriteRepositoryImpl
    ): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(
        implementation: PlaylistRepositoryImpl
    ): PlaylistRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        historyRepositoryImpl: HistoryRepositoryImpl
    ): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindQueueRepository(
        queueRepositoryImpl: QueueRepositoryImpl
    ): QueueRepository
}