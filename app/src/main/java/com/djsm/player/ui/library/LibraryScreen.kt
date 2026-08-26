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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.PaddingValues
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
import com.djsm.player.ui.components.CategoryListItem
import com.djsm.player.ui.components.SongListItem
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.QueueMusic

@Composable
fun LibraryScreen(
    songs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    folders: List<Folder>,
    genres: List<Genre>,
    playlists: List<Playlist>,
    historyEvents: List<com.djsm.player.domain.repository.HistoryEvent>,
    recentlyPlayed: List<com.djsm.player.domain.repository.HistoryAggregateItem>,
    mostPlayed: List<com.djsm.player.domain.repository.HistoryAggregateItem>,
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
    onSettingsClick: () -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onAddSongToPlaylist: (String, Song) -> Unit,
    modifier: Modifier = Modifier,
    isNoSearchResults: Boolean = false
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Songs", "Albums", "Artists", "Folders", "Genres", "Playlists", "History", "Recent", "Most Played")

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
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 24.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DJSM Player",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onSettingsClick) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Ajustes",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

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
                            CategoryListItem(
                                title = album.name,
                                subtitle = "${album.artist} • ${album.songCount} canciones",
                                icon = androidx.compose.material.icons.Icons.Filled.Album,
                                onClick = { onAlbumClick(album) }
                            )
                            HorizontalDivider()
                        }
                    }
                    2 -> {
                        items(
                            items = artists,
                            key = { it.id }
                        ) { artist ->
                            CategoryListItem(
                                title = artist.name,
                                subtitle = "${artist.albumCount} álbumes • ${artist.songCount} canciones",
                                icon = androidx.compose.material.icons.Icons.Filled.Person,
                                onClick = { onArtistClick(artist) }
                            )
                            HorizontalDivider()
                        }
                    }
                    3 -> {
                        items(
                            items = folders,
                            key = { it.id }
                        ) { folder ->
                            CategoryListItem(
                                title = folder.name,
                                subtitle = "${folder.songCount} canciones",
                                icon = androidx.compose.material.icons.Icons.Filled.Folder,
                                onClick = { onFolderClick(folder) }
                            )
                            HorizontalDivider()
                        }
                    }
                    4 -> {
                        items(
                            items = genres,
                            key = { it.id }
                        ) { genre ->
                            CategoryListItem(
                                title = genre.name,
                                subtitle = "${genre.songCount} canciones",
                                icon = androidx.compose.material.icons.Icons.Filled.Category,
                                onClick = { onGenreClick(genre) }
                            )
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
                            CategoryListItem(
                                title = playlist.name,
                                subtitle = "Playlist",
                                icon = androidx.compose.material.icons.Icons.Filled.QueueMusic,
                                onClick = { onPlaylistClick(playlist) }
                            )
                            HorizontalDivider()
                        }
                    }
                    6 -> {
                        items(historyEvents) { event ->
                            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                                Text(text = "${event.snapshotTitle} by ${event.snapshotArtist}", style = MaterialTheme.typography.bodyLarge)
                                Text(text = "Played at: ${event.playedAt}", style = MaterialTheme.typography.bodyMedium)
                            }
                            HorizontalDivider()
                        }
                    }
                    7 -> {
                        items(recentlyPlayed) { item ->
                            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                                Text(text = "Fingerprint: ${item.fingerprint.take(8)}...", style = MaterialTheme.typography.bodyLarge)
                                Text(text = "Plays: ${item.playCount} | Last: ${item.lastPlayedAt}", style = MaterialTheme.typography.bodyMedium)
                            }
                            HorizontalDivider()
                        }
                    }
                    8 -> {
                        items(mostPlayed) { item ->
                            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
                                Text(text = "Fingerprint: ${item.fingerprint.take(8)}...", style = MaterialTheme.typography.bodyLarge)
                                Text(text = "Plays: ${item.playCount} | Last: ${item.lastPlayedAt}", style = MaterialTheme.typography.bodyMedium)
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistList(
    playlists: List<com.djsm.player.domain.model.Playlist>,
    onCreatePlaylistClick: () -> Unit,
    onPlaylistClick: (com.djsm.player.domain.model.Playlist) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Button(
                onClick = onCreatePlaylistClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Create Playlist")
            }
        }
        items(playlists) { playlist ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPlaylistClick(playlist) }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        }
    }
}
