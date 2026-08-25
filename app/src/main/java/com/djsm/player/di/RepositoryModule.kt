package com.djsm.player.di

import com.djsm.player.data.repository.FavoriteRepositoryImpl
import com.djsm.player.data.repository.MediaStoreMusicRepository
import com.djsm.player.domain.repository.FavoriteRepository
import com.djsm.player.domain.repository.MusicRepository
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
}