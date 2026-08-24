package com.djsm.player.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import androidx.media3.session.MediaController

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

    return state
}

private fun Player.toPlaybackUiState(): PlaybackUiState {

    val currentItem = currentMediaItem
    val metadata = mediaMetadata

    return PlaybackUiState(
        mediaId = currentItem?.mediaId,
        title = metadata.title?.toString().orEmpty(),
        artist = metadata.artist?.toString().orEmpty(),
        isPlaying = isPlaying,
        hasMedia = currentItem != null
    )
}