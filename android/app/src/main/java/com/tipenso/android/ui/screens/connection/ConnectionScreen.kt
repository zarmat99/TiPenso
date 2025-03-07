package com.tipenso.android.ui.screens.connection

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tipenso.android.data.ble.ConnectionState

@Composable
fun ConnectionScreen(
    viewModel: ConnectionViewModel,
    deviceAddress: String,
    onBackClick: () -> Unit,
    onConnected: () -> Unit
) {
    val connectionState by viewModel.connectionState.collectAsState()

    LaunchedEffect(deviceAddress) {
        viewModel.connectToDevice(deviceAddress)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (connectionState) {
            is ConnectionState.Connected -> {
                Text("Connected to device")
                Button(onClick = onConnected) {
                    Text("Continue")
                }
            }
            is ConnectionState.Connecting -> {
                CircularProgressIndicator()
                Text("Connecting...")
            }
            is ConnectionState.Disconnected -> {
                Text("Disconnected")
                Button(onClick = { viewModel.connectToDevice(deviceAddress) }) {
                    Text("Retry Connection")
                }
            }
            is ConnectionState.Error -> {
                Text("Error: ${(connectionState as ConnectionState.Error).message}")
                Button(onClick = { viewModel.connectToDevice(deviceAddress) }) {
                    Text("Retry")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
} 