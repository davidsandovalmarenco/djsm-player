package com.djsm.player.domain.usecase

import com.djsm.player.domain.model.Song
import com.djsm.player.domain.repository.MusicRepository
import com.djsm.player.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val musicRepository: MusicRepository,
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): List<Song> {
        val settings = settingsRepository.observeSettings().first()
        val songs = musicRepository.getSongs()
        return filterSongs(songs, settings)
    }

    fun observeSongs(): Flow<List<Song>> {
        return musicRepository.observeSongs().combine(settingsRepository.observeSettings()) { songs, settings ->
            filterSongs(songs, settings)
        }
    }
    
    private fun filterSongs(songs: List<Song>, settings: com.djsm.player.domain.repository.AppSettings): List<Song> {
        return songs.filter { song ->
            val isLongEnough = song.durationMs >= settings.minimumDurationMs
            val isNotExcludedFolder = !settings.excludedFolders.contains(song.folderId)
            isLongEnough && isNotExcludedFolder
        }
    }
}