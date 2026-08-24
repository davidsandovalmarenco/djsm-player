package com.djsm.player.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.djsm.player.domain.model.Song

@Composable
fun LibraryScreen(
    songs: List<Song>,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Text(
            text = "DJSM Player",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(
                start = 20.dp,
                top = 24.dp,
                end = 20.dp
            )
        )

        Text(
            text = "${songs.size} canciones",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(
                start = 20.dp,
                top = 4.dp,
                bottom = 16.dp
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            items(
                items = songs,
                key = { song -> song.id }
            ) { song ->

                SongListItem(
                    song = song
                )

                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun SongListItem(
    song: Song
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${song.artist} • ${song.album}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        Text(
            text = formatDuration(song.durationMs),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun formatDuration(
    durationMs: Long
): String {

    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "$minutes:${seconds.toString().padStart(2, '0')}"
}