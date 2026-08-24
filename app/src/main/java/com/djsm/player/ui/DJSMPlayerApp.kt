package com.djsm.player.ui

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.djsm.player.core.permission.audioPermission
import com.djsm.player.data.local.MediaStoreAudioDataSource
import com.djsm.player.domain.model.Song
import com.djsm.player.navigation.AppRoute
import com.djsm.player.navigation.LibraryRoute
import com.djsm.player.navigation.NowPlayingRoute
import com.djsm.player.playback.rememberPlaybackController
import com.djsm.player.playback.rememberPlaybackUiState
import com.djsm.player.ui.components.MiniPlayer
import com.djsm.player.ui.library.LibraryScreen
import com.djsm.player.ui.permission.AudioPermissionScreen
import com.djsm.player.ui.player.NowPlayingScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun DJSMPlayerApp() {

    val context = LocalContext.current
    val permission = audioPermission()

    val playbackController = rememberPlaybackController()

    val playbackUiState = rememberPlaybackUiState(
        controller = playbackController
    )

    val currentPlaybackController by rememberUpdatedState(
        playbackController
    )

    val currentPlaybackUiState by rememberUpdatedState(
        playbackUiState
    )

    val backStack = remember {
        mutableStateListOf<AppRoute>(
            LibraryRoute
        )
    }

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

    if (!hasAudioPermission) {

        AudioPermissionScreen(
            onRequestPermission = {
                permissionLauncher.launch(permission)
            }
        )

        return
    }

    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryProvider = { route ->

            when (route) {

                LibraryRoute -> {

                    NavEntry(route) {

                        Scaffold(
                            bottomBar = {

                                MiniPlayer(
                                    state = currentPlaybackUiState,
                                    onPlayPauseClick = {

                                        currentPlaybackController?.let { controller ->

                                            if (controller.isPlaying) {
                                                controller.pause()
                                            } else {
                                                controller.play()
                                            }
                                        }
                                    },
                                    onClick = {
                                        backStack.add(NowPlayingRoute)
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

                                    currentPlaybackController?.apply {
                                        setMediaItem(mediaItem)
                                        prepare()
                                        play()
                                    }
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }

                NowPlayingRoute -> {

                    NavEntry(route) {

                        NowPlayingScreen(
                            state = currentPlaybackUiState,
                            onPlayPauseClick = {

                                currentPlaybackController?.let { controller ->

                                    if (controller.isPlaying) {
                                        controller.pause()
                                    } else {
                                        controller.play()
                                    }
                                }
                            },
                            onSeek = { positionMs ->

                                currentPlaybackController?.seekTo(
                                    positionMs
                                )
                            }
                        )
                    }
                }
            }
        }
    )
}