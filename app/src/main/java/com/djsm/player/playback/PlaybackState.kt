package com.djsm.player.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import kotlinx.coroutines.delay

@Composable
fun rememberPlaybackUiState(
    controller: MediaController?
): PlaybackUiState {

    var state by remember(controller) {
        mutableStateOf(
            controller?.toPlaybackUiState()
                ?: PlaybackUiState()
        )
    }

    DisposableEffect(controller) {

        if (controller == null) {
            state = PlaybackUiState()

            return@DisposableEffect onDispose { }
        }

        val listener = object : Player.Listener {

            override fun onEvents(
                player: Player,
                events: Player.Events
            ) {
                state = player.toPlaybackUiState()
            }
        }

        controller.addListener(listener)

        state = controller.toPlaybackUiState()

        onDispose {
            controller.removeListener(listener)
        }
    }

    LaunchedEffect(controller, state.hasMedia) {

        if (controller == null || !state.hasMedia) {
            return@LaunchedEffect
        }

        while (true) {

            val currentPosition = controller.currentPosition
                .coerceAtLeast(0L)

            val currentDuration = controller.duration
                .takeIf {
                    it > 0L && it != C.TIME_UNSET
                }
                ?: 0L

            state = state.copy(
                positionMs = currentPosition,
                durationMs = currentDuration
            )

            delay(500)
        }
    }

    return state
}

private fun Player.toPlaybackUiState(): PlaybackUiState {

    val currentItem = currentMediaItem
    val metadata = mediaMetadata

    val safeDuration = duration
        .takeIf {
            it > 0L && it != C.TIME_UNSET
        }
        ?: 0L

    return PlaybackUiState(
        mediaId = currentItem?.mediaId,
        title = metadata.title?.toString().orEmpty(),
        artist = metadata.artist?.toString().orEmpty(),
        isPlaying = isPlaying,
        hasMedia = currentItem != null,
        positionMs = currentPosition.coerceAtLeast(0L),
        durationMs = safeDuration
    )
}