package com.djsm.player.di

import android.content.Context
import androidx.room.Room
import com.djsm.player.data.local.room.DJSMPlayerDatabase
import com.djsm.player.data.local.room.FavoriteDao
import com.djsm.player.data.local.room.HistoryDao
import com.djsm.player.data.local.room.PlaylistDao
import com.djsm.player.data.local.room.QueueDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DJSMPlayerDatabase {
        return Room.databaseBuilder(
            context,
            DJSMPlayerDatabase::class.java,
            "djsm_player_database"
        )
            .addMigrations(DJSMPlayerDatabase.MIGRATION_1_2, DJSMPlayerDatabase.MIGRATION_2_3, DJSMPlayerDatabase.MIGRATION_3_4)
            .build()
    }

    @Provides
    fun provideFavoriteDao(database: DJSMPlayerDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    fun providePlaylistDao(database: DJSMPlayerDatabase): PlaylistDao {
        return database.playlistDao()
    }

    @Provides
    fun provideHistoryDao(database: DJSMPlayerDatabase): HistoryDao {
        return database.historyDao()
    }

    @Provides
    fun provideQueueDao(database: DJSMPlayerDatabase): QueueDao {
        return database.queueDao()
    }
}
