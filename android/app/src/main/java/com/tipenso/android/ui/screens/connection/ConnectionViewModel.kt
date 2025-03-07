package com.tipenso.android.ui.screens.connection

import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tipenso.android.data.ble.BleManager
import com.tipenso.android.data.ble.ConnectionState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConnectionViewModel(
    private val bleManager: BleManager,
    private val bluetoothAdapter: BluetoothAdapter
) : ViewModel() {
    val connectionState: StateFlow<ConnectionState> = bleManager.connectionState

    fun connectToDevice(address: String) {
        viewModelScope.launch {
            try {
                val device = bluetoothAdapter.getRemoteDevice(address)
                bleManager.connect(device)
            } catch (e: Exception) {
                // Gestione errori di connessione
            }
        }
    }

    fun disconnect() {
        bleManager.disconnect()
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
} 