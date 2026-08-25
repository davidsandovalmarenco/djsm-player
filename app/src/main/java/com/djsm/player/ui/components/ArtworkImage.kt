package com.djsm.player.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.djsm.player.data.local.ArtworkLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ArtworkImage(
    contentUri: String?,
    albumId: Long?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {

    val context = LocalContext.current

    val artwork by produceState<Bitmap?>(
        initialValue = null,
        key1 = contentUri,
        key2 = albumId
    ) {

        value = contentUri?.let { uri ->

            withContext(Dispatchers.IO) {

                ArtworkLoader(
                    context.applicationContext
                ).loadArtwork(
                    contentUri = uri,
                    albumId = albumId
                )
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        if (artwork != null) {

            Image(
                bitmap = artwork!!.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

        } else {

            Text(
                text = "♪",
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}