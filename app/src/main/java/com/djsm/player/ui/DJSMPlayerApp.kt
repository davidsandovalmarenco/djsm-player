package com.djsm.player.ui

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.djsm.player.core.permission.audioPermission
import com.djsm.player.data.local.MediaStoreAudioDataSource
import com.djsm.player.ui.library.LibraryScreen
import com.djsm.player.ui.permission.AudioPermissionScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DJSMPlayerApp() {

    val context = LocalContext.current
    val permission = audioPermission()

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var songCount by remember {
        mutableIntStateOf(0)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    LaunchedEffect(hasAudioPermission) {

        if (hasAudioPermission) {

            songCount = withContext(Dispatchers.IO) {

                val dataSource = MediaStoreAudioDataSource(
                    context = context.applicationContext
                )

                dataSource.getSongs().size
            }
        }
    }

    if (hasAudioPermission) {

        LibraryScreen(
            songCount = songCount
        )

    } else {

        AudioPermissionScreen(
            onRequestPermission = {
                permissionLauncher.launch(permission)
            }
        )
    }
}