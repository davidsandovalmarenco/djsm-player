package com.djsm.player.ui.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.djsm.player.playback.PlaybackUiState
import androidx.media3.common.Player
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.djsm.player.data.local.ArtworkLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun NowPlayingScreen(
    state: PlaybackUiState,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier

){

    val context = LocalContext.current

    val artwork by produceState<Bitmap?>(
        initialValue = null,
        key1 = state.contentUri,
        key2 = state.albumId
    ) {

        value = state.contentUri?.let { contentUri ->

            withContext(Dispatchers.IO) {

                ArtworkLoader(
                    context.applicationContext
                ).loadArtwork(
                    contentUri = contentUri,
                    albumId = state.albumId
                )
            }
        }
    }

    val progress = if (state.durationMs > 0L) {
        state.positionMs
            .toFloat()
            .div(state.durationMs.toFloat())
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(48.dp)
        )

        /*
         * Este Box será reemplazado después
         * por la portada real del álbum.
         */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {

            if (artwork != null) {

                Image(
                    bitmap = artwork!!.asImageBitmap(),
                    contentDescription = "Portada de ${state.title}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

            } else {

                Text(
                    text = "♪",
                    style = MaterialTheme.typography.displayLarge
                )
            }
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Text(
            text = state.title,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = state.artist,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Slider(
            value = progress,
            onValueChange = { newProgress ->

                if (state.durationMs > 0L) {

                    val newPosition =
                        (newProgress * state.durationMs).toLong()

                    onSeek(newPosition)
                }
            },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = formatPlaybackTime(state.positionMs),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = formatPlaybackTime(state.durationMs),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(
                onClick = onShuffleClick
            ) {
                Text(
                    text = if (state.shuffleEnabled) {
                        "🔀✓"
                    } else {
                        "🔀"
                    }
                )
            }

            TextButton(
                onClick = onPreviousClick
            ) {
                Text("⏮")
            }

            TextButton(
                onClick = onPlayPauseClick
            ) {
                Text(
                    text = if (state.isPlaying) {
                        "⏸"
                    } else {
                        "▶"
                    }
                )
            }

            TextButton(
                onClick = onNextClick
            ) {
                Text("⏭")
            }

            TextButton(
                onClick = onRepeatClick
            ) {
                Text(
                    text = when (state.repeatMode) {
                        Player.REPEAT_MODE_ONE -> "🔂"
                        Player.REPEAT_MODE_ALL -> "🔁"
                        else -> "↪"
                    }
                )
            }
        }
    }
}
private fun formatPlaybackTime(
    durationMs: Long
): String {

    val safeDuration = durationMs.coerceAtLeast(0L)

    val totalSeconds = safeDuration / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "$minutes:${seconds.toString().padStart(2, '0')}"
}