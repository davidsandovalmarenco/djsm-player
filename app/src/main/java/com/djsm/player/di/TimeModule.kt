package com.djsm.player.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.djsm.player.playback.TimeProvider
import com.djsm.player.playback.DefaultTimeProvider

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeModule {
    @Binds
    abstract fun bindTimeProvider(
        defaultTimeProvider: DefaultTimeProvider
    ): TimeProvider
}
