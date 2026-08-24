package com.djsm.player.playback

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken

@Composable
fun rememberPlaybackController(): MediaController? {

    val context = LocalContext.current

    var controller by remember {
        mutableStateOf<MediaController?>(null)
    }

    val sessionToken = remember {
        SessionToken(
            context,
            ComponentName(
                context,
                PlaybackService::class.java
            )
        )
    }

    DisposableEffect(sessionToken) {

        val controllerFuture = MediaController.Builder(
            context,
            sessionToken
        ).buildAsync()

        controllerFuture.addListener(
            {
                controller = controllerFuture.get()
            },
            ContextCompat.getMainExecutor(context)
        )

        onDispose {
            MediaController.releaseFuture(controllerFuture)
            controller = null
        }
    }

    return controller
}