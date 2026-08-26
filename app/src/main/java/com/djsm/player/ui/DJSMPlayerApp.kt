package com.djsm.player.ui

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.djsm.player.navigation.AppRoute
import com.djsm.player.navigation.LibraryRoute
import com.djsm.player.navigation.NowPlayingRoute
import com.djsm.player.playback.rememberPlaybackController
import com.djsm.player.playback.rememberPlaybackUiState
import com.djsm.player.ui.components.MiniPlayer
import com.djsm.player.ui.permission.AudioPermissionScreen
import com.djsm.player.ui.player.NowPlayingScreen
import androidx.compose.runtime.rememberUpdatedState
import com.djsm.player.playback.toMediaItem
import androidx.media3.common.Player
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.djsm.player.ui.library.LibraryRoute

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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    if (!hasAudioPermission) {

        AudioPermissionScreen(
            onRequestPermission = {
                permissionLauncher.launch(permission)
            }
        )

        return
    }

    val libraryViewModel: com.djsm.player.ui.library.LibraryViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
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
                                    onNextClick = {
                                        currentPlaybackController?.seekToNextMediaItem()
                                    },
                                    onClick = {
                                        backStack.add(NowPlayingRoute)
                                    }
                                )
                            }
                        ) { innerPadding ->

                            LibraryRoute(
                                viewModel = libraryViewModel,
                                onSongClick = { selectedSong, songs ->

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
                                onAlbumClick = { albumId ->
                                    backStack.add(com.djsm.player.navigation.AlbumDetailRoute(albumId))
                                },
                                onArtistClick = { artistId ->
                                    backStack.add(com.djsm.player.navigation.ArtistDetailRoute(artistId))
                                },
                                onFolderClick = { folderId ->
                                    backStack.add(com.djsm.player.navigation.FolderDetailRoute(folderId))
                                },
                                onGenreClick = { genreId ->
                                    backStack.add(com.djsm.player.navigation.GenreDetailRoute(genreId))
                                },
                                onPlaylistClick = { playlistId ->
                                    backStack.add(com.djsm.player.navigation.PlaylistDetailRoute(playlistId))
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }

                is com.djsm.player.navigation.AlbumDetailRoute -> {
                    NavEntry(route) {
                        Scaffold(
                            bottomBar = {
                                MiniPlayer(
                                    state = currentPlaybackUiState,
                                    onPlayPauseClick = {
                                        currentPlaybackController?.let { controller ->
                                            if (controller.isPlaying) controller.pause() else controller.play()
                                        }
                                    },
                                    onNextClick = {
                                        currentPlaybackController?.seekToNextMediaItem()
                                    },
                                    onClick = {
                                        backStack.add(NowPlayingRoute)
                                    }
                                )
                            }
                        ) { innerPadding ->
                            com.djsm.player.ui.library.AlbumDetailRoute(
                                albumId = route.albumId,
                                viewModel = libraryViewModel,
                                onBack = { backStack.removeLastOrNull() },
                                onSongClick = { selectedSong, songs ->
                                    val startIndex = songs.indexOfFirst { it.id == selectedSong.id }
                                    if (startIndex >= 0) {
                                        val mediaItems = songs.map { it.toMediaItem() }
                                        currentPlaybackController?.apply {
                                            setMediaItems(mediaItems, startIndex, 0L)
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

                is com.djsm.player.navigation.ArtistDetailRoute -> {
                    NavEntry(route) {
                        Scaffold(
                            bottomBar = {
                                MiniPlayer(
                                    state = currentPlaybackUiState,
                                    onPlayPauseClick = {
                                        currentPlaybackController?.let { controller ->
                                            if (controller.isPlaying) controller.pause() else controller.play()
                                        }
                                    },
                                    onNextClick = {
                                        currentPlaybackController?.seekToNextMediaItem()
                                    },
                                    onClick = {
                                        backStack.add(NowPlayingRoute)
                                    }
                                )
                            }
                        ) { innerPadding ->
                            com.djsm.player.ui.library.ArtistDetailRoute(
                                artistId = route.artistId,
                                viewModel = libraryViewModel,
                                onBack = { backStack.removeLastOrNull() },
                                onSongClick = { selectedSong, songs ->
                                    val startIndex = songs.indexOfFirst { it.id == selectedSong.id }
                                    if (startIndex >= 0) {
                                        val mediaItems = songs.map { it.toMediaItem() }
                                        currentPlaybackController?.apply {
                                            setMediaItems(mediaItems, startIndex, 0L)
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

                is com.djsm.player.navigation.FolderDetailRoute -> {
                    NavEntry(route) {
                        Scaffold(
                            bottomBar = {
                                MiniPlayer(
                                    state = currentPlaybackUiState,
                                    onPlayPauseClick = {
                                        currentPlaybackController?.let { controller ->
                                            if (controller.isPlaying) controller.pause() else controller.play()
                                        }
                                    },
                                    onNextClick = {
                                        currentPlaybackController?.seekToNextMediaItem()
                                    },
                                    onClick = {
                                        backStack.add(NowPlayingRoute)
                                    }
                                )
                            }
                        ) { innerPadding ->
                            com.djsm.player.ui.library.FolderDetailRoute(
                                folderId = route.folderId,
                                viewModel = libraryViewModel,
                                onBack = { backStack.removeLastOrNull() },
                                onSongClick = { selectedSong, songs ->
                                    val startIndex = songs.indexOfFirst { it.id == selectedSong.id }
                                    if (startIndex >= 0) {
                                        val mediaItems = songs.map { it.toMediaItem() }
                                        currentPlaybackController?.apply {
                                            setMediaItems(mediaItems, startIndex, 0L)
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

                is com.djsm.player.navigation.GenreDetailRoute -> {
                    NavEntry(route) {
                        Scaffold(
                            bottomBar = {
                                MiniPlayer(
                                    state = currentPlaybackUiState,
                                    onPlayPauseClick = {
                                        currentPlaybackController?.let { controller ->
                                            if (controller.isPlaying) controller.pause() else controller.play()
                                        }
                                    },
                                    onNextClick = {
                                        currentPlaybackController?.seekToNextMediaItem()
                                    },
                                    onClick = {
                                        backStack.add(NowPlayingRoute)
                                    }
                                )
                            }
                        ) { innerPadding ->
                            com.djsm.player.ui.library.GenreDetailRoute(
                                genreId = route.genreId,
                                viewModel = libraryViewModel,
                                onBack = { backStack.removeLastOrNull() },
                                onSongClick = { selectedSong, songs ->
                                    val startIndex = songs.indexOfFirst { it.id == selectedSong.id }
                                    if (startIndex >= 0) {
                                        val mediaItems = songs.map { it.toMediaItem() }
                                        currentPlaybackController?.apply {
                                            setMediaItems(mediaItems, startIndex, 0L)
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

                is com.djsm.player.navigation.PlaylistDetailRoute -> {
                    NavEntry(route) {
                        Scaffold(
                            bottomBar = {
                                MiniPlayer(
                                    state = currentPlaybackUiState,
                                    onPlayPauseClick = {
                                        currentPlaybackController?.let { controller ->
                                            if (controller.isPlaying) controller.pause() else controller.play()
                                        }
                                    },
                                    onNextClick = {
                                        currentPlaybackController?.seekToNextMediaItem()
                                    },
                                    onClick = {
                                        backStack.add(NowPlayingRoute)
                                    }
                                )
                            }
                        ) { innerPadding ->
                            com.djsm.player.ui.library.PlaylistDetailRoute(
                                playlistId = route.playlistId,
                                onBackClick = { backStack.removeLastOrNull() },
                                onSongClick = { songs, index ->
                                    val mediaItems = songs.map { it.toMediaItem() }
                                    currentPlaybackController?.apply {
                                        setMediaItems(mediaItems, index, 0L)
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

                            onShuffleClick = {

                                currentPlaybackController?.let { controller ->
                                    controller.shuffleModeEnabled =
                                        !controller.shuffleModeEnabled
                                }
                            },

                            onRepeatClick = {

                                currentPlaybackController?.let { controller ->

                                    controller.repeatMode = when (controller.repeatMode) {

                                        Player.REPEAT_MODE_OFF ->
                                            Player.REPEAT_MODE_ALL

                                        Player.REPEAT_MODE_ALL ->
                                            Player.REPEAT_MODE_ONE

                                        else ->
                                            Player.REPEAT_MODE_OFF
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