package com.tipenso.android.ui.screens.control

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Card

@Composable
fun ControlScreen(
    viewModel: ControlViewModel,
    deviceAddress: String,
    onBackClick: () -> Unit
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val messages by viewModel.messages.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Status Bar
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Status: ${connectionState::class.simpleName}",
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comandi principali
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.sendCommand("start") }) {
                Text("Start")
            }
            Button(onClick = { viewModel.sendCommand("stop") }) {
                Text("Stop")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Log dei messaggi
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(messages) { message ->
                    Text(message)
                    HorizontalDivider()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Disconnect")
        }
    }
} 