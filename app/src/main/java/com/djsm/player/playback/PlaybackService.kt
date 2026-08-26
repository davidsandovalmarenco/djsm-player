package com.djsm.player.playback

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    @Inject
    lateinit var playbackHistoryTracker: PlaybackHistoryTracker

    override fun onCreate() {
        super.onCreate()

        val player = ExoPlayer.Builder(this)
            .build()

        mediaSession = MediaSession.Builder(
            this,
            player
        ).build()

        playbackHistoryTracker.attach(player)
    }

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        playbackHistoryTracker.detach()
        
        mediaSession?.run {
            player.release()
            release()
        }

        mediaSession = null

        super.onDestroy()
    }
}