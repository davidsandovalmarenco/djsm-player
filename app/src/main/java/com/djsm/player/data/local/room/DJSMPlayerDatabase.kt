package com.djsm.player.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteEntity::class],
    version = 1,
    exportSchema = true
)
abstract class DJSMPlayerDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
