package com.djsm.player.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.djsm.player.domain.model.Song
import com.djsm.player.ui.components.SongListItem

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