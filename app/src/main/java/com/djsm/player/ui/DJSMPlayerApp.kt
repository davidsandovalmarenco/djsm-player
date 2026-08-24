package com.djsm.player.ui

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.djsm.player.core.permission.audioPermission
import com.djsm.player.data.local.MediaStoreAudioDataSource
import com.djsm.player.domain.model.Song
import com.djsm.player.playback.rememberPlaybackController
import com.djsm.player.playback.rememberPlaybackUiState
import com.djsm.player.ui.components.MiniPlayer
import com.djsm.player.ui.library.LibraryScreen
import com.djsm.player.ui.permission.AudioPermissionScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DJSMPlayerApp() {

    val context = LocalContext.current
    val permission = audioPermission()

    val playbackController = rememberPlaybackController()

    val playbackUiState = rememberPlaybackUiState(
        controller = playbackController
    )

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var songs by remember {
        mutableStateOf<List<Song>>(emptyList())
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    LaunchedEffect(hasAudioPermission) {
        if (hasAudioPermission) {

            songs = withContext(Dispatchers.IO) {

                val dataSource = MediaStoreAudioDataSource(
                    context = context.applicationContext
                )

                dataSource.getSongs()
            }
        }
    }

    if (hasAudioPermission) {

        Scaffold(
            bottomBar = {
                MiniPlayer(
                    state = playbackUiState,
                    onPlayPauseClick = {

                        playbackController?.let { controller ->

                            if (controller.isPlaying) {
                                controller.pause()
                            } else {
                                controller.play()
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->

            LibraryScreen(
                songs = songs,
                onSongClick = { song ->

                    val mediaItem = MediaItem.Builder()
                        .setMediaId(song.id.toString())
                        .setUri(song.contentUri)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(song.title)
                                .setArtist(song.artist)
                                .setAlbumTitle(song.album)
                                .build()
                        )
                        .build()

                    playbackController?.apply {
                        setMediaItem(mediaItem)
                        prepare()
                        play()
                    }
                },
                modifier = Modifier.padding(innerPadding)
            )
        }

    } else {

        AudioPermissionScreen(
            onRequestPermission = {
                permissionLauncher.launch(permission)
            }
        )
    }
}