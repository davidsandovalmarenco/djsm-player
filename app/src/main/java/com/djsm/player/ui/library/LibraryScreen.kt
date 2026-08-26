package com.djsm.player.ui.library

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.djsm.player.domain.model.Album
import com.djsm.player.domain.model.Artist
import com.djsm.player.domain.model.Folder
import com.djsm.player.domain.model.Genre
import com.djsm.player.domain.model.Playlist
import com.djsm.player.domain.model.Song
import com.djsm.player.domain.model.SortOption
import com.djsm.player.domain.model.SortOrder
import com.djsm.player.ui.components.SongListItem

@Composable
fun LibraryScreen(
    songs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    folders: List<Folder>,
    genres: List<Genre>,
    playlists: List<Playlist>,
    favoriteSongIds: Set<Long>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    sortOrder: SortOrder,
    onSortOrderToggle: () -> Unit,
    onSongClick: (Song) -> Unit,
    onToggleFavorite: (Song, Boolean) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onFolderClick: (Folder) -> Unit,
    onGenreClick: (Genre) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onAddSongToPlaylist: (String, Song) -> Unit,
    modifier: Modifier = Modifier,
    isNoSearchResults: Boolean = false
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Songs", "Albums", "Artists", "Folders", "Genres", "Playlists")

    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }

    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onConfirm = { name ->
                onCreatePlaylist(name)
                showCreatePlaylistDialog = false
            }
        )
    }

    if (songToAddToPlaylist != null) {
        AddToPlaylistDialog(
            playlists = playlists,
            onDismiss = { songToAddToPlaylist = null },
            onPlaylistSelected = { playlist ->
                onAddSongToPlaylist(playlist.id, songToAddToPlaylist!!)
                songToAddToPlaylist = null
            },
            onCreateNewClick = {
                songToAddToPlaylist = null
                showCreatePlaylistDialog = true
            }
        )
    }

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

        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 20.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val countText = when(selectedTabIndex) {
                0 -> "${songs.size} canciones"
                1 -> "${albums.size} álbumes"
                2 -> "${artists.size} artistas"
                3 -> "${folders.size} carpetas"
                4 -> "${genres.size} géneros"
                5 -> "${playlists.size} playlists"
                else -> ""
            }
            Text(
                text = if (isNoSearchResults) "0 resultados" else countText,
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
                when (selectedTabIndex) {
                    0 -> {
                        items(
                            items = songs,
                            key = { it.id }
                        ) { song ->
                            SongListItem(
                                song = song,
                                onClick = { onSongClick(song) },
                                isFavorite = favoriteSongIds.contains(song.id),
                                onToggleFavorite = { onToggleFavorite(song, favoriteSongIds.contains(song.id)) },
                                onAddToPlaylist = { songToAddToPlaylist = song }
                            )
                            HorizontalDivider()
                        }
                    }
                    1 -> {
                        items(
                            items = albums,
                            key = { it.id }
                        ) { album ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onAlbumClick(album) }
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Text(album.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = "${album.artist} • ${album.songCount} canciones",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                    2 -> {
                        items(
                            items = artists,
                            key = { it.id }
                        ) { artist ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onArtistClick(artist) }
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Text(artist.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = "${artist.albumCount} álbumes • ${artist.songCount} canciones",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                    3 -> {
                        items(
                            items = folders,
                            key = { it.id }
                        ) { folder ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onFolderClick(folder) }
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Text(folder.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = "${folder.songCount} canciones",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                    4 -> {
                        items(
                            items = genres,
                            key = { it.id }
                        ) { genre ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onGenreClick(genre) }
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Text(genre.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = "${genre.songCount} canciones",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                    5 -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).clickable { showCreatePlaylistDialog = true }
                            ) {
                                Text("➕ Nueva Playlist", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                            }
                            HorizontalDivider()
                        }
                        items(
                            items = playlists,
                            key = { it.id }
                        ) { playlist ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPlaylistClick(playlist) }
                                    .padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Text(playlist.name, style = MaterialTheme.typography.bodyLarge)
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}