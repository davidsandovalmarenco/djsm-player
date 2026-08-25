package com.djsm.player.domain.repository

import com.djsm.player.domain.model.Song

interface MusicRepository {

    suspend fun getSongs(): List<Song>
}