package com.djsm.player.domain.repository

import com.djsm.player.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    suspend fun getSongs(): List<Song>
    fun observeSongs(): Flow<List<Song>>
}