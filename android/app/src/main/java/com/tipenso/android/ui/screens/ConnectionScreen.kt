package com.tipenso.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ConnectionScreen(
    onDeviceConnected: (String) -> Unit,
    onInitBluetooth: () -> Unit,
    scanForDevices: () -> List<Pair<String, String>>
) {
    var isScanning by remember { mutableStateOf(false) }
    var devices by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        // Inizializza Bluetooth quando la schermata viene mostrata
        onInitBluetooth()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Connessione Dispositivo",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Button(
            onClick = {
                isScanning = true
                scope.launch {
                    // Avvia la scansione dei dispositivi
                    println("Avvio scansione da ConnectionScreen")
                    devices = emptyList() // Resetta la lista
                    delay(1000) // Breve attesa
                    devices = scanForDevices()
                    println("Scansione completata, trovati ${devices.size} dispositivi")
                    isScanning = false
                }
            },
            enabled = !isScanning,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Cerca",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(if (isScanning) "Ricerca in corso..." else "Cerca dispositivi")
        }
        
        if (isScanning) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }
        
        if (devices.isEmpty() && !isScanning) {
            Text(
                text = "Nessun dispositivo trovato. Prova a cercare di nuovo.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(32.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(devices) { device ->
                    DeviceItem(
                        deviceName = device.first,
                        deviceAddress = device.second,
                        onClick = { onDeviceConnected(device.second) }
                    )
                }
            }
        }
        
        // Informazioni sul sistema Ti Penso
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Informazioni",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Ti Penso è un sistema che permette di connettere due dispositivi tramite Bluetooth. " +
                           "Quando un dispositivo rileva la pressione del pulsante, l'altro dispositivo accende il LED.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceItem(
    deviceName: String,
    deviceAddress: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Bluetooth,
                contentDescription = "Dispositivo Bluetooth",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            Column {
                Text(
                    text = deviceName,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = deviceAddress,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
} 