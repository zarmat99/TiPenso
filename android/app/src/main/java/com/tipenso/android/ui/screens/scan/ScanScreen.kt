package com.tipenso.android.ui.screens.scan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.bluetooth.le.ScanResult
import androidx.compose.foundation.clickable

@Composable
fun ScanScreen(
    viewModel: ScanViewModel,
    onDeviceClick: (String) -> Unit
) {
    val scanResults by viewModel.scanResults.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { 
                if (isScanning) viewModel.stopScan() 
                else viewModel.startScan() 
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isScanning) "Stop Scan" else "Start Scan")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            if (scanResults.isEmpty() && isScanning) {
                item {
                    Text(
                        "Ricerca in corso...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (scanResults.isEmpty()) {
                item {
                    Text(
                        "Nessun dispositivo trovato. Avvia la scansione.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            items(scanResults) { result ->
                DeviceItem(
                    name = result.device.name ?: "Unknown Device",
                    address = result.device.address,
                    rssi = result.rssi,
                    onClick = { onDeviceClick(result.device.address) }
                )
            }
        }
    }
}

@Composable
fun DeviceItem(
    name: String,
    address: String,
    rssi: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Address: $address", style = MaterialTheme.typography.bodyMedium)
            Text(text = "RSSI: $rssi dBm", style = MaterialTheme.typography.bodySmall)
        }
    }
} 