package com.djsm.player.playback

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.djsm.player.domain.repository.MusicRepository
import com.djsm.player.domain.repository.PlaybackState
import com.djsm.player.domain.repository.QueueItem
import com.djsm.player.domain.repository.QueueRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    @Inject
    lateinit var queueRepository: QueueRepository

    @Inject
    lateinit var musicRepository: MusicRepository
    
    @Inject
    lateinit var playbackHistoryTracker: PlaybackHistoryTracker

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var saveQueueJob: Job? = null
    
    private val playerListener = object : Player.Listener {
        override fun onTimelineChanged(timeline: androidx.media3.common.Timeline, reason: Int) {
            scheduleSaveQueue()
        }
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            scheduleSaveQueue()
        }
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            scheduleSaveQueue()
        }
        override fun onRepeatModeChanged(repeatMode: Int) {
            scheduleSaveQueue()
        }
    }

    override fun onCreate() {
        super.onCreate()

        val player = ExoPlayer.Builder(this)
            .build()
            
        player.addListener(playerListener)

        mediaSession = MediaSession.Builder(
            this,
            player
        ).build()

        playbackHistoryTracker.attach(player)
        
        restoreQueue(player)
    }

    private fun scheduleSaveQueue() {
        val player = mediaSession?.player ?: return
        saveQueueJob?.cancel()
        saveQueueJob = serviceScope.launch {
            delay(1000) // Debounce rapid changes
            
            val state = PlaybackState(
                currentMediaId = player.currentMediaItem?.mediaId,
                currentPositionMs = player.currentPosition,
                repeatMode = player.repeatMode,
                shuffleModeEnabled = player.shuffleModeEnabled
            )
            
            val queue = (0 until player.mediaItemCount).mapNotNull { i ->
                val item = player.getMediaItemAt(i)
                val extras = item.mediaMetadata.extras
                val mediaStoreId = extras?.getLong("mediaStoreId") ?: return@mapNotNull null
                val fingerprint = extras.getString("fingerprint") ?: return@mapNotNull null
                QueueItem(mediaStoreId, fingerprint)
            }
            
            queueRepository.saveQueue(queue)
            queueRepository.savePlaybackState(state)
        }
    }
    
    private fun restoreQueue(player: ExoPlayer) {
        serviceScope.launch {
            val savedQueue = queueRepository.getQueue()
            val savedState = queueRepository.getPlaybackState()
            
            if (savedQueue.isNotEmpty()) {
                val allSongs = musicRepository.getSongs()
                
                val validItems = savedQueue.mapNotNull { savedItem ->
                    // Fast path by mediaStoreId + fingerprint verification
                    var song = allSongs.find { it.id == savedItem.mediaStoreId && it.fingerprint == savedItem.fingerprint }
                    // Fallback by fingerprint only
                    if (song == null) {
                        song = allSongs.find { it.fingerprint == savedItem.fingerprint }
                    }
                    song?.toMediaItem()
                }
                
                if (validItems.isNotEmpty()) {
                    player.setMediaItems(validItems)
                    
                    savedState?.let { state ->
                        player.repeatMode = state.repeatMode
                        player.shuffleModeEnabled = state.shuffleModeEnabled
                        
                        val validIndex = validItems.indexOfFirst { it.mediaId == state.currentMediaId }
                        if (validIndex != -1) {
                            player.seekTo(validIndex, state.currentPositionMs)
                        }
                    }
                }
            }
        }
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        saveQueueJob?.cancel()
        playbackHistoryTracker.detach()
        
        mediaSession?.run {
            player.removeListener(playerListener)
            player.release()
            release()
        }

        mediaSession = null

        super.onDestroy()
    }
}