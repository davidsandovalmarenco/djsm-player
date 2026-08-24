package com.djsm.player.ui.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AudioPermissionScreen(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tu música. Tu dispositivo."
        )

        Text(
            text = "DJSM Player necesita acceso a tus archivos de audio para crear tu biblioteca.",
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        )

        Button(
            onClick = onRequestPermission
        ) {
            Text("Permitir acceso")
        }
    }
}