package com.djsm.player.ui.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.djsm.player.domain.model.Song
import com.djsm.player.ui.components.SongListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderDetailRoute(
    folderId: String,
    viewModel: LibraryViewModel,
    onBack: () -> Unit,
    onSongClick: (Song, List<Song>) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState is LibraryUiState.Success) {
        val successState = uiState as LibraryUiState.Success
        val folder = successState.folders.find { it.id == folderId }

        if (folder != null) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(folder.name) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Text("<-")
                            }
                        }
                    )
                },
                modifier = modifier
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(
                        items = folder.songs,
                        key = { it.id }
                    ) { song ->
                        SongListItem(
                            song = song,
                            onClick = { onSongClick(song, folder.songs) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Carpeta no encontrada")
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Cargando...")
        }
    }
}
