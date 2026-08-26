package com.djsm.player.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        FavoriteEntity::class,
        PlaylistEntity::class,
        PlaylistSongEntity::class,
        PlaybackHistoryEntity::class,
        QueueEntity::class,
        PlaybackStateEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class DJSMPlayerDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun historyDao(): HistoryDao
    abstract fun queueDao(): QueueDao

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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `playback_history` (
                        `eventId` TEXT NOT NULL, 
                        `mediaStoreId` INTEGER NOT NULL, 
                        `fingerprint` TEXT NOT NULL, 
                        `playedAt` INTEGER NOT NULL, 
                        `snapshotTitle` TEXT NOT NULL, 
                        `snapshotArtist` TEXT NOT NULL, 
                        `durationMs` INTEGER NOT NULL,
                        PRIMARY KEY(`eventId`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_playback_history_fingerprint` ON `playback_history` (`fingerprint`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_playback_history_playedAt` ON `playback_history` (`playedAt`)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `playback_queue` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `mediaStoreId` INTEGER NOT NULL, 
                        `fingerprint` TEXT NOT NULL, 
                        `sortIndex` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `playback_state` (
                        `id` INTEGER NOT NULL, 
                        `currentMediaId` TEXT, 
                        `currentPositionMs` INTEGER NOT NULL, 
                        `repeatMode` INTEGER NOT NULL, 
                        `shuffleModeEnabled` INTEGER NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
