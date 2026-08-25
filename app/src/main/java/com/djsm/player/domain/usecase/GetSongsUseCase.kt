package com.djsm.player.domain.usecase

import com.djsm.player.domain.model.Song
import com.djsm.player.domain.repository.MusicRepository
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {

    suspend operator fun invoke(): List<Song> {
        return musicRepository.getSongs()
    }
}