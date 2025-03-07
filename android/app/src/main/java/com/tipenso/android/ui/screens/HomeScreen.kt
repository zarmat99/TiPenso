package com.tipenso.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.BluetoothDisabled
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    isConnected: Boolean,
    deviceAddress: String?,
    onDisconnect: () -> Unit,
    onToggleLed: (Boolean) -> Unit,
    buttonState: Boolean
) {
    var devicePower by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Stato connessione
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isConnected) Icons.Outlined.Bluetooth else Icons.Outlined.BluetoothDisabled,
                    contentDescription = "Stato connessione",
                    tint = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isConnected) "Dispositivo connesso" else "Disconnesso",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (deviceAddress != null) {
                        Text(
                            text = deviceAddress,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            Button(onClick = onDisconnect) {
                Text("Disconnetti")
            }
        }
        
        Divider()
        
        // Controlli dispositivo
        if (isConnected) {
            Text(
                text = "Controllo Dispositivo",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 24.dp)
            )
            
            // Stato pulsante (in sola lettura)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Stato Pulsante",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (buttonState) "Premuto" else "Rilasciato",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (buttonState) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            // Controllo LED
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LED",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Switch(
                        checked = devicePower,
                        onCheckedChange = { 
                            devicePower = it
                            onToggleLed(it)
                        },
                        thumbContent = if (devicePower) {
                            {
                                Icon(
                                    imageVector = Icons.Outlined.Power,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize)
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
            
            // Informazioni sul dispositivo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Informazioni Dispositivo",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tipo:")
                        Text("Ti Penso")
                    }
                    
                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Versione:")
                        Text("1.0")
                    }
                    
                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Stato:")
                        Text("Attivo", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Connetti un dispositivo per visualizzare i controlli",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
} 