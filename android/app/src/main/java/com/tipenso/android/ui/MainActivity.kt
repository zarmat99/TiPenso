package com.tipenso.android.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tipenso.android.TiPensoApplication
import com.tipenso.android.data.ble.ConnectionState
import com.tipenso.android.data.ble.TiPensoDevice
import com.tipenso.android.ui.theme.TiPensoTheme
import com.tipenso.android.ui.screens.ConnectionScreen
import com.tipenso.android.ui.screens.HomeScreen
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    private lateinit var app: TiPensoApplication
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var scanningActive = false

    private val bluetoothEnableLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            bluetoothAdapter?.let { setupBluetooth(it) }
        } else {
            Toast.makeText(this, "Bluetooth è necessario per questa app", Toast.LENGTH_LONG).show()
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            bluetoothAdapter?.let { setupBluetooth(it) }
        } else {
            Toast.makeText(this, "I permessi sono necessari per la scansione BLE", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        app = application as TiPensoApplication

        try {
            val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter
        } catch (e: Exception) {
            Toast.makeText(this, "Bluetooth non disponibile su questo dispositivo", Toast.LENGTH_LONG).show()
        }

        setContent {
            TiPensoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        onInitBluetooth = { bluetoothAdapter?.let { setupBluetooth(it) } }
                    )
                }
            }
        }

        // Verifica Bluetooth dopo aver impostato l'UI
        checkBluetoothAvailability()
    }

    @Composable
    fun AppNavigation(onInitBluetooth: () -> Unit) {
        val navController = rememberNavController()
        val connectionState by app.tiPensoDevice.connectionState.collectAsState()
        val buttonState by app.tiPensoDevice.buttonState.collectAsState()
        var connectedDeviceAddress by remember { mutableStateOf<String?>(null) }
        
        // Monitora lo stato della connessione
        LaunchedEffect(connectionState) {
            when (connectionState) {
                is ConnectionState.Connected -> {
                    // Se siamo connessi ma siamo nella schermata di connessione, naviga alla home
                    if (navController.currentBackStackEntry?.destination?.route == "connection") {
                        navController.navigate("home")
                    }
                }
                is ConnectionState.Disconnected -> {
                    // Se siamo disconnessi ma siamo nella home, torna alla schermata di connessione
                    if (navController.currentBackStackEntry?.destination?.route == "home") {
                        navController.navigate("connection") {
                            popUpTo("connection") { inclusive = true }
                        }
                    }
                }
                else -> {} // Gestisci altri stati se necessario
            }
        }
        
        NavHost(navController = navController, startDestination = "connection") {
            composable("connection") {
                ConnectionScreen(
                    onDeviceConnected = { deviceAddress ->
                        // Connetti al dispositivo selezionato
                        connectToDevice(deviceAddress)
                        connectedDeviceAddress = deviceAddress
                    },
                    onInitBluetooth = onInitBluetooth,
                    scanForDevices = { scanForTiPensoDevices() }
                )
            }
            composable("home") {
                HomeScreen(
                    isConnected = connectionState is ConnectionState.Connected,
                    deviceAddress = connectedDeviceAddress,
                    onDisconnect = {
                        app.tiPensoDevice.disconnect()
                        connectedDeviceAddress = null
                    },
                    onToggleLed = { state -> app.tiPensoDevice.setLedState(state) },
                    buttonState = buttonState
                )
            }
        }
    }

    private fun checkBluetoothAvailability() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth non supportato su questo dispositivo", Toast.LENGTH_LONG).show()
            return
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothEnableLauncher.launch(enableBtIntent)
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        } else {
            bluetoothAdapter?.let { setupBluetooth(it) }
        }
    }

    private fun setupBluetooth(adapter: BluetoothAdapter) {
        try {
            app.initializeBluetooth(adapter)
            // Ora possiamo iniziare a usare il Bluetooth
            Toast.makeText(this, "Bluetooth inizializzato con successo", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Errore nell'inizializzazione del Bluetooth: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun scanForTiPensoDevices(): List<Pair<String, String>> {
        // Avvia la scansione se non è già attiva
        if (!scanningActive) {
            println("Avvio scansione dispositivi Ti Penso")
            app.bleScanner.startScan()
            scanningActive = true
        }
        
        // Mostra tutti i dispositivi trovati per debug
        val allDevices = app.bleScanner.scanResults.value
        println("Dispositivi trovati: ${allDevices.size}")
        allDevices.forEach { 
            println("- ${it.device.name ?: "Unknown"} (${it.device.address})")
        }
        
        // Ritorna tutti i dispositivi trovati per ora, per debug
        return app.bleScanner.scanResults.value
            .map { Pair(it.device.name ?: "Dispositivo sconosciuto", it.device.address) }
    }
    
    private fun connectToDevice(deviceAddress: String) {
        // Ferma la scansione prima di connettersi
        if (scanningActive) {
            app.bleScanner.stopScan()
            scanningActive = false
        }
        
        // Trova il dispositivo e connettiti
        bluetoothAdapter?.let { adapter ->
            val device = adapter.getRemoteDevice(deviceAddress)
            app.tiPensoDevice.connect(device)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Assicurati di fermare la scansione e disconnetterti quando l'attività viene distrutta
        if (scanningActive) {
            app.bleScanner.stopScan()
        }
        app.tiPensoDevice.disconnect()
    }
} 