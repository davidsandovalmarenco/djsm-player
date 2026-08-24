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
import com.djsm.player.playback.toMediaItem

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
                                onSongClick = { selectedSong ->

                                    val startIndex = songs.indexOfFirst { song ->
                                        song.id == selectedSong.id
                                    }

                                    if (startIndex >= 0) {

                                        val mediaItems = songs.map { song ->
                                            song.toMediaItem()
                                        }

                                        currentPlaybackController?.apply {
                                            setMediaItems(
                                                mediaItems,
                                                startIndex,
                                                0L
                                            )
                                            prepare()
                                            play()
                                        }
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

                            onPreviousClick = {
                                currentPlaybackController?.seekToPreviousMediaItem()
                            },

                            onNextClick = {
                                currentPlaybackController?.seekToNextMediaItem()
                            },

                            onSeek = { positionMs ->
                                currentPlaybackController?.seekTo(positionMs)
                            }
                        )
                    }
                }
            }
        }
    )
}