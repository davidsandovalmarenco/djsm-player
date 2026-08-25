package com.djsm.player.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.djsm.player.domain.model.Song

@Composable
fun LibraryRoute(
    onSongClick: (
        selectedSong: Song,
        songs: List<Song>
    ) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {

        uiState.isLoading -> {

            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.errorMessage != null -> {

            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = uiState.errorMessage
                        ?: "No se pudo cargar la biblioteca"
                )

                Button(
                    onClick = viewModel::loadSongs
                ) {
                    Text("Reintentar")
                }
            }
        }

        else -> {

            LibraryScreen(
                songs = uiState.songs,
                onSongClick = { selectedSong ->

                    onSongClick(
                        selectedSong,
                        uiState.songs
                    )
                },
                modifier = modifier
            )
        }
    }
}