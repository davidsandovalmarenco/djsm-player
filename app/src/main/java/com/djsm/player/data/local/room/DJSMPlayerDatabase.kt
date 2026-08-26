package com.djsm.player.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        FavoriteEntity::class,
        PlaylistEntity::class,
        PlaylistSongEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class DJSMPlayerDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Create playlists table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `playlists` (
                        `playlistId` TEXT NOT NULL, 
                        `name` TEXT NOT NULL, 
                        `createdAt` INTEGER NOT NULL, 
                        `updatedAt` INTEGER NOT NULL, 
                        PRIMARY KEY(`playlistId`)
                    )
                    """.trimIndent()
                )
                // Create playlist_songs table
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `playlist_songs` (
                        `playlistSongId` TEXT NOT NULL, 
                        `playlistId` TEXT NOT NULL, 
                        `mediaStoreId` INTEGER NOT NULL, 
                        `fingerprint` TEXT NOT NULL, 
                        `sortIndex` INTEGER NOT NULL, 
                        `addedAt` INTEGER NOT NULL, 
                        PRIMARY KEY(`playlistSongId`), 
                        FOREIGN KEY(`playlistId`) REFERENCES `playlists`(`playlistId`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                // Create indices for playlist_songs
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_playlist_songs_playlistId` ON `playlist_songs` (`playlistId`)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_playlist_songs_playlistId_fingerprint` ON `playlist_songs` (`playlistId`, `fingerprint`)")
            }
        }
    }
}
