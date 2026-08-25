package com.djsm.player.ui.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.djsm.player.domain.model.Song
import com.djsm.player.domain.model.SortOption
import com.djsm.player.domain.model.SortOrder
import com.djsm.player.ui.components.SongListItem

@Composable
fun LibraryScreen(
    songs: List<Song>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    sortOrder: SortOrder,
    onSortOrderToggle: () -> Unit,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    isNoSearchResults: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "DJSM Player",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 20.dp, top = 24.dp, end = 20.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            placeholder = { Text("Buscar canción, artista o álbum...") },
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isNoSearchResults) "0 resultados" else "${songs.size} canciones",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            var expanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Text("▼")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name) },
                            onClick = {
                                onSortOptionChange(option)
                                expanded = false
                            }
                        )
                    }
                }
            }

            IconButton(onClick = onSortOrderToggle) {
                Text(
                    text = if (sortOrder == SortOrder.ASCENDING) "↑" else "↓"
                )
            }
        }

        if (isNoSearchResults) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay resultados para \"$searchQuery\"")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = songs,
                    key = { song -> song.id }
                ) { song ->
                    SongListItem(
                        song = song,
                        onClick = { onSongClick(song) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}