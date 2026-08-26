package com.djsm.player.playback

import android.os.SystemClock
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.djsm.player.domain.usecase.history.RecordPlaybackUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaybackHistoryTracker @Inject constructor(
    private val recordPlaybackUseCase: RecordPlaybackUseCase,
    private val timeProvider: TimeProvider,
    @com.djsm.player.di.MainDispatcher private val mainDispatcher: kotlinx.coroutines.CoroutineDispatcher
) : Player.Listener {

    private var player: Player? = null
    private var trackingJob: Job? = null
    private val scope = CoroutineScope(mainDispatcher)

    private var currentMediaId: String? = null
    private var accumulatedPlayTimeMs: Long = 0L
    private var lastTickTime: Long = 0L
    private var isCurrentTrackRecorded = false

    fun attach(player: Player) {
        this.player = player
        player.addListener(this)
        // If already playing when attached
        if (player.isPlaying) {
            startTracking()
        }
    }

    fun detach() {
        stopTracking()
        player?.removeListener(this)
        player = null
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        resetSession()
        mediaItem?.let {
            currentMediaId = it.mediaId
        }
        if (player?.isPlaying == true) {
            startTracking()
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (isPlaying) {
            startTracking()
        } else {
            stopTracking()
        }
    }

    private fun startTracking() {
        if (trackingJob?.isActive == true) return
        lastTickTime = timeProvider.elapsedRealtime()

        trackingJob = scope.launch {
            while (isActive) {
                delay(1000)
                if (player?.isPlaying == true) {
                    val now = timeProvider.elapsedRealtime()
                    val delta = now - lastTickTime
                    lastTickTime = now

                    accumulatedPlayTimeMs += delta
                    checkThreshold()
                } else {
                    // Safety check if we somehow miss an onIsPlayingChanged(false)
                    lastTickTime = timeProvider.elapsedRealtime()
                }
            }
        }
    }

    private fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null
        if (player?.isPlaying == true) { // Edge case: stopped manually while playing
            val now = timeProvider.elapsedRealtime()
            accumulatedPlayTimeMs += (now - lastTickTime)
            checkThreshold()
        }
    }

    private fun resetSession() {
        stopTracking()
        currentMediaId = null
        accumulatedPlayTimeMs = 0L
        isCurrentTrackRecorded = false
    }

    private fun checkThreshold() {
        if (isCurrentTrackRecorded) return

        val currentPlayer = player ?: return
        val currentItem = currentPlayer.currentMediaItem ?: return

        val duration = currentItem.mediaMetadata.extras?.getLong("durationMs") ?: 0L
        
        // Threshold: MIN(30s, 50% of duration). Fallback safely to 30s for invalid durations.
        val threshold = if (duration > 0) {
            minOf(30_000L, duration / 2)
        } else {
            30_000L
        }

        if (accumulatedPlayTimeMs >= threshold) {
            isCurrentTrackRecorded = true
            recordPlayback(currentItem)
        }
    }

    private fun recordPlayback(item: MediaItem) {
        val extras = item.mediaMetadata.extras
        val mediaStoreId = extras?.getLong("mediaStoreId") ?: return
        val fingerprint = extras.getString("fingerprint") ?: return
        val durationMs = extras.getLong("durationMs", 0L)
        val title = item.mediaMetadata.title?.toString() ?: "Unknown"
        val artist = item.mediaMetadata.artist?.toString() ?: "Unknown"

        scope.launch {
            recordPlaybackUseCase(
                mediaStoreId = mediaStoreId,
                fingerprint = fingerprint,
                title = title,
                artist = artist,
                durationMs = durationMs
            )
        }
    }
}
